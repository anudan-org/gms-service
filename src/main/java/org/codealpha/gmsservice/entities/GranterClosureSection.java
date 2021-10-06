package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity(name = "granter_closure_sections")
public class GranterClosureSection {

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
  private GranterClosureTemplate closureTemplate;

  @OneToMany(mappedBy = "section",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
  private List<GranterClosureSectionAttribute> attributes;

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

  public GranterClosureTemplate getClosureTemplate() {
    return closureTemplate;
  }

  public void setClosureTemplate(GranterClosureTemplate closureTemplate) {
    this.closureTemplate = closureTemplate;
  }

  public List<GranterClosureSectionAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<GranterClosureSectionAttribute> attributes) {
    this.attributes = attributes;
  }
}
