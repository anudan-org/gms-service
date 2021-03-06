package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

@Entity(name = "report_string_attributes")
public class ReportStringAttribute {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id")
  private Long id;
  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonProperty("attributeDetails")
  private ReportSpecificSectionAttribute sectionAttribute;
  @Column(columnDefinition = "text")
  private String value;
  @Column
  private String target;
  @Column
  private Double actualTarget;
  @Column
  private String frequency;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private Report report;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonProperty("sectionDetails")
  private ReportSpecificSection section;
  @OneToMany(mappedBy = "reportStringAttribute",cascade = CascadeType.ALL)
  private List<ReportStringAttributeAttachments> attachments;
  @Column
  private String grantLevelTarget;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ReportSpecificSectionAttribute getSectionAttribute() {
    return sectionAttribute;
  }

  public void setSectionAttribute(
          ReportSpecificSectionAttribute sectionAttribute) {
    this.sectionAttribute = sectionAttribute;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Report getReport() {
    return report;
  }

  public void setReport(Report report) {
    this.report = report;
  }

  public ReportSpecificSection getSection() {
    return section;
  }

  public void setSection(ReportSpecificSection section) {
    this.section = section;
  }

  public void setTarget(String tr) {
    this.target = tr;
  }

  public String getTarget() {
    return this.target;
  }

  public void setFrequency(String fq) {
    this.frequency = fq;
  }

  public String getFrequency() {
    return this.frequency;
  }

  public List<ReportStringAttributeAttachments> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<ReportStringAttributeAttachments> attachments) {
    this.attachments = attachments;
  }

    public String getGrantLevelTarget() {
        return grantLevelTarget;
    }

    public void setGrantLevelTarget(String grantLevelTarget) {
        this.grantLevelTarget = grantLevelTarget;
    }

  public Double getActualTarget() {
    return actualTarget;
  }

  public void setActualTarget(Double actualTarget) {
    this.actualTarget = actualTarget;
  }
}
