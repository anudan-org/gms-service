package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Entity
public class GrantDocumentKpiData extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String goal;
  @Column(nullable = true)
  private String actuals;
  @Column(nullable = true)
  private String type;
  @Column
  private Boolean toReport;

  @Column(nullable = true)
  private String note;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private Submission submission;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private GrantKpi grantKpi;

  @OneToMany(mappedBy = "docKpiData")
  List<DocKpiDataDocument> submissionDocs;

  @OneToMany(mappedBy = "kpiData", fetch = FetchType.LAZY)
  private List<DocumentKpiNotes> notesHistory;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public GrantKpi getGrantKpi() {
    return grantKpi;
  }

  public void setGrantKpi(GrantKpi grantKpi) {
    this.grantKpi = grantKpi;
  }

  public String getActuals() {
    return actuals;
  }

  public void setActuals(String actuals) {
    this.actuals = actuals;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public List<DocKpiDataDocument> getSubmissionDocs() {
    return submissionDocs;
  }

  public void setSubmissionDocs(
      List<DocKpiDataDocument> submissionDocs) {
    this.submissionDocs = submissionDocs;
  }

  public List<DocumentKpiNotes> getNotesHistory() {
    return notesHistory;
  }

  public void setNotesHistory(
      List<DocumentKpiNotes> notesHistory) {
    this.notesHistory = notesHistory;
  }

  public Boolean getToReport() {
    return toReport;
  }

  public void setToReport(Boolean toReport) {
    this.toReport = toReport;
  }
}
