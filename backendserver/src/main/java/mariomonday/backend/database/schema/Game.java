package mariomonday.backend.database.schema;

import java.util.List;
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
   * The player IDs of the players in the game, ordered by their placement in the game.
   */
  @Singular
  List<PlayerSet> playerSets;

  /**
   * The game type of this game
   */
  private GameType gameType;
}
