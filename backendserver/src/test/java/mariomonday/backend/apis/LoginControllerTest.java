package mariomonday.backend.apis;

import mariomonday.backend.apis.schema.LoginRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class LoginControllerTest {

  @Autowired
  LoginController loginController;

  @Autowired
  InMemoryUserDetailsManager userDetailsManager;

  @Autowired
  PasswordEncoder passwordEncoder;

  @BeforeAll
  public void setUp() {
    userDetailsManager.createUser(
      User.withUsername("freakyUser").password(passwordEncoder.encode("I like feet stuff")).roles("USER").build()
    );
  }

  @Test
  public void testLogin_shouldSucceed_whenUsernameAndPasswordCorrect() {
    // Act
    // No exception means we were able to log in!
    loginController.login(
      LoginRequest.builder().username("freakyUser").password("I like feet stuff").build(),
      new MockHttpServletRequest(),
      new MockHttpServletResponse()
    );
  }

  @Test
  public void testLogin_shouldFail_whenPasswordWrong() {
    // Act
    Assertions.assertThrows(BadCredentialsException.class, () ->
      loginController.login(
        LoginRequest.builder().username("freakyUser").password("Fuck you!").build(),
        new MockHttpServletRequest(),
        new MockHttpServletResponse()
      )
    );
  }

  @Test
  public void testLogin_shouldFail_whenUserNotExist() {
    // Act
    Assertions.assertThrows(BadCredentialsException.class, () ->
      loginController.login(
        LoginRequest.builder().username("normalUser").password("Fuck you!").build(),
        new MockHttpServletRequest(),
        new MockHttpServletResponse()
      )
    );
  }
}
