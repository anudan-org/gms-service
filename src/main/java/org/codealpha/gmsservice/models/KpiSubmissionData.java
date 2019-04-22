package org.codealpha.gmsservice.models;

public class KpiSubmissionData {

  private Long submissionId;
  private Long kpiDataId;
  private String type;
  private String value;
  private Long toStatusId;

  public Long getSubmissionId() {
    return submissionId;
  }

  public void setSubmissionId(Long submissionId) {
    this.submissionId = submissionId;
  }

  public Long getKpiDataId() {
    return kpiDataId;
  }

  public void setKpiDataId(Long kpiDataId) {
    this.kpiDataId = kpiDataId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Long getToStatusId() {
    return toStatusId;
  }

  public void setToStatusId(Long toStatusId) {
    this.toStatusId = toStatusId;
  }
}
