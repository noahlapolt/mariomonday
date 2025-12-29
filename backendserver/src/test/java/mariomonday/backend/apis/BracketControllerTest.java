package mariomonday.backend.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import mariomonday.backend.apis.schema.ApiBracket;
import mariomonday.backend.apis.schema.CompleteGameSetRequest;
import mariomonday.backend.apis.schema.CreateBracketRequest;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import mariomonday.backend.error.exceptions.InvalidRequestException;
import mariomonday.backend.error.exceptions.NotFoundException;
import mariomonday.backend.utils.BaseSpringTest;
import mariomonday.backend.utils.TestDataUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BracketControllerTest extends BaseSpringTest {

  Map<String, Player> playerNameToPlayer;

  private Set<Player> randomPlayers;

  @BeforeEach
  public void setUp() {
    var testPlayer1 = Player.builder().name("Reed").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer2 = Player.builder().name("Zach").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer3 = Player.builder().name("Noah").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer4 = Player.builder().name("Jack").eloMap(Player.generateStartingEloMap()).build();
    playerRepository.saveAll(List.of(testPlayer1, testPlayer2, testPlayer3, testPlayer4));
    randomPlayers = TestDataUtil.createNFakePlayers(16)
      .stream()
      .map(playerSet -> playerSet.getPlayers().stream().findFirst().get())
      .collect(Collectors.toSet());
    playerRepository.insert(randomPlayers);
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

  @Test
  public void testCompleteGameSet_shouldAddGamesAndUpdateGameSet_whenHappyPath() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, 1))
      .build();
    bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req);

    // Verify
    var gameSet = gameSetRepository
      .findById(firstGameSet.getId())
      .orElseThrow(() -> new AssertionError("Could not find game set after completing it."));
    Assertions.assertEquals(gameSet.getWinners().stream().map(PlayerSet::getId).toList(), winningOrder.subList(0, 1));
    Assertions.assertEquals(gameSet.getLosers().stream().map(PlayerSet::getId).toList(), winningOrder.subList(1, 2));
    gameSet
      .getGames()
      .forEach(game -> {
        Assertions.assertEquals(game.getPlayerSets().stream().map(PlayerSet::getId).toList(), winningOrder);
        Assertions.assertEquals(GameType.SMASH_ULTIMATE_SINGLES, game.getGameType());
        gameRepository
          .findById(game.getId())
          .orElseThrow(() ->
            new AssertionError("Game with ID " + game.getId() + " was not saved during set completion process")
          );
      });
    Assertions.assertEquals(1, gameSet.getGames().size());
  }

  @Test
  public void testCompleteGameSet_shouldAddGamesAndUpdateGameSet_whenMultipleGames() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder, winningOrder, winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, 1))
      .build();
    bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req);

    // Verify
    var gameSet = gameSetRepository
      .findById(firstGameSet.getId())
      .orElseThrow(() -> new AssertionError("Could not find game set after completing it."));
    Assertions.assertEquals(gameSet.getWinners().stream().map(PlayerSet::getId).toList(), winningOrder.subList(0, 1));
    Assertions.assertEquals(gameSet.getLosers().stream().map(PlayerSet::getId).toList(), winningOrder.subList(1, 2));
    gameSet
      .getGames()
      .forEach(game -> {
        Assertions.assertEquals(game.getPlayerSets().stream().map(PlayerSet::getId).toList(), winningOrder);
        gameRepository
          .findById(game.getId())
          .orElseThrow(() ->
            new AssertionError("Game with ID " + game.getId() + " was not saved during set completion process")
          );
      });
    Assertions.assertEquals(3, gameSet.getGames().size());
  }

  @Test
  public void testCompleteGameSet_shouldUpdateGameSet_whenForfeit() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of())
      .forfeit(true)
      .winners(winningOrder.subList(0, 1))
      .build();
    bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req);

    // Verify
    var gameSet = gameSetRepository
      .findById(firstGameSet.getId())
      .orElseThrow(() -> new AssertionError("Could not find game set after completing it."));
    Assertions.assertEquals(gameSet.getWinners().stream().map(PlayerSet::getId).toList(), winningOrder.subList(0, 1));
    Assertions.assertEquals(gameSet.getLosers().stream().map(PlayerSet::getId).toList(), winningOrder.subList(1, 2));
    Assertions.assertEquals(0, gameSet.getGames().size());
  }

  @Test
  public void testCompleteGameSet_shouldAddGamesAndUpdateGameSet_whenMultipleWinners() {
    // Setup
    var bracket = createBracketWithRandomPlayers(GameType.MARIO_KART_8);
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, 2))
      .build();
    bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req);

    // Verify
    var gameSet = gameSetRepository
      .findById(firstGameSet.getId())
      .orElseThrow(() -> new AssertionError("Could not find game set after completing it."));
    Assertions.assertEquals(
      gameSet.getWinners().stream().map(PlayerSet::getId).collect(Collectors.toSet()),
      new HashSet<>(winningOrder.subList(0, 2))
    );
    Assertions.assertEquals(
      gameSet.getLosers().stream().map(PlayerSet::getId).collect(Collectors.toSet()),
      new HashSet<>(winningOrder.subList(2, 4))
    );
    gameSet
      .getGames()
      .forEach(game -> {
        Assertions.assertEquals(game.getPlayerSets().stream().map(PlayerSet::getId).toList(), winningOrder);
        gameRepository
          .findById(game.getId())
          .orElseThrow(() ->
            new AssertionError("Game with ID " + game.getId() + " was not saved during set completion process")
          );
      });
    Assertions.assertEquals(1, gameSet.getGames().size());
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenNullWinners() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder().games(List.of(winningOrder)).forfeit(false).winners(null).build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenNullGames() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder().games(null).forfeit(false).winners(winningOrder.subList(0, 1)).build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenEmptyWinners() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder().games(List.of(winningOrder)).forfeit(false).winners(List.of()).build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenBracketNotFound() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, 1))
      .build();
    Assertions.assertThrows(NotFoundException.class, () ->
      bracketController.completeGameSet("fake bracket", firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenGameSetNotFound() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, 1))
      .build();
    Assertions.assertThrows(NotFoundException.class, () ->
      bracketController.completeGameSet(bracket.getId(), "Fake game set", req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenGameSetOnWrongBracket() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var bracket2 = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, 1))
      .build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket2.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenTooManyWinners() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder)
      .build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenGamesEmptyNoForfeit() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of())
      .forfeit(false)
      .winners(winningOrder.subList(0, 1))
      .build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenGamesNotEmptyWithForfeit() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(true)
      .winners(winningOrder.subList(0, 1))
      .build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenNullWinner() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();
    var winners = new ArrayList<String>();
    winners.add(null);

    // Act
    var req = CompleteGameSetRequest.builder().games(List.of(winningOrder)).forfeit(false).winners(winners).build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenWinnerNotInBracket() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();
    var winners = new ArrayList<String>();
    winners.add("Fake guy");

    // Act
    var req = CompleteGameSetRequest.builder().games(List.of(winningOrder)).forfeit(false).winners(winners).build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenNullGame() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();
    var game = new ArrayList<List<String>>();
    game.add(null);

    // Act
    var req = CompleteGameSetRequest.builder().games(game).forfeit(false).winners(winningOrder.subList(0, 1)).build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenGamePlayerNotInBracket() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayers().stream().map(PlayerSet::getId).toList();
    var game = new ArrayList<List<String>>();
    game.add(List.of("fake guy", winningOrder.get(1)));

    // Act
    var req = CompleteGameSetRequest.builder().games(game).forfeit(false).winners(winningOrder.subList(0, 1)).build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  private ApiBracket createBracketWithRandomPlayers() {
    return createBracketWithRandomPlayers(GameType.SMASH_ULTIMATE_SINGLES);
  }

  private ApiBracket createBracketWithRandomPlayers(GameType gameType) {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(randomPlayers.stream().collect(Collectors.toMap(Player::getId, player -> List.of(player.getId()))))
      .gameType(gameType)
      .build();
    return bracketController.postBracket(bracketReq);
  }
}
