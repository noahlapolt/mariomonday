package mariomonday.backend.database.schema;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * A game
 */
@Value
@Builder
public class Game {

  /**
   * The game's ID
   */
  @Id
  String id;

  /**
   * The player IDs of the winners of the game
   */
  @DocumentReference(lazy = true)
  @Singular
  Set<PlayerSet> winners;

  /**
   * The player IDs of the players in the game
   */
  @DocumentReference(lazy = true)
  @Singular
  Set<PlayerSet> playerSets;
}
