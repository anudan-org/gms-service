package org.codealpha.gmsservice.models;

import java.util.List;
import org.codealpha.gmsservice.entities.GrantSection;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkflowStatus;

/**
 * @author Developer <developer@enstratify.com>
 **/
public class UIConfig {

	private String logoUrl;

	private String navbarColor;

	private String tenantCode;

	private String navbarTextColor;

	private List<GrantSection> defaultSections;

	private WorkflowStatus grantInitialStatus;
	private WorkflowStatus submissionInitialStatus;
	private List<Organization> granteeOrgs;
	private List<WorkflowStatus> workflowStatuses;
	private List<User> tenantUsers;

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
}
