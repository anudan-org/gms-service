package org.codealpha.gmsservice.models;

import java.util.List;

public class KpiSubmissionData {

  private Long submissionId;
  private Long kpiDataId;
  private String type;
  private String value;
  private Long toStatusId;
  private String fileName;
  private String fileType;
  private String[] notes;
  private List<UploadFile> files;

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

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String[] getNotes() {
    return notes;
  }

  public void setNotes(String[] notes) {
    this.notes = notes;
  }

  public List<UploadFile> getFiles() {
    return files;
  }

  public void setFiles(List<UploadFile> files) {
    this.files = files;
  }
}
