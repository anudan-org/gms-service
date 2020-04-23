package org.codealpha.gmsservice.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "workflow_status_transitions")
public class WorkflowStatusTransition {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private WorkflowStatus fromState;
  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private WorkflowStatus toState;
  @Column
  private String action;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Workflow workflow;
  @Column
  private boolean noteRequired;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private Role role;
  @Column
  private Date createdAt;
  @Column
  private String createdBy;
  @Column
  private Date updatedAt;
  @Column
  private String updatedBy;
  @Column
  private Integer seqOrder;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public WorkflowStatus getFromState() {
    return fromState;
  }

  public void setFromState(WorkflowStatus fromState) {
    this.fromState = fromState;
  }

  public WorkflowStatus getToState() {
    return toState;
  }

  public void setToState(WorkflowStatus toState) {
    this.toState = toState;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
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

  public boolean getNoteRequired() {
    return noteRequired;
  }

  public void setNoteRequired(boolean noteRequired) {
    this.noteRequired = noteRequired;
  }

  public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public boolean isNoteRequired() {
    return noteRequired;
  }

  public Integer getSeqOrder() {
    return seqOrder;
  }

  public void setSeqOrder(Integer seqOrder) {
    this.seqOrder = seqOrder;
  }
}
