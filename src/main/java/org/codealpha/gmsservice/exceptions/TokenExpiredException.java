package org.codealpha.gmsservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class TokenExpiredException extends RuntimeException {

  public TokenExpiredException(String message) {
    super(message);
  }

}
