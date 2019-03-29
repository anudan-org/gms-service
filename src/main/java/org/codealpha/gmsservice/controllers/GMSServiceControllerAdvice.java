package org.codealpha.gmsservice.controllers;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.servlet.http.HttpServletRequest;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.models.APIError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestControllerAdvice
public class GMSServiceControllerAdvice {

	@ExceptionHandler(value = ResourceNotFoundException.class)
	@ResponseStatus(code = NOT_FOUND)
	public ResponseEntity<APIError> internalServerHandler(ResourceNotFoundException e,
			HttpServletRequest request) {
		return new ResponseEntity<>(new APIError(NOT_FOUND.value(), e.getMessage()),
				NOT_FOUND);
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(code = INTERNAL_SERVER_ERROR)
	public ResponseEntity<APIError> internalServerHandler(Exception e, HttpServletRequest request) {
		e.printStackTrace();
		return new ResponseEntity<>(new APIError(INTERNAL_SERVER_ERROR.value(), e.getMessage()),
				INTERNAL_SERVER_ERROR);
	}

}
