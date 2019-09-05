package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity(name = "grant_string_attributes")
public class GrantStringAttribute {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id")
  private Long id;
  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonProperty("attributeDetails")
  private GrantSpecificSectionAttribute sectionAttribute;
  @Column(columnDefinition = "text")
  private String value;
  @Column
  private String target;
  @Column
  private String frequency;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private Grant grant;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonProperty("sectionDetails")
  private GrantSpecificSection section;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GrantSpecificSectionAttribute getSectionAttribute() {
    return sectionAttribute;
  }

  public void setSectionAttribute(
          GrantSpecificSectionAttribute sectionAttribute) {
    this.sectionAttribute = sectionAttribute;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Grant getGrant() {
    return grant;
  }

  public void setGrant(Grant grant) {
    this.grant = grant;
  }

  public GrantSpecificSection getSection() {
    return section;
  }

  public void setSection(GrantSpecificSection section) {
    this.section = section;
  }

  public void setTarget(String tr){
    this.target = tr;
  }

  public String getTarget(){
    return this.target;
  }

  public void setFrequency(String fq){
    this.frequency = fq;
  }

  public String getFrequency(){
    return this.frequency;
  }
}
