package org.codealpha.gmsservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.InvalidCredentialsException;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

  @Autowired
  private UserRepository userRepository;

  private final OrganizationRepository organizationRepository;

  private AccountCredentials creds;

public JWTLoginFilter(String url,
                      AuthenticationManager authManager,
                      UserRepository userRepository,
                      OrganizationRepository organizationRepository) {

    super(pathMatcher(url));                 // <-- explicit super call (fixes your error)
    setAuthenticationManager(authManager);
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
}

// Non-deprecated matcher using AntPathMatcher (Spring core)
private static RequestMatcher pathMatcher(String pattern) {
    AntPathMatcher matcher = new AntPathMatcher();
    return request -> {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            path = path + pathInfo;
        }
        return matcher.match(pattern, path);
    };
}

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                             HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    creds = new ObjectMapper().readValue(request.getInputStream(), AccountCredentials.class);
    
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add((GrantedAuthority) () -> creds.getProvider());

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword(), authorities);

    Map<String, String> detailsMap = new HashMap<>();
    detailsMap.put("TOKEN", request.getHeader("X-TENANT-CODE"));
    detailsMap.put("CAPTCHA", creds.getRecaptchaToken());
    authToken.setDetails(detailsMap);
    
    return getAuthenticationManager().authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
                                          HttpServletResponse res,
                                          FilterChain chain,
                                          Authentication auth) throws IOException, ServletException {

    User user = new User();

    // Safe extraction from auth.getDetails() (avoid unchecked cast)
    String token = null;
    Object detailsObj = auth.getDetails();
    if (detailsObj instanceof Map<?, ?> detailsMap) {
      Object tokenObj = detailsMap.get("TOKEN");
      if (tokenObj != null) {
        token = tokenObj.toString();
      }
    }

    if (token == null) {
      throw new InvalidCredentialsException("Tenant token missing");
    }
     if (!"ANUDAN".equalsIgnoreCase(token)) {
    //   user = userRepository.findByEmailIdAndOrganization(
    //       auth.getName(),
    //       organizationRepository.findByCode(token)
    //   );
    
    
      user = userRepository.findByEmailAndOrg(
          auth.getName(),
          organizationRepository.findByCode(token).getId()
      );

    } else {
      List<User> users = userRepository.findByEmailId(auth.getName());
      for (User user1 : users) {
        if (user1.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")
            || user1.getOrganization().getOrganizationType().equalsIgnoreCase("PLATFORM")) {
          user = user1;
          break;
        }
      }
    }

    ObjectMapper mapper = new ObjectMapper();
    String userJSON = mapper.writeValueAsString(user);
    JsonNode userNode = mapper.readTree(userJSON);

    String tenant = req.getHeader("X-TENANT-CODE");

    TokenAuthenticationService.addAuthentication(res, auth.getName(), userNode, tenant);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException failed)
      throws IOException, ServletException {
    throw new InvalidCredentialsException(failed.getMessage());
  }
}
