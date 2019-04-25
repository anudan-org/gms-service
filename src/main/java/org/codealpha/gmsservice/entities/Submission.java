package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import org.springframework.core.annotation.Order;

@Entity(name = "submissions")
public class Submission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private Grant grant;

  @Column
  private String title;

  @Column
  @OrderBy("ASC")
  private Date submitBy;
  @Column
  protected Date submittedOn;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private WorkflowStatus submissionStatus;

  @OneToMany(mappedBy = "submission")
  private List<GrantQuantitativeKpiData> quantitiaveKpisubmissions;

  @OneToMany(mappedBy = "submission")
  private List<GrantQualitativeKpiData> qualitativeKpiSubmissions;

  @OneToMany(mappedBy = "submission")
  private List<GrantDocumentKpiData> documentKpiSubmissions;

  @Column
  private Date createdAt;
  @Column
  private String createdBy;
  @Column
  private Date updatedAt;
  @Column
  private String updatedBy;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Grant getGrant() {
    return grant;
  }

  public void setGrant(Grant grant) {
    this.grant = grant;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getSubmitBy() {
    return submitBy;
  }

  public void setSubmitBy(Date submitBy) {
    this.submitBy = submitBy;
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

  public void setSubmissionStatus(WorkflowStatus submissionStatus) {
    this.submissionStatus = submissionStatus;
  }

  public List<GrantQuantitativeKpiData> getQuantitiaveKpisubmissions() {
    return quantitiaveKpisubmissions;
  }

  public void setQuantitiaveKpisubmissions(
      List<GrantQuantitativeKpiData> submissions) {
    this.quantitiaveKpisubmissions = submissions;
  }

  public List<GrantQualitativeKpiData> getQualitativeKpiSubmissions() {
    return qualitativeKpiSubmissions;
  }

  public void setQualitativeKpiSubmissions(
      List<GrantQualitativeKpiData> qualitativeKpiSubmissions) {
    this.qualitativeKpiSubmissions = qualitativeKpiSubmissions;
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

  public List<GrantDocumentKpiData> getDocumentKpiSubmissions() {
    return documentKpiSubmissions;
  }

  public void setDocumentKpiSubmissions(
      List<GrantDocumentKpiData> documentKpiSubmissions) {
    this.documentKpiSubmissions = documentKpiSubmissions;
  }
}
