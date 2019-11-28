package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ReportVO {

    private static Logger logger = LoggerFactory.getLogger(ReportVO.class);
    
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
    private ReportDetailVO reportDetails;
    private List<ReportAssignment> assignments;
    @JsonIgnore
    private List<ReportStringAttribute> stringAttributes;
    private Grant grant;
    private String securityCode;
    private String note;
    private Date noteAdded;
    private Long noteAddedBy;
    private User noteAddedByUser;
    private List<AssignedTo> currentAssignment;
    private List<ReportAssignmentsVO> workflowAssignments;
    private boolean canManage;
    private String stDate;
    private String enDate;
    private String dDate;
    private List<WorkFlowPermission> flowAuthorities;
    private List<User> granteeUsers;

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

    public void setAssignments(List<ReportAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<ReportAssignment> getAssignments() {
        return assignments;
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

    public List<ReportAssignmentsVO> getWorkflowAssignments() {
        return workflowAssignments;
    }

    public void setWorkflowAssignments(List<ReportAssignmentsVO> workflowAssignments) {
        this.workflowAssignments = workflowAssignments;
    }

    public boolean isCanManage() {
        return canManage;
    }

    public void setCanManage(boolean canManage) {
        this.canManage = canManage;
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

    public String getdDate() {
        return dDate;
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

    // BUILD THE VO

    public ReportVO build(Report report, List<ReportSpecificSection> sections,UserService userService) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(report.getClass());
        ReportVO vo = new ReportVO();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            if (!descriptor.getName().equalsIgnoreCase("class")) {
                try {
                    Object value = descriptor.getReadMethod().invoke(report);
                    PropertyDescriptor voPd = BeanUtils
                            .getPropertyDescriptor(vo.getClass(), descriptor.getName());
                    if (voPd.getName().equalsIgnoreCase("stringAttributes")) {
                        ReportDetailVO reportDetailVO = null;
                        reportDetailVO = vo.getReportDetails();
                        if(reportDetailVO == null){
                            reportDetailVO = new ReportDetailVO();
                        }
                        reportDetailVO = reportDetailVO.buildStringAttributes(sections, (List<ReportStringAttribute>) value);
                        vo.setReportDetails(reportDetailVO);
                    }else if (voPd.getName().equalsIgnoreCase("noteAddedBy") || voPd.getName().equalsIgnoreCase("noteAddedByUser")) {
                        vo.setNoteAddedBy(report.getNoteAddedBy());
                        if(report.getNoteAddedBy()!=null) {
                            vo.setNoteAddedByUser(userService.getUserById(report.getNoteAddedBy()));
                        }

                    } else {
                        voPd.getWriteMethod().invoke(vo, value);
                    }
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        Collections.sort(vo.getReportDetails().getSections());


        return vo;
    }
}
