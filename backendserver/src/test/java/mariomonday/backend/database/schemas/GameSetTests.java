package mariomonday.backend.database.schemas;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.utils.TestDataUtil;

class GameSetTests {

  private static Stream<Arguments> isByeRound() {
    return Stream.of(Arguments.of(GameType.MARIO_KART_8, 4, 2, 0));
  }

  @ParameterizedTest
  @MethodSource
  void isByeRound(GameType gameType, int playersInGame1, int playersInGame2, int playersDirectlyAdded) {
    GameSet testGame = GameSet.builder()
      .previousGameSets(
        Set.of(TestDataUtil.createFakeGameSet().playerSets(TestDataUtil.createNFakePlayers(playersInGame1)).build())
      )
      .build();
  }
}
