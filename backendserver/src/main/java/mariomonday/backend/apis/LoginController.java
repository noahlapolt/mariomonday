package mariomonday.backend.apis;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mariomonday.backend.apis.schema.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * API for logging in. This is mostly handled by Spring.
 * We just authenticate the user by verifying the username and password exist in our store,
 * then we save the session in our session repository.
 * Spring will then send a JSESSIONID cookie in the response,
 * which is tied to the authenticated session and should be used for all requests that require auth
 */
@RestController
public class LoginController {

    /**
     * Used to verify the given credentials are correct
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Session repo to update on successful login
     */
    @Autowired
    private HttpSessionSecurityContextRepository httpSessionSecurityContextRepository;

    /**
     * Perform the login
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest,
        HttpServletRequest request, HttpServletResponse response) {
        // Authenticate user
        Authentication authenticationRequest =
            UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authenticationResponse =
            this.authenticationManager.authenticate(authenticationRequest);

        // Store authenticated user in "Sessions" table to persist authentication
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationResponse);
        httpSessionSecurityContextRepository.saveContext(context, request, response);

        return null;
    }
}
