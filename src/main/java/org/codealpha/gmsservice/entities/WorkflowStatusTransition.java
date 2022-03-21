package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

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
  @Column
  private Boolean allowTransitionOnValidationWarning;
  @Column
  private Boolean isForwardDirection;


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

  public boolean isNoteRequired() {
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

  public Integer getSeqOrder() {
    return seqOrder;
  }

  public void setSeqOrder(Integer seqOrder) {
    this.seqOrder = seqOrder;
  }

  public Boolean getAllowTransitionOnValidationWarning() {
    return allowTransitionOnValidationWarning;
  }

  public void setAllowTransitionOnValidationWarning(Boolean allowTransitionOnValidationWarning) {
    this.allowTransitionOnValidationWarning = allowTransitionOnValidationWarning;
  }

  public Boolean getForwardDirection() {
    return isForwardDirection;
  }

  public void setForwardDirection(Boolean forwardDirection) {
    isForwardDirection = forwardDirection;
  }
}
