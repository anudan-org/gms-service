package org.codealpha.gmsservice.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidFileTypeException extends RuntimeException {

    private static Logger logger = LoggerFactory.getLogger(UserNotFoundException.class);
    public InvalidFileTypeException(String invalid_header) {

        super(invalid_header);
    }
}
