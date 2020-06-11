package org.codealpha.gmsservice.entities;

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

  
    
}
