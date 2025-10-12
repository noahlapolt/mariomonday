package mariomonday.backend.database.schema;

import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.google.common.collect.ImmutableSet;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

/**
 * A set of games within a bracket, where the winner moves on
 */
@Data
@Builder
@Document
public class GameSet {

  @Id
  private final String id;

  /**
   * The winners of the set
   */
  @DocumentReference(lazy = true)
  @Singular
  private Set<PlayerSet> winners;

  /**
   * The losers of the set
   */
  @DocumentReference(lazy = true)
  @Singular
  private Set<PlayerSet> losers;

  /**
   * The playerSets added to this set not from previous games
   */
  @DocumentReference(lazy = true)
  @Singular
  private Set<PlayerSet> addedPlayerSets;

  /**
   * The game type of this set
   */
  @NonNull
  private GameType gameType;

  @DocumentReference(lazy = true)
  @Singular
  private final Set<GameSet> previousGameSets;

  @DocumentReference(lazy = true)
  @Singular
  private List<Game> games;

  public int getNumEmptySlots() {
    return Math.max(0, gameType.getMaxPlayerSets() - getTotalPlayers());
  }

  public boolean isByeRound() {
    return gameType.getPlayerSetsToMoveOn() >= getTotalPlayers();
  }

  public Set<PlayerSet> getPlayerSetsFromPreviousGames() {
    ImmutableSet.Builder<PlayerSet> setBuilder = ImmutableSet.builder();

    getPreviousGameSets().forEach(gameSet -> setBuilder.addAll(gameSet.getWinners()));

    return setBuilder.build();
  }

  public int getTotalPlayers() {
    return getNumPlayersFromPreviousGames() + addedPlayerSets.size();
  }

  public int getNumPlayersFromPreviousGames() {
    return previousGameSets.stream().mapToInt(GameSet::getExpectedPlayersMovingOn).sum();
  }

  public int getExpectedPlayersMovingOn() {
    if (winners.isEmpty()) {
      return Math.min(getTotalPlayers(), gameType.getPlayerSetsToMoveOn());
    } else {
      return winners.size();
    }
  }

  public boolean isValidGameSet() {
    return getTotalPlayers() <= gameType.getMaxPlayerSets();
  }
}
