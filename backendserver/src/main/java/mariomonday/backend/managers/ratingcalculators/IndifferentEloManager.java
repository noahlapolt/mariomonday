package mariomonday.backend.managers.ratingcalculators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mariomonday.backend.database.schema.PlayerSet;

/**
 * ELO manager that treats all winners and losers the same. This really only matters for Mario Kart.
 * In Mario Kart, first and second will both be treated the same as "winners"
 * and third and fourth will both be treated the same as "losers"
 */
public class IndifferentEloManager extends AbstractEloManager {

  @Override
  public Map<PlayerSet, Double> calculateActualScores(List<List<PlayerSet>> gameResults,
      int winnersPerGame) {
    var playerCount = gameResults.get(0).size();
    var playerToScore = new HashMap<PlayerSet, Double>();
    var loserCount = playerCount - winnersPerGame;
    for (var game : gameResults) {
      for (int i = 0; i < playerCount; i++) {
        var player = game.get(i);
        if (!playerToScore.containsKey(player)) {
          playerToScore.put(player, 0.0);
        }

        double scoreToAdd;
        if (i < winnersPerGame) {
          // If you won, you get 1 point for each loser, and 0.5 points for each other winner
          scoreToAdd = loserCount + 0.5 * (winnersPerGame - 1);
        } else {
          // If you lost, you get 0.5 points for each other loser
          scoreToAdd = 0.5 * (loserCount - 1);
        }
        playerToScore.put(player, playerToScore.get(player)
            + scoreToAdd / (playerCount * (playerCount - 1) / 2));
      }
    }
    return playerToScore;
  }
}
