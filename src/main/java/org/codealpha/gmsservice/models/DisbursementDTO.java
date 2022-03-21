package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.*;

import java.util.Date;
import java.util.List;

public class DisbursementDTO {

    private Long id;
    private Double requestedAmount;
    private String reason;
    private Date requestedOn;
    private Long requestedBy;
    private WorkflowStatus status;
    private Grant grant;
    private List<DisbursementAssignment> assignments;
    private List<WorkFlowPermission> flowPermissions;
    private String note;
    private Date noteAdded;
    private Long noteAddedBy;
    private String createdBy;
    private Date createdAt;
    private String updatedBy;
    private Date updatedAt;
    private Date movedOn;
    private User noteAddedByUser;
    List<ActualDisbursement> actualDisbursements;
    private List<ActualDisbursement> approvedActualsDibursements;
    private boolean granteeEntry;
    private Double otherSources;
    private Long reportId;
    private boolean disabledByAmendment;
    private List<DisbursementDocument> disbursementDocuments;
    private String ownerName;
    private Integer ownerId;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

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

    public List<ActualDisbursement> getApprovedActualsDibursements() {
        return approvedActualsDibursements;
    }

    public void setApprovedActualsDibursements(List<ActualDisbursement> approvedActualsDibursements) {
        this.approvedActualsDibursements = approvedActualsDibursements;
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

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public boolean isDisabledByAmendment() {
        return disabledByAmendment;
    }

    public void setDisabledByAmendment(boolean disabledByAmendment) {
        this.disabledByAmendment = disabledByAmendment;
    }

    public List<DisbursementDocument> getDisbursementDocuments() {
        return disbursementDocuments;
    }

    public void setDisbursementDocuments(List<DisbursementDocument> disbursementDocuments) {
        this.disbursementDocuments = disbursementDocuments;
    }
}
