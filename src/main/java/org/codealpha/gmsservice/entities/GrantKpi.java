package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.codealpha.gmsservice.constants.Frequency;
import org.codealpha.gmsservice.constants.KPIStatus;
import org.codealpha.gmsservice.constants.KpiType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "grant_kpis")
public class GrantKpi {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String title;
  @Column
  private String description;
  @Column(name = "is_scheduled")
  private boolean scheduled;
  @Column
  private int periodicity;
  @Column(name = "periodicity_unit")
  @Enumerated(EnumType.STRING)
  private Frequency frequency;
  @Column
  @Enumerated(EnumType.STRING)
  private KPIStatus status;
  @Column
  @Enumerated(EnumType.STRING)
  private KpiType kpiType;
  @Column
  private Date createdAt;
  @Column
  private String createdBy;
  @Column(nullable = true)
  private Date updatedAt;
  @Column(nullable = true)
  private String updatedBy;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private Grant grant;

  @OneToMany(mappedBy = "grantKpi")
  @OrderBy("submitByDate asc")
  List<KpiSubmission> submissions;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isScheduled() {
    return scheduled;
  }

  public void setScheduled(boolean scheduled) {
    this.scheduled = scheduled;
  }

  public int getPeriodicity() {
    return periodicity;
  }

  public void setPeriodicity(int periodicity) {
    this.periodicity = periodicity;
  }

  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }

  public KPIStatus getStatus() {
    return status;
  }

  public void setStatus(KPIStatus status) {
    this.status = status;
  }

  public KpiType getKpiType() {
    return kpiType;
  }

  public void setKpiType(KpiType kpiType) {
    this.kpiType = kpiType;
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

  public Grant getGrant() {
    return grant;
  }

  public void setGrant(Grant grant) {
    this.grant = grant;
  }

  public List<KpiSubmission> getSubmissions() {
    return submissions;
  }

  public void setSubmissions(List<KpiSubmission> submissions) {
    this.submissions = submissions;
  }
}
