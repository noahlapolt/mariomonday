package mariomonday.backend.managers.seeders;

import java.util.List;
import java.util.Set;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;

/**
 * I called it seeder cuz its a funny name. Like breeder.
 * It does the seeding what else would it do you freak
 */
public abstract class AbstractSeeder {

  /**
   * Takes in a set of teams, and seeds those teams. Method of seeding depends on child class.
   * @param unseededPlayers  Set of teams to seed
   * @param gameType  The type of game being played
   * @return A seeded list of teams, with the first team being the highest seed
   */
  public abstract List<PlayerSet> seed(Set<PlayerSet> unseededPlayers, GameType gameType);
}
