package org.codealpha.gmsservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codealpha.gmsservice.exceptions.TokenExpiredException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(1)
public class ExceptionHandlingFilter extends GenericFilterBean {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      chain.doFilter(request, response);
    } catch (RuntimeException e) {

      Error error = null;
      switch (e.getClass().getSimpleName()) {
        case "TokenExpiredException":
          error = new Error(HttpStatus.FORBIDDEN, "Token Expired.", e.getMessage());
          break;
        case "InvalidCredentialsException":
          error = new Error(HttpStatus.FORBIDDEN, "Invalid Credentials", e.getMessage());
          break;
        case "InvalidTenantException":
          error = new Error(HttpStatus.FORBIDDEN, "Invalid Tenant", e.getMessage());
          break;
        case "BadCredentialsException":
          error = new Error(HttpStatus.FORBIDDEN, "Invalid Credentials", e.getMessage());
          break;
        default:
          error = new Error(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error",
              "There was a problem processing the request");
      }

      ObjectMapper mapper = new ObjectMapper();
      String errorMessage = mapper.writeValueAsString(error);

      HttpServletResponse resp = (HttpServletResponse) response;
      resp.setStatus(error.getHttpStatus().value());
      resp.setContentType("application/json");
      StreamUtils.copy(errorMessage, Charset.defaultCharset(), resp.getOutputStream());
    }
  }
}
