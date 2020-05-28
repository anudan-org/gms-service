package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "disbursements_history")
public class DisbursementHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seqid;
    @Column
    private Long id;
    @Column
    private Double requestedAmount;
    @Column(columnDefinition = "text")
    private String reason;
    @Column
    private Date requestedOn;
    @Column
    private Long requestedBy;
    @Column
    private Long statusId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(Date requestedOn) {
        this.requestedOn = requestedOn;
    }

    public Long getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Long requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getSeqid() {
        return seqid;
    }

    public void setSeqid(Long seqid) {
        this.seqid = seqid;
    }
}
