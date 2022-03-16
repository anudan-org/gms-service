package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.*;

import java.util.Date;
import java.util.List;

public class GrantClosureDTO {
    private Long id;
    private ClosureReason reason;
    private GranterClosureTemplate template;
    private Grant grant;
    private Date movedOn;
    private Long createBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    private WorkflowStatus status;
    private List<ClosureAssignmentsVO> workflowAssignment;
    private ClosureDetailVO closureDetails;
    private List<ClosureStringAttribute> stringAttributes;
    private boolean canManage;
    private boolean forGranteeUse;
    private List<AssignedTo> currentAssignment;
    private Long noteAddedBy;
    private User noteAddedByUser;
    private List<User> granteeUsers;
    private List<WorkFlowPermission> flowAuthorities;
    private String note;
    private Date noteAdded;
    private boolean deleted;
    private String closureDetail;
    private String linkedApprovedReports;
    private String description;
    private Long ownerId;
    private String ownerName;
    private List<ClosureDocument> closureDocuments;

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<WorkFlowPermission> getFlowAuthorities() {
        return flowAuthorities;
    }

    public void setFlowAuthorities(List<WorkFlowPermission> flowAuthorities) {
        this.flowAuthorities = flowAuthorities;
    }

    public List<User> getGranteeUsers() {
        return granteeUsers;
    }

    public void setGranteeUsers(List<User> granteeUsers) {
        this.granteeUsers = granteeUsers;
    }

    public User getNoteAddedByUser() {
        return noteAddedByUser;
    }

    public void setNoteAddedByUser(User noteAddedByUser) {
        this.noteAddedByUser = noteAddedByUser;
    }

    public Long getNoteAddedBy() {
        return noteAddedBy;
    }

    public void setNoteAddedBy(Long noteAddedBy) {
        this.noteAddedBy = noteAddedBy;
    }

    public List<AssignedTo> getCurrentAssignment() {
        return currentAssignment;
    }

    public void setCurrentAssignment(List<AssignedTo> currentAssignment) {
        this.currentAssignment = currentAssignment;
    }

    public boolean isForGranteeUse() {
        return forGranteeUse;
    }

    public void setForGranteeUse(boolean forGranteeUse) {
        this.forGranteeUse = forGranteeUse;
    }

    public boolean isCanManage() {
        return canManage;
    }

    public void setCanManage(boolean canManage) {
        this.canManage = canManage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClosureReason getReason() {
        return reason;
    }

    public void setReason(ClosureReason reason) {
        this.reason = reason;
    }

    public GranterClosureTemplate getTemplate() {
        return template;
    }

    public void setTemplate(GranterClosureTemplate template) {
        this.template = template;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public Date getMovedOn() {
        return movedOn;
    }

    public void setMovedOn(Date movedOn) {
        this.movedOn = movedOn;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public List<ClosureAssignmentsVO> getWorkflowAssignment() {
        return workflowAssignment;
    }

    public void setWorkflowAssignment(List<ClosureAssignmentsVO> workflowAssignment) {
        this.workflowAssignment = workflowAssignment;
    }

    public ClosureDetailVO getClosureDetails() {
        return closureDetails;
    }

    public void setClosureDetails(ClosureDetailVO closureDetails) {
        this.closureDetails = closureDetails;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ClosureStringAttribute> getStringAttributes() {
        return stringAttributes;
    }

    public void setStringAttributes(List<ClosureStringAttribute> stringAttributes) {
        this.stringAttributes = stringAttributes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getClosureDetail() {
        return closureDetail;
    }

    public void setClosureDetail(String closureDetail) {
        this.closureDetail = closureDetail;
    }

    public String getLinkedApprovedReports() {
        return linkedApprovedReports;
    }

    public void setLinkedApprovedReports(String linkedApprovedReports) {
        this.linkedApprovedReports = linkedApprovedReports;
    }

    public Date getNoteAdded() {
        return noteAdded;
    }

    public void setNoteAdded(Date noteAdded) {
        this.noteAdded = noteAdded;
    }

    public List<ClosureDocument> getClosureDocuments() {
        return closureDocuments;
    }

    public void setClosureDocuments(List<ClosureDocument> closureDocuments) {
        this.closureDocuments = closureDocuments;
    }
}
