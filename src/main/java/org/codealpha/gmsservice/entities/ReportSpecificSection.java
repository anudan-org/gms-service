package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.List;

@Entity(name = "report_specific_sections")
public class ReportSpecificSection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String sectionName;
  @Column
  private Integer sectionOrder;
  @Column
  private Boolean deletable;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Granter granter;
  @Column
  private Long reportId;
  @Column
  private Long reportTemplateId;
  @OneToMany(mappedBy = "section")
  private List<ReportSpecificSectionAttribute> attributes;
  @Column
  private Boolean isSystemGenerated;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSectionName() {
    return sectionName;
  }

  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }

  public Granter getGranter() {
    return granter;
  }

  public void setGranter(Granter granter) {
    this.granter = granter;
  }

  public Boolean getDeletable() {
    return deletable;
  }

  public void setDeletable(Boolean deletable) {
    this.deletable = deletable;
  }

  public Long getReportTemplateId() {
    return reportTemplateId;
  }

  public void setReportTemplateId(Long reportTemplateId) {
    this.reportTemplateId = reportTemplateId;
  }

  public Integer getSectionOrder() {
    return sectionOrder;
  }

  public void setSectionOrder(Integer sectionOrder) {
    this.sectionOrder = sectionOrder;
  }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

  public List<ReportSpecificSectionAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<ReportSpecificSectionAttribute> attributes) {
    this.attributes = attributes;
  }

  public Boolean getSystemGenerated() {
    return isSystemGenerated;
  }

  public void setSystemGenerated(Boolean systemGenerated) {
    isSystemGenerated = systemGenerated;
  }
}
