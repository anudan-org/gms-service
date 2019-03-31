package org.codealpha.gmsservice.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserNotFoundException extends RuntimeException {

  private static Logger logger = LoggerFactory.getLogger(UserNotFoundException.class);
  public UserNotFoundException(String username_not_found) {

    logger.error(username_not_found,this.getCause());
  }
}
