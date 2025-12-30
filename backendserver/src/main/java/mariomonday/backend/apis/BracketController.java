package mariomonday.backend.apis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
import mariomonday.backend.managers.seeders.AbstractSeeder;
import mariomonday.backend.managers.tournamentcreators.AbstractBracketCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
   * Create a new bracket
   * @param request The request to create the bracket
   * @return The newly created bracket
   */
  @PostMapping("/bracket/{bracketId}/completeGameSet/{gameSetId}")
  public ApiBracket completeGameSet(
    @PathVariable String bracketId,
    @PathVariable String gameSetId,
    @RequestBody CompleteGameSetRequest request
  ) {
    var bracket = bracketRepo.findById(bracketId).orElseThrow(() -> new NotFoundException("Bracket not found"));
    var gameSet = gameSetRepo.findById(gameSetId).orElseThrow(() -> new NotFoundException("Game Set not found"));
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
    if (!request.isForfeit() && request.getGames().isEmpty()) {
      throw new InvalidRequestException("If set is not forfeit, games must be submitted.");
    }
    if (request.isForfeit() && !request.getGames().isEmpty()) {
      throw new InvalidRequestException("If set is forfeit, games must be empty.");
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
    gameSet.setGames(new HashSet<>(gameRepo.saveAll(games)));
    gameSetRepo.save(gameSet);
    return ApiBracket.fromBracket(bracketRepo.findById(bracket.getId()).get());
  }
}
