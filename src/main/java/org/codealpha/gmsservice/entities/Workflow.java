package org.codealpha.gmsservice.entities;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.codealpha.gmsservice.constants.WorkflowObject;

@Entity(name = "workflows")
public class Workflow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String name;
  @Column
  private String description;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Organization granter;

  @Column
  @Enumerated(EnumType.STRING)
  private WorkflowObject object;

  @OneToMany(mappedBy = "workflow")
  private List<WorkflowStatus> states;

  @OneToMany(mappedBy = "workflow")
  private List<WorkflowStatusTransition> workflowStatusTransitions;

  @Column
  private Date createdAt;
  @Column
  private String createdBy;
  @Column
  private Date updatedAt;
  @Column
  private String updatedBy;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Organization getGranter() {
    return granter;
  }

  public void setGranter(Organization granter) {
    this.granter = granter;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public WorkflowObject getObject() {
    return object;
  }

  public void setObject(WorkflowObject object) {
    this.object = object;
  }

  public List<WorkflowStatus> getStates() {
    return states;
  }

  public void setStates(List<WorkflowStatus> states) {
    this.states = states;
  }

  public List<WorkflowStatusTransition> getWorkflowStatusTransitions() {
    return workflowStatusTransitions;
  }

  public void setWorkflowStatusTransitions(
      List<WorkflowStatusTransition> workflowStatusTransitions) {
    this.workflowStatusTransitions = workflowStatusTransitions;
  }
}
