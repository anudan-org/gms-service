package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(name = "submissions")
public class Submission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(referencedColumnName = "id")
  @JsonProperty(access = Access.WRITE_ONLY)
  @JsonBackReference
  private Grant grant;

  @Column
  private String title;

  @Column
  @OrderBy("ASC")
  private Date submitBy;
  @Transient
  private String submitDateStr;
  @Column
  protected Date submittedOn;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private WorkflowStatus submissionStatus;

  @OneToMany(mappedBy = "submission",cascade = CascadeType.ALL)
  @OrderBy("id ASC")
  @JsonManagedReference
  private List<GrantQuantitativeKpiData> quantitiaveKpisubmissions;

  @OneToMany(mappedBy = "submission",cascade = CascadeType.ALL)
  @OrderBy("id ASC")
  @JsonManagedReference
  private List<GrantQualitativeKpiData> qualitativeKpiSubmissions;

  @OneToMany(mappedBy = "submission",cascade = CascadeType.ALL)
  @OrderBy("id ASC")
  @JsonManagedReference
  private List<GrantDocumentKpiData> documentKpiSubmissions;

  @OneToMany (mappedBy = "submission")
  @OrderBy("id DESC")
  private List<SubmissionNote> submissionNotes;

  @Transient
  private WorkflowActionPermission actionAuthorities;
  @Transient
  private List<WorkFlowPermission> flowAuthorities;
  @Transient
  private boolean openForReporting;

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

  public List<SubmissionNote> getSubmissionNotes() {
    return submissionNotes;
  }

  public void setSubmissionNotes(
      List<SubmissionNote> submissionNotes) {
    this.submissionNotes = submissionNotes;
  }

  public String getSubmitDateStr() {
    return new SimpleDateFormat("yyyy-MM-dd").format(submitBy);
  }

  public void setSubmitDateStr(String submitDateStr) {

    this.submitDateStr = submitDateStr;
    submitBy = DateTime.parse(submitDateStr).toDate();
  }

  public WorkflowActionPermission getActionAuthorities() {
    return actionAuthorities;
  }

  public void setActionAuthorities(
      WorkflowActionPermission actionAuthorities) {
    this.actionAuthorities = actionAuthorities;
  }

  public List<WorkFlowPermission> getFlowAuthorities() {
    return flowAuthorities;
  }

  public void setFlowAuthorities(
      List<WorkFlowPermission> flowAuthorities) {
    this.flowAuthorities = flowAuthorities;
  }

  public boolean isOpenForReporting() {
    return openForReporting;
  }

  public void setOpenForReporting(boolean openForReporting) {
    this.openForReporting = openForReporting;
  }
}
