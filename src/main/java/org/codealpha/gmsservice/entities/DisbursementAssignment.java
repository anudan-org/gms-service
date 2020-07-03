package org.codealpha.gmsservice.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity(name = "disbursement_assignments")
public class DisbursementAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long disbursementId;
    @Column
    private Long owner;
    @Column
    private Boolean anchor;
    @Column
    private Long stateId;

    @Column
    private Date assignedOn;

    @Column
    private Long updatedBy;

    @Transient
    private List<DisbursementAssignmentHistory> history;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDisbursementId() {
        return disbursementId;
    }

    public void setDisbursementId(Long disbursementId) {
        this.disbursementId = disbursementId;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Boolean getAnchor() {
        return anchor;
    }

    public void setAnchor(Boolean anchor) {
        this.anchor = anchor;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public List<DisbursementAssignmentHistory> getHistory() {
        return history;
    }

    public void setHistory(List<DisbursementAssignmentHistory> history) {
        this.history = history;
    }

    public Date getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(Date assignedOn) {
        this.assignedOn = assignedOn;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

}
