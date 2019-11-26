package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity(name = "granter_report_sections")
public class GranterReportSection {

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
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonBackReference
  private GranterReportTemplate reportTemplate;

  @OneToMany(mappedBy = "section",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
  private List<GranterReportSectionAttribute> attributes;

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

  public Integer getSectionOrder() {
    return sectionOrder;
  }

  public void setSectionOrder(Integer sectionOrder) {
    this.sectionOrder = sectionOrder;
  }

  public GranterReportTemplate getReportTemplate() {
    return reportTemplate;
  }

  public void setReportTemplate(GranterReportTemplate reportTemplate) {
    this.reportTemplate = reportTemplate;
  }

  public List<GranterReportSectionAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<GranterReportSectionAttribute> attributes) {
    this.attributes = attributes;
  }
}
