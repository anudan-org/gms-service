package org.codealpha.gmsservice.constants;

public enum Frequency {
  MONTHLY("MONTHLY"),
  QUARTERLY("QUARTERLY"),
  YEARLY("YEARLY");

  private String val;
  Frequency(String value) {
    this.val=value;
  }
}
