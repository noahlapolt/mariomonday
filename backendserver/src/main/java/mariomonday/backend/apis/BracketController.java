package mariomonday.backend.apis;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import mariomonday.backend.apis.schema.CreateBracketRequest;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import mariomonday.backend.database.tables.BracketRepository;
import mariomonday.backend.database.tables.GameSetRepository;
import mariomonday.backend.database.tables.PlayerRepository;
import mariomonday.backend.error.exceptions.InvalidRequestException;
import mariomonday.backend.error.exceptions.NotFoundException;
import mariomonday.backend.managers.seeders.AbstractSeeder;
import mariomonday.backend.managers.tournamentcreators.AbstractBracketCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
@RequestMapping(value = "/api" )
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
  Bracket getBracket(@PathVariable String bracketId) {
    return bracketRepo
        .findById(bracketId)
        .orElseThrow(() -> new NotFoundException("Bracket not found with id: " + bracketId));
  }

  /**
   * Create a new bracket
   * @param request The request to create the bracket
   * @return The newly created bracket
   */
  @PostMapping("/bracket")
  Bracket postBracket(@RequestBody CreateBracketRequest request) {
    // Need to populate team names as well
    GameType gameType = request.getGameType();
    List<List<String>> teams = request.getTeams();
    if (gameType == null) {
      throw new InvalidRequestException("Game Type must have a value");
    }

    if (teams.size() < 2) {
      throw new InvalidRequestException("Cannot run a tournament with less than two participants!");
    }
    Set<PlayerSet> unseededTeams = new HashSet<>();

    for (List<String> team : teams) {
      if (team == null) {
        throw new InvalidRequestException("Cannot have a null team!");
      }
      if (team.isEmpty()) {
        throw new InvalidRequestException("Cannot have an empty team!");
      }
      if (team.stream().anyMatch(Objects::isNull)) {
        throw new InvalidRequestException("Cannot have a null player ID!");
      }
      List<Player> players = team.stream().map(playerId ->
          playerRepo.findById(playerId).get()).toList();
      if (players.stream().anyMatch(Objects::isNull)) {
        throw new InvalidRequestException("Some players cannot be found");
      }
      unseededTeams.add(PlayerSet.builder().players(players).build());
    }

    Bracket bracket = bracketCreator.fromPlayerSets(gameType, seeder.seed(unseededTeams, gameType));

    // Save bracket and game sets in DB. Make sure to returned saved objects,
    // since they will have IDs
    bracket.setGameSets(bracket.getGameSets().stream()
        .map(gameSet -> gameSetRepo.save(gameSet)).collect(
            Collectors.toSet()));
    bracket = bracketRepo.save(bracket);
    return bracket;
  }
}
