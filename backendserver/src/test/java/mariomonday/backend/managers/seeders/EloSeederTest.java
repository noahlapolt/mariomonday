package mariomonday.backend.managers.seeders;

import java.util.List;
import java.util.Map;
import java.util.Set;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EloSeederTest {

  @Test
  public void testEloSeeder_whenAllTeamsOnePlayer() {
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
    var eloSeeder = new EloSeeder();

    // Act
    var seededList = eloSeeder.seed(Set.of(player1, player2, player3), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify
    Assertions.assertEquals(List.of(player2, player3, player1), seededList);
  }

  @Test
  public void testEloSeeder_whenAllTeamsHaveMultiplePlayers() {
    // Setup
    var team1 = PlayerSet.builder()
      .players(
        List.of(
          Player.builder().name("Reed").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1800)).build(),
          Player.builder().name("Fuck you guy").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1500)).build()
        )
      )
      .build();
    var team2 = PlayerSet.builder()
      .players(
        List.of(
          Player.builder().name("Guy who sucks at smash").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1400)).build(),
          Player.builder().name("The man who lurks").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1700)).build()
        )
      )
      .build();
    var team3 = PlayerSet.builder()
      .players(
        List.of(
          Player.builder().name("The one who loves").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1750)).build(),
          Player.builder().name("Guy is like ok at smash").eloMap(Map.of(GameType.SMASH_ULTIMATE_SINGLES, 1600)).build()
        )
      )
      .build();
    var eloSeeder = new EloSeeder();

    // Act
    var seededList = eloSeeder.seed(Set.of(team1, team2, team3), GameType.SMASH_ULTIMATE_SINGLES);

    // Verify
    Assertions.assertEquals(List.of(team2, team1, team3), seededList);
  }

  @Test
  public void testEloSeeder_whenAllTeamsOnePlayerAndSameElo() {
    // Setup
    var player1 = PlayerSet.builder()
      .player(Player.builder().name("Reed").eloMap(Map.of(GameType.MARIO_KART_8, 1500)).build())
      .build();
    var player2 = PlayerSet.builder()
      .player(Player.builder().name("Guy who sucks at smash").eloMap(Map.of(GameType.MARIO_KART_8, 1500)).build())
      .build();
    var player3 = PlayerSet.builder()
      .player(Player.builder().name("Guy is like ok at smash").eloMap(Map.of(GameType.MARIO_KART_8, 1500)).build())
      .build();
    var eloSeeder = new EloSeeder();

    // Act
    var seededList = eloSeeder.seed(Set.of(player1, player2, player3), GameType.MARIO_KART_8);

    // Verify
    Assertions.assertEquals(3, seededList.size());
    Assertions.assertTrue(seededList.containsAll(List.of(player1, player2, player3)));
    var prevTeam = seededList.get(0);
    for (int i = 1; i < seededList.size(); i++) {
      // Assert ELO is increasing
      Assertions.assertTrue(prevTeam.getElo(GameType.MARIO_KART_8) <= seededList.get(i).getElo(GameType.MARIO_KART_8));
      prevTeam = seededList.get(i);
    }
  }

  @Test
  public void testEloSeeder_whenAllTeamsOnePlayerAndSomeSameElo() {
    // Setup
    var player1 = PlayerSet.builder()
      .player(Player.builder().name("Reed").eloMap(Map.of(GameType.MARIO_KART_8, 1600)).build())
      .build();
    var player2 = PlayerSet.builder()
      .player(Player.builder().name("Guy who sucks at smash").eloMap(Map.of(GameType.MARIO_KART_8, 1500)).build())
      .build();
    var player3 = PlayerSet.builder()
      .player(Player.builder().name("Guy is like ok at smash").eloMap(Map.of(GameType.MARIO_KART_8, 1500)).build())
      .build();
    var eloSeeder = new EloSeeder();

    // Act
    var seededList = eloSeeder.seed(Set.of(player1, player2, player3), GameType.MARIO_KART_8);

    // Verify
    Assertions.assertEquals(3, seededList.size());
    Assertions.assertTrue(seededList.containsAll(List.of(player1, player2, player3)));
    var prevTeam = seededList.get(0);
    for (int i = 1; i < seededList.size(); i++) {
      // Assert ELO is increasing
      Assertions.assertTrue(prevTeam.getElo(GameType.MARIO_KART_8) <= seededList.get(i).getElo(GameType.MARIO_KART_8));
      prevTeam = seededList.get(i);
    }
  }
}
