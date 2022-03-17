package org.codealpha.gmsservice.models;

/**
 * @author Developer code-alpha.org
 **/
public class APIError {

	private int status;
	private String message;

	public APIError(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
