package org.codealpha.gmsservice.models;

import java.util.List;
import java.util.Map;

public class SecureReportEntity {
    private Long reportId;
    private String userId;
    private Long templateId;
    private Map<Long, List<Long>> sectionAndAtrribIds;
    private Long granterId;
    private String tenantCode;
    private List<Long> grantWorkflowIds;
    private Map<Long,List<Long>> workflowStatusIds;
    private Map<Long,Long[][]> workflowStatusTransitionIds;
    private List<Long> grantTemplateIds;
    private List<Long> templateLibraryIds;


    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Map<Long, List<Long>> getSectionAndAtrribIds() {
        return sectionAndAtrribIds;
    }

    public void setSectionAndAtrribIds(Map<Long, List<Long>> sectionAndAtrribIds) {
        this.sectionAndAtrribIds = sectionAndAtrribIds;
    }

    public Long getGranterId() {
        return granterId;
    }

    public void setGranterId(Long granterId) {
        this.granterId = granterId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public List<Long> getGrantWorkflowIds() {
        return grantWorkflowIds;
    }

    public void setGrantWorkflowIds(List<Long> grantWorkflowIds) {
        this.grantWorkflowIds = grantWorkflowIds;
    }

    public Map<Long, List<Long>> getWorkflowStatusIds() {
        return workflowStatusIds;
    }

    public void setWorkflowStatusIds(Map<Long, List<Long>> workflowStatusIds) {
        this.workflowStatusIds = workflowStatusIds;
    }

    public Map<Long, Long[][]> getWorkflowStatusTransitionIds() {
        return workflowStatusTransitionIds;
    }

    public void setWorkflowStatusTransitionIds(Map<Long, Long[][]> workflowStatusTransitionIds) {
        this.workflowStatusTransitionIds = workflowStatusTransitionIds;
    }

    public List<Long> getGrantTemplateIds() {
        return grantTemplateIds;
    }

    public void setGrantTemplateIds(List<Long> grantTemplateIds) {
        this.grantTemplateIds = grantTemplateIds;
    }

    public List<Long> getTemplateLibraryIds() {
        return templateLibraryIds;
    }

    public void setTemplateLibraryIds(List<Long> templateLibraryIds) {
        this.templateLibraryIds = templateLibraryIds;
    }
}
