package mariomonday.backend.database.schema;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

/**
 * A tournament, containing multiple games
 */
@Data
@Builder
@Document
public class Bracket {

  /**
   * ID for the bracket
   */
  @Id
  private final String id;

  /**
   * The moment the bracket started
   */
  @NonNull
  private final Instant date;

  /**
   * The number of rounds in the bracket
   */
  private int rounds;

  /**
   * The winners
   */
  @DocumentReference(lazy = true)
  @Singular
  private Set<Player> winners;

  /**
   * The type of game this bracket was for
   */
  @NonNull
  private final GameType gameType;

  /**
   * The players who participated in this bracket
   */
  @DocumentReference(lazy = true)
  @Singular
  private Set<Player> players;

  /**
   * The sets in this bracket
   */
  @DocumentReference(lazy = true)
  @Singular
  private Set<GameSet> gameSets;
}
