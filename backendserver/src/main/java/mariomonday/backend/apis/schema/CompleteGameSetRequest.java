package mariomonday.backend.apis.schema;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Request to complete a set.
 */
@Data
@Builder
public class CompleteGameSetRequest {

  /**
   * Was this game set forfeit?
   */
  private boolean forfeit;

  /**
   * The games within this set, with each game represented as an ordered list of team IDs,
   * with the team at index 0 getting first, etc.
   */
  private List<List<String>> games;

  /**
   * The IDs of the teams that won the set.
   */
  private List<String> winners;
}
