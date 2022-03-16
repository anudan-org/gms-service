package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.entities.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GrantDTO {
  private Long id;
  private Organization organization;
  private Granter grantorOrganization;
  private List<GrantStringAttribute> stringAttributes;
  private List<ActualRefund> actualRefunds;
  private String name;
  private String description;
  private Long templateId;
  private GranterGrantTemplate grantTemplate;
  private Double amount;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
  private WorkflowStatus grantStatus;
  private GrantStatus statusName;
  private WorkflowStatus substatus;
  private Date startDate;
  private String stDate;
  private Date endDate;
  private String enDate;
  private String representative;
  private String note;
  private Date noteAdded;
  private String noteAddedBy;
  private User noteAddedByUser;
  private List<Submission> submissions;
  private WorkflowActionPermission actionAuthorities;
  private List<WorkFlowPermission> flowAuthorities;
  private GrantDetailVO grantDetails;
  private Long currentAssignment;
  private List<GrantAssignments> workflowAssignment;
  List<GrantAssignmentsVO> workflowAssignments;
  private List<GrantTag> grantTags;
  private List<GrantTagVO> tags;
  private String securityCode;
  private Date movedOn;
  private List<TableData> approvedReportsDisbursements;
  private String referenceNo;
  private Boolean deleted;
  private Boolean hasOngoingDisbursement = false;
  private Double ongoingDisbursementAmount;
  private int projectDocumentsCount = 0;
  private Double approvedDisbursementsTotal = 0d;
  private int approvedReportsForGrant;
  private Long origGrantId;
  private String origGrantRefNo;
  private Long amendGrantId;
  private boolean amended;
  private int amendmentNo = 0;
  private Date minEndEndate;
  private Boolean internal;
  private Long grantTypeId;
  private boolean hashClosure = false;
  private Long closureId;
  private String amendmentDetailsSnapshot;
  private Boolean closureInProgress = false;
  private Double refundAmount;
  private String refundReason;

  public Boolean getClosureInProgress() {
    return closureInProgress;
  }

  public void setClosureInProgress(Boolean closureInProgress) {
    this.closureInProgress = closureInProgress;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Organization getGrantorOrganization() {
    return grantorOrganization;
  }

  public void setGrantorOrganization(Granter grantorOrganization) {
    this.grantorOrganization = grantorOrganization;
  }

  public GrantStatus getStatusName() {
    return statusName;
  }

  public void setStatusName(GrantStatus status) {
    this.statusName = status;
  }

  public WorkflowStatus getSubstatus() {
    return substatus;
  }

  public void setSubstatus(WorkflowStatus substatus) {
    this.substatus = substatus;
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

  public List<Submission> getSubmissions() {
    return submissions;
  }

  public void setSubmissions(List<Submission> submissions) {
    this.submissions = submissions;
  }

  public WorkflowStatus getGrantStatus() {
    return grantStatus;
  }

  public void setGrantStatus(WorkflowStatus status) {
    this.grantStatus = status;
  }

  public List<GrantStringAttribute> getStringAttributes() {
    return stringAttributes;
  }

  public void setStringAttributes(List<GrantStringAttribute> stringAttributes) {
    this.stringAttributes = stringAttributes;
  }

  public WorkflowActionPermission getActionAuthorities() {
    return actionAuthorities;
  }

  public void setActionAuthorities(WorkflowActionPermission actionAuthorities) {
    this.actionAuthorities = actionAuthorities;
  }

  public List<WorkFlowPermission> getFlowAuthorities() {
    return flowAuthorities;
  }

  public void setFlowAuthorities(List<WorkFlowPermission> flowAuthorities) {
    this.flowAuthorities = flowAuthorities;
  }

  public GrantDetailVO getGrantDetails() {
    return grantDetails;
  }

  public void setGrantDetails(GrantDetailVO grantDetails) {
    this.grantDetails = grantDetails;
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

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setRepresentative(String rep) {
    this.representative = rep;
  }

  public String getRepresentative() {
    return this.representative;
  }

  public Long getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

  public GranterGrantTemplate getGrantTemplate() {
    return grantTemplate;
  }

  public void setGrantTemplate(GranterGrantTemplate grantTemplate) {
    this.grantTemplate = grantTemplate;
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

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public Long getCurrentAssignment() {
    return currentAssignment;
  }

  public void setCurrentAssignment(Long currentAssignment) {
    this.currentAssignment = currentAssignment;
  }

  public List<GrantAssignments> getWorkflowAssignment() {
    return workflowAssignment;
  }

  public void setWorkflowAssignment(List<GrantAssignments> workflowAssignment) {
    this.workflowAssignment = workflowAssignment;
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

  public String getNoteAddedBy() {
    return noteAddedBy;
  }

  public void setNoteAddedBy(String noteAddedBy) {
    this.noteAddedBy = noteAddedBy;
  }

  public User getNoteAddedByUser() {
    return noteAddedByUser;
  }

  public void setNoteAddedByUser(User noteAddedByUser) {
    this.noteAddedByUser = noteAddedByUser;
  }

  public String getSecurityCode() {
    return securityCode;
  }

  public void setSecurityCode(String securityCode) {
    this.securityCode = securityCode;
  }

  public Date getMovedOn() {
    return movedOn;
  }

  public void setMovedOn(Date movedOn) {
    this.movedOn = movedOn;
  }

  public List<TableData> getApprovedReportsDisbursements() {
    return approvedReportsDisbursements;
  }

  public void setApprovedReportsDisbursements(List<TableData> approvedReportsDisbursements) {
    this.approvedReportsDisbursements = approvedReportsDisbursements;
  }

  public String getReferenceNo() {
    return referenceNo;
  }

  public void setReferenceNo(String referenceNo) {
    this.referenceNo = referenceNo;
  }

  public Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  public Boolean getHasOngoingDisbursement() {
    return hasOngoingDisbursement;
  }

  public void setHasOngoingDisbursement(Boolean hasOngoingDisbursement) {
    this.hasOngoingDisbursement = hasOngoingDisbursement;
  }

  public int getProjectDocumentsCount() {
    return projectDocumentsCount;
  }

  public void setProjectDocumentsCount(int projectDocumentsCount) {
    this.projectDocumentsCount = projectDocumentsCount;
  }

  public Double getApprovedDisbursementsTotal() {
    return approvedDisbursementsTotal;
  }

  public void setApprovedDisbursementsTotal(Double approvedDisbursementsTotal) {
    this.approvedDisbursementsTotal = approvedDisbursementsTotal;
  }

  public int getApprovedReportsForGrant() {
    return approvedReportsForGrant;
  }

  public void setApprovedReportsForGrant(int approvedReportsForGrant) {
    this.approvedReportsForGrant = approvedReportsForGrant;
  }

  public Long getOrigGrantId() {
    return origGrantId;
  }

  public void setOrigGrantId(Long origGrantId) {
    this.origGrantId = origGrantId;
  }

  public Long getAmendGrantId() {
    return amendGrantId;
  }

  public void setAmendGrantId(Long amendGrantId) {
    this.amendGrantId = amendGrantId;
  }

  public boolean isAmended() {
    return amended;
  }

  public void setAmended(boolean amended) {
    this.amended = amended;
  }

  public String getOrigGrantRefNo() {
    return origGrantRefNo;
  }

  public void setOrigGrantRefNo(String origGrantRefNo) {
    this.origGrantRefNo = origGrantRefNo;
  }

  public int getAmendmentNo() {
    return amendmentNo;
  }

  public void setAmendmentNo(int amendmentNo) {
    this.amendmentNo = amendmentNo;
  }

  public Date getMinEndEndate() {
    return minEndEndate;
  }

  public void setMinEndEndate(Date minEndEndate) {
    this.minEndEndate = minEndEndate;
  }

  public Boolean getInternal() {
    return internal;
  }

  public void setInternal(Boolean internal) {
    this.internal = internal;
  }

  public Long getGrantTypeId() {
    return grantTypeId;
  }

  public void setGrantTypeId(Long grantTypeId) {
    this.grantTypeId = grantTypeId;
  }

  public List<GrantTag> getGrantTags() {
    return grantTags;
  }

  public void setGrantTags(List<GrantTag> grantTags) {
    this.grantTags = grantTags;
  }

  public List<GrantAssignmentsVO> getWorkflowAssignments() {
    return workflowAssignments;
  }

  public void setWorkflowAssignments(List<GrantAssignmentsVO> workflowAssignments) {
    this.workflowAssignments = workflowAssignments;
  }

  public List<GrantTagVO> getTags() {
    return tags;
  }

  public void setTags(List<GrantTagVO> tags) {
    this.tags = tags;
  }

  public String getAmendmentDetailsSnapshot() {
    return amendmentDetailsSnapshot;
  }

  public void setAmendmentDetailsSnapshot(String amendmentDetailsSnapshot) {
    this.amendmentDetailsSnapshot = amendmentDetailsSnapshot;
  }

  public boolean isHashClosure() {
    return hashClosure;
  }

  public void setHashClosure(boolean hashClosure) {
    this.hashClosure = hashClosure;
  }

  public Long getClosureId() {
    return closureId;
  }

  public void setClosureId(Long closureId) {
    this.closureId = closureId;
  }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

  public Double getOngoingDisbursementAmount() {
    return ongoingDisbursementAmount;
  }

  public void setOngoingDisbursementAmount(Double ongoingDisbursementAmount) {
    this.ongoingDisbursementAmount = ongoingDisbursementAmount;
  }

  public List<ActualRefund> getActualRefunds() {
    return actualRefunds;
  }

  public void setActualRefunds(List<ActualRefund> actualRefunds) {
    this.actualRefunds = actualRefunds;
  }
}
