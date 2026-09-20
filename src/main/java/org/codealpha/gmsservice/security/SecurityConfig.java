package org.codealpha.gmsservice.security;

import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final AuthProvider authProvider;
  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;

  public SecurityConfig(AuthProvider authProvider,
                        UserRepository userRepository,
                        OrganizationRepository organizationRepository) {
    this.authProvider = authProvider;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    // Equivalent to auth.authenticationProvider(authProvider);
    return new ProviderManager(authProvider);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    AuthenticationManager authManager = authenticationManager();

    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/release").permitAll()
            .requestMatchers("/users/check").permitAll()
            .requestMatchers("/users/forgot/**").permitAll()
            .requestMatchers("/users/set-password").permitAll()
            .requestMatchers("/user/*/grant/resolve").permitAll()
            .requestMatchers("/public/images/*/logo").permitAll()
            .requestMatchers("/public/tenant/**").permitAll()
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/v2/api-docs").permitAll()
            .requestMatchers("/public/grants/*/file/**").permitAll()
            .requestMatchers("/configuration/ui").permitAll()
            .requestMatchers("/swagger-resources/**").permitAll()
            .requestMatchers("/configuration/**").permitAll()
            .requestMatchers("/swagger-ui.html").permitAll()
            .requestMatchers("/users/").permitAll()
            .anyRequest().authenticated()
        )

        // Filters (same order as legacy)
        .addFilterBefore(
            new JWTLoginFilter("/authenticate", authManager, userRepository, organizationRepository),
            UsernamePasswordAuthenticationFilter.class
        )
        .addFilterBefore(new ExceptionHandlingFilter(), JWTLoginFilter.class)
        .addFilterAfter(
            new JWTAuthenticationFilter(userRepository, organizationRepository),
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(
        "/webjars/**"
    );
  }
}
