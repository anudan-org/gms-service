package org.codealpha.gmsservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

@Order(1)
public class ExceptionHandlingFilter extends GenericFilterBean {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      chain.doFilter(request, response);
    } catch (RuntimeException e) {
      logger.error("[ExceptionHandlingFilter] Caught exception of type: {} with message: {}", 
          e.getClass().getSimpleName(), e.getMessage());

      Error error = null;
      switch (e.getClass().getSimpleName()) {
        case "TokenExpiredException":
          logger.warn("[ExceptionHandlingFilter] Returning 403 FORBIDDEN for TokenExpiredException");
          error = new Error(HttpStatus.FORBIDDEN, "Token Expired.", e.getMessage());
          break;
        case "InvalidCredentialsException":
          logger.warn("[ExceptionHandlingFilter] Returning 403 FORBIDDEN for InvalidCredentialsException: {}", e.getMessage());
          error = new Error(HttpStatus.FORBIDDEN, "Invalid Credentials", e.getMessage());
          break;
        case "InvalidTenantException":
          logger.warn("[ExceptionHandlingFilter] Returning 403 FORBIDDEN for InvalidTenantException");
          error = new Error(HttpStatus.FORBIDDEN, "Invalid Tenant", e.getMessage());
          break;
        case "BadCredentialsException":
          logger.warn("[ExceptionHandlingFilter] Returning 403 FORBIDDEN for BadCredentialsException");
          error = new Error(HttpStatus.FORBIDDEN, "Bad Credentials", e.getMessage());
          break;
        default:
          logger.error("[ExceptionHandlingFilter] Returning 500 INTERNAL_SERVER_ERROR for: {}", e.getClass().getSimpleName());
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
