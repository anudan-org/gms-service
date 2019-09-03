package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity(name = "granter_grant_sections")
public class GranterGrantSection {

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
  private GranterGrantTemplate grantTemplate;

  @OneToMany(mappedBy = "section",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
  private List<GranterGrantSectionAttribute> attributes;

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

  public GranterGrantTemplate getGrantTemplate() {
    return grantTemplate;
  }

  public void setGrantTemplate(GranterGrantTemplate grantTemplate) {
    this.grantTemplate = grantTemplate;
  }

  public List<GranterGrantSectionAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<GranterGrantSectionAttribute> attributes) {
    this.attributes = attributes;
  }
}
