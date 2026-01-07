package mariomonday.backend.apis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import mariomonday.backend.apis.schema.ApiBracket;
import mariomonday.backend.apis.schema.ApiGameSet;
import mariomonday.backend.apis.schema.CompleteGameSetRequest;
import mariomonday.backend.apis.schema.CreateBracketRequest;
import mariomonday.backend.database.schema.Bracket;
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
    playerRepository.saveAll(List.of(testPlayer1, testPlayer2, testPlayer3, testPlayer4));
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
  public void testEntireSinglesBracket_startToFinish() {
    // 4 players, 2 rounds
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
    var bracket = bracketController.postBracket(bracketReq);
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
    var gameOne = bracket.getGameSets().get(0).get(0);
    completeGameSet(bracket.getId(), gameOne, gameOne.getPlayerSets().stream().sorted(reedWins).toList());
    var gameTwo = bracket.getGameSets().get(0).get(1);
    completeGameSet(bracket.getId(), gameTwo, gameTwo.getPlayerSets().stream().sorted(reedWins).toList());
    var finals = gameSetRepository.findById(bracket.getGameSets().get(1).get(0).getId()).get();
    completeGameSet(
      bracket.getId(),
      ApiGameSet.fromGameSet(finals),
      finals.getPlayers().stream().map(PlayerSet::getId).sorted(reedWins).toList()
    );

    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    Assertions.assertEquals(1, completedBracket.getWinners().size());
    Assertions.assertEquals("Reed", completedBracket.getWinners().stream().findFirst().get().getName());
  }

  @Test
  public void testEntireKartBracket_startToFinish() {
    // 8 players, 2 rounds
    // Setup
    var teams = randomPlayers
      .stream()
      .toList()
      .subList(0, 8)
      .stream()
      .collect(Collectors.toMap(Player::getId, player -> List.of(player.getId())));
    teams.remove(randomPlayers.stream().findFirst().get().getId());
    teams.put("Reed", List.of(playerNameToPlayer.get("Reed").getId()));
    var bracketReq = CreateBracketRequest.builder().teams(teams).gameType(GameType.MARIO_KART_8).build();

    // Act
    var bracket = bracketController.postBracket(bracketReq);
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
    var gameOne = bracket.getGameSets().get(0).get(0);
    completeGameSet(bracket.getId(), gameOne, gameOne.getPlayerSets().stream().sorted(reedWins).toList(), 2);
    var gameTwo = bracket.getGameSets().get(0).get(1);
    completeGameSet(bracket.getId(), gameTwo, gameTwo.getPlayerSets().stream().sorted(reedWins).toList(), 2);
    var finals = gameSetRepository.findById(bracket.getGameSets().get(1).get(0).getId()).get();
    completeGameSet(
      bracket.getId(),
      ApiGameSet.fromGameSet(finals),
      finals.getPlayers().stream().map(PlayerSet::getId).sorted(reedWins).toList(),
      1
    );

    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    Assertions.assertEquals(1, completedBracket.getWinners().size());
    Assertions.assertEquals("Reed", completedBracket.getWinners().stream().findFirst().get().getName());
  }

  @Test
  public void testEntireDoublesBracket_startToFinish() {
    // 4 teams (8 players), 2 rounds
    // Setup
    var teams = new HashMap<String, List<String>>();
    for (int i = 0; i < randomPlayers.size() / 2 - 2; i += 2) {
      teams.put("Team " + i, List.of(randomPlayers.get(i).getId(), randomPlayers.get(i + 1).getId()));
    }
    teams.put("The GOATs", List.of(playerNameToPlayer.get("Reed").getId(), playerNameToPlayer.get("Zach").getId()));
    var bracketReq = CreateBracketRequest.builder().teams(teams).gameType(GameType.SMASH_ULTIMATE_DOUBLES).build();

    // Act
    var bracket = bracketController.postBracket(bracketReq);
    var playerSetIdToPlayerName = bracket
      .getTeams()
      .stream()
      .collect(Collectors.toMap(PlayerSet::getId, PlayerSet::getName));
    Comparator<String> reedWins = (teamId1, teamId2) -> {
      var team1 = playerSetIdToPlayerName.get(teamId1);
      var team2 = playerSetIdToPlayerName.get(teamId2);
      if (team1.equals("The GOATs")) {
        return -1;
      } else if (team2.equals("The GOATs")) {
        return 1;
      } else {
        return -1;
      }
    };
    var gameOne = bracket.getGameSets().get(0).get(0);
    completeGameSet(bracket.getId(), gameOne, gameOne.getPlayerSets().stream().sorted(reedWins).toList());
    var gameTwo = bracket.getGameSets().get(0).get(1);
    completeGameSet(bracket.getId(), gameTwo, gameTwo.getPlayerSets().stream().sorted(reedWins).toList());
    var finals = gameSetRepository.findById(bracket.getGameSets().get(1).get(0).getId()).get();
    completeGameSet(
      bracket.getId(),
      ApiGameSet.fromGameSet(finals),
      finals.getPlayers().stream().map(PlayerSet::getId).sorted(reedWins).toList()
    );

    bracketController.completeBracket(bracket.getId());

    // Verify
    var completedBracket = Bracket.loadLazyBracket(bracketRepository.findById(bracket.getId()).get());
    // Assert bracket winners are set and are the same as the winners of the final game set
    Assertions.assertEquals(2, completedBracket.getWinners().size());
    var winnerIds = completedBracket.getWinners().stream().map(Player::getId).collect(Collectors.toSet());
    Assertions.assertTrue(winnerIds.contains(playerNameToPlayer.get("Reed").getId()));
    Assertions.assertTrue(winnerIds.contains(playerNameToPlayer.get("Zach").getId()));
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
}
