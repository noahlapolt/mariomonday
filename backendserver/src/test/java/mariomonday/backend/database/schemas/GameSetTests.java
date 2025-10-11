package mariomonday.backend.database.schemas;

import java.util.Set;
import java.util.stream.Stream;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.utils.TestDataUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GameSetTests {

  @Test
  void contextLoads() {}

  private static Stream<Arguments> isByeRound() {
    return Stream.of();
  }

  @ParameterizedTest
  @MethodSource
  void isByeRound(GameType gameType, int playersInGame1, int playersInGame2, int playersDirectlyAdded) {
    GameSet testGame = GameSet.builder().previousGameSets(
      Set.of(TestDataUtil.createFakeGameSet().playerSets(TestDataUtil.createNFakePlayers(playersInGame1))).build()
    );
  }
}
