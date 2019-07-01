package org.codealpha.gmsservice.constants;

public enum KpiReportingType {

  QUANTITATIVE("QUANTITATIVE"),
  QUALITATIVE("QUALITATIVE"),
  DOCUMENT("DOCUMENT");


  private String val;

  KpiReportingType(String value) {
    this.val=value;
  }
}
