package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowTransitionModel;

import java.util.List;

public class Configuration {

    private List<User> tenantUsers;
    private List<WorkflowStatus> reportWorkflowStatuses;
    private List<WorkflowStatus> grantWorkflowStatuses;
    private List<WorkflowStatus> disbursementWorkflowStatuses;
    private List<WorkflowStatus> closureWorkflowStatuses;
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

    public List<WorkflowStatus> getDisbursementWorkflowStatuses(){
        return this.disbursementWorkflowStatuses;
    }

    public void setDisbursementWorkflowStatuses(List<WorkflowStatus> disbursementWorkflowStatuses){
        this.disbursementWorkflowStatuses = disbursementWorkflowStatuses;
    }

    public List<WorkflowStatus> getClosureWorkflowStatuses() {
        return closureWorkflowStatuses;
    }

    public void setClosureWorkflowStatuses(List<WorkflowStatus> closureWorkflowStatuses) {
        this.closureWorkflowStatuses = closureWorkflowStatuses;
    }
}
