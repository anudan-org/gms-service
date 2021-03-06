package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.codealpha.gmsservice.constants.Frequency;
import org.codealpha.gmsservice.constants.KpiReportingType;
import org.codealpha.gmsservice.constants.KpiType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "grant_kpis")
public class GrantKpi  {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
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
  private KpiType kpiType;
  @Column
  @Enumerated(EnumType.STRING)
  private KpiReportingType kpiReportingType;
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
  @OneToMany(mappedBy = "kpi")
  private List<Template> templates;



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

  public List<Template> getTemplates() {
    return templates;
  }

  public void setTemplates(List<Template> templates) {
    this.templates = templates;
  }

  public KpiReportingType getKpiReportingType() {
    return kpiReportingType;
  }

  public void setKpiReportingType(KpiReportingType kpiReportingType) {
    this.kpiReportingType = kpiReportingType;
  }

}
