package org.codealpha.gmsservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class WorkFlowPermission {

  @Id
  private Long id;
  @Column
  private Long fromStateId;
  @Column
  private String fromName;
  @Column
  private Long toStateId;
  @Column
  private String toName;
  @Column
  private String action;
  @Column
  private Boolean noteRequired;
  @Column
  private Integer seqOrder;
  @Column
  private Boolean isForwardDirection;

  public Boolean getNoteRequired() {
    return noteRequired;
  }

  public void setNoteRequired(Boolean noteRequired) {
    this.noteRequired = noteRequired;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getFromStateId() {
    return fromStateId;
  }

  public void setFromStateId(Long fromStateId) {
    this.fromStateId = fromStateId;
  }

  public String getFromName() {
    return fromName;
  }

  public void setFromName(String fromName) {
    this.fromName = fromName;
  }

  public Long getToStateId() {
    return toStateId;
  }

  public void setToStateId(Long toStateId) {
    this.toStateId = toStateId;
  }

  public String getToName() {
    return toName;
  }

  public void setToName(String toName) {
    this.toName = toName;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }



  public Integer getSeqOrder() {
    return seqOrder;
  }

  public void setSeqOrder(Integer seqOrder) {
    this.seqOrder = seqOrder;
  }




  public Boolean getForwardDirection() {
    return isForwardDirection;
  }

  public void setForwardDirection(Boolean forwardDirection) {
    isForwardDirection = forwardDirection;
  }
}
