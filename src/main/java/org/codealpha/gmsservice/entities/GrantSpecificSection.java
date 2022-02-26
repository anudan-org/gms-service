package org.codealpha.gmsservice.entities;

import javax.persistence.*;

@Entity(name = "grant_specific_sections")
public class GrantSpecificSection {

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
  private Long grantId;
  @Column
  private Long grantTemplateId;
  @Column
  private Boolean isSystemGenerated;

  public Boolean getSystemGenerated() {
    return isSystemGenerated;
  }

  public void setSystemGenerated(Boolean systemGenerated) {
    isSystemGenerated = systemGenerated;
  }

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

  public Long getGrantTemplateId() {
    return grantTemplateId;
  }

  public void setGrantTemplateId(Long grantTemplateId) {
    this.grantTemplateId = grantTemplateId;
  }

  public Integer getSectionOrder() {
    return sectionOrder;
  }

  public void setSectionOrder(Integer sectionOrder) {
    this.sectionOrder = sectionOrder;
  }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
    }
}
