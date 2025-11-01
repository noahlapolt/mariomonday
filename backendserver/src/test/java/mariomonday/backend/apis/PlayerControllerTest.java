package mariomonday.backend.apis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.tables.PlayerRepository;
import mariomonday.backend.error.exceptions.AlreadyExistsException;
import mariomonday.backend.error.exceptions.InvalidRequestException;
import mariomonday.backend.error.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PlayerControllerTest {

  @Autowired
  PlayerRepository playerRepository;

  @Autowired
  PlayerController playerController;

  Map<String, Player> playerNameToPlayer;

  @BeforeEach
  public void setUp() throws Exception {
    var testPlayer1 = Player.builder().name("Reed").eloMap(generateDefaultEloMap()).build();
    var testPlayer2 = Player.builder().name("Zach").eloMap(generateDefaultEloMap()).build();
    var testPlayer3 = Player.builder().name("Noah").eloMap(generateDefaultEloMap()).build();
    var testPlayer4 = Player.builder().name("Jack").eloMap(generateDefaultEloMap()).build();
    playerRepository.saveAll(List.of(testPlayer1, testPlayer2, testPlayer3, testPlayer4));
    playerNameToPlayer = playerRepository
      .findAll()
      .stream()
      .collect(Collectors.toMap(Player::getName, Function.identity()));
  }

  @AfterEach
  public void cleanUp() {
    playerRepository.deleteAll();
  }

  @Test
  public void testGetAllPlayers_shouldReturnAllPlayers_whenCalled() {
    // Act
    var players = playerController.getAllPlayers();

    // Verify
    Assertions.assertEquals(players.size(), 4);
    Assertions.assertTrue(players.containsAll(playerNameToPlayer.values()));
  }

  @Test
  public void testGetPlayer_shouldReturnPlayer_whenCalled() {
    // Act
    var player = playerController.getPlayer(playerNameToPlayer.get("Reed").getId());

    // Verify
    Assertions.assertEquals(player, playerNameToPlayer.get("Reed"));
  }

  @Test
  public void testGetPlayer_shouldThrow_whenPlayerDoesNotExist() {
    // Act & Verify
    Assertions.assertThrows(NotFoundException.class, () -> playerController.getPlayer(" Aahah h Im evil fucker"));
  }

  @Test
  public void testPostPlayer_shouldAddPlayer_whenValidPlayer() {
    // Act
    var addedPlayer = playerController.postPlayer(Player.builder().name("New guy in town").build());

    // Verify
    var dbPlayer = playerRepository.findById(addedPlayer.getId()).get();
    Assertions.assertEquals(dbPlayer, addedPlayer);
    Assertions.assertEquals(dbPlayer.getName(), "New guy in town");
    for (var gameType : GameType.values()) {
      Assertions.assertEquals(dbPlayer.getEloMap().get(gameType), Player.STARTING_ELO);
    }
  }

  @Test
  public void testPostPlayer_shouldThrow_whenNullPlayerName() {
    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> playerController.postPlayer(Player.builder().build()));
  }

  @Test
  public void testPostPlayer_shouldThrow_whenEmptyPlayerName() {
    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () ->
      playerController.postPlayer(Player.builder().name("").build())
    );
  }

  @Test
  public void testPostPlayer_shouldThrow_whenIdProvided() {
    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () ->
      playerController.postPlayer(Player.builder().name("evil").id("six seven AHHHHHHH").build())
    );
  }

  @Test
  public void testPostPlayer_shouldThrow_whenPlayerAlreadyExists() {
    // Act & Verify
    Assertions.assertThrows(AlreadyExistsException.class, () ->
      playerController.postPlayer(Player.builder().name("Reed").build())
    );
  }

  @Test
  public void testPatchPlayer_shouldUpdatePlayer_whenValidNameProvided() {
    // Setup
    var currentReed = playerNameToPlayer.get("Reed");
    var updatedReed = Player.builder().id(currentReed.getId()).name("Reed Prime").build();

    // Act
    playerController.patchPlayer(currentReed.getId(), updatedReed);

    // Verify
    var dbReed = playerRepository.findById(currentReed.getId()).get();
    Assertions.assertEquals(dbReed.getName(), "Reed Prime");
    for (var gameType : GameType.values()) {
      Assertions.assertEquals(dbReed.getEloMap().get(gameType), Player.STARTING_ELO);
    }
  }

  @Test
  public void testPatchPlayer_shouldNotUpdateElo_whenEloProvided() {
    // Setup
    var currentReed = playerNameToPlayer.get("Reed");
    var updatedReed = Player.builder().id(currentReed.getId()).name("Reed Prime").build();
    updatedReed.setEloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, Integer.MAX_VALUE));

    // Act
    playerController.patchPlayer(currentReed.getId(), updatedReed);

    // Verify
    var dbReed = playerRepository.findById(currentReed.getId()).get();
    Assertions.assertEquals(dbReed.getName(), "Reed Prime");
    // Elo should be unchanged
    for (var gameType : GameType.values()) {
      Assertions.assertEquals(dbReed.getEloMap().get(gameType), Player.STARTING_ELO);
    }
  }

  @Test
  public void testPatchPlayer_shouldThrow_whenNullPlayerName() {
    // Setup
    var currentReed = playerNameToPlayer.get("Reed");

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () ->
      playerController.patchPlayer(currentReed.getId(), Player.builder().build())
    );
  }

  @Test
  public void testPatchPlayer_shouldThrow_whenInvalidPlayerName() {
    // Setup
    var currentReed = playerNameToPlayer.get("Reed");

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () ->
      playerController.patchPlayer(currentReed.getId(), Player.builder().name("").build())
    );
  }

  @Test
  public void testPatchPlayer_shouldThrow_whenPlayerNameInUse() {
    // Setup
    var currentReed = playerNameToPlayer.get("Reed");

    // Act & Verify
    Assertions.assertThrows(AlreadyExistsException.class, () ->
      playerController.patchPlayer(currentReed.getId(), Player.builder().name("Zach").build())
    );
  }

  @Test
  public void testPatchPlayer_shouldThrow_whenPlayerNotFound() {
    // Act & Verify
    Assertions.assertThrows(NotFoundException.class, () ->
      playerController.patchPlayer("Fake ID", Player.builder().name("Reed Prime").build())
    );
  }

  @Test
  public void testDeletePlayer_shouldDeletePlayer_whenPlayerExists() {
    // Setup
    var reed = playerNameToPlayer.get("Reed");

    // Act
    playerController.deletePlayer(reed.getId());

    // Verify
    var playerData = playerRepository.findAll();
    Assertions.assertEquals(playerData.size(), 3);
    Assertions.assertTrue(
      playerData.containsAll(
        playerNameToPlayer
          .values()
          .stream()
          .filter(player -> !player.getName().equals("Reed"))
          .toList()
      )
    );
    Assertions.assertTrue(playerRepository.findById(reed.getId()).isEmpty());
  }

  @Test
  public void testDeletePlayer_shouldThrow_whenPlayerDoesNotExist() {
    // Act & Verify
    Assertions.assertThrows(NotFoundException.class, () ->
      playerController.deletePlayer("Gonna play some silk song today :)")
    );
  }

  private Map<GameType, Integer> generateDefaultEloMap() {
    var result = new HashMap<GameType, Integer>();
    for (var gameType : GameType.values()) {
      result.put(gameType, Player.STARTING_ELO);
    }
    return result;
  }
}
