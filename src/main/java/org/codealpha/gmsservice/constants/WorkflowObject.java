package org.codealpha.gmsservice.constants;

public enum WorkflowObject {

  GRANT("GRANT"),
  APPLICATION("APPLICATION"),
  KPI("KPI");

  private String val;

  WorkflowObject(String value) {
    this.val=value;
  }
}
