package org.codealpha.gmsservice.security;

import org.springframework.http.HttpStatus;

public class Error {

  private HttpStatus httpStatus;
  private String message;
  private String messageTitle;

  public Error(HttpStatus httpStatus, String title, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
    this.messageTitle = title;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessageTitle() {
    return messageTitle;
  }

  public void setMessageTitle(String messageTitle) {
    this.messageTitle = messageTitle;
  }
}
