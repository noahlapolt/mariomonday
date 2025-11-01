package mariomonday.backend.managers.ratingcalculators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;

/**
 * Abstract class for handling everything relevant to ELO.
 */
public abstract class AbstractEloManager {

  /**
   * K Factor used when calculating ELO,
   * representing the maximum amount the ELO can move from one game.
   * A higher value indicates more volatile scores.
   * We will start out with 32, but it may be worth switching to 16 once we get a few
   * tourneys in.
   */
  public static final int K_FACTOR = 32;

  /**
   * Function to calculate the ELO change after a game.
   * NOTE: The reason this is abstract is that I do not know how we plan to handle Mario Kart
   * and I want to keep it open-ended. We can either treat all winners and losers the same,
   * or order them and treat 3rd place differently from 4th.
   * @param gameResults List of lists, where each outer entry is a game,
   *                    and each inner entry is the result of that game as an ordered list of players,
   *                    with the first entry being the player who came first
   *                    and the last entry being the player who came last
   * @param gameType The type of game being played
   * @return Map from player to the ELO to add to that player's score.
   */
  public Map<PlayerSet, Integer> calculateEloChange(List<List<PlayerSet>> gameResults, GameType gameType) {
    if (gameResults.isEmpty()) {
      throw new IllegalArgumentException("Must have at least one game");
    }
    var playerCount = gameResults.get(0).size();
    var players = new HashSet<>(gameResults.get(0));
    if (!gameResults.stream().allMatch(game -> game.size() == playerCount)) {
      throw new IllegalArgumentException("All games in a set must have the same number of players");
    }

    var playerToScore = calculateActualScores(gameResults, gameType.getPlayerSetsToMoveOn());
    var playerToExpectedScore = calculateAllExpectedScores(players, gameResults.size(), gameType);
    return players
      .stream()
      .collect(
        Collectors.toMap(
          player -> player,
          player -> (int) Math.round((K_FACTOR * (playerToScore.get(player) - playerToExpectedScore.get(player))))
        )
      );
  }

  protected abstract Map<PlayerSet, Double> calculateActualScores(
    List<List<PlayerSet>> gameResults,
    int winnersPerGame
  );

  /**
   * Calculates the expected scores for all players in a set.
   * @param players The players in the game
   * @param gameCount The number of games being played
   * @param gameType The type of game being played
   * @return A map from player to the expected score for the game
   */
  private Map<PlayerSet, Double> calculateAllExpectedScores(Set<PlayerSet> players, int gameCount, GameType gameType) {
    var playerCount = players.size();
    var playerToExpectedScore = new HashMap<PlayerSet, Double>();
    for (var player : players) {
      playerToExpectedScore.put(player, 0.0);
      var playerSetElo = player
        .getPlayers()
        .stream()
        .mapToInt(p -> p.getEloMap().get(gameType))
        .sum();
      for (var competitor : players) {
        if (player.equals(competitor)) {
          continue;
        }
        var competitorElo = competitor
          .getPlayers()
          .stream()
          .mapToInt(p -> p.getEloMap().get(gameType))
          .sum();
        playerToExpectedScore.put(
          player,
          playerToExpectedScore.get(player) + calculateExpectedScore(playerSetElo, competitorElo)
        );
      }
      playerToExpectedScore.put(
        player,
        (gameCount * playerToExpectedScore.get(player)) / ((playerCount * (playerCount - 1)) / 2)
      );
    }
    return playerToExpectedScore;
  }

  /**
   * Calculate the expected score for a game between player and competitor.
   * In this case, expected score is a number between 0 and 1 representing the probability
   * that the player beats their competitor
   * @param playerElo The ELO of the player to calculate an expected score for
   * @param competitorElo The ELO of the competitor (whose expected score is not calculated here)
   * @return The expected score for the player when playing against the competitor
   */
  private double calculateExpectedScore(int playerElo, int competitorElo) {
    return 1 / (1 + Math.pow(10, (double) (competitorElo - playerElo) / 400));
  }
}
