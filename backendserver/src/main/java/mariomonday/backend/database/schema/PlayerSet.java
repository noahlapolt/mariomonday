package mariomonday.backend.database.schema;

import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * The unit that participates in the set. This can be multiple players or just one.
 */
@Value
@Builder
public class PlayerSet {

  /**
   * Id of this player set
   */
  String id;

  /**
   * The players in this playerSet. Attempting to make this unique so every team/person combo only appears a single time
   */
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

  /**
   * Get the total ELO for this team
   * @param gameType The game type to get ELO for
   * @return The ELO of this team
   */
  public int getElo(GameType gameType) {
    return players.stream().reduce(0, (eloSoFar, player) -> player.getEloMap().get(gameType) + eloSoFar, Integer::sum);
  }
}
