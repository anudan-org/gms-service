package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.services.GrantService;
import org.codealpha.gmsservice.services.UserService;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GrantVO {

    private Long id;
    private Organization organization;
    private Granter grantorOrganization;
    private String name;
    private String description;
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
    private String note;
    private Date noteAdded;
    private String noteAddedBy;
    private User noteAddedByUser;
    private String representative;
    private Double amount;
    private Long currentAssignment;
    private List<GrantAssignments> workflowAssignment;
    List<GrantAssignmentsVO> workflowAssignments;
    private List<Submission> submissions;
    private WorkflowActionPermission actionAuthorities;
    private List<WorkFlowPermission> flowAuthorities;
    private GrantDetailVO grantDetails;
    private Long templateId;
    private GranterGrantTemplate grantTemplate;
    private Long grantId;
    private List<List<TableData>> approvedReportsDisbursements;
    private String referenceNo;
    private Boolean deleted;
    private Date movedOn;
    private Boolean hasOngoingDisbursement;
    private Double actualOngoingDisbursementRecorded;
    private String ongoingDisbursementNote;
    private int projectDocumentsCount = 0;
    private Double approvedDisbursementsTotal = 0d;
    private int approvedReportsForGrant;
    private Long grantTypeId;
    private List<GrantTag> grantTags;
    private List<GrantTagVO> tags;
   


    @JsonIgnore
    private List<GrantStringAttribute> stringAttributes;

    private String securityCode;
    private boolean hashClosure = false;
    private Long closureId;
    private Long origGrantId;
    private Long amendGrantId;
    private boolean amended;
    private String origGrantRefNo;
    private int amendmentNo;
    private Date minEndEndate;
    private Boolean internal;
    private String amendmentDetailsSnapshot;
    private Boolean closureInProgress;
    private Double ongoingDisbursementAmount;

    private static Logger logger = LoggerFactory.getLogger(GrantVO.class);

    public Long getId() {
        return id;
    }

    public String getAmendmentDetailsSnapshot() {
        return amendmentDetailsSnapshot;
    }

    public void setAmendmentDetailsSnapshot(String amendmentDetailsSnapshot) {
        this.amendmentDetailsSnapshot = amendmentDetailsSnapshot;
    }

    public Boolean getClosureInProgress() {
        return closureInProgress;
    }

    public void setClosureInProgress(Boolean closureInProgress) {
        this.closureInProgress = closureInProgress;
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

    public Organization getGrantorOrganization() {
        return grantorOrganization;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setGrantorOrganization(Granter grantorOrganization) {
        this.grantorOrganization = grantorOrganization;
    }

    public Date getMovedOn() {
        return movedOn;
    }

    public void setMovedOn(Date movedOn) {
        this.movedOn = movedOn;
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

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public WorkflowStatus getGrantStatus() {
        return grantStatus;
    }

    public void setGrantStatus(WorkflowStatus grantStatus) {
        this.grantStatus = grantStatus;
    }

    public GrantStatus getStatusName() {
        return statusName;
    }

    public void setStatusName(GrantStatus statusName) {
        this.statusName = statusName;
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

    public Long getCurrentAssignment() {
        return currentAssignment;
    }

    public void setCurrentAssignment(Long currentAssignment) {
        this.currentAssignment = currentAssignment;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {

        this.endDate = endDate;
    }

    public String getStDate() {
        return stDate;
    }

    public void setStDate(String stDate) {
        this.stDate = stDate;
    }

    public String getEnDate() {
        return enDate;
    }

    public void setEnDate(String enDate) {
        this.enDate = enDate;
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

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public GrantDetailVO getGrantDetails() {
        return grantDetails;
    }

    public void setGrantDetails(GrantDetailVO attributes) {
        this.grantDetails = attributes;
    }

    public List<GrantStringAttribute> getStringAttributes() {
        return stringAttributes;
    }

    public void setStringAttributes(List<GrantStringAttribute> stringAttributes) {
        this.stringAttributes = stringAttributes;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
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

    public GranterGrantTemplate getGrantTemplate() {
        return grantTemplate;
    }

    public void setGrantTemplate(GranterGrantTemplate grantTemplate) {
        this.grantTemplate = grantTemplate;
    }

    public List<GrantAssignments> getWorkflowAssignment() {
        return workflowAssignment;
    }

    public void setWorkflowAssignment(List<GrantAssignments> workflowAssignment) {
        this.workflowAssignment = workflowAssignment;
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

    public GrantVO build(Grant grant, List<GrantSpecificSection> sections,
                         WorkflowPermissionService workflowPermissionService, User user,
                         UserService userService, GrantService grantService) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(grant.getClass());
        GrantVO vo = new GrantVO();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            if (!descriptor.getName().equalsIgnoreCase("class")) {
                try {
                    Object value = descriptor.getReadMethod().invoke(grant);
                    PropertyDescriptor voPd = BeanUtils.getPropertyDescriptor(vo.getClass(), descriptor.getName());
                    if (voPd!=null && voPd.getName().equalsIgnoreCase("stringAttributes")) {
                        GrantDetailVO grantDetailVO = null;
                        grantDetailVO = vo.getGrantDetails();
                        if (grantDetailVO == null) {
                            grantDetailVO = new GrantDetailVO();
                        }
                        grantDetailVO = grantDetailVO.buildStringAttributes(sections, (List<GrantStringAttribute>) value);
                        vo.setGrantDetails(grantDetailVO);
                    } else if (voPd!=null && (voPd.getName().equalsIgnoreCase("noteAddedBy")
                            || voPd.getName().equalsIgnoreCase("noteAddedByUser"))) {
                        vo.setNoteAddedBy(grant.getNoteAddedBy());
                        if(grant.getNoteAddedBy()!=null) {
                            vo.setNoteAddedByUser(
                                    userService.getUserByEmailAndOrg(grant.getNoteAddedBy(), grant.getGrantorOrganization()));
                        }

                    } else if (voPd!=null && voPd.getName().equalsIgnoreCase("amendGrantId")) {
                        if (grant.getAmendGrantId() != null) {
                            Grant amendGrant = grantService.getById(grant.getAmendGrantId());
                            if (amendGrant == null) {
                                grant.setAmendGrantId(null);
                            }
                        }
                    } else {
                        if(voPd!=null) {
                            voPd.getWriteMethod().invoke(vo, value);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        Collections.sort(vo.getGrantDetails().getSections());
        vo.setFlowAuthorities(
                workflowPermissionService.getGrantFlowPermissions(vo.grantStatus.getId(), vo.getId()));
        vo.setActionAuthorities(workflowPermissionService.getGrantActionPermissions(vo.getGrantorOrganization().getId(),
                user.getUserRoles(), vo.getGrantStatus().getId(), user.getId(), grant.getId()));

        return vo;
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

    public List<List<TableData>> getApprovedReportsDisbursements() {
        return approvedReportsDisbursements;
    }

    public void setApprovedReportsDisbursements(List<List<TableData>> approvedReportsDisbursements) {
        this.approvedReportsDisbursements = approvedReportsDisbursements;
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

    public List<GrantTagVO> getTags() {
        return tags;
    }

    public void setTags(List<GrantTagVO> tags) {
        this.tags = tags;
    }

    public List<GrantAssignmentsVO> getWorkflowAssignments() {
        return workflowAssignments;
    }

    public void setWorkflowAssignments(List<GrantAssignmentsVO> workflowAssignments) {
        this.workflowAssignments = workflowAssignments;
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

    public Double getOngoingDisbursementAmount() {
        return ongoingDisbursementAmount;
    }

    public void setOngoingDisbursementAmount(Double ongoingDisbursementAmount) {
        this.ongoingDisbursementAmount = ongoingDisbursementAmount;
    }

    public Double getActualOngoingDisbursementRecorded() {
        return actualOngoingDisbursementRecorded;
    }

    public void setActualOngoingDisbursementRecorded(Double actualOngoingDisbursementRecorded) {
        this.actualOngoingDisbursementRecorded = actualOngoingDisbursementRecorded;
    }

    public String getOngoingDisbursementNote() {
        return ongoingDisbursementNote;
    }

    public void setOngoingDisbursementNote(String ongoingDisbursementNote) {
        this.ongoingDisbursementNote = ongoingDisbursementNote;
    }
}
