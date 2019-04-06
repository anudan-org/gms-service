package org.codealpha.gmsservice.constants;

public enum GrantSubStatus {

  KPI_SUBMISSION_PENDING("KPI_SUBMISSION_PENDING"),
  KPI_SUBMITTED("KPI_SUBMITTED"),
  KPI_APPROVED("KPI_APPROVED"),
  MODIFICATION_REQUESTED("MODIFICATION_REQUESTED"),
  MODIFICATION_SUBMITTED("MODIFICATION_SUBMITTED");

  private String val;

  GrantSubStatus(String value) {
    this.val=value;
  }
}
