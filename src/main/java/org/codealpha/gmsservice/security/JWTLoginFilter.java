package org.codealpha.gmsservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

  @Autowired
  private UserRepository userRepository;

  public JWTLoginFilter(String url, AuthenticationManager authManager,
      UserRepository userRepository) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(authManager);
    this.userRepository = userRepository;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    AccountCredentials creds =
        new ObjectMapper().readValue(request.getInputStream(), AccountCredentials.class);
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new GrantedAuthority() {

      @Override
      public String getAuthority() {
        // TODO Auto-generated method stub
        return creds.getRole();
      }
    });
    return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
        creds.getUsername(), creds.getPassword(), authorities));
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain, Authentication auth) throws IOException, ServletException {
    User user = userRepository.findByEmailId(auth.getName());
    Long userId = user.getId();


    ObjectMapper mapper = new ObjectMapper();
    String userJSON = mapper.writeValueAsString(user);

    JsonNode userNode = mapper.readTree(userJSON);


    TokenAuthenticationService.addAuthentication(res, auth.getName(),userNode);

  }
}
