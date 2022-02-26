package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.List;

@Entity(name = "closure_specific_sections")
public class ClosureSpecificSection {

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
  private Long closureId;
  @Column
  private Long closureTemplateId;
  @OneToMany(mappedBy = "section")
  private List<ClosureSpecificSectionAttribute> attributes;
@Column
private Boolean isRefund;

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

  public Long getClosureTemplateId() {
    return closureTemplateId;
  }

  public void setClosureTemplateId(Long closureTemplateId) {
    this.closureTemplateId = closureTemplateId;
  }

  public Integer getSectionOrder() {
    return sectionOrder;
  }

  public void setSectionOrder(Integer sectionOrder) {
    this.sectionOrder = sectionOrder;
  }

    public Long getClosureId() {
        return closureId;
    }

    public void setClosureId(Long closureId) {
        this.closureId = closureId;
    }

  public List<ClosureSpecificSectionAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<ClosureSpecificSectionAttribute> attributes) {
    this.attributes = attributes;
  }

  public Boolean getRefund() {
    return isRefund;
  }

  public void setRefund(Boolean refund) {
    isRefund = refund;
  }

  public Boolean getSystemGenerated() {
    return isSystemGenerated;
  }

  public void setSystemGenerated(Boolean systemGenerated) {
    isSystemGenerated = systemGenerated;
  }
}
