package org.codealpha.gmsservice.security;

import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class JWTAuthenticationFilter extends GenericFilterBean {

  private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
  private UserRepository userRepository;
  private OrganizationRepository organizationRepository;

  public JWTAuthenticationFilter(UserRepository userRepo, OrganizationRepository organizationRepo) {
    this.userRepository = userRepo;
    this.organizationRepository = organizationRepo;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException, RuntimeException {
    String pathWithinApplication = new UrlPathHelper().getPathWithinApplication((HttpServletRequest) request);
    logger.debug("[JWTAuthenticationFilter] Processing request path: {}", pathWithinApplication);
    
    if (pathWithinApplication.equalsIgnoreCase("/favicon.ico")) {
      logger.debug("[JWTAuthenticationFilter] Bypassing token check for favicon");
      filterChain.doFilter(request, response);
    } else if (pathWithinApplication.startsWith("/public/")) {
      logger.debug("[JWTAuthenticationFilter] Bypassing token check for public endpoint");
      filterChain.doFilter(request, response);
    } else {
      logger.debug("[JWTAuthenticationFilter] Checking token for path: {}", pathWithinApplication);
      Authentication authentication = null;

      authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) request);
      logger.debug("[JWTAuthenticationFilter] Token validation successful for path: {}", pathWithinApplication);
      if (authentication != null) {
        String[] principalTokens = authentication.getPrincipal().toString().split("\\^");
        User user = null;
        if(!"ANUDAN".equalsIgnoreCase(principalTokens[1])){
          user = userRepository.findByEmailAndOrg(principalTokens[0],
                  organizationRepository.findByCode(principalTokens[1]).getId());

        }else if("ANUDAN".equalsIgnoreCase(principalTokens[1])){
          List<User> users = userRepository.findByEmailId(principalTokens[0]);
          for (User user1 : users) {
            if(user1.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE") || user1.getOrganization().getOrganizationType().equalsIgnoreCase("PLATFORM")){
              user = user1;
              break;
            }
          }
        }
        if (user==null || !user.isActive() || user.isDeleted()) {
          throw new BadCredentialsException("Inactive user");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
      filterChain.doFilter(request, response);
    }
  }

}
