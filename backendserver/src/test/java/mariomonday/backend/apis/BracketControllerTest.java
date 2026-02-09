package mariomonday.backend.apis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mariomonday.backend.apis.schema.AddPlayerSetToBracketRequest;
import mariomonday.backend.apis.schema.ApiBracket;
import mariomonday.backend.apis.schema.ApiGameSet;
import mariomonday.backend.apis.schema.CompleteGameSetRequest;
import mariomonday.backend.apis.schema.CreateBracketRequest;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import mariomonday.backend.error.exceptions.InvalidRequestException;
import mariomonday.backend.error.exceptions.NotFoundException;
import mariomonday.backend.managers.ratingcalculators.AbstractEloManager;
import mariomonday.backend.managers.tournamentcreators.MaxSetsStrategyCreator;
import mariomonday.backend.utils.BaseSpringTest;
import mariomonday.backend.utils.TestDataUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BracketControllerTest extends BaseSpringTest {

  Map<String, Player> playerNameToPlayer;

  private List<Player> randomPlayers;

  @BeforeEach
  public void setUp() {
    var testPlayer1 = Player.builder().id("Reed").name("Reed").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer2 = Player.builder().id("Zach").name("Zach").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer3 = Player.builder().id("Noah").name("Noah").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer4 = Player.builder().id("Jack").name("Jack").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer5 = Player.builder().id("Evil").name("Evil").eloMap(Player.generateStartingEloMap()).build();
    var testPlayer6 = Player.builder().id("DrEvil").name("DrEvil").eloMap(Player.generateStartingEloMap()).build();
    playerRepository.saveAll(List.of(testPlayer1, testPlayer2, testPlayer3, testPlayer4, testPlayer5, testPlayer6));
    randomPlayers = TestDataUtil.createNFakePlayers(16)
      .stream()
      .map(playerSet -> playerSet.getPlayers().stream().findFirst().get())
      .collect(Collectors.toList());
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
    var winningOrder = firstGameSet.getPlayerSets();

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
  public void testCompleteGameSet_shouldAddGamesAndDeleteGamesAndUpdateGameSet_whenGameSetAlreadyCompleted() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayerSets();
    var reverseOrder = firstGameSet.getPlayerSets().stream().sorted(Comparator.reverseOrder()).toList();
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, 1))
      .build();
    bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req);
    var originalGames = gameSetRepository
      .findById(firstGameSet.getId())
      .orElseThrow(() -> new AssertionError("Could not find game set after completing it."))
      .getGames();

    // Act
    req = CompleteGameSetRequest.builder()
      .games(List.of(reverseOrder))
      .forfeit(false)
      .winners(reverseOrder.subList(0, 1))
      .build();
    bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req);

    // Verify
    var gameSet = gameSetRepository
      .findById(firstGameSet.getId())
      .orElseThrow(() -> new AssertionError("Could not find game set after completing it."));
    Assertions.assertEquals(gameSet.getWinners().stream().map(PlayerSet::getId).toList(), winningOrder.subList(1, 2));
    Assertions.assertEquals(gameSet.getLosers().stream().map(PlayerSet::getId).toList(), winningOrder.subList(0, 1));
    gameSet
      .getGames()
      .forEach(game -> {
        Assertions.assertEquals(game.getPlayerSets().stream().map(PlayerSet::getId).toList(), reverseOrder);
        Assertions.assertEquals(GameType.SMASH_ULTIMATE_SINGLES, game.getGameType());
        gameRepository
          .findById(game.getId())
          .orElseThrow(() ->
            new AssertionError("Game with ID " + game.getId() + " was not saved during set completion process")
          );
      });
    Assertions.assertEquals(1, gameSet.getGames().size());
    // Make sure original game was deleted
    Assertions.assertTrue(originalGames.stream().allMatch(game -> gameRepository.findById(game.getId()).isEmpty()));
  }

  @Test
  public void testCompleteGameSet_shouldAddGamesAndUpdateGameSet_whenMultipleGames() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
    var winningOrder = firstGameSet.getPlayerSets();

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
  public void testCompleteGameSet_shouldComplain_whenGamesNotEmptyWithByeRound() {
    // Setup
    // Create a bracket with a single player, the only game is a bye round
    var bracket = new MaxSetsStrategyCreator(clock).fromPlayerSets(
      GameType.SMASH_ULTIMATE_SINGLES,
      List.of(PlayerSet.builder().id(UUID.randomUUID().toString()).player(playerNameToPlayer.get("Reed")).build())
    );
    gameSetRepository.save(bracket.getFinalGameSet());
    var savedBracket = bracketRepository.save(bracket);
    var winningOrder = bracket.getFinalGameSet().getPlayers().stream().map(PlayerSet::getId).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, 1))
      .build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(savedBracket.getId(), bracket.getFinalGameSet().getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenNullWinner() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var winningOrder = firstGameSet.getPlayerSets();
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
    var winningOrder = firstGameSet.getPlayerSets();
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
    var winningOrder = firstGameSet.getPlayerSets();
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
    var winningOrder = firstGameSet.getPlayerSets();
    var game = new ArrayList<List<String>>();
    game.add(List.of("fake guy", winningOrder.get(1)));

    // Act
    var req = CompleteGameSetRequest.builder().games(game).forfeit(false).winners(winningOrder.subList(0, 1)).build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteGameSet_shouldComplain_whenGameSetAlreadyCompletedAndNextGameSetAlsoCompleted() {
    // Setup
    var bracket = createBracketWithRandomPlayers();
    // Complete the first two rounds, then we can try and change the first round after the fact
    // (it should fail)
    bracket
      .getGameSets()
      .get(0)
      .forEach(gs -> completeGameSet(bracket.getId(), gs));
    bracket
      .getGameSets()
      .get(1)
      .forEach(gs ->
        completeGameSet(bracket.getId(), ApiGameSet.fromGameSet(gameSetRepository.findById(gs.getId()).get()))
      );

    // Act
    var firstGameSet = bracket.getGameSets().get(0).get(0);
    var newWinningOrder = firstGameSet.getPlayerSets().stream().sorted(Comparator.reverseOrder()).toList();

    // Act
    var req = CompleteGameSetRequest.builder()
      .games(List.of(newWinningOrder))
      .forfeit(false)
      .winners(newWinningOrder.subList(0, 1))
      .build();
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.completeGameSet(bracket.getId(), firstGameSet.getId(), req)
    );
  }

  @Test
  public void testCompleteBracket_shouldUpdateWinnersAndElo_whenHappyPath() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var reedId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Reed"))
      .findFirst()
      .get()
      .getId();
    var zachId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Zach"))
      .findFirst()
      .get()
      .getId();
    // haha I win fuck you Zach
    completeGameSet(bracket.getId(), bracket.getGameSets().get(0).get(0), List.of(reedId, zachId));

    // Act
    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    // Assert bracket winners are set and are the same as the winners of the final game set
    Assertions.assertEquals(
      completedBracket.getWinners(),
      completedBracket.getFinalGameSet().getWinners().stream().findFirst().get().getPlayers()
    );

    // Assert ELO updates went through
    var winner = playerRepository.findById(playerNameToPlayer.get("Reed").getId()).get();
    Assertions.assertEquals(
      Player.STARTING_ELO + AbstractEloManager.K_FACTOR / 2,
      winner.getEloMap().get(GameType.SMASH_ULTIMATE_SINGLES)
    );
    var loser = playerRepository.findById(playerNameToPlayer.get("Zach").getId()).get();
    Assertions.assertEquals(
      Player.STARTING_ELO - AbstractEloManager.K_FACTOR / 2,
      loser.getEloMap().get(GameType.SMASH_ULTIMATE_SINGLES)
    );
  }

  @Test
  public void testCompleteBracket_shouldUpdateWinnersAndSplitElo_whenTeams() {
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
    var bracket = bracketController.postBracket(bracketReq);
    var goatId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("The GOATs"))
      .findFirst()
      .get()
      .getId();
    var woatId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("The WOATs"))
      .findFirst()
      .get()
      .getId();
    completeGameSet(bracket.getId(), bracket.getGameSets().get(0).get(0), List.of(goatId, woatId));

    // Act
    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    // Assert bracket winners are set and are the same as the winners of the final game set
    Assertions.assertEquals(
      completedBracket.getWinners(),
      completedBracket.getFinalGameSet().getWinners().stream().findFirst().get().getPlayers()
    );

    // Assert ELO updates went through for winners, should be split evenly between players
    var winner = playerRepository.findById(playerNameToPlayer.get("Reed").getId()).get();
    Assertions.assertEquals(
      Player.STARTING_ELO + AbstractEloManager.K_FACTOR / 4,
      winner.getEloMap().get(GameType.SMASH_ULTIMATE_DOUBLES)
    );
    var winner2 = playerRepository.findById(playerNameToPlayer.get("Zach").getId()).get();
    Assertions.assertEquals(
      Player.STARTING_ELO + AbstractEloManager.K_FACTOR / 4,
      winner2.getEloMap().get(GameType.SMASH_ULTIMATE_DOUBLES)
    );

    // Assert ELO updates went through for losers, should be split evenly between players
    var loser = playerRepository.findById(playerNameToPlayer.get("Noah").getId()).get();
    Assertions.assertEquals(
      Player.STARTING_ELO - AbstractEloManager.K_FACTOR / 4,
      loser.getEloMap().get(GameType.SMASH_ULTIMATE_DOUBLES)
    );
    var loser2 = playerRepository.findById(playerNameToPlayer.get("Jack").getId()).get();
    Assertions.assertEquals(
      Player.STARTING_ELO - AbstractEloManager.K_FACTOR / 4,
      loser2.getEloMap().get(GameType.SMASH_ULTIMATE_DOUBLES)
    );
  }

  @Test
  public void testCompleteBracket_shouldUpdateWinnersAndSplitElo_whenTeamMembersHaveDifferentElo() {
    // Setup
    var reed = playerRepository.findById(playerNameToPlayer.get("Reed").getId()).get();
    reed.getEloMap().put(GameType.SMASH_ULTIMATE_DOUBLES, 1600);
    playerRepository.save(reed);
    var zach = playerRepository.findById(playerNameToPlayer.get("Zach").getId()).get();
    zach.getEloMap().put(GameType.SMASH_ULTIMATE_DOUBLES, 1400);
    playerRepository.save(zach);
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
    var bracket = bracketController.postBracket(bracketReq);
    var goatId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("The GOATs"))
      .findFirst()
      .get()
      .getId();
    var woatId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("The WOATs"))
      .findFirst()
      .get()
      .getId();
    completeGameSet(bracket.getId(), bracket.getGameSets().get(0).get(0), List.of(goatId, woatId));

    // Act
    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    // Assert bracket winners are set and are the same as the winners of the final game set
    Assertions.assertEquals(
      completedBracket.getWinners(),
      completedBracket.getFinalGameSet().getWinners().stream().findFirst().get().getPlayers()
    );

    // Assert ELO update is split evenly between winners even though they are different ELO
    var winner = playerRepository.findById(playerNameToPlayer.get("Reed").getId()).get();
    Assertions.assertEquals(
      1600 + AbstractEloManager.K_FACTOR / 4,
      winner.getEloMap().get(GameType.SMASH_ULTIMATE_DOUBLES)
    );
    var winner2 = playerRepository.findById(playerNameToPlayer.get("Zach").getId()).get();
    Assertions.assertEquals(
      1400 + AbstractEloManager.K_FACTOR / 4,
      winner2.getEloMap().get(GameType.SMASH_ULTIMATE_DOUBLES)
    );
  }

  @Test
  public void testCompleteBracket_shouldIgnoreElo_whenByeRound() {
    // Setup
    // Create a bracket with a single player, the only game is a bye round
    var bracket = new MaxSetsStrategyCreator(clock).fromPlayerSets(
      GameType.SMASH_ULTIMATE_SINGLES,
      List.of(PlayerSet.builder().id(UUID.randomUUID().toString()).player(playerNameToPlayer.get("Reed")).build())
    );
    gameSetRepository.save(bracket.getFinalGameSet());
    bracket = bracketRepository.save(bracket);

    var reedId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Reed"))
      .findFirst()
      .get()
      .getId();
    var req = CompleteGameSetRequest.builder().games(List.of()).forfeit(false).winners(List.of(reedId)).build();
    bracketController.completeGameSet(bracket.getId(), bracket.getFinalGameSet().getId(), req);

    // Act
    bracketController.completeBracket(bracket.getId());

    // Verify
    // Assert ELO is unchanged because only round was a bye
    var winner = playerRepository.findById(playerNameToPlayer.get("Reed").getId()).get();
    Assertions.assertEquals(Player.STARTING_ELO, winner.getEloMap().get(GameType.SMASH_ULTIMATE_DOUBLES));
  }

  @Test
  public void testCompleteBracket_shouldIgnoreElo_whenForfeit() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var reedId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Reed"))
      .findFirst()
      .get()
      .getId();
    var req = CompleteGameSetRequest.builder().games(List.of()).forfeit(true).winners(List.of(reedId)).build();
    bracketController.completeGameSet(bracket.getId(), bracket.getGameSets().get(0).get(0).getId(), req);

    // Act
    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    // Assert bracket winners are set and are the same as the winners of the final game set
    Assertions.assertEquals(
      completedBracket.getWinners(),
      completedBracket.getFinalGameSet().getWinners().stream().findFirst().get().getPlayers()
    );

    // Assert ELO updates went through
    // No ELO should be impacted, since it was a forfeit
    var winner = playerRepository.findById(playerNameToPlayer.get("Reed").getId()).get();
    Assertions.assertEquals(Player.STARTING_ELO, winner.getEloMap().get(GameType.SMASH_ULTIMATE_SINGLES));
    var loser = playerRepository.findById(playerNameToPlayer.get("Zach").getId()).get();
    Assertions.assertEquals(Player.STARTING_ELO, loser.getEloMap().get(GameType.SMASH_ULTIMATE_SINGLES));
  }

  @Test
  public void testCompleteBracket_shouldUpdateWinnersAndElo_whenMultipleRounds() {
    // Setup
    // We build the bracket manually so we can set playerSet IDs
    // to guarantee the exact order of the bracket games
    var bracket = new MaxSetsStrategyCreator(clock).fromPlayerSets(
      GameType.SMASH_ULTIMATE_SINGLES,
      List.of(
        PlayerSet.builder().id("Reed").player(playerNameToPlayer.get("Reed")).build(),
        PlayerSet.builder().id("Zach").player(playerNameToPlayer.get("Zach")).build(),
        PlayerSet.builder().id("Jack").player(playerNameToPlayer.get("Jack")).build(),
        PlayerSet.builder().id("Noah").player(playerNameToPlayer.get("Noah")).build()
      )
    );
    bracket.setGameSets(
      bracket
        .getGameSets()
        .stream()
        .map(gameSet -> gameSetRepository.save(gameSet))
        .collect(Collectors.toSet())
    );
    var apiBracket = ApiBracket.fromBracket(bracketRepository.save(bracket));
    var playerSetIdToPlayerName = bracket
      .getTeams()
      .stream()
      .collect(Collectors.toMap(PlayerSet::getId, PlayerSet::getName));
    Comparator<String> reedWins = (teamId1, teamId2) -> {
      var team1 = playerSetIdToPlayerName.get(teamId1);
      var team2 = playerSetIdToPlayerName.get(teamId2);
      if (team1.equals("Reed")) {
        return -1;
      } else if (team2.equals("Reed")) {
        return 1;
      } else if (team1.equals("Jack")) {
        return -1;
      } else if (team2.equals("Jack")) {
        return 1;
      } else if (team1.equals("Noah")) {
        return -1;
      } else if (team2.equals("Noah")) {
        return 1;
      } else {
        return 0;
      }
    };
    // I beat you all :P
    var gameOne = apiBracket.getGameSets().get(0).get(0);
    completeGameSet(apiBracket.getId(), gameOne, gameOne.getPlayerSets().stream().sorted(reedWins).toList());
    var gameTwo = apiBracket.getGameSets().get(0).get(1);
    completeGameSet(apiBracket.getId(), gameTwo, gameTwo.getPlayerSets().stream().sorted(reedWins).toList());
    var finals = gameSetRepository.findById(apiBracket.getGameSets().get(1).get(0).getId()).get();
    completeGameSet(
      apiBracket.getId(),
      ApiGameSet.fromGameSet(finals),
      finals.getPlayers().stream().map(PlayerSet::getId).sorted(reedWins).toList()
    );

    // Act
    bracketController.completeBracket(apiBracket.getId());

    // Verify
    // Assert ELO updates went through
    var winner = playerRepository.findById(playerNameToPlayer.get("Reed").getId()).get();
    Assertions.assertEquals(Player.STARTING_ELO + 32, winner.getEloMap().get(GameType.SMASH_ULTIMATE_SINGLES));
    var loser = playerRepository.findById(playerNameToPlayer.get("Zach").getId()).get();
    Assertions.assertEquals(Player.STARTING_ELO - 16, loser.getEloMap().get(GameType.SMASH_ULTIMATE_SINGLES));
    var loser2 = playerRepository.findById(playerNameToPlayer.get("Jack").getId()).get();
    Assertions.assertEquals(Player.STARTING_ELO, loser2.getEloMap().get(GameType.SMASH_ULTIMATE_SINGLES));
    var loser3 = playerRepository.findById(playerNameToPlayer.get("Noah").getId()).get();
    Assertions.assertEquals(Player.STARTING_ELO - 16, loser3.getEloMap().get(GameType.SMASH_ULTIMATE_SINGLES));
  }

  @Test
  public void testCompleteBracket_shouldComplain_whenFinalGameSetNotCompleted() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.completeBracket(bracket.getId()));
  }

  @Test
  public void testCompleteBracket_shouldComplain_whenBracketAlreadyCompleted() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        Map.of(
          "Reed",
          List.of(playerNameToPlayer.get("Reed").getId()),
          "Zach",
          List.of(playerNameToPlayer.get("Zach").getId())
        )
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var reedId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Reed"))
      .findFirst()
      .get()
      .getId();
    var zachId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Zach"))
      .findFirst()
      .get()
      .getId();
    completeGameSet(bracket.getId(), bracket.getGameSets().get(0).get(0), List.of(reedId, zachId));
    bracketController.completeBracket(bracket.getId());

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.completeBracket(bracket.getId()));
  }

  @Test
  public void testCompleteBracket_shouldComplain_whenBracketDoesNotExist() {
    Assertions.assertThrows(NotFoundException.class, () -> bracketController.completeBracket("Fake bracket"));
  }

  @Test
  public void testCompleteBracket_shouldComplain_whenTooManyWinners() {
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
      .gameType(GameType.MARIO_KART_WORLD)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var reedId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Reed"))
      .findFirst()
      .get()
      .getId();
    var zachId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Zach"))
      .findFirst()
      .get()
      .getId();
    var noahId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Noah"))
      .findFirst()
      .get()
      .getId();
    var jackId = bracket
      .getTeams()
      .stream()
      .filter(team -> team.getName().equals("Jack"))
      .findFirst()
      .get()
      .getId();
    var req = CompleteGameSetRequest.builder()
      .games(List.of(List.of(reedId, zachId, jackId, noahId)))
      .forfeit(false)
      .winners(List.of(reedId, zachId))
      .build();
    bracketController.completeGameSet(bracket.getId(), bracket.getGameSets().get(0).get(0).getId(), req);

    // Act & Verify
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.completeBracket(bracket.getId()));
  }

  @Test
  public void testAddPlayer_shouldAddPlayerToGameSet_whenSinglesNotInTourney() {
    // Setup
    var bracket = createPredictableBracket(15, GameType.SMASH_ULTIMATE_SINGLES);

    // Act
    var request = AddPlayerSetToBracketRequest.builder().playerIds(List.of("Reed")).gameSetId("0").build();
    bracketController.addPlayer(bracket.getId(), request);

    // Verify
    var newBracket = bracketRepository.findById(bracket.getId()).get();
    var addedPlayerSet = newBracket
      .getTeams()
      .stream()
      .filter(team -> team.getPlayers().stream().map(Player::getId).collect(Collectors.toSet()).contains("Reed"))
      .findFirst()
      .get();
    // Assert player set was added for new player
    Assertions.assertNotNull(addedPlayerSet);
    var newGameSet = GameSet.loadLazyGameSet(gameSetRepository.findById("0").get());
    // Assert player set was added to game set
    Assertions.assertTrue(
      newGameSet
        .getAddedPlayerSets()
        .stream()
        .map(PlayerSet::getId)
        .collect(Collectors.toSet())
        .contains(addedPlayerSet.getId())
    );
  }

  @Test
  public void testAddPlayer_shouldAddPlayerToGameSet_whenSinglesRevive() {
    // Setup
    var bracket = createPredictableBracket(15, GameType.SMASH_ULTIMATE_SINGLES);
    advancePredictableBracket(bracket.getId(), "1v14");

    // Act
    var request = AddPlayerSetToBracketRequest.builder().playerIds(List.of("14")).gameSetId("0").build();
    bracketController.addPlayer(bracket.getId(), request);

    // Verify
    var newBracket = bracketRepository.findById(bracket.getId()).get();
    // No new teams should have been added
    Assertions.assertEquals(newBracket.getTeams().size(), 15);
    var newGameSet = GameSet.loadLazyGameSet(gameSetRepository.findById("0").get());
    // Assert player set was added to game set
    Assertions.assertTrue(
      newGameSet.getAddedPlayerSets().stream().map(PlayerSet::getId).collect(Collectors.toSet()).contains("14")
    );
    Assertions.assertEquals(newGameSet.getAddedPlayerSets().size(), 2);
  }

  @Test
  public void testAddPlayer_shouldAddTeamToGameSet_whenDoublesNeitherInTourney() {
    // Setup
    var bracket = createPredictableBracket(30, GameType.SMASH_ULTIMATE_DOUBLES);

    // Act
    var request = AddPlayerSetToBracketRequest.builder()
      .playerIds(List.of("Reed", "Zach"))
      .teamName("The epics")
      .gameSetId("0x1")
      .build();
    bracketController.addPlayer(bracket.getId(), request);

    // Verify
    var newBracket = bracketRepository.findById(bracket.getId()).get();
    var addedPlayerSet = newBracket
      .getTeams()
      .stream()
      .filter(ps -> ps.getName().equals("The epics"))
      .findFirst()
      .get();
    // Assert player set was added for new team
    Assertions.assertNotNull(addedPlayerSet);
    Assertions.assertEquals(addedPlayerSet.getPlayers().size(), 2);
    Assertions.assertTrue(
      addedPlayerSet
        .getPlayers()
        .stream()
        .map(Player::getId)
        .collect(Collectors.toSet())
        .containsAll(List.of("Reed", "Zach"))
    );
    var newGameSet = GameSet.loadLazyGameSet(gameSetRepository.findById("0x1").get());
    // Assert player set was added to game set
    Assertions.assertTrue(
      newGameSet
        .getAddedPlayerSets()
        .stream()
        .map(PlayerSet::getId)
        .collect(Collectors.toSet())
        .contains(addedPlayerSet.getId())
    );
  }

  @Test
  public void testAddPlayer_shouldAddTeamToGameSet_whenDoublesOneRevive() {
    // Setup
    var bracket = createPredictableBracket(30, GameType.SMASH_ULTIMATE_DOUBLES);
    advancePredictableBracket(bracket.getId(), "2x3v28x29");

    // Act
    var request = AddPlayerSetToBracketRequest.builder()
      .playerIds(List.of("Reed", "29"))
      .teamName("The comeback kings")
      .gameSetId("0x1")
      .build();
    bracketController.addPlayer(bracket.getId(), request);

    // Verify
    var newBracket = bracketRepository.findById(bracket.getId()).get();
    var addedPlayerSet = newBracket
      .getTeams()
      .stream()
      .filter(ps -> ps.getName().equals("The comeback kings"))
      .findFirst()
      .get();
    // Assert player set was added for new team
    Assertions.assertNotNull(addedPlayerSet);
    Assertions.assertEquals(addedPlayerSet.getPlayers().size(), 2);
    Assertions.assertTrue(
      addedPlayerSet
        .getPlayers()
        .stream()
        .map(Player::getId)
        .collect(Collectors.toSet())
        .containsAll(List.of("Reed", "29"))
    );
    var newGameSet = GameSet.loadLazyGameSet(gameSetRepository.findById("0x1").get());
    // Assert player set was added to game set
    Assertions.assertTrue(
      newGameSet
        .getAddedPlayerSets()
        .stream()
        .map(PlayerSet::getId)
        .collect(Collectors.toSet())
        .contains(addedPlayerSet.getId())
    );
  }

  @Test
  public void testAddPlayer_shouldAddTeamToGameSet_whenDoublesSameTeamRevive() {
    // Setup
    var bracket = createPredictableBracket(30, GameType.SMASH_ULTIMATE_DOUBLES);
    advancePredictableBracket(bracket.getId(), "2x3v28x29");

    // Act
    var request = AddPlayerSetToBracketRequest.builder()
      .playerIds(List.of("28", "29"))
      .teamName("The walking Ls")
      .gameSetId("0x1")
      .build();
    bracketController.addPlayer(bracket.getId(), request);

    // Verify
    var newBracket = bracketRepository.findById(bracket.getId()).get();
    // No new teams should have been added
    Assertions.assertEquals(newBracket.getTeams().size(), 15);
    var newGameSet = GameSet.loadLazyGameSet(gameSetRepository.findById("0x1").get());
    // Assert loser team was added to game set
    Assertions.assertTrue(
      newGameSet.getAddedPlayerSets().stream().map(PlayerSet::getId).collect(Collectors.toSet()).contains("28x29")
    );
  }

  @Test
  public void testAddPlayer_shouldAddTeamToGameSet_whenDoublesNewTeamRevive() {
    // Both players have been eliminated, but were on different teams
    // Setup
    var bracket = createPredictableBracket(30, GameType.SMASH_ULTIMATE_DOUBLES);
    advancePredictableBracket(bracket.getId(), "2x3v28x29");
    advancePredictableBracket(bracket.getId(), "4x5v26x27");

    // Act
    var request = AddPlayerSetToBracketRequest.builder()
      .playerIds(List.of("26", "29"))
      .teamName("The comeback kings")
      .gameSetId("0x1")
      .build();
    bracketController.addPlayer(bracket.getId(), request);

    // Verify
    var newBracket = bracketRepository.findById(bracket.getId()).get();
    var addedPlayerSet = newBracket
      .getTeams()
      .stream()
      .filter(ps -> ps.getName().equals("The comeback kings"))
      .findFirst()
      .get();
    // Assert player set was added for new team
    Assertions.assertNotNull(addedPlayerSet);
    Assertions.assertEquals(addedPlayerSet.getPlayers().size(), 2);
    Assertions.assertTrue(
      addedPlayerSet
        .getPlayers()
        .stream()
        .map(Player::getId)
        .collect(Collectors.toSet())
        .containsAll(List.of("26", "29"))
    );
    var newGameSet = GameSet.loadLazyGameSet(gameSetRepository.findById("0x1").get());
    // Assert player set was added to game set
    Assertions.assertTrue(
      newGameSet
        .getAddedPlayerSets()
        .stream()
        .map(PlayerSet::getId)
        .collect(Collectors.toSet())
        .contains(addedPlayerSet.getId())
    );
  }

  @Test
  public void testAddPlayer_shouldComplain_whenGameSetDoesNotExist() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        randomPlayers.stream().limit(15).collect(Collectors.toMap(Player::getId, player -> List.of(player.getId())))
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var reedId = playerNameToPlayer.get("Reed").getId();
    var request = AddPlayerSetToBracketRequest.builder()
      .gameSetId("HAHA FUCK YOU IM FAKE")
      .playerIds(List.of(reedId))
      .build();

    // Act
    Assertions.assertThrows(NotFoundException.class, () -> bracketController.addPlayer(bracket.getId(), request));
  }

  @Test
  public void testAddPlayer_shouldComplain_whenBracketDoesNotExist() {
    // Setup
    createPredictableBracket(3, GameType.SMASH_ULTIMATE_SINGLES);

    // Act
    var request = AddPlayerSetToBracketRequest.builder().playerIds(List.of("Reed")).gameSetId("0").build();
    Assertions.assertThrows(NotFoundException.class, () -> bracketController.addPlayer("Evil bracket", request));
  }

  @Test
  public void testAddPlayer_shouldComplain_whenPlayerDoesNotExist() {
    // Setup
    var bracket = createPredictableBracket(3, GameType.SMASH_ULTIMATE_SINGLES);

    // Act
    var request = AddPlayerSetToBracketRequest.builder().playerIds(List.of("Fake guy")).gameSetId("0").build();
    Assertions.assertThrows(NotFoundException.class, () -> bracketController.addPlayer(bracket.getId(), request));
  }

  @Test
  public void testAddPlayer_shouldComplain_whenGameIsCompleted() {
    // Setup
    var bracket = createPredictableBracket(3, GameType.SMASH_ULTIMATE_SINGLES);
    advancePredictableBracket(bracket.getId(), "0");

    // Act
    var request = AddPlayerSetToBracketRequest.builder().playerIds(List.of("Reed")).gameSetId("0").build();
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.addPlayer(bracket.getId(), request));
  }

  @Test
  public void testAddPlayer_shouldComplain_whenPlayerListNull() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        randomPlayers.stream().limit(15).collect(Collectors.toMap(Player::getId, player -> List.of(player.getId())))
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var byeGameId = bracket
      .getGameSets()
      .get(0)
      .stream()
      .filter(gs -> gs.getPlayerSets().size() == 1)
      .findFirst()
      .get()
      .getId();
    var request = AddPlayerSetToBracketRequest.builder().gameSetId(byeGameId).playerIds(null).build();

    // Act
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.addPlayer(bracket.getId(), request));
  }

  @Test
  public void testAddPlayer_shouldComplain_whenGameSetFull() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        randomPlayers.stream().limit(15).collect(Collectors.toMap(Player::getId, player -> List.of(player.getId())))
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var normalGameId = bracket
      .getGameSets()
      .get(0)
      .stream()
      .filter(gs -> gs.getPlayerSets().size() == 2)
      .findFirst()
      .get()
      .getId();
    var reedId = playerNameToPlayer.get("Reed").getId();
    var request = AddPlayerSetToBracketRequest.builder().gameSetId(normalGameId).playerIds(List.of(reedId)).build();

    // Act
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.addPlayer(bracket.getId(), request));
  }

  @Test
  public void testAddPlayer_shouldComplain_whenBracketGameSetMismatch() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        randomPlayers.stream().limit(15).collect(Collectors.toMap(Player::getId, player -> List.of(player.getId())))
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var bracket2 = bracketController.postBracket(bracketReq);
    var byeGameId = bracket
      .getGameSets()
      .get(0)
      .stream()
      .filter(gs -> gs.getPlayerSets().size() == 1)
      .findFirst()
      .get()
      .getId();
    var reedId = playerNameToPlayer.get("Reed").getId();
    var request = AddPlayerSetToBracketRequest.builder().gameSetId(byeGameId).playerIds(List.of(reedId)).build();

    // Act
    Assertions.assertThrows(InvalidRequestException.class, () ->
      bracketController.addPlayer(bracket2.getId(), request)
    );
  }

  @Test
  public void testAddPlayer_shouldComplain_whenNewTeamAndTeamNameNull() {
    // Setup
    Map<String, List<String>> teams = new HashMap<>();
    for (int i = 0; i < randomPlayers.size() - 2; i += 2) {
      teams.put(String.valueOf(i), List.of(randomPlayers.get(i).getId(), randomPlayers.get(i + 1).getId()));
    }
    var bracketReq = CreateBracketRequest.builder().teams(teams).gameType(GameType.SMASH_ULTIMATE_DOUBLES).build();
    var bracket = bracketController.postBracket(bracketReq);
    var byeGameId = bracket
      .getGameSets()
      .get(0)
      .stream()
      .filter(gs -> gs.getPlayerSets().size() == 1)
      .findFirst()
      .get()
      .getId();
    var reedId = playerNameToPlayer.get("Reed").getId();
    var zachId = playerNameToPlayer.get("Zach").getId();
    var request = AddPlayerSetToBracketRequest.builder()
      .gameSetId(byeGameId)
      .playerIds(List.of(reedId, zachId))
      .teamName(null)
      .build();

    // Act
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.addPlayer(bracket.getId(), request));
  }

  @Test
  public void testAddPlayer_shouldComplain_whenPlayerStillAlive() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(
        randomPlayers.stream().limit(15).collect(Collectors.toMap(Player::getId, player -> List.of(player.getId())))
      )
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();
    var bracket = bracketController.postBracket(bracketReq);
    var byeGameId = bracket
      .getGameSets()
      .get(0)
      .stream()
      .filter(gs -> gs.getPlayerSets().size() == 1)
      .findFirst()
      .get()
      .getId();
    var playerId = randomPlayers.get(0).getId();
    var request = AddPlayerSetToBracketRequest.builder().gameSetId(byeGameId).playerIds(List.of(playerId)).build();

    // Act
    Assertions.assertThrows(InvalidRequestException.class, () -> bracketController.addPlayer(bracket.getId(), request));
  }

  @Test
  public void testEntireSinglesBracket_startToFinish() {
    // 16 Players, 4 rounds
    // Setup
    var bracket = createPredictableBracket(16, GameType.SMASH_ULTIMATE_SINGLES);
    bracket
      .getGameSets()
      .forEach(round -> round.forEach(gameSet -> advancePredictableBracket(bracket.getId(), gameSet.getId())));

    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    Assertions.assertEquals(1, completedBracket.getWinners().size());
    Assertions.assertEquals("0", completedBracket.getWinners().stream().findFirst().get().getName());
  }

  @Test
  public void testEntireKartBracket_startToFinish() {
    // 16 Players, 3 rounds
    // Setup
    var bracket = createPredictableBracket(16, GameType.MARIO_KART_8);
    bracket
      .getGameSets()
      .forEach(round -> round.forEach(gameSet -> advancePredictableBracket(bracket.getId(), gameSet.getId())));
    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    Assertions.assertEquals(1, completedBracket.getWinners().size());
    Assertions.assertEquals("0", completedBracket.getWinners().stream().findFirst().get().getName());
  }

  @Test
  public void testEntireDoublesBracket_startToFinish() {
    // 32 Players (16 teams), 4 rounds
    // Setup
    var bracket = createPredictableBracket(32, GameType.SMASH_ULTIMATE_DOUBLES);
    bracket
      .getGameSets()
      .forEach(round -> round.forEach(gameSet -> advancePredictableBracket(bracket.getId(), gameSet.getId())));
    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    Assertions.assertEquals(2, completedBracket.getWinners().size());
    Assertions.assertTrue(
      completedBracket.getWinners().stream().map(Player::getId).collect(Collectors.toSet()).contains("0")
    );
    Assertions.assertTrue(
      completedBracket.getWinners().stream().map(Player::getId).collect(Collectors.toSet()).contains("1")
    );
  }

  private void completeGameSet(String bracketId, ApiGameSet gameSet) {
    completeGameSet(bracketId, gameSet, gameSet.getPlayerSets());
  }

  private void completeGameSet(String bracketId, ApiGameSet gameSet, List<String> winningOrder) {
    completeGameSet(bracketId, gameSet, winningOrder, 1);
  }

  private void completeGameSet(String bracketId, ApiGameSet gameSet, List<String> winningOrder, int winnerCount) {
    var req = CompleteGameSetRequest.builder()
      .games(List.of(winningOrder))
      .forfeit(false)
      .winners(winningOrder.subList(0, winnerCount))
      .build();
    bracketController.completeGameSet(bracketId, gameSet.getId(), req);
  }

  private ApiBracket createBracketWithRandomPlayers() {
    return createBracketWithRandomPlayers(GameType.SMASH_ULTIMATE_SINGLES);
  }

  private ApiBracket createBracketWithRandomPlayers(GameType gameType) {
    var bracketReq = CreateBracketRequest.builder()
      .teams(randomPlayers.stream().collect(Collectors.toMap(Player::getId, player -> List.of(player.getId()))))
      .gameType(gameType)
      .build();
    return bracketController.postBracket(bracketReq);
  }

  /**
   * Generates a bracket in a predictable manner, with predictable IDs for easy access.
   * The players will have IDs 0 to N - 1, where N is the number of players requested.
   * Teams will have IDs of the format 0x1, where 0 and 1 are team members.
   *  The higher seeded players will always come first.
   * Game Sets will have IDs of the format 0v1 where 0 is one player and 1 is the other.
   *  The higher seeded players will always come first.
   *    - For a team game, this would look like 0x1v2x3.
   *    - For a game with many players, this would look like 0v1v2v3
   *  Game Sets in later rounds will assume the highest seeded player in the previous game would win,
   *  thus a game set that is preceded by 0v1 and 2v3 would be labeled 0v2.
   * The bracket will be seeded so that player 0 is the highest seed, and player N-1 is the lowest seed.
   */
  private ApiBracket createPredictableBracket(int numPlayers, GameType gameType) {
    if (numPlayers % gameType.getPlayersOnATeam() != 0) {
      throw new RuntimeException(
        numPlayers +
          " cannot be divided evenly amongst teams of size " +
          gameType.getPlayersOnATeam() +
          " for GameType " +
          gameType
      );
    }
    var players = IntStream.range(0, numPlayers)
      .mapToObj(i ->
        Player.builder().eloMap(Player.generateStartingEloMap()).name(String.valueOf(i)).id(String.valueOf(i)).build()
      )
      .toList();
    playerRepository.saveAll(players);

    var teams = new ArrayList<PlayerSet>();
    for (int i = 0; i < numPlayers; i += gameType.getPlayersOnATeam()) {
      var playersOnTeam = players.subList(i, i + gameType.getPlayersOnATeam());
      var teamId = playersOnTeam
        .stream()
        .map(Player::getId)
        .sorted(Comparator.comparingInt(Integer::parseInt))
        .collect(Collectors.joining("x"));
      teams.add(PlayerSet.builder().players(playersOnTeam).id(teamId).name(teamId).build());
    }

    var bracket = new MaxSetsStrategyCreator(clock).fromPlayerSets(gameType, teams);
    var roundToSets = new HashMap<Integer, List<GameSet>>();
    for (GameSet gameSet : bracket.getGameSets()) {
      int round = gameSet.getRoundIndex();
      if (!roundToSets.containsKey(round)) {
        roundToSets.put(round, new ArrayList<>());
      }
      roundToSets.get(round).add(gameSet);
    }

    for (int round = bracket.getRounds() - 1; round >= 0; round--) {
      for (int i = 0; i < roundToSets.get(round).size(); i++) {
        var gameSet = roundToSets.get(round).get(i);
        var addedPlayerIds = gameSet.getAddedPlayerSets().stream().map(PlayerSet::getId);
        // All game sets are sorted by player name (which is also seeding), so we can split on "v"
        // and take the first n instances to get who should win that set (where n is playerSetsToMoveOn)
        var previousGameSetPlayerIds = gameSet
          .getPreviousGameSets()
          .stream()
          .flatMap(pgs -> Arrays.asList(pgs.getId().split("v")).subList(0, gameType.getPlayerSetsToMoveOn()).stream());
        gameSet.setId(
          Stream.concat(addedPlayerIds, previousGameSetPlayerIds)
            .sorted(
              Comparator.comparingInt(teamId ->
                Arrays.stream(teamId.split("x")).map(Integer::parseInt).reduce(0, Integer::sum)
              )
            )
            .collect(Collectors.joining("v"))
        );
        gameSetRepository.save(gameSet);
      }
    }
    bracket = bracketRepository.save(bracket);
    return ApiBracket.fromBracket(bracket);
  }

  /**
   * Advance a bracket by completing the given game set,
   * awarding the higher seeded player/team the win.
   * This will always insert a one game set, unless it was a bye.
   * NOTE: The bracket must have been created by the "createPredicatableBracket" function.
   */
  private void advancePredictableBracket(String bracketId, String gameSetId) {
    // NOTE: this is slow because we get this bracket every time. Could be made faster
    // if we cache it or pass it in or something but idc rn I'm lazy
    var bracket = bracketRepository.findById(bracketId).get();
    var gameType = bracket.getGameType();
    var finalGameSet = gameSetId.equals(bracket.getFinalGameSet().getId());
    var winningOrder = Arrays.asList(gameSetId.split("v"));
    bracketController.completeGameSet(
      bracketId,
      gameSetId,
      CompleteGameSetRequest.builder()
        .games(winningOrder.size() <= gameType.getPlayerSetsToMoveOn() ? List.of() : List.of(winningOrder))
        .winners(winningOrder.subList(0, finalGameSet ? 1 : gameType.getPlayerSetsToMoveOn()))
        .build()
    );
  }
}
