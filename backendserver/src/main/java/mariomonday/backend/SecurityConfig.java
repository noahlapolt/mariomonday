package mariomonday.backend;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 * Configuration of Spring Security stuff
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${admincredentials.username}")
    private String adminUsername;

    @Value("${admincredentials.password}")
    private String adminPassword;


    /**
     * Global storage of current user sessions. Used to keep track of who is logged in.
     * Cleared upon server restart.
     */
    @Bean
    HttpSessionSecurityContextRepository httpSessionSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * Set Admin username/password as only user present
     * @return User Manager for Spring Security to use
     */
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(User.withUsername(adminUsername)
            .password(passwordEncoder.encode(adminPassword)).roles("USER").build());
    }

    /**
     * Create authentication manager which uses the in memory user details manager
     * @param userDetailsService Bean storing all available users/passwords
     * @param passwordEncoder Password encoder
     * @return Authentication Manager
     */
    @Bean
    public AuthenticationManager authenticationManager(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    /**
     * Create bean of default password encoder to be used globally
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Allow forward and error responses to make it out,
     * and allow all get requests and login requests.
     * All other requests require authentication.
     * @param http HttpSecurity object to modify with authorization information
     * @return Security Filter chain
     * @throws Exception When we fail to build the HTTP authorizer thing (we won't)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO figure out CSRF, maybe enable it later. Its just annoying without any frontend
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((authorize) -> authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers(HttpMethod.GET).permitAll()
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}