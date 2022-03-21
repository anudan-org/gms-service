package org.codealpha.gmsservice.controllers;

import org.codealpha.gmsservice.exceptions.InvalidFileTypeException;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.models.APIError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.*;

/**
 * @author Developer code-alpha.org
 **/
@RestControllerAdvice
public class GMSServiceControllerAdvice {

  private static Logger logger = LoggerFactory.getLogger(GMSServiceControllerAdvice.class);

  @ExceptionHandler(value = ResourceNotFoundException.class)
  @ResponseStatus(code = NOT_FOUND)
  public ResponseEntity<APIError> internalServerHandler(ResourceNotFoundException e, HttpServletRequest request) {
    return new ResponseEntity<>(
        new APIError(NOT_FOUND.value(),
            "You are not authorized to access the requested information, or the requested information does not exist."),
        NOT_FOUND);
  }

  @ExceptionHandler(value = InvalidFileTypeException.class)
  @ResponseStatus(code = NOT_FOUND)
  public ResponseEntity<APIError> internalServerHandler(InvalidFileTypeException e, HttpServletRequest request) {
    return new ResponseEntity<>(new APIError(UNSUPPORTED_MEDIA_TYPE.value(), "This file type is not allowed."),
        UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(value = MaxUploadSizeExceededException.class)
  @ResponseStatus(code = EXPECTATION_FAILED)
  public ResponseEntity<APIError> internalServerHandler(MaxUploadSizeExceededException e, HttpServletRequest request) {
    return new ResponseEntity<>(new APIError(EXPECTATION_FAILED.value(), "Maximum file upload limit exceeded."),
        EXPECTATION_FAILED);
  }

  @ExceptionHandler(value = Exception.class)
  @ResponseStatus(code = INTERNAL_SERVER_ERROR)
  public ResponseEntity<APIError> internalServerHandler(Exception e, HttpServletRequest request) {
    logger.error(e.getMessage(), e);
    return new ResponseEntity<>(
        new APIError(INTERNAL_SERVER_ERROR.value(),
            "There was a problem carrying out the requested action. Please try again in some time."),
        INTERNAL_SERVER_ERROR);
  }

}
