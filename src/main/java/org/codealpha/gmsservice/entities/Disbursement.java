package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "disbursements")
public class Disbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Double requestedAmount;
    @Column(columnDefinition = "text")
    private String reason;
    @Column
    private Date requestedOn;
    @Column
    private Long requestedBy;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private WorkflowStatus status;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Grant grant;
    @Transient
    private List<DisbursementAssignment> assignments;
    @Transient
    private List<WorkFlowPermission> flowPermissions;
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
    @Transient
    List<ActualDisbursement> actualDisbursements;

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

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public void setAssignments(List<DisbursementAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<DisbursementAssignment> getAssignments() {
        return assignments;
    }

    public void setFlowPermissions(List<WorkFlowPermission> flowPermissions) {
        this.flowPermissions = flowPermissions;
    }

    public List<WorkFlowPermission> getFlowPermissions() {
        return flowPermissions;
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

    public List<ActualDisbursement> getActualDisbursements() {
        return actualDisbursements;
    }

    public void setActualDisbursements(List<ActualDisbursement> actualDisbursements) {
        this.actualDisbursements = actualDisbursements;
    }

    

}
