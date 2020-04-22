package org.codealpha.gmsservice.security;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

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

        .and()
            .authorizeRequests().anyRequest().authenticated().and()
        .addFilterBefore(
            new JWTLoginFilter("/authenticate", authenticationManager(), userRepository, organizationRepository),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new ExceptionHandlingFilter(),JWTLoginFilter.class)
        .addFilterAfter(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;

  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/public/release","/users/check","/user/**/grant/resolve","/users/","/public/**","/v2/api-docs","/public/grants/**/file/**", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authProvider);
  }



}
