package mariomonday.backend.apis.schema;

import lombok.Data;

/**
 * Request to log in
 */
@Data
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
