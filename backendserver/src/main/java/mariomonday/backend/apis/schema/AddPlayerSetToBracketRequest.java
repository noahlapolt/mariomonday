package mariomonday.backend.apis.schema;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Request to add a player set/team to a bracket
 */
@Data
@Builder
public class AddPlayerSetToBracketRequest {

  /**
   * Game set to add the team to
   */
  private String gameSetId;

  /**
   * Team to add
   */
  private List<String> playerIds;

  /**
   * Team name. If the bracket is single player, this will be ignored.
   */
  private String teamName;
}
