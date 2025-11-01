package mariomonday.backend.apis.schema;

import lombok.Builder;
import lombok.Data;

/**
 * Request to log in
 */
@Data
@Builder
public class LoginRequest {

  /**
   * Username to log in with
   */
  private String username;

  /**
   * Password to log in with
   */
  private String password;
}
