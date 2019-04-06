package org.codealpha.gmsservice.constants;

public enum KPIStatus {

  NOT_SUBMITTED("NOT_SUBMITTED"),
  SUBMITTED("SUBMITTED"),
  MODIFICATION_REQUESTED("MODIFICATION_REQUESTED"),
  APPROVED("APPROVED");

  private String val;

  KPIStatus(String value) {
    this.val=value;
  }

}
