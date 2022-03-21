package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codealpha.gmsservice.entities.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportDTO {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    private Long id;
    private String name;
    private Date startDate;
    private Date endDate;
    private Date dueDate;
    private WorkflowStatus status;
    private Date createdAt;
    private Long createdBy;
    private Date updatedAt;
    private Long updatedBy;
    private String type;
    private GranterReportTemplate template;
    ReportDetailVO reportDetails;
    private List<ReportAssignmentsVO> workflowAssignments;
    @JsonProperty("stringAttribute")
    private List<ReportStringAttribute> stringAttributes;
    private Grant grant;
    private String securityCode;
    private List<User> granteeUsers;
    private String linkedApprovedReports;

    private String note;

    private Date noteAdded;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long noteAddedBy;

    private User noteAddedByUser;

    private List<AssignedTo> currentAssignment;
    private boolean canManage;
    private List<WorkFlowPermission> flowAuthorities;
    private boolean forGranteeUse;
    private int futureReportsCount = 0;
    private Date movedOn;
    private boolean disabledByAmendment;

    @JsonIgnore
    private String reportDetail;

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

    public List<ReportStringAttribute> getStringAttributes() {
        return stringAttributes;
    }

    public void setStringAttributes(List<ReportStringAttribute> stringAttributes) {
        this.stringAttributes = stringAttributes;
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
        return new SimpleDateFormat(YYYY_MM_DD).format(startDate);
    }


    public String getEnDate() {
        if (endDate == null) {
            return "";
        }
        return new SimpleDateFormat(YYYY_MM_DD).format(endDate);
    }


    public String getdDate() {
        if (dueDate == null) {
            return "";
        }
        return new SimpleDateFormat(YYYY_MM_DD).format(dueDate);
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
