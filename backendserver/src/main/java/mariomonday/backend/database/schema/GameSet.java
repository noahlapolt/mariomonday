package mariomonday.backend.database.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * A set of games within a bracket, where the winner moves on
 */
@Data
@Builder
@Document
public class GameSet {

  @Id
  private final String id;

  private final Integer roundIndex;

  /**
   * The winners of the set
   */
  @Singular
  private Set<PlayerSet> winners;

  /**
   * The losers of the set
   */
  @Singular
  private Set<PlayerSet> losers;

  /**
   * The playerSets added to this set not from previous games
   */
  @Singular
  private Set<PlayerSet> addedPlayerSets;

  /**
   * The game type of this set
   */
  private GameType gameType;

  @DocumentReference(lazy = true)
  @Singular
  private final Set<GameSet> previousGameSets;

  @DocumentReference(lazy = true)
  @Singular
  private List<Game> games;

  @JsonIgnore
  public int getNumEmptySlots() {
    return Math.max(0, gameType.getMaxPlayerSets() - getTotalPlayers());
  }

  @JsonIgnore
  public boolean isByeRound() {
    return gameType.getPlayerSetsToMoveOn() >= getTotalPlayers();
  }

  @JsonIgnore
  public Set<PlayerSet> getPlayerSetsFromPreviousGames() {
    ImmutableSet.Builder<PlayerSet> setBuilder = ImmutableSet.builder();

    getPreviousGameSets().forEach(gameSet -> setBuilder.addAll(gameSet.getWinners()));

    return setBuilder.build();
  }

  @JsonIgnore
  public int getTotalPlayers() {
    return getNumPlayersFromPreviousGames() + addedPlayerSets.size();
  }

  @JsonIgnore
  public int getNumPlayersFromPreviousGames() {
    return previousGameSets.stream().mapToInt(GameSet::getExpectedPlayersMovingOn).sum();
  }

  @JsonIgnore
  public int getExpectedPlayersMovingOn() {
    if (winners.isEmpty()) {
      return Math.min(getTotalPlayers(), gameType.getPlayerSetsToMoveOn());
    } else {
      return winners.size();
    }
  }

  @JsonIgnore
  public boolean isValidGameSet() {
    return getTotalPlayers() <= gameType.getMaxPlayerSets();
  }
}
