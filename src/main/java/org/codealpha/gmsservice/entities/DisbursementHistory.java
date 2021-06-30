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
    @Column(columnDefinition = "text")
    private String note;
    @Column
    private Date noteAdded;
    @Column
    private Long noteAddedBy;
    @Column
    private String createdBy;
    @Column
    private Date createdAt;
    @Column
    private String updatedBy;
    @Column
    private Date updatedAt;
    @Column
    private Date movedOn;
    @Transient
    private User noteAddedByUser;
    @Column
    private boolean granteeEntry;
    @Column
    private Double otherSources;
    @Transient
    private WorkflowStatus status;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getNoteAdded() {
        return noteAdded;
    }

    public void setNoteAdded(Date noteAdded) {
        this.noteAdded = noteAdded;
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getMovedOn() {
        return movedOn;
    }

    public void setMovedOn(Date movedOn) {
        this.movedOn = movedOn;
    }

    public Long getNoteAddedBy() {
        return noteAddedBy;
    }

    public void setNoteAddedBy(Long noteAddedBy) {
        this.noteAddedBy = noteAddedBy;
    }

    public User getNoteAddedByUser() {
        return noteAddedByUser;
    }

    public void setNoteAddedByUser(User noteAddedByUser) {
        this.noteAddedByUser = noteAddedByUser;
    }

    public boolean isGranteeEntry() {
        return granteeEntry;
    }

    public void setGranteeEntry(boolean granteeEntry) {
        this.granteeEntry = granteeEntry;
    }

    public Double getOtherSources() {
        return otherSources;
    }

    public void setOtherSources(Double otherSources) {
        this.otherSources = otherSources;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

}
