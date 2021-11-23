package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codealpha.gmsservice.models.AssignedTo;
import org.codealpha.gmsservice.models.ClosureAssignmentsVO;
import org.codealpha.gmsservice.models.ClosureDetailVO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "grant_closure_history")
public class GrantClosureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seqid;
    @Column Long id;
    @Column
    private String reason;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private GranterReportTemplate template;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Grant grant;
    @Column
    private Date movedOn;
    @Column
    private Long createBy;
    @Column
    private Date createdAt;
    @Column
    private Long updatedBy;
    @Column
    private Date updatedAt;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private WorkflowStatus status;
    @Transient
    private List<ClosureAssignmentsVO> workflowAssignment;
    @Transient
    private ClosureDetailVO closureDetails;
    @OneToMany(mappedBy = "closure", fetch = FetchType.EAGER)
    @JsonProperty("stringAttribute")
    private List<ClosureStringAttribute> stringAttributes;
    @Transient
    private boolean canManage;
    @Transient
    private boolean forGranteeUse;
    @Transient
    private List<AssignedTo> currentAssignment;
    @Column
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long noteAddedBy;
    @Transient
    private User noteAddedByUser;
    @Transient
    private List<User> granteeUsers;
    @Transient
    private List<WorkFlowPermission> flowAuthorities;
    @Column(columnDefinition = "text")
    private String note;
    @Column
    private String closureDetail;
    @Column
    private boolean deleted;
    @Column
    private String linkedApprovedReports;
    @Column
    private String description;
    @Column
    private Date noteAdded;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkedApprovedReports() {
        return linkedApprovedReports;
    }

    public void setLinkedApprovedReports(String linkedApprovedReports) {
        this.linkedApprovedReports = linkedApprovedReports;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getSeqid() {
        return seqid;
    }

    public void setSeqid(Long seqid) {
        this.seqid = seqid;
    }

    public GranterReportTemplate getTemplate() {
        return template;
    }

    public void setTemplate(GranterReportTemplate template) {
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

    public String getClosureDetail() {
        return closureDetail;
    }

    public void setClosureDetail(String closureDetail) {
        this.closureDetail = closureDetail;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getNoteAdded() {
        return noteAdded;
    }

    public void setNoteAdded(Date noteAdded) {
        this.noteAdded = noteAdded;
    }
}
