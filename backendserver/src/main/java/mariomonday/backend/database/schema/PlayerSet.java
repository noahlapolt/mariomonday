package mariomonday.backend.database.schema;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;

/**
 * The unit that participates in the set. This can be multiple players or just one.
 */
@Value
@Builder
public class PlayerSet {

  /**
   * Id of this player set
   */
  @Id
  String id;

  /**
   * The players in this playerSet. Attempting to make this unique so every team/person combo only appears a single time
   */
  @Indexed(unique = true)
  @DocumentReference(lazy = true)
  @Singular
  Set<Player> players;

  /**
   * Name of this playerSet
   */
  @Getter(AccessLevel.NONE)
  String name;

  /**
   * Get the name of this playerSet
   *
   * @return the name of the playerSet or the name of the one player in this set
   */
  public String getName() {
    if (players.size() == 1) {
      return players.iterator().next().getName();
    } else {
      return name;
    }
  }
}
