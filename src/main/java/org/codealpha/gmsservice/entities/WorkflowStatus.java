package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "workflow_statuses")
public class WorkflowStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String name;
  @Column(nullable = true)
  String displayName;
  @Column(nullable = true)
  private String verb;
  @Column
  private String internalStatus;
  @Column
  private boolean initial;
  @Column
  private Boolean terminal;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private Workflow workflow;

  @OneToMany(mappedBy = "workflowStatus")
  private List<WorkflowStatePermission> statePermissions;

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
