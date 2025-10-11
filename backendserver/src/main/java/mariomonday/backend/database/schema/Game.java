package mariomonday.backend.database.schema;

import java.util.Set;

import org.springframework.data.annotation.Id;

import lombok.Builder;
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
  Set<PlayerSet> winners;

  /**
   * The player IDs of the players in the game
   */
  Set<PlayerSet> playerSets;
}