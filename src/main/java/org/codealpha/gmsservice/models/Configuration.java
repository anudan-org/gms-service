package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowTransitionModel;

import java.util.List;

public class Configuration {

    private List<User> tenantUsers;
    private List<WorkflowStatus> reportWorkflowStatuses;
    private List<WorkflowStatus> grantWorkflowStatuses;
    List<WorkflowTransitionModel> reportTransitions;

    public List<User> getTenantUsers() {
        return tenantUsers;
    }

    public void setTenantUsers(List<User> tenantUsers) {
        this.tenantUsers = tenantUsers;
    }

    public List<WorkflowStatus> getReportWorkflowStatuses() {
        return reportWorkflowStatuses;
    }

    public void setReportWorkflowStatuses(List<WorkflowStatus> reportWorkflowStatuses) {
        this.reportWorkflowStatuses = reportWorkflowStatuses;
    }

    public List<WorkflowStatus> getGrantWorkflowStatuses() {
        return grantWorkflowStatuses;
    }

    public void setGrantWorkflowStatuses(List<WorkflowStatus> grantWorkflowStatuses) {
        this.grantWorkflowStatuses = grantWorkflowStatuses;
    }

    public List<WorkflowTransitionModel> getReportTransitions() {
        return reportTransitions;
    }

    public void setReportTransitions(List<WorkflowTransitionModel> reportTransitions) {
        this.reportTransitions = reportTransitions;
    }
}
