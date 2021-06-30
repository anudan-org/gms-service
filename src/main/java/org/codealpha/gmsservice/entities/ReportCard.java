package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.codealpha.gmsservice.models.AssignedTo;
import org.codealpha.gmsservice.models.ReportAssignmentsVO;
import org.codealpha.gmsservice.models.ReportDetailVO;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(name = "reports")
public class ReportCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text")
    private String name;
    @Column
    private Date startDate;
    @Transient
    private String stDate;
    @Column
    private Date endDate;
    @Transient
    private String enDate;
    @Column
    private Date dueDate;
    @Transient
    private String dDate;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private WorkflowStatus status;
    @Column
    private Date createdAt;
    @Column
    private Long createdBy;
    @Column
    private Date updatedAt;
    @Column
    private Long updatedBy;
    @Column
    private String type;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private GranterReportTemplate template;
    @Transient
    ReportDetailVO reportDetails;
    @Transient
    private List<ReportAssignmentsVO> workflowAssignments;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Grant grant;
    @Transient
    @ApiModelProperty(name = "securityCode", value = "Secure code for report")
    private String securityCode;
    @Transient
    private List<User> granteeUsers;
    @Column(columnDefinition = "text")
    private String linkedApprovedReports;

    @Column(columnDefinition = "text")
    @ApiModelProperty(name = "note", value = "Current note associated with the grant", dataType = "String")
    private String note;

    @Column
    @ApiModelProperty(name = "noteAdded", value = "Date when current note was associated with the grant", dataType = "Date")
    private Date noteAdded;

    @Column
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(name = "noteAddedBy", value = "Email id of the user who added the current note", dataType = "String")
    private Long noteAddedBy;

    @Transient
    @ApiModelProperty(name = "noteAddedByUser", value = "User who added the current note", dataType = "User")
    private User noteAddedByUser;

    @Transient
    @ApiModelProperty(name = "currentAssignment", value = "Current owner of grant based on grant status", dataType = "List<AssignedTo>")
    private List<AssignedTo> currentAssignment;
    @Column
    private boolean canManage;
    @Transient
    private List<WorkFlowPermission> flowAuthorities;
    @Transient
    private boolean forGranteeUse;
    @Transient
    private int futureReportsCount = 0;
    @Column
    private Date movedOn;
    @Column
    private boolean disabledByAmendment;

    @Column
    @JsonIgnore
    private String reportDetail;

    @Column
    private boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public void setWorkflowAssignments(List<ReportAssignmentsVO> workflowAssignments) {
        this.workflowAssignments = workflowAssignments;
    }

    public List<ReportAssignmentsVO> getWorkflowAssignments() {
        return workflowAssignments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GranterReportTemplate getTemplate() {
        return template;
    }

    public void setTemplate(GranterReportTemplate template) {
        this.template = template;
    }

    public ReportDetailVO getReportDetails() {
        return reportDetails;
    }

    public void setReportDetails(ReportDetailVO reportDetails) {
        this.reportDetails = reportDetails;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
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

    public List<AssignedTo> getCurrentAssignment() {
        return currentAssignment;
    }

    public void setCurrentAssignment(List<AssignedTo> currentAssignment) {
        this.currentAssignment = currentAssignment;
    }

    public void setCanManage(boolean canManage) {
        this.canManage = canManage;
    }

    public boolean getCanManage() {
        return canManage;
    }

    public String getStDate() {
        if (startDate == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(startDate);
    }

    public void setStDate(String stDate) {
        this.stDate = stDate;
    }

    public String getEnDate() {
        if (endDate == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(endDate);
    }

    public void setEnDate(String enDate) {
        this.enDate = enDate;
    }

    public String getdDate() {
        if (dueDate == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(dueDate);
    }

    public void setdDate(String dDate) {
        this.dDate = dDate;
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

    public boolean isForGranteeUse() {
        return forGranteeUse;
    }

    public void setForGranteeUse(boolean forGranteeUse) {
        this.forGranteeUse = forGranteeUse;
    }

    public void setFutureReportsCount(int futureReportsCount) {
        this.futureReportsCount = futureReportsCount;
    }

    public int getFutureReportsCount() {
        return futureReportsCount;
    }

    public Date getMovedOn() {
        return movedOn;
    }

    public void setMovedOn(Date movedOn) {
        this.movedOn = movedOn;
    }

    public String getLinkedApprovedReports() {
        return linkedApprovedReports;
    }

    public void setLinkedApprovedReports(String linkedApprovedReports) {
        this.linkedApprovedReports = linkedApprovedReports;
    }

    public String getReportDetail() {
        return reportDetail;
    }

    public void setReportDetail(String reportDetail) {
        this.reportDetail = reportDetail;
    }

    public boolean isDisabledByAmendment() {
        return disabledByAmendment;
    }

    public void setDisabledByAmendment(boolean disabledByAmendment) {
        this.disabledByAmendment = disabledByAmendment;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
