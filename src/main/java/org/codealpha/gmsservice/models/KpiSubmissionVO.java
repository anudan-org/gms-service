package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowStatus;

import java.util.Date;
import java.util.List;

public class KpiSubmissionVO {

  private Long id;
  @JsonIgnore
  private GrantKpi grantKpi;
  private Date submitByDate;
  private Date submittedOn;
  private WorkflowStatus submissionStatus;
  private List<GrantQuantitativeKpiDataVO> grantQuantitativeKpiData;
  private List<GrantQualitativeKpiDataVO> grantQualitativeKpiData;
  private String statusName;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
  List<WorkFlowPermission> flowAuthority;
  private String title;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GrantKpi getGrantKpi() {
    return grantKpi;
  }

  public void setGrantKpi(GrantKpi grantKpi) {
    this.grantKpi = grantKpi;
  }

  public Date getSubmitByDate() {
    return submitByDate;
  }

  public void setSubmitByDate(Date submitByDate) {
    this.submitByDate = submitByDate;
  }

  public Date getSubmittedOn() {
    return submittedOn;
  }

  public void setSubmittedOn(Date submittedOn) {
    this.submittedOn = submittedOn;
  }

  public WorkflowStatus getSubmissionStatus() {
    return submissionStatus;
  }

  public void setSubmissionStatus(WorkflowStatus status) {
    this.submissionStatus = status;
  }

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public List<GrantQuantitativeKpiDataVO> getGrantQuantitativeKpiData() {
    return grantQuantitativeKpiData;
  }

  public void setGrantQuantitativeKpiData(
      List<GrantQuantitativeKpiDataVO> grantQuantitativeKpiData) {
    this.grantQuantitativeKpiData = grantQuantitativeKpiData;
  }

  public List<GrantQualitativeKpiDataVO> getGrantQualitativeKpiData() {
    return grantQualitativeKpiData;
  }

  public void setGrantQualitativeKpiData(
      List<GrantQualitativeKpiDataVO> grantQualitativeKpiData) {
    this.grantQualitativeKpiData = grantQualitativeKpiData;
  }

  public List<WorkFlowPermission> getFlowAuthority() {
    return flowAuthority;
  }

  public void setFlowAuthority(
      List<WorkFlowPermission> flowAuthority) {
    this.flowAuthority = flowAuthority;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
