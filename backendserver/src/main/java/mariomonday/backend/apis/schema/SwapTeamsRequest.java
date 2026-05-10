package mariomonday.backend.apis.schema;

import lombok.Builder;
import lombok.Data;

/**
 * Request to swap two players/teams within a bracket
 */
@Data
@Builder
public class SwapTeamsRequest {

  /**
   * First team to swap
   */
  private String firstTeamId;

  /**
   * Second team to swap
   */
  private String secondTeamId;

  /**
   * Bracket they are swapping in
   */
  private String bracketId;
}
