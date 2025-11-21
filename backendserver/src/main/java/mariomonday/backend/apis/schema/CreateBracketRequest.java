package mariomonday.backend.apis.schema;


import java.util.List;
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
   * List of teams, where each team is represented by a list of player IDs
   */
  private List<List<String>> teams;

  /**
   * The game type of the bracket
   */
  private GameType gameType;
}
