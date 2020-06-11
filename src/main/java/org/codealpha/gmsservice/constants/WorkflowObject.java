package org.codealpha.gmsservice.constants;

public enum WorkflowObject {

  GRANT("GRANT"),
  APPLICATION("APPLICATION"),
  SUBMISSION("SUBMISSION"),
  REPORT("REPORT"),
  DISBURSEMENT("DISBURSEMENT");

  private String val;

  WorkflowObject(String value) {
    this.val=value;
  }
}
