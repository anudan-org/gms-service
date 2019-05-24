package org.codealpha.gmsservice.constants;

public enum Frequency {
  ONCE("ONCE"),
  MONTHLY("MONTHLY"),
  QUARTERLY("QUARTERLY"),
  YEARLY("YEARLY"),
  HALF_YEARLY("HALF-YEARLY");

  private String val;
  Frequency(String value) {
    this.val=value;
  }
}
