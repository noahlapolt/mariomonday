package mariomonday.backend.database.schemas;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Singular;
import lombok.Value;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.utils.TestDataUtil;

class GameSetTests {

  private static Stream<Arguments> testData() {
    return Stream.of(
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Mario kart with full previous games")
          .gameType(GameType.MARIO_KART_8)
          .previousGameWinners(List.of(2, 2))
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Mario kart with one missing winner from previous game")
          .gameType(GameType.MARIO_KART_8)
          .previousGameWinners(List.of(2, 1))
          .numEmptySlots(1)
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Mario kart with missing winners from previous games")
          .gameType(GameType.MARIO_KART_8)
          .previousGameWinners(List.of(1, 1))
          .isByeRound(true)
          .numEmptySlots(2)
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Smash bros with full previous games")
          .gameType(GameType.SMASH_ULTIMATE_SINGLES)
          .previousGameWinners(List.of(1, 1))
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Smash bros with no players")
          .gameType(GameType.SMASH_ULTIMATE_SINGLES)
          .previousGameWinners(List.of(0, 0))
          .isByeRound(true)
          .numEmptySlots(2)
          .numPlayersMovingOn(0)
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Smash bros with too many players")
          .gameType(GameType.SMASH_ULTIMATE_SINGLES)
          .previousGameWinners(List.of(1, 1))
          .playersAddedToGame(1)
          .isValid(false)
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Mario kart with too many players")
          .gameType(GameType.MARIO_KART_8)
          .previousGameWinners(List.of(4, 4))
          .playersAddedToGame(1)
          .isValid(false)
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Mario kart with no previous games and full players added")
          .gameType(GameType.MARIO_KART_8)
          .playersAddedToGame(4)
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Mario kart with no previous games not enough players added for a game")
          .gameType(GameType.MARIO_KART_8)
          .playersAddedToGame(2)
          .numEmptySlots(2)
          .isByeRound(true)
          .build()
      ),
      Arguments.of(
        GameSetTestData.builder()
          .descriptor("Mario Kart with no previous games and not enough players added for moving on")
          .gameType(GameType.MARIO_KART_8)
          .playersAddedToGame(1)
          .numPlayersMovingOn(1)
          .numEmptySlots(3)
          .isByeRound(true)
          .build()
      )
    );
  }

  @Value
  @Builder
  private static class GameSetTestData {

    String descriptor;

    GameType gameType;

    @Singular
    List<Integer> previousGameWinners;

    @Default
    int playersAddedToGame = 0;

    @Default
    boolean isByeRound = false;

    @Default
    int numEmptySlots = 0;

    Integer numPlayersMovingOn;

    @Default
    boolean isValid = true;

    public int getNumPlayersMovingOn() {
      return numPlayersMovingOn != null ? numPlayersMovingOn : gameType.getPlayerSetsToMoveOn();
    }
  }

  @ParameterizedTest
  @MethodSource("testData")
  void isByeRound(GameSetTestData testData) {
    GameSet testGame = createGameSetFromTestData(testData);
    assertThat(testGame.isByeRound())
      .as(String.format("%s results in %s bye round", testData.getDescriptor(), testData.isByeRound() ? "a" : "no"))
      .isEqualTo(testData.isByeRound());
  }

  @ParameterizedTest
  @MethodSource("testData")
  void numEmptySlots(GameSetTestData testData) {
    GameSet testGame = createGameSetFromTestData(testData);
    assertThat(testGame.getNumEmptySlots())
      .as(String.format("%s results in %s empty slots", testData.getDescriptor(), testData.getNumEmptySlots()))
      .isEqualTo(testData.getNumEmptySlots());
  }

  @ParameterizedTest
  @MethodSource("testData")
  void isValidGameSet(GameSetTestData testData) {
    GameSet testGame = createGameSetFromTestData(testData);
    assertThat(testGame.isValidGameSet())
      .as(
        String.format("%s results in %s game set", testData.getDescriptor(), testData.isValid() ? "valid" : "invalid")
      )
      .isEqualTo(testData.isValid());
  }

  @ParameterizedTest
  @MethodSource("testData")
  void getNumPlayersMovingOn(GameSetTestData testData) {
    GameSet testGame = createGameSetFromTestData(testData);
    assertThat(testGame.getExpectedPlayersMovingOn())
      .as(
        String.format("%s results in %s players moving on", testData.getDescriptor(), testData.getNumPlayersMovingOn())
      )
      .isEqualTo(testData.getNumPlayersMovingOn());
  }

  private GameSet createGameSetFromTestData(GameSetTestData testData) {
    return GameSet.builder()
      .previousGameSets(
        IntStream.range(0, testData.getPreviousGameWinners().size())
          .boxed()
          .map(gameNum ->
            TestDataUtil.createFakeGameSet()
              .winners(TestDataUtil.createNFakePlayers(testData.getPreviousGameWinners().get(gameNum)))
              .gameType(testData.getGameType())
              .id(String.valueOf(gameNum))
              .build()
          )
          .collect(Collectors.toSet())
      )
      .addedPlayerSets(TestDataUtil.createNFakePlayers(testData.getPlayersAddedToGame()))
      .gameType(testData.getGameType())
      .build();
  }
}
