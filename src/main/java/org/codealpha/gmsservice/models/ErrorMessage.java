package org.codealpha.gmsservice.models;

public class ErrorMessage {

  private boolean success;
  private String message;

  public ErrorMessage(boolean b, String s) {
    this.success = b;
    this.message = s;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
