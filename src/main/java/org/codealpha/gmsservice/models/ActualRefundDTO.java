package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

import java.util.Date;

public class ActualRefundDTO {
    private Long id;
    private Double amount;
    private String note;
    private Date refundDate;
    private String refundDateStr;
    private Date createdDate;
    private Long createdBy;
    private Grant associatedGrant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Grant getAssociatedGrant() {
        return associatedGrant;
    }

    public void setAssociatedGrant(Grant associatedGrant) {
        this.associatedGrant = associatedGrant;
    }

    public String getRefundDateStr() {
        return refundDateStr;
    }

    public void setRefundDateStr(String refundDateStr) {
        this.refundDateStr = refundDateStr;
    }
}
