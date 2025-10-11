package mariomonday.backend.database.schema;

import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.Builder;
import lombok.Data;

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
  private Set<PlayerSet> winners;

  /**
   * The players in the set
   */
  @DocumentReference(lazy = true)
  private Set<PlayerSet> playerSets;

  /**
   * The game type of this set
   */
  private GameType gameType;


  public boolean hasEmptySlots() {
    return getEmptySlotsCount() > 0;
  }

  public int getEmptySlotsCount() {
    return gameType.getMaxPlayerSets() - previousGameSets.size() * gameType.getPlayerSetsToMoveOn();
  }

  public boolean isByeRound() {
    return gameType.getPlayerSetsToMoveOn() >= previousGameSets.size();
  }

  public int getPlayerSetCount() {
    return playerSets.size();
  }

  @DocumentReference(lazy = true)
  private Set<GameSet> previousGameSets;

  private List<Game> games;

}
