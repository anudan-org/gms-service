package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity(name = "report_specific_section_attributes")
public class ReportSpecificSectionAttribute {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(columnDefinition = "text")
  private String fieldName;
  @Column(columnDefinition = "text")
  private String fieldType;
  @Column
  private int attributeOrder;
  @Column
  private Boolean deletable;
  @Column
  private Boolean required;
  @Column(columnDefinition = "text")
  private String extras;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private ReportSpecificSection section;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Granter granter;
  @Column
  private Boolean canEdit;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ReportSpecificSection getSection() {
    return section;
  }

  public void setSection(ReportSpecificSection section) {
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

  public Boolean getCanEdit() {
    return canEdit;
  }

  public void setCanEdit(Boolean canEdit) {
    this.canEdit = canEdit;
  }
}
