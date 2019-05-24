package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "granter_grant_section_attributes")
public class GranterGrantSectionAttribute {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String fieldName;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private GranterGrantSection section;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Organization granter;
  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private GrantSectionAttribute attribute;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GranterGrantSection getSection() {
    return section;
  }

  public void setSection(GranterGrantSection section) {
    this.section = section;
  }

  public Organization getGranter() {
    return granter;
  }

  public void setGranter(Organization granter) {
    this.granter = granter;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public GrantSectionAttribute getAttribute() {
    return attribute;
  }

  public void setAttribute(GrantSectionAttribute attribute) {
    this.attribute = attribute;
  }
}
