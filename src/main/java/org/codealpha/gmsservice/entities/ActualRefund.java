package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "actual_refunds")
public class ActualRefund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Double amount;
    @Column
    private String note;
    @Column
    private Date refundDate;
    @Transient
    private String refundDateStr;
    @Column
    private Date createdDate;
    @Column
    private Long createdBy;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(referencedColumnName = "id")
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
