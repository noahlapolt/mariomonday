package mariomonday.backend.managers.seeders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;

/**
 * Seeder that uses ELO, but will do a small randomization to ensure that
 * not each bracket is the exact same once ELO is settled.
 * This is done by finding the highest and lowest ELOs, taking the difference between the two
 * and then adding/subtracting a random percentage of that number to each player
 * (with a cap on how much it can change)
 */
public class LocalRandomSeeder extends AbstractSeeder {

  /**
   * The maximum percent of ELO a player can "gain" or "lose" while seeding
   */
  private static final double MAX_ELO_VARIANCE = 0.1;

  /**
   * Random object to use, can be injected for testing
   */
  private Random rand;

  public LocalRandomSeeder() {
    this(new Random());
  }

  public LocalRandomSeeder(Random rand) {
    this.rand = rand;
  }

  @Override
  public List<PlayerSet> seed(Set<PlayerSet> unseededPlayers, GameType gameType) {
    List<PlayerSet> teams = new ArrayList<>(unseededPlayers);
    // First sort by ELO to make random consistent with seed
    // since set ordering is random
    teams.sort(Comparator.comparingInt(team -> team.getElo(gameType)));
    int eloDifference = teams.get(teams.size() - 1).getElo(gameType) - teams.get(0).getElo(gameType);
    int maxEloChange = (int) (MAX_ELO_VARIANCE * eloDifference);

    // In order to make the randomization more understandable to follow,
    // each team gets assigned a "new ELO", which is then used to seed
    Map<PlayerSet, Integer> teamToNewElo = new HashMap<>();
    teams.forEach(team ->
      teamToNewElo.put(team, team.getElo(gameType) + rand.nextInt(maxEloChange * 2) - maxEloChange)
    );
    teams.sort(Comparator.comparingInt(teamToNewElo::get));
    return teams;
  }
}
