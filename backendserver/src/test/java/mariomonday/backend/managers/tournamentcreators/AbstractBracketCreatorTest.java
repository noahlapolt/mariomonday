package mariomonday.backend.managers.tournamentcreators;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.stream.Stream;
import mariomonday.backend.database.schema.GameType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AbstractBracketCreatorTest {

  private static Stream<Arguments> getNumBottomLevelGames() {
    return Stream.of(
      Arguments.of(1, 1L),
      Arguments.of(2, 2L),
      Arguments.of(3, 4L),
      Arguments.of(4, 4L),
      Arguments.of(5, 8L),
      Arguments.of(6, 8L),
      Arguments.of(7, 8L),
      Arguments.of(8, 8L),
      Arguments.of(9, 16L),
      Arguments.of(15, 16L),
      Arguments.of(16, 16L),
      Arguments.of(17, 32L)
    );
  }

  @ParameterizedTest(name = "{0} min games necessary, returns {1} bottom level games")
  @MethodSource
  void getNumBottomLevelGames(int minGamesNecessary, long expected) {
    long actual = AbstractBracketCreator.getNumBottomLevelGames(minGamesNecessary, GameType.MARIO_KART_8);
    assertThat(actual).isEqualTo(expected);
  }

  private static Stream<Arguments> getMaxRound() {
    return Stream.of(
      Arguments.of(1, 0),
      Arguments.of(2, 1),
      Arguments.of(3, 2),
      Arguments.of(4, 2),
      Arguments.of(5, 3),
      Arguments.of(6, 3),
      Arguments.of(7, 3),
      Arguments.of(8, 3),
      Arguments.of(9, 4),
      Arguments.of(16, 4),
      Arguments.of(17, 5),
      Arguments.of(32, 5),
      Arguments.of(33, 6)
    );
  }

  @ParameterizedTest(name = "{0} min games necessary, returns {1} max round number")
  @MethodSource
  void getMaxRound(int minGamesNecessary, int expected) {
    assertThat(AbstractBracketCreator.getMaxRound(minGamesNecessary, GameType.MARIO_KART_8)).isEqualTo(expected);
  }
}
