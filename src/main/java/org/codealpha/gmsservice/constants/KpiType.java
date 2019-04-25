package org.codealpha.gmsservice.constants;

public enum KpiType {

  QUANTITATIVE("QUANTITATIVE"),
  QUALITATIVE("QUALITATIVE"),
  DOCUMENT("DOCUMENT");


  private String val;

  KpiType(String value) {
    this.val=value;
  }
}
