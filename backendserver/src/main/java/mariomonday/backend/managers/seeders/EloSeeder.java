package mariomonday.backend.managers.seeders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;

/**
 * Seeder that is purely off of ELO
 */
public class EloSeeder extends AbstractSeeder {

  @Override
  public List<PlayerSet> seed(Set<PlayerSet> unseededPlayers, GameType gameType) {
    List<PlayerSet> teams = new ArrayList<>(unseededPlayers);
    teams.sort(Comparator.comparingInt(team -> team.getElo(gameType)));
    return teams;
  }
}
