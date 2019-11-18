package org.codealpha.gmsservice.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.codealpha.gmsservice.exceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

public class JWTAuthenticationFilter extends GenericFilterBean {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException, RuntimeException {
    if(new UrlPathHelper().getPathWithinApplication((HttpServletRequest) request).equalsIgnoreCase("/favicon.ico")){
      filterChain.doFilter(request, response);
    }else {
      Authentication authentication = null;

        authentication =
            TokenAuthenticationService
                .getAuthentication((HttpServletRequest) request, (HttpServletResponse) response);


        if(authentication!=null) {
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      filterChain.doFilter(request, response);
    }
  }
}
