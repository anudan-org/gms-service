package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity(name = "granter_report_section_attributes")
public class GranterReportSectionAttribute {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String fieldName;
  @Column
  private String fieldType;
  @Column(columnDefinition = "text")
  private String extras;
  @Column
  private int attributeOrder;
  @Column
  private Boolean deletable;
  @Column
  private Boolean required;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private GranterReportSection section;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Granter granter;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GranterReportSection getSection() {
    return section;
  }

  public void setSection(GranterReportSection section) {
    this.section = section;
  }

  public Organization getGranter() {
    return granter;
  }

  public void setGranter(Granter granter) {
    this.granter = granter;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  public Boolean getDeletable() {
    return deletable;
  }

  public void setDeletable(Boolean deletable) {
    this.deletable = deletable;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public int getAttributeOrder() {
    return attributeOrder;
  }

  public void setAttributeOrder(int attributeOrder) {
    this.attributeOrder = attributeOrder;
  }

  public String getExtras() {
    return extras;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }
}
