package mariomonday.backend.apis.schema;


import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import mariomonday.backend.database.schema.GameType;

/**
 * Request to create a bracket
 */
@Data
@Builder
public class CreateBracketRequest {

  /**
   * Map from team name to players on the team,
   * where each team is represented by a list of player IDs
   */
  private Map<String, List<String>> teams;

  /**
   * The game type of the bracket
   */
  private GameType gameType;
}
