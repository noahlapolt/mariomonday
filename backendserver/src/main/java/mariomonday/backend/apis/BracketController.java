package mariomonday.backend.apis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import mariomonday.backend.apis.schema.AddPlayerSetToBracketRequest;
import mariomonday.backend.apis.schema.ApiBracket;
import mariomonday.backend.apis.schema.CompleteGameSetRequest;
import mariomonday.backend.apis.schema.CreateBracketRequest;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.Game;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import mariomonday.backend.database.tables.BracketRepository;
import mariomonday.backend.database.tables.GameRepository;
import mariomonday.backend.database.tables.GameSetRepository;
import mariomonday.backend.database.tables.PlayerRepository;
import mariomonday.backend.error.exceptions.InvalidRequestException;
import mariomonday.backend.error.exceptions.NotFoundException;
import mariomonday.backend.managers.ratingcalculators.AbstractEloManager;
import mariomonday.backend.managers.seeders.AbstractSeeder;
import mariomonday.backend.managers.tournamentcreators.AbstractBracketCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API endpoint for everything related to brackets
 */
@RestController
@RequestMapping(value = "/api")
public class BracketController {

  /**
   * Bracket creator used to create a bracket from a seeded list of players
   */
  @Autowired
  AbstractBracketCreator bracketCreator;

  /**
   * Seeder algorithm to use
   */
  @Autowired
  AbstractSeeder seeder;

  /**
   * ELO algorithm to use
   */
  @Autowired
  AbstractEloManager eloManager;

  /**
   * Bracket table
   */
  @Autowired
  BracketRepository bracketRepo;

  /**
   * Game Set table
   */
  @Autowired
  GameSetRepository gameSetRepo;

  /**
   * Game table
   */
  @Autowired
  GameRepository gameRepo;

  /**
   * Player table
   */
  @Autowired
  PlayerRepository playerRepo;

  /**
   * Mongo template object, used for updates/deletions that require more precision
   * than the Spring Repository infrastructure provides
   */
  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * Get the given bracket
   * @param bracketId The ID of the bracket to get
   */
  @GetMapping("/bracket/{bracketId}")
  ApiBracket getBracket(@PathVariable String bracketId) {
    return ApiBracket.fromBracket(
      bracketRepo
        .findById(bracketId)
        .orElseThrow(() -> new NotFoundException("Bracket not found with id: " + bracketId))
    );
  }

  /**
   * Get the most recent bracket
   */
  @GetMapping("/getCurrentBracket")
  ApiBracket getCurrentBracket() {
    var query = new Query();
    var results = mongoTemplate.find(query.with(Sort.by(Order.desc("date"))), Bracket.class);
    return ApiBracket.fromBracket(
      results
        .stream()
        .findFirst()
        .orElseThrow(() -> new NotFoundException("No brackets exist!"))
    );
  }

  /**
   * Create a new bracket
   * @param request The request to create the bracket
   * @return The newly created bracket
   */
  @PostMapping("/bracket")
  public ApiBracket postBracket(@RequestBody CreateBracketRequest request) {
    GameType gameType = request.getGameType();
    Map<String, List<String>> teams = request.getTeams();
    if (gameType == null) {
      throw new InvalidRequestException("Game Type must have a value");
    }
    if (teams == null) {
      throw new InvalidRequestException("Teams must have a value");
    }
    if (teams.size() < 2) {
      throw new InvalidRequestException("Cannot run a tournament with less than two participants!");
    }
    Set<PlayerSet> unseededTeams = new HashSet<>();
    Set<String> playerIdsInTourney = new HashSet<>();
    for (Entry<String, List<String>> team : teams.entrySet()) {
      String teamName = team.getKey();
      List<String> playerIds = team.getValue();
      if (teamName == null) {
        throw new InvalidRequestException("Cannot have a null team name!");
      }
      if (playerIds == null) {
        throw new InvalidRequestException("Cannot have a null team!");
      }
      if (playerIds.isEmpty()) {
        throw new InvalidRequestException("Cannot have an empty team!");
      }
      if (playerIds.size() != gameType.getPlayersOnATeam()) {
        throw new InvalidRequestException(
          "For game type " + gameType + " teams should be of size " + gameType.getPlayersOnATeam()
        );
      }
      if (playerIds.stream().anyMatch(Objects::isNull)) {
        throw new InvalidRequestException("Cannot have a null player ID!");
      }
      for (String playerId : playerIds) {
        if (playerIdsInTourney.contains(playerId)) {
          throw new InvalidRequestException("Cannot have repeat players in tournament!");
        }
        playerIdsInTourney.add(playerId);
      }
      List<Player> players = playerIds
        .stream()
        .map(playerId ->
          playerRepo.findById(playerId).orElseThrow(() -> new InvalidRequestException("Some players cannot be found"))
        )
        .toList();
      unseededTeams.add(PlayerSet.builder().name(teamName).players(players).id(UUID.randomUUID().toString()).build());
    }

    Bracket bracket = bracketCreator.fromPlayerSets(gameType, seeder.seed(unseededTeams, gameType));

    // Save bracket and game sets in DB. Make sure to returned saved objects,
    // since they will have IDs
    bracket.setGameSets(
      bracket
        .getGameSets()
        .stream()
        .map(gameSet -> gameSetRepo.save(gameSet))
        .collect(Collectors.toSet())
    );
    bracket = bracketRepo.save(bracket);
    // MongoDB does some time truncation and such so we want to get it in that state
    return ApiBracket.fromBracket(bracketRepo.findById(bracket.getId()).get());
  }

  /**
   * Complete a game set within a bracket
   * @param bracketId The bracket this game set is a part of
   * @param gameSetId The game set to complete
   * @param request The request to complete the game set
   * @return The bracket, updated with the completed game set
   */
  @PostMapping("/bracket/{bracketId}/completeGameSet/{gameSetId}")
  public ApiBracket completeGameSet(
    @PathVariable String bracketId,
    @PathVariable String gameSetId,
    @RequestBody CompleteGameSetRequest request
  ) {
    var bracket = bracketRepo.findById(bracketId).orElseThrow(() -> new NotFoundException("Bracket not found"));
    if (!bracket.getWinners().isEmpty()) {
      throw new InvalidRequestException("May not update a completed bracket!");
    }
    var gameSet = gameSetRepo.findById(gameSetId).orElseThrow(() -> new NotFoundException("Game Set not found"));
    var previouslyCompleted = !gameSet.getWinners().isEmpty();
    if (previouslyCompleted) {
      var nextGameSetHasWinners = bracket
        .getGameSets()
        .stream()
        .filter(gs ->
          gs
            .getPreviousGameSets()
            .stream()
            .anyMatch(pgs -> pgs.getId().equals(gameSet.getId()))
        )
        .anyMatch(gs -> !gs.getWinners().isEmpty());
      if (nextGameSetHasWinners) {
        throw new InvalidRequestException(
          "The result previously uploaded for the given game set " +
            "has already been used in a future game set. It can no longer be changed"
        );
      }
    }
    if (!bracket.getGameSets().stream().map(GameSet::getId).collect(Collectors.toSet()).contains(gameSetId)) {
      throw new InvalidRequestException("The given game set is not associated with the given bracket");
    }
    if (request.getWinners() == null) {
      throw new InvalidRequestException("Winners list may not be null");
    }
    if (request.getWinners().isEmpty()) {
      throw new InvalidRequestException("Game set must have winners to be completed!");
    }
    if (
      gameSet
        .getPreviousGameSets()
        .stream()
        .anyMatch(gs -> gs.getWinners().isEmpty())
    ) {
      throw new InvalidRequestException("Previous games must have been completed first!");
    }
    var teamsToMoveOn = bracket.getGameType().getPlayerSetsToMoveOn();
    if (request.getWinners().size() > teamsToMoveOn) {
      throw new InvalidRequestException(
        "Only " + teamsToMoveOn + " teams may win for game type " + bracket.getGameType()
      );
    }
    if (request.getGames() == null) {
      throw new InvalidRequestException("Games list may not be null");
    }
    if (request.isForfeit() || gameSet.isByeRound()) {
      if (!request.getGames().isEmpty()) {
        throw new InvalidRequestException("If set is forfeit or a bye round, games must be empty.");
      }
    } else if (request.getGames().isEmpty()) {
      throw new InvalidRequestException("If set is not forfeit or a bye round, games must be submitted.");
    }
    var players = gameSet.getPlayers();
    var teamIdToPlayerSet = players.stream().collect(Collectors.toMap(PlayerSet::getId, playerSet -> playerSet));
    var winners = new HashSet<PlayerSet>();
    for (var teamId : request.getWinners()) {
      if (teamId == null) {
        throw new InvalidRequestException("Winner ID may not be null");
      }
      var winningTeam = teamIdToPlayerSet.get(teamId);
      if (winningTeam == null) {
        throw new InvalidRequestException("Team ID " + teamId + " not associated with given game set");
      }
      winners.add(winningTeam);
    }
    gameSet.setWinners(winners);
    var losers = new HashSet<>(players);
    losers.removeAll(winners);
    gameSet.setLosers(losers);

    var games = new ArrayList<Game>();
    for (var game : request.getGames()) {
      if (game == null) {
        throw new InvalidRequestException("Game may not be null");
      }
      var orderedTeams = game.stream().map(teamIdToPlayerSet::get).toList();
      if (orderedTeams.stream().anyMatch(team -> team == null)) {
        throw new InvalidRequestException("Game references invalid team.");
      }
      games.add(Game.builder().gameType(bracket.getGameType()).playerSets(orderedTeams).build());
    }
    if (previouslyCompleted) {
      // This set was previously completed with different games, remove them.
      gameRepo.deleteAll(gameSet.getGames());
    }
    gameSet.setGames(new HashSet<>(gameRepo.saveAll(games)));
    gameSetRepo.save(gameSet);
    return ApiBracket.fromBracket(bracketRepo.findById(bracket.getId()).get());
  }

  /**
   * Adds the given player to the given bracket in the given game set
   * @param bracketId The bracket to add the player to
   * @return The bracket, updated with the new player
   */
  @PostMapping("/bracket/{bracketId}/addPlayer")
  public ApiBracket addPlayer(@PathVariable String bracketId, @RequestBody AddPlayerSetToBracketRequest request) {
    var gameSet = gameSetRepo
      .findById(request.getGameSetId())
      .orElseThrow(() -> new NotFoundException("Game Set not found"));
    var bracket = bracketRepo.findById(bracketId).orElseThrow(() -> new NotFoundException("Bracket not found"));
    if (
      !bracket.getGameSets().stream().map(GameSet::getId).collect(Collectors.toSet()).contains(request.getGameSetId())
    ) {
      throw new InvalidRequestException("The given game set is not associated with the given bracket");
    }
    if (gameSet.getTotalPlayers() == bracket.getGameType().getMaxPlayerSets()) {
      throw new InvalidRequestException("Requested game is full!");
    }
    if (!gameSet.getWinners().isEmpty()) {
      throw new InvalidRequestException("Cannot add player to completed game set!");
    }
    if (request.getPlayerIds() == null) {
      throw new InvalidRequestException("Players may not be null!");
    }
    var remainingPlayerIds = bracket.getRemainingPlayers().stream().map(Player::getId).collect(Collectors.toSet());
    List<Player> players = new ArrayList<>();
    for (var playerId : request.getPlayerIds()) {
      if (remainingPlayerIds.contains(playerId)) {
        throw new InvalidRequestException("Player ID " + playerId + " is still alive and kicking!");
      }
      players.add(
        playerRepo.findById(playerId).orElseThrow(() -> new NotFoundException("Player ID " + playerId + " not found"))
      );
    }

    var playerSetOpt = bracket
      .getTeams()
      .stream()
      .filter(team ->
        team.getPlayers().stream().map(Player::getId).collect(Collectors.toSet()).containsAll(request.getPlayerIds())
      )
      .findFirst();
    PlayerSet playerSet;
    if (playerSetOpt.isEmpty()) {
      // In single player, player not in the tourney yet.
      // In doubles or more, this player combo is not in the tourney yet.
      if (players.size() > 1 && request.getTeamName() == null) {
        throw new InvalidRequestException("Must provide a team name!");
      }
      playerSet = PlayerSet.builder()
        .name(players.size() == 1 ? players.get(0).getName() : request.getTeamName())
        .id(UUID.randomUUID().toString())
        .players(players)
        .build();
      var currTeams = bracket.getTeams();
      currTeams.add(playerSet);
      bracket.setTeams(currTeams);
      bracketRepo.save(bracket);
    } else {
      playerSet = playerSetOpt.get();
    }
    var addedPlayerSets = gameSet.getAddedPlayerSets();
    addedPlayerSets.add(playerSet);
    gameSet.setAddedPlayerSets(addedPlayerSets);
    gameSetRepo.save(gameSet);
    return ApiBracket.fromBracket(bracketRepo.findById(bracketId).get());
  }

  /**
   * Complete the given bracket, updating the ELO of all players accordingly.
   * @param bracketId The ID of the bracket to complete
   */
  @PostMapping("/bracket/{bracketId}/complete")
  public void completeBracket(@PathVariable String bracketId) {
    var bracket = bracketRepo.findById(bracketId).orElseThrow(() -> new NotFoundException("Bracket not found"));
    if (bracket.getFinalGameSet().getWinners().isEmpty()) {
      throw new InvalidRequestException("Cannot complete bracket until final game set is completed!");
    }
    if (!bracket.getWinners().isEmpty()) {
      throw new InvalidRequestException("Bracket is already completed!");
    }
    if (bracket.getFinalGameSet().getWinners().size() != 1) {
      throw new InvalidRequestException("Multiple teams cannot win a bracket!");
    }
    // Convert to API bracket to make traversal easier for ELO updating
    // Order is relevant for ELO calculations,
    // so we need to make sure to go round by round when updating
    var apiBracket = ApiBracket.fromBracket(bracket);
    for (var round : apiBracket.getGameSets()) {
      var roundEloChange = bracket.getTeams().stream().collect(Collectors.toMap(team -> team, team -> 0));
      var idToPlayerSet = bracket.getTeams().stream().collect(Collectors.toMap(PlayerSet::getId, ps -> ps));
      round.forEach(gameSet -> {
        var games = gameSet.getGames();
        // If games is empty, it was a forfeit or bye round and we do not update ELO.
        // This is a meritocracy, no freeloaders
        if (!games.isEmpty()) {
          eloManager
            .calculateEloChange(
              games
                .stream()
                .map(game ->
                  // There are multiple Java objects for the same DB entry.
                  // Since we are doing a bunch of modifications to the Java objects
                  // before pushing to DB, we need to make sure
                  // we keep referencing the same objects, so we get them from this map
                  game
                    .getPlayerSets()
                    .stream()
                    .map(gps -> idToPlayerSet.get(gps.getId()))
                    .toList()
                )
                .toList(),
              bracket.getGameType()
            )
            .forEach((team, elo) -> roundEloChange.put(team, roundEloChange.get(team) + elo));
        }
      });
      // We must update the player objects ELO after each round
      // so that the next round takes into account the player's new ELO
      bracket
        .getTeams()
        .forEach(team ->
          team
            .getPlayers()
            .forEach(player -> {
              var eloMap = player.getEloMap();
              // Divide the points evenly between the team
              var eloChange = roundEloChange.get(team) / team.getPlayers().size();
              eloMap.put(bracket.getGameType(), eloMap.get(bracket.getGameType()) + eloChange);
            })
        );
    }
    // All ELO calcs have been done, now apply them to the database transactionally
    applyBracketCompletion(
      bracketId,
      bracket.getFinalGameSet().getWinners().stream().findFirst().get(),
      bracket.getTeams()
    );
  }

  /**
   * Apply the necessary database updates that are required
   * when completing a bracket, transactionally.
   * @param bracketId The bracket ID to complete
   * @param playerSets The teams in the bracket, all with updated ELO.
   */
  @Transactional
  private void applyBracketCompletion(String bracketId, PlayerSet winners, Set<PlayerSet> playerSets) {
    var bracket = bracketRepo
      .findById(bracketId)
      .orElseThrow(() -> new NotFoundException("Bracket was deleted by another process while updating."));
    if (!bracket.getWinners().isEmpty()) {
      throw new InvalidRequestException("Bracket was completed by another process while updating.");
    }
    bracket.setWinners(winners.getPlayers());
    bracketRepo.save(bracket);
    playerSets.forEach(team -> playerRepo.saveAll(team.getPlayers()));
  }
}
