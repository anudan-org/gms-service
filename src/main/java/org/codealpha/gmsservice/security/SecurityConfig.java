package org.codealpha.gmsservice.security;

import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.transaction.Transactional;

@Configuration
@Transactional
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthProvider authProvider;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private OrganizationRepository organizationRepository;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().authorizeRequests().antMatchers("/public/images/**/logo").permitAll()
        .antMatchers("/public/tenant/**").permitAll().antMatchers("/users/").permitAll()

        .and().authorizeRequests().anyRequest().authenticated().and()
        .addFilterBefore(
            new JWTLoginFilter("/authenticate", authenticationManager(), userRepository, organizationRepository),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new ExceptionHandlingFilter(), JWTLoginFilter.class)
        .addFilterAfter(new JWTAuthenticationFilter(userRepository, organizationRepository),
            UsernamePasswordAuthenticationFilter.class);

  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/public/release", "/users/check", "/users/forgot/**", "/users/set-password",
        "/user/**/grant/resolve", "/users/", "/public/**", "/v2/api-docs", "/public/grants/**/file/**",
        "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authProvider);
  }

}
