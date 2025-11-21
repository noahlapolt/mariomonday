package mariomonday.backend.managers.seeders;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocalRandomSeederTest {

  @Test
  public void testLocalRandomSeederTest_whenEloFarApart() {
    // Setup
    var player1 = PlayerSet.builder()
      .player(Player.builder().name("Reed").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1800)).build())
      .build();
    var player2 = PlayerSet.builder()
      .player(
        Player.builder().name("Guy who sucks at smash").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1400)).build()
      )
      .build();
    var player3 = PlayerSet.builder()
      .player(
        Player.builder().name("Guy is like ok at smash").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1600)).build()
      )
      .build();
    var localRandomSeeder = new LocalRandomSeeder(new Random(67));

    // Act
    var seededList = localRandomSeeder.seed(Set.of(player1, player2, player3), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify
    Assertions.assertEquals(List.of(player2, player3, player1), seededList);
  }

  @Test
  public void testLocalRandomSeederTest_whenEloCanOverlap() {
    // Setup
    var player1 = PlayerSet.builder()
      .player(Player.builder().name("Reed").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1800)).build())
      .build();
    var player2 = PlayerSet.builder()
      .player(
        Player.builder().name("Guy who sucks at smash").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1400)).build()
      )
      .build();
    var player3 = PlayerSet.builder()
      .player(
        Player.builder().name("Guy is like ok at smash").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1790)).build()
      )
      .build();
    var localRandomSeeder = new LocalRandomSeeder(new Random(67));

    // Act
    var seededList = localRandomSeeder.seed(Set.of(player1, player2, player3), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify
    Assertions.assertEquals(List.of(player2, player1, player3), seededList);
  }
}
