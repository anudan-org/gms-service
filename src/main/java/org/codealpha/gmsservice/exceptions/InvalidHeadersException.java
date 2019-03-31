package org.codealpha.gmsservice.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidHeadersException extends RuntimeException {

  private static Logger logger = LoggerFactory.getLogger(UserNotFoundException.class);
  public InvalidHeadersException(String invalid_header) {

    logger.error(invalid_header,this.getCause());
  }

}
