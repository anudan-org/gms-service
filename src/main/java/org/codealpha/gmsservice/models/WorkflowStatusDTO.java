package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Workflow;
import org.codealpha.gmsservice.entities.WorkflowStatePermission;

import java.util.Date;
import java.util.List;

public class WorkflowStatusDTO {
  private Long id;
  private String name;
  String displayName;
  private String verb;
  private String internalStatus;
  private boolean initial;
  private Boolean terminal;
  private Workflow workflow;

  private List<WorkflowStatePermission> statePermissions;

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

  public Boolean getTerminal() {
    return terminal;
  }

  public void setTerminal(Boolean terminal) {
    this.terminal = terminal;
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

  public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public List<WorkflowStatePermission> getStatePermissions() {
    return statePermissions;
  }

  public void setStatePermissions(
          List<WorkflowStatePermission> statePermissions) {
    this.statePermissions = statePermissions;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public boolean isInitial() {
    return initial;
  }

  public void setInitial(boolean initial) {
    this.initial = initial;
  }

  public String getVerb() {
    return verb;
  }

  public void setVerb(String verb) {
    this.verb = verb;
  }

  public String getInternalStatus() {
    return internalStatus;
  }

  public void setInternalStatus(String internalStatus) {
    this.internalStatus = internalStatus;
  }
}
