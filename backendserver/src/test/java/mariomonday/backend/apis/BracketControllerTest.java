package mariomonday.backend.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
import mariomonday.backend.error.exceptions.AlreadyExistsException;
import mariomonday.backend.error.exceptions.InvalidRequestException;
import mariomonday.backend.error.exceptions.NotFoundException;
import mariomonday.backend.managers.tournamentcreators.AbstractBracketCreator;
import mariomonday.backend.utils.BaseSpringTest;
import mariomonday.backend.utils.TestDataUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

public class BracketControllerTest extends BaseSpringTest {

  Map<String, Player> playerNameToPlayer;

  @BeforeEach
  public void setUp() {
    var testPlayer1 = Player.builder().name("Reed").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer2 = Player.builder().name("Zach").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer3 = Player.builder().name("Noah").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer4 = Player.builder().name("Jack").eloMap(Player.generateStartingEloMap()).build();
    playerRepository.saveAll(List.of(testPlayer1, testPlayer2, testPlayer3, testPlayer4));
    playerNameToPlayer = playerRepository
      .findAll()
      .stream()
      .collect(Collectors.toMap(Player::getName, Function.identity()));
  }

  @Test
  public void testCreateBracket_shouldCreate_whenOnePlayerTeams() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId()),
          "Noah",
          List.of(playerNameToPlayer.get("Noah").getId()),
          "Jack",
          List.of(playerNameToPlayer.get("Jack").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();

    // Act
    var resultBracket = bracketController.postBracket(bracketReq);

    // Verify
    // We are not testing the algorithm itself here, just that everything is being put into
    // the DB as expected
    var dbBracket = bracketRepository.findById(resultBracket.getId()).get();
    Assertions.assertNotNull(dbBracket);
    for (var team : dbBracket.getTeams()) {
      // Validate teams are created correctly
      Assertions.assertTrue(playerNameToPlayer.containsKey(team.getName()));
      Assertions.assertEquals(1, team.getPlayers().size());
      Assertions.assertEquals(playerNameToPlayer.get(team.getName()), team.getPlayers().stream().findFirst().get());
    }

    Assertions.assertEquals(3, dbBracket.getGameSets().size());
    for (var gameSet : dbBracket.getGameSets()) {
      Assertions.assertTrue(gameSetRepository.findById(gameSet.getId()).isPresent());
    }
  }

  @Test
  public void testCreateBracket_shouldCreate_whenTwoPlayerTeams() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "The GOATs",
          List.of(playerNameToPlayer.get("Reed").getId(), playerNameToPlayer.get("Zach").getId()),
          "The WOATs",
          List.of(playerNameToPlayer.get("Noah").getId(), playerNameToPlayer.get("Jack").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_DOUBLES)
      .build();

    // Act
    var resultBracket = bracketController.postBracket(bracketReq);

    // Verify
    // We are not testing the algorithm itself here, just that everything is being put into
    // the DB as expected
    var dbBracket = bracketRepository.findById(resultBracket.getId()).get();
    Assertions.assertNotNull(dbBracket);
    var teamNameToPlayers = Map.of(
      "The GOATs",
      Set.of(playerNameToPlayer.get("Reed"), playerNameToPlayer.get("Zach")),
      "The WOATs",
      Set.of(playerNameToPlayer.get("Noah"), playerNameToPlayer.get("Jack"))
    );
    for (var team : dbBracket.getTeams()) {
      // Validate teams are created correctly
      Assertions.assertTrue(teamNameToPlayers.containsKey(team.getName()));
      Assertions.assertEquals(2, team.getPlayers().size());
      Assertions.assertEquals(teamNameToPlayers.get(team.getName()), team.getPlayers());
    }

    Assertions.assertEquals(1, dbBracket.getGameSets().size());
    for (var gameSet : dbBracket.getGameSets()) {
      Assertions.assertTrue(gameSetRepository.findById(gameSet.getId()).isPresent());
    }
  }

  @Test
  public void testCreateBracket_shouldThrow_whenNullGameType() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId()),
          "Noah",
          List.of(playerNameToPlayer.get("Noah").getId()),
          "Jack",
          List.of(playerNameToPlayer.get("Jack").getId())
        )
      )
      .gameType(null)
      .build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testCreateBracket_shouldThrow_whenNullTeams() {
    // Setup
    var bracketReq = CreateBracketRequest.builder().teams(null).gameType(GameType.SMASH_ULTIMATE_SINGLES).build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testCreateBracket_shouldThrow_whenNullTeam() {
    // Setup
    var teamMap = new HashMap<String, List<String>>();
    teamMap.put("Reed", null);
    teamMap.put("Zach", List.of(playerNameToPlayer.get("Zach").getId()));
    var bracketReq = CreateBracketRequest.builder().teams(teamMap).gameType(GameType.SMASH_ULTIMATE_SINGLES).build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testCreateBracket_shouldThrow_whenNullTeamName() {
    // Setup
    var teamMap = new HashMap<String, List<String>>();
    teamMap.put(null, List.of(playerNameToPlayer.get("Reed").getId()));
    teamMap.put("Zach", List.of(playerNameToPlayer.get("Zach").getId()));
    var bracketReq = CreateBracketRequest.builder().teams(teamMap).gameType(GameType.SMASH_ULTIMATE_SINGLES).build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testCreateBracket_shouldThrow_whenEmptyTeam() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId()),
          "Noah",
          List.of(playerNameToPlayer.get("Noah").getId()),
          "Jack",
          List.of(playerNameToPlayer.get("Jack").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testCreateBracket_shouldThrow_whenTeamWrongSize() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId(), playerNameToPlayer.get("Jack").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId()),
          "Noah",
          List.of(playerNameToPlayer.get("Noah").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testCreateBracket_shouldThrow_whenDuplicatePlayers() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId()),
          "Noah",
          List.of(playerNameToPlayer.get("Noah").getId()),
          "Jack",
          List.of(playerNameToPlayer.get("Reed").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testCreateBracket_shouldThrow_whenNullPlayer() {
    // Setup
    var list = new ArrayList<String>();
    list.add(null);
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          list,
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId()),
          "Noah",
          List.of(playerNameToPlayer.get("Noah").getId()),
          "Jack",
          List.of(playerNameToPlayer.get("Jack").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testCreateBracket_shouldThrow_whenPlayerNotFound() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of("haha fake ID bitch"),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId()),
          "Noah",
          List.of(playerNameToPlayer.get("Noah").getId()),
          "Jack",
          List.of(playerNameToPlayer.get("Jack").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.postBracket(bracketReq));
  }

  @Test
  public void testGetBracket_shouldReturnBracket_whenExists() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId()),
          "Noah",
          List.of(playerNameToPlayer.get("Noah").getId()),
          "Jack",
          List.of(playerNameToPlayer.get("Jack").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var expectedBracket = bracketController.postBracket(bracketReq);

    // Act
    var actualBracket = bracketController.getBracket(expectedBracket.getId());

    // Verify
    Assertions.assertEquals(expectedBracket, actualBracket);
  }
}
