package org.codealpha.gmsservice.security;

import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JWTAuthenticationFilter extends GenericFilterBean {

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
    if (pathWithinApplication.equalsIgnoreCase("/favicon.ico")) {
      filterChain.doFilter(request, response);
    } else if (pathWithinApplication.contains("/public/images/")) {
      filterChain.doFilter(request, response);
    } else {
      Authentication authentication = null;

      authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) request,
          (HttpServletResponse) response);

      if (authentication != null) {
        String[] principalTokens = authentication.getPrincipal().toString().split("\\^");
        User user = null;
        if(!"ANUDAN".equalsIgnoreCase(principalTokens[1])){
          user = userRepository.findByEmailIdAndOrganization(principalTokens[0],
                  organizationRepository.findByCode(principalTokens[1]));
        }else if("ANUDAN".equalsIgnoreCase(principalTokens[1])){
          List<User> users = userRepository.findByEmailId(principalTokens[0]);
          for (User user1 : users) {
            if(user1.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE") || user1.getOrganization().getOrganizationType().equalsIgnoreCase("PLATFORM")){
              user = user1;
              break;
            }
          }
        }

        if (!user.isActive() || user.isDeleted()) {
          throw new BadCredentialsException("Inactive user");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
      filterChain.doFilter(request, response);
    }
  }

}
