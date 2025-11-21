package mariomonday.backend.database.schema;

import java.util.Set;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * A game
 */
@Value
@Builder
@Document
public class Game {

  /**
   * The game's ID
   */
  @Id
  String id;

  /**
   * The player IDs of the winners of the game
   */
  @Singular
  Set<PlayerSet> winners;

  /**
   * The player IDs of the players in the game
   */
  @Singular
  Set<PlayerSet> playerSets;
}
