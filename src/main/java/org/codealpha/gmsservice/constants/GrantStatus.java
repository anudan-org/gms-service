package org.codealpha.gmsservice.constants;

public enum GrantStatus {

  DRAFT("DRAFT"),
  ONGOING("ONGOING"),
  CLOSED("CLOSED");

  private String val;

  GrantStatus(String value) {
    this.val=value;
  }
}
