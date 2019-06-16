package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class GrantQuantitativeKpiData extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
  private Long id;
  @Column
  private Integer goal;
  @Column(nullable = true)
  private Integer actuals;
  @Column(nullable = true)
  private String note;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(referencedColumnName = "id")
  @JsonProperty(access = Access.WRITE_ONLY)
  @JsonBackReference
  private Submission submission;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private GrantKpi grantKpi;

  @Column
  private Boolean toReport;


  @OneToMany(mappedBy = "kpiData", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<QuantitativeKpiNotes> notesHistory;

  @OneToMany(mappedBy = "quantKpiData", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  List<QuantKpiDataDocument> submissionDocs;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public int getGoal() {
    return goal;
  }

  public void setGoal(int goal) {
    this.goal = goal;
  }

  public void setGoal(Integer goal) {
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

  public Integer getActuals() {
    return actuals;
  }

  public void setActuals(Integer actuals) {
    this.actuals = actuals;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public List<QuantitativeKpiNotes> getNotesHistory() {
    return notesHistory;
  }

  public void setNotesHistory(List<QuantitativeKpiNotes> notes) {
    this.notesHistory = notes;
  }

  public List<QuantKpiDataDocument> getSubmissionDocs() {
    return submissionDocs;
  }

  public void setSubmissionDocs(
      List<QuantKpiDataDocument> submissionDocs) {
    this.submissionDocs = submissionDocs;
  }

  public Boolean getToReport() {
    return toReport;
  }

  public void setToReport(Boolean toReport) {
    this.toReport = toReport;
  }
}
