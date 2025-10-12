package mariomonday.backend.managers.tournamentcreators;

import java.util.List;

import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.PlayerSet;

public abstract class AbstractBracketCreator {

  /**
   * Create a bracket from a list of player IDs
   *
   * @param gameType   the game type
   * @param playerSets the player sets in seeded order (0 index is highest seed)
   * @return the created bracket
   */
  public abstract Bracket fromPlayerSets(GameType gameType, List<PlayerSet> playerSets);

  /**
   * Get the number of games in the bottom level of the bracket necessary to fit the minimum number of games
   *
   * @param minGamesNecessary the minimum number of games necessary
   * @param gameType          the game type
   * @return the number of games in the bottom level of the bracket
   */
  static int getNumBottomLevelGames(int minGamesNecessary, GameType gameType) {
    return (int) Math.pow(gameType.getNumPreviousGames(), getMaxRound(minGamesNecessary, gameType));
  }

  /**
   * Get the maximum round number necessary to fit the minimum number of games
   *
   * @param minGamesNecessary the minimum number of games necessary
   * @param gameType          the game type
   * @return the maximum round number (0-indexed)
   */
  static int getMaxRound(int minGamesNecessary, GameType gameType) {
    return (int) Math.ceil(Math.log(minGamesNecessary) / Math.log(gameType.getNumPreviousGames()));
  }
}
