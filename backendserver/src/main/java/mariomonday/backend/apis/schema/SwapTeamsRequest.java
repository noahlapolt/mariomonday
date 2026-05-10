package mariomonday.backend.apis.schema;

import lombok.Builder;
import lombok.Data;

/**
 * Request to swap two players/teams within a bracket
 */
@Data
@Builder
public class SwapTeamsRequest {

  private String firstTeamId;

  private String secondTeamId;

  private String bracketId;
}
