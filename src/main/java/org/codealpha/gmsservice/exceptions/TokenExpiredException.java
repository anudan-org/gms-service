package org.codealpha.gmsservice.exceptions;

public class TokenExpiredException extends RuntimeException {

  public TokenExpiredException(String message) {
    super(message);
  }

}
