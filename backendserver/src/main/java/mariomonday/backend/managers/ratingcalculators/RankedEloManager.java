package mariomonday.backend.managers.ratingcalculators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mariomonday.backend.database.schema.PlayerSet;

/**
 * ELO manager that grants ELO based on ranking within a game. This really only matters for Mario Kart.
 * In Mario Kart, first will get more points than second even though they both won.
 */
public class RankedEloManager extends AbstractEloManager {

  @Override
  public Map<PlayerSet, Double> calculateActualScores(List<List<PlayerSet>> gameResults, int winnersPerGame) {
    var playerCount = gameResults.get(0).size();
    var playerToScore = new HashMap<PlayerSet, Double>();
    for (var game : gameResults) {
      for (int i = 0; i < playerCount; i++) {
        var player = game.get(i);
        if (!playerToScore.containsKey(player)) {
          playerToScore.put(player, 0.0);
        }
        double wins = (playerCount - i - 1);
        playerToScore.put(player, playerToScore.get(player) + wins / ((playerCount * (playerCount - 1)) / 2));
      }
    }
    return playerToScore;
  }
}
