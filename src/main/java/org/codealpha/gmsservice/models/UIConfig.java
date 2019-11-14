package org.codealpha.gmsservice.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codealpha.gmsservice.entities.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Developer <developer@enstratify.com>
 **/
@ApiModel("User Interface Configuration and Application data for tenant")
public class UIConfig {

	@ApiModelProperty("Tenant's logo URL")
	private String logoUrl;

	@ApiModelProperty("Basic header styling")
	private String navbarColor;

	@ApiModelProperty("Tenant code that uniquely identifies tenant" )
	private String tenantCode;

	@ApiModelProperty("Basic styling for header text")
	private String navbarTextColor;

	@JsonIgnore
	private List<GrantSection> defaultSections;

	@ApiModelProperty("Initial status of new grant for tenant")
	private WorkflowStatus grantInitialStatus;
	@JsonIgnore
	private WorkflowStatus submissionInitialStatus;
	@ApiModelProperty("Grantee organizations associated with tenant")
	private List<Organization> granteeOrgs;
	@ApiModelProperty("Tenant grant workflow and statuses")
	private List<WorkflowStatus> workflowStatuses;
	@ApiModelProperty("Tenant grant workflow state transitions")
	private List<WorkflowTransitionModel> transitions;
	@ApiModelProperty("Tenant users")
	private List<User> tenantUsers;
	@ApiModelProperty("Days before opening Report for publishing")
	private Integer daysBeforePublishingReport;

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
}
