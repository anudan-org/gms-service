package org.codealpha.gmsservice.entities;

import java.util.Date;

import javax.persistence.*;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Entity
@Table(name = "disbursement_snapshot")
public class DisbursementSnapshot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
  private Long id;

  @Column
  private Long assignedToId;

  @Column
  private Long disbursementId;

  @Column
  private Long statusId;

  @Column
  private Double requestedAmount;

  @Column
  private String reason;
  @Column
  private Long fromStateId;
  @Column
  private Long toStateId;
  @Column
  private String fromNote;
  @Column
  private Long movedBy;
  @Column
  private String fromStringAttributes;
  @Column
  private Date movedOn;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAssignedToId() {
    return assignedToId;
  }

  public void setAssignedToId(Long assignedToId) {
    this.assignedToId = assignedToId;
  }

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  public Long getDisbursementId() {
    return disbursementId;
  }

  public void setDisbursementId(Long disbursementId) {
    this.disbursementId = disbursementId;
  }

  public Double getRequestedAmount() {
    return requestedAmount;
  }

  public void setRequestedAmount(Double requestedAmount) {
    this.requestedAmount = requestedAmount;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Long getFromStateId() {
    return fromStateId;
  }

  public void setFromStateId(Long fromStateId) {
    this.fromStateId = fromStateId;
  }

  public Long getToStateId() {
    return toStateId;
  }

  public void setToStateId(Long toStateId) {
    this.toStateId = toStateId;
  }

  public String getFromNote() {
    return fromNote;
  }

  public void setFromNote(String fromNote) {
    this.fromNote = fromNote;
  }

  public Long getMovedBy() {
    return movedBy;
  }

  public void setMovedBy(Long movedBy) {
    this.movedBy = movedBy;
  }

  public String getFromStringAttributes() {
    return fromStringAttributes;
  }

  public void setFromStringAttributes(String fromStringAttributes) {
    this.fromStringAttributes = fromStringAttributes;
  }

  public Date getMovedOn() {
    return movedOn;
  }

  public void setMovedOn(Date movedOn) {
    this.movedOn = movedOn;
  }

}
