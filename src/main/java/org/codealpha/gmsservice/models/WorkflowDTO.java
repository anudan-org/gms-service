package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowStatusTransition;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class WorkflowDTO {
  private Long id;
  private String name;
  private String description;
  private Organization granter;
  private WorkflowObject object;
  private List<WorkflowStatus> states;
  private List<WorkflowStatusTransition> workflowStatusTransitions;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
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
