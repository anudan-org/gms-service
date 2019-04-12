package org.codealpha.gmsservice.models;

public class KpiSubmissionData {

  private Long id;
  private String type;
  private String value;
  private Long toStatusId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
