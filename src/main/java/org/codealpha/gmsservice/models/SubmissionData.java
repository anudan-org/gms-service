package org.codealpha.gmsservice.models;

import java.util.List;

public class SubmissionData {

  private Long id;
  private List<String> notes;
  private List<KpiSubmissionData> kpiSubmissionData;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<String> getNotes() {
    return notes;
  }

  public void setNotes(List<String> notes) {
    this.notes = notes;
  }

  public List<KpiSubmissionData> getKpiSubmissionData() {
    return kpiSubmissionData;
  }

  public void setKpiSubmissionData(
      List<KpiSubmissionData> kpiSubmissionData) {
    this.kpiSubmissionData = kpiSubmissionData;
  }
}
