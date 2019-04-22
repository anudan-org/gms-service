package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class KpiSubmission {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private GrantKpi grantKpi;

  @Column
  private String title;

  private Date submitByDate;

  private Date submittedOn;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private WorkflowStatus submissionStatus;

  @OneToMany(mappedBy = "kpiSubmission")
  private List<GrantQuantitativeKpiData> grantQuantitativeKpiData;

  @OneToMany(mappedBy = "kpiSubmission")
  private List<GrantQualitativeKpiData> grantQualitativeKpiData;


  private String statusName;

  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;



  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
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

  public List<GrantQuantitativeKpiData> getGrantQuantitativeKpiData() {
    return grantQuantitativeKpiData;
  }

  public void setGrantQuantitativeKpiData(
      List<GrantQuantitativeKpiData> grantQuantitativeKpiData) {
    this.grantQuantitativeKpiData = grantQuantitativeKpiData;
  }

  public List<GrantQualitativeKpiData> getGrantQualitativeKpiData() {
    return grantQualitativeKpiData;
  }

  public void setGrantQualitativeKpiData(
      List<GrantQualitativeKpiData> grantQualitativeKpiData) {
    this.grantQualitativeKpiData = grantQualitativeKpiData;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
