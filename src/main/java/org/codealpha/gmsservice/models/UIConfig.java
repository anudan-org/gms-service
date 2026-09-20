package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.codealpha.gmsservice.entities.*;

import java.util.List;

/**
 * @author Developer code-alpha.org
 **/
@Schema(description="User Interface Configuration and Application data for tenant")
public class UIConfig {

	@Schema(description = "Tenant's logo URL")
	private String logoUrl;

	@Schema(description ="Basic header styling")
	private String navbarColor;

	@Schema(description ="Tenant code that uniquely identifies tenant")
	private String tenantCode;

	@Schema(description ="Basic styling for header text")
	private String navbarTextColor;

	@JsonIgnore
	private List<GrantSection> defaultSections;

	@Schema(description ="Initial status of new grant for tenant")
	private WorkflowStatus grantInitialStatus;
	@JsonIgnore
	private WorkflowStatus submissionInitialStatus;
	@Schema(description ="Grantee organizations associated with tenant")
	private List<Organization> granteeOrgs;
	@Schema(description ="Tenant grant workflow and statuses")
	private List<WorkflowStatus> workflowStatuses;
	@Schema(description ="Tenant report workflow and statuses")
	private List<WorkflowStatus> reportWorkflowStatuses;
	private List<WorkflowStatus> closureWorkflowStatuses;
	@Schema(description ="Tenant grant workflow state transitions")
	private List<WorkflowTransitionModel> transitions;
	@Schema(description ="Tenant report workflow state transitions")
	private List<WorkflowTransitionModel> reportTransitions;
	@Schema(description ="Tenant users")
	private List<User> tenantUsers;
	@Schema(description ="Days before opening Report for publishing")
	private Integer daysBeforePublishingReport;
	@Schema(description ="Template Library of organization")
	private List<TemplateLibrary> templateLibrary;

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getNavbarColor() {
		return navbarColor;
	}

	public void setNavbarColor(String navbarColor) {
		this.navbarColor = navbarColor;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getNavbarTextColor() {
		return navbarTextColor;
	}

	public void setNavbarTextColor(String navbarTextColor) {
		this.navbarTextColor = navbarTextColor;
	}

	public List<GrantSection> getDefaultSections() {
		return defaultSections;
	}

	public void setDefaultSections(
			List<GrantSection> defaultSections) {
		this.defaultSections = defaultSections;
	}

	public WorkflowStatus getGrantInitialStatus() {
		return grantInitialStatus;
	}

	public void setGrantInitialStatus(WorkflowStatus grantInitialStatus) {
		this.grantInitialStatus = grantInitialStatus;
	}

	public WorkflowStatus getSubmissionInitialStatus() {
		return submissionInitialStatus;
	}

	public void setSubmissionInitialStatus(WorkflowStatus submissionInitialStatus) {
		this.submissionInitialStatus = submissionInitialStatus;
	}

	public List<Organization> getGranteeOrgs() {
		return granteeOrgs;
	}

	public void setGranteeOrgs(List<Organization> granteeOrgs) {
		this.granteeOrgs = granteeOrgs;
	}

	public List<WorkflowStatus> getWorkflowStatuses() {
		return workflowStatuses;
	}

	public void setWorkflowStatuses(List<WorkflowStatus> workflowStatuses) {
		this.workflowStatuses = workflowStatuses;
	}

	public List<User> getTenantUsers() {
		return tenantUsers;
	}

	public void setTenantUsers(List<User> tenantUsers) {
		this.tenantUsers = tenantUsers;
	}

	public List<WorkflowTransitionModel> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<WorkflowTransitionModel> transitions) {
		this.transitions = transitions;
	}

	public Integer getDaysBeforePublishingReport() {
		return daysBeforePublishingReport;
	}

	public void setDaysBeforePublishingReport(Integer daysBeforePublishingReport) {
		this.daysBeforePublishingReport = daysBeforePublishingReport;
	}

	public List<WorkflowStatus> getReportWorkflowStatuses() {
		return reportWorkflowStatuses;
	}

	public void setReportWorkflowStatuses(List<WorkflowStatus> reportWorkflowStatuses) {
		this.reportWorkflowStatuses = reportWorkflowStatuses;
	}

	public List<WorkflowTransitionModel> getReportTransitions() {
		return reportTransitions;
	}

	public void setReportTransitions(List<WorkflowTransitionModel> reportTransitions) {
		this.reportTransitions = reportTransitions;
	}

	public List<TemplateLibrary> getTemplateLibrary() {
		return templateLibrary;
	}

	public void setTemplateLibrary(List<TemplateLibrary> templateLibrary) {
		this.templateLibrary = templateLibrary;
	}

	public List<WorkflowStatus> getClosureWorkflowStatuses() {
		return closureWorkflowStatuses;
	}

	public void setClosureWorkflowStatuses(List<WorkflowStatus> closureWorkflowStatuses) {
		this.closureWorkflowStatuses = closureWorkflowStatuses;
	}
}
