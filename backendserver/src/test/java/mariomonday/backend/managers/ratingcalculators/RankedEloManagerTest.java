package mariomonday.backend.managers.ratingcalculators;

import java.util.List;
import java.util.Map;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RankedEloManagerTest {

  private static RankedEloManager rankedEloManager;

  @BeforeAll
  public static void setupTest() {
    rankedEloManager = new RankedEloManager();
  }

  @Test
  public void testSingleGame1v1WithSameElo() {
    // Setup
    var player1 = PlayerSet.builder().player(Player.builder()
        .name("Reed")
        .eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1500)).build())
        .build();
    var player2 = PlayerSet.builder().player(Player.builder()
            .name("Guy who sucks at smash")
            .eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1500)).build())
        .build();

    // Act
    var result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify
    Assertions.assertEquals(result.size(), 2);
    Assertions.assertEquals(result.get(player1), 16);
    Assertions.assertEquals(result.get(player2), -16);
  }

  @Test
  public void testMultiGameSet1v1WithSameElo() {
    // Setup
    var player1 = PlayerSet.builder().player(Player.builder()
            .name("Reed")
            .eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1500)).build())
        .build();
    var player2 = PlayerSet.builder().player(Player.builder()
            .name("Guy who sucks at smash")
            .eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1500)).build())
        .build();

    // Act
    // Player 1 wins 2-0
    var result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2), List.of(player1, player2)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify
    Assertions.assertEquals(result.size(), 2);
    Assertions.assertEquals(result.get(player1), 32);
    Assertions.assertEquals(result.get(player2), -32);

    // Act 2
    // Player 1 wins 2-1
    result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2), List.of(player2, player1), List.of(player1, player2)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify 2
    Assertions.assertEquals(result.size(), 2);
    Assertions.assertEquals(result.get(player1), 16);
    Assertions.assertEquals(result.get(player2), -16);

    // Act 3
    // 1-1 tie
    result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2), List.of(player2, player1)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify 3
    Assertions.assertEquals(result.size(), 2);
    Assertions.assertEquals(result.get(player1), 0);
    Assertions.assertEquals(result.get(player2), 0);
  }

  @Test
  public void testSingleGameSet1v1WithDifferentElo() {
    // Setup
    var player1 = PlayerSet.builder().player(Player.builder()
            .name("Reed")
            .eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1600)).build())
        .build();
    var player2 = PlayerSet.builder().player(Player.builder()
            .name("Guy who sucks at smash")
            .eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1400)).build())
        .build();

    // Act
    var result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify
    // Should have small winnings punching down
    Assertions.assertEquals(result.size(), 2);
    Assertions.assertEquals(result.get(player1), 8);
    Assertions.assertEquals(result.get(player2), -8);

    // Act 2
    result = rankedEloManager
        .calculateEloChange(List.of(List.of(player2, player1)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify 2
    // Big win punching up
    Assertions.assertEquals(result.size(), 2);
    Assertions.assertEquals(result.get(player1), -24);
    Assertions.assertEquals(result.get(player2), 24);
  }

  @Test
  public void testMultiGameSet1v1WithDifferentElo() {
    // Setup
    var player1 = PlayerSet.builder().player(Player.builder()
            .name("Reed")
            .eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1600)).build())
        .build();
    var player2 = PlayerSet.builder().player(Player.builder()
            .name("Guy who sucks at smash")
            .eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1400)).build())
        .build();

    // Act
    // Player 1 wins 2-0
    var result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2), List.of(player1, player2)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify
    Assertions.assertEquals(result.size(), 2);
    // If the math was perfect this would be 16, but
    // because the log math doesn't exactly work out to 25:75 win percentage
    // with a 200 point difference, the numbers can be slightly different
    Assertions.assertEquals(result.get(player1), 15);
    Assertions.assertEquals(result.get(player2), -15);

    // Act 2
    // Player 1 wins 2-1
    result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2), List.of(player2, player1), List.of(player1, player2)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify 2
    // Pretty crazy, even though player 1 won the set they lost ELO cuz the skill gap is so large
    Assertions.assertEquals(result.size(), 2);
    Assertions.assertEquals(result.get(player1), -9);
    Assertions.assertEquals(result.get(player2), 9);

    // Act 3
    // 1-1 tie
    result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2), List.of(player2, player1)), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify 3
    // Same here, even in a tie player 1 loses big
    Assertions.assertEquals(result.size(), 2);
    Assertions.assertEquals(result.get(player1), -17);
    Assertions.assertEquals(result.get(player2), 17);
  }

  @Test
  public void testSingleGameSetFreeForAllWithSameElo() {
    // Setup
    var player1 = PlayerSet.builder().player(Player.builder()
            .name("Reed")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();
    var player2 = PlayerSet.builder().player(Player.builder()
            .name("Jack")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();
    var player3 = PlayerSet.builder().player(Player.builder()
            .name("Zach")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();
    var player4 = PlayerSet.builder().player(Player.builder()
            .name("Noah")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();

    // Act
    var result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2, player3, player4)), GameType.MARIO_KART_WORLD);

    // Verify
    Assertions.assertEquals(result.size(), 4);
    Assertions.assertEquals(result.get(player1), 8);
    Assertions.assertEquals(result.get(player2), 3);
    Assertions.assertEquals(result.get(player3), -3);
    Assertions.assertEquals(result.get(player4), -8);
  }

  @Test
  public void testMultiGameSetFreeForAllWithSameElo() {
    // Setup
    var player1 = PlayerSet.builder().player(Player.builder()
            .name("Reed")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();
    var player2 = PlayerSet.builder().player(Player.builder()
            .name("Jack")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();
    var player3 = PlayerSet.builder().player(Player.builder()
            .name("Zach")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();
    var player4 = PlayerSet.builder().player(Player.builder()
            .name("Noah")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();

    // Act
    var result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2, player3, player4),
            List.of(player4, player3, player2, player1)), GameType.MARIO_KART_WORLD);

    // Verify
    Assertions.assertEquals(result.size(), 4);
    Assertions.assertEquals(result.get(player1), 0);
    Assertions.assertEquals(result.get(player2), 0);
    Assertions.assertEquals(result.get(player3), 0);
    Assertions.assertEquals(result.get(player4), 0);

    // Act 2
    result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2, player3, player4),
            List.of(player1, player4, player3, player2)), GameType.MARIO_KART_WORLD);

    // Verify 2
    // A point gets added here due to rounding, I think thats just gonna happen when we use ints
    Assertions.assertEquals(result.size(), 4);
    Assertions.assertEquals(result.get(player1), 16);
    Assertions.assertEquals(result.get(player2), -5);
    Assertions.assertEquals(result.get(player3), -5);
    Assertions.assertEquals(result.get(player4), -5);
  }

  @Test
  public void testSingleGameSetFreeForAllWithDifferentElo() {
    // Setup
    var player1 = PlayerSet.builder().player(Player.builder()
            .name("Reed")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1800)).build())
        .build();
    var player2 = PlayerSet.builder().player(Player.builder()
            .name("Jack")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1600)).build())
        .build();
    var player3 = PlayerSet.builder().player(Player.builder()
            .name("Zach")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1400)).build())
        .build();
    var player4 = PlayerSet.builder().player(Player.builder()
            .name("Noah")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1200)).build())
        .build();

    // Act
    var result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2, player3, player4)), GameType.MARIO_KART_WORLD);

    // Verify
    Assertions.assertEquals(result.size(), 4);
    Assertions.assertEquals(result.get(player1), 2);
    Assertions.assertEquals(result.get(player2), 0);
    Assertions.assertEquals(result.get(player3), 0);
    Assertions.assertEquals(result.get(player4), -2);

    // Setup 2
    player1 = PlayerSet.builder().player(Player.builder()
            .name("Reed")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1600)).build())
        .build();
    player2 = PlayerSet.builder().player(Player.builder()
            .name("Jack")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1550)).build())
        .build();
    player3 = PlayerSet.builder().player(Player.builder()
            .name("Zach")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();
    player4 = PlayerSet.builder().player(Player.builder()
            .name("Noah")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1450)).build())
        .build();

    // Act 2
    result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2, player3, player4)), GameType.MARIO_KART_WORLD);

    // Verify 2
    Assertions.assertEquals(result.size(), 4);
    Assertions.assertEquals(result.get(player1), 6);
    Assertions.assertEquals(result.get(player2), 2);
    Assertions.assertEquals(result.get(player3), -2);
    Assertions.assertEquals(result.get(player4), -6);
  }

  @Test
  public void testMultiGameSetFreeForAllWithDifferentElo() {
    // Setup 2
    var player1 = PlayerSet.builder().player(Player.builder()
            .name("Reed")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1600)).build())
        .build();
    var player2 = PlayerSet.builder().player(Player.builder()
            .name("Jack")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1550)).build())
        .build();
    var player3 = PlayerSet.builder().player(Player.builder()
            .name("Zach")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1500)).build())
        .build();
    var player4 = PlayerSet.builder().player(Player.builder()
            .name("Noah")
            .eloMap(Map.of(GameType.MARIO_KART_WORLD, 1450)).build())
        .build();

    // Act 2
    var result = rankedEloManager
        .calculateEloChange(List.of(List.of(player1, player2, player3, player4),
            List.of(player1, player4, player2, player3)), GameType.MARIO_KART_WORLD);

    // Verify 2
    Assertions.assertEquals(result.size(), 4);
    Assertions.assertEquals(result.get(player1), 12);
    Assertions.assertEquals(result.get(player2), -1);
    Assertions.assertEquals(result.get(player3), -9);
    Assertions.assertEquals(result.get(player4), -1);
  }
}
