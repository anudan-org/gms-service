package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

@Entity(name = "grant_sections")
public class GrantSection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  @JsonProperty("sectionName")
  private String sectionName;
  @Column
  private boolean deletable;
  @OneToMany(mappedBy = "section")
  private List<GrantSectionAttribute> attributes;

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

  public boolean isDeletable() {
    return deletable;
  }

  public void setDeletable(boolean deletable) {
    this.deletable = deletable;
  }

  public List<GrantSectionAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(
      List<GrantSectionAttribute> attributes) {
    this.attributes = attributes;
  }
}
