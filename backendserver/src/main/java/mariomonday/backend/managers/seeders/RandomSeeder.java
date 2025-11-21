package mariomonday.backend.managers.seeders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;

/**
 * Seeder that is purely random
 */
public class RandomSeeder extends AbstractSeeder {

  /**
   * Random object to use, can be injected for testing
   */
  private Random rand;

  public RandomSeeder() {
    this(new Random());
  }

  public RandomSeeder(Random rand) {
    this.rand = rand;
  }

  @Override
  public List<PlayerSet> seed(Set<PlayerSet> unseededPlayers, GameType gameType) {
    List<PlayerSet> teams = new ArrayList<>(unseededPlayers);
    // First sort by ELO to make random consistent with seed
    // since set ordering is random
    teams.sort(Comparator.comparingInt(team -> team.getElo(gameType)));
    Collections.shuffle(teams, rand);
    return teams;
  }
}
