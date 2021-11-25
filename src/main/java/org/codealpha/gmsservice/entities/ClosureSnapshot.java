package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Developer code-alpha.org
 **/
@Entity
@Table(name = "closure_snapshot")
public class ClosureSnapshot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
  private Long id;

  @Column
  private Long assignedToId;

  @Column
  private Long closureId;

  @Column(columnDefinition = "text")
  private String stringAttributes;

  @ManyToOne
  @JoinColumn(name = "reason")
  private ClosureReason reason;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Column
  private Long statusId;
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


  public String getStringAttributes() {
    return stringAttributes;
  }

  public void setStringAttributes(String stringAttributes) {
    this.stringAttributes = stringAttributes;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
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

  public Long getClosureId() {
    return closureId;
  }

  public void setClosureId(Long closureId) {
    this.closureId = closureId;
  }

  public ClosureReason getReason() {
    return reason;
  }

  public void setReason(ClosureReason reason) {
    this.reason = reason;
  }
}
