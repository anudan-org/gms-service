package org.codealpha.gmsservice.constants;

public enum KPIStatus {

  NOT_SUBMITTED("NOT SUBMITTED"),
  SUBMITTED("SUBMITTED"),
  MODIFICATION_REQUESTED("MODIFICATION REQUESTED"),
  APPROVED("APPROVED");

  private String val;

  KPIStatus(String value) {
    this.val=value;
  }

}
