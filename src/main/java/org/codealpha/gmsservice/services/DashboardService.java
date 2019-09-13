package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private User user;

    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private GranterGrantTemplateService granterGrantTemplateService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private TemplateLibraryService templateLibraryService;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkflowStatusService workflowStatusService;


    List<Tenant> tenants;


    public DashboardService build(User user, List<Grant> grants, Organization tenantOrg) {
        this.user = user;
        List<String> tenantNames = new ArrayList<>();
        if (!tenantNames.contains(tenantOrg.getCode())) {
            tenantNames.add(tenantOrg.getCode());
        }

        tenants = new ArrayList<>();
        for (String name : tenantNames) {
            Tenant tenant = new Tenant();
            tenant.setName(name);
            List<Grant> grantsList = new ArrayList<>();
            tenant.setGrants(grantsList);
            tenant.setGrantTemplates(granterGrantTemplateService.findByGranterId(user.getOrganization().getId()));
            tenant.setTemplateLibrary(templateLibraryService.getTemplateLibraryForGranter((Granter) user.getOrganization()));
            tenants.add(tenant);
        }

        for (Grant grant : grants) {
            for (Tenant tenant : tenants) {
                if (tenant.getName().equalsIgnoreCase(grant.getGrantorOrganization().getCode())) {
                    List<Grant> grantList = tenant.getGrants();

                    grant.setActionAuthorities(workflowPermissionService
                            .getGrantActionPermissions(grant.getGrantorOrganization().getId(),
                                    user.getUserRoles(), grant.getGrantStatus().getId()));

                    grant.setFlowAuthorities(workflowPermissionService
                            .getGrantFlowPermissions(grant.getGrantorOrganization().getId(),
                                    user.getUserRoles(), grant.getGrantStatus().getId()));

                    for (Submission submission : grant.getSubmissions()) {
                        submission.setActionAuthorities(workflowPermissionService
                                .getSubmissionActionPermission(grant.getGrantorOrganization().getId(),
                                        user.getUserRoles()));

                        AppConfig submissionWindow = appConfigService
                                .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS);
                        Date submissionWindowStart = new DateTime(submission.getSubmitBy())
                                .minusDays(Integer.valueOf(submissionWindow.getConfigValue()) + 1).toDate();

                        List<WorkFlowPermission> flowPermissions = workflowPermissionService
                                .getSubmissionFlowPermissions(grant.getGrantorOrganization().getId(),
                                        user.getUserRoles(), submission.getSubmissionStatus().getId());

                        if (!flowPermissions.isEmpty() && DateTime.now().toDate()
                                .after(submissionWindowStart)) {
                            submission.setFlowAuthorities(flowPermissions);
                        }

                        if (DateTime.now().toDate()
                                .after(submissionWindowStart)) {
                            submission.setOpenForReporting(true);
                        } else {
                            submission.setOpenForReporting(false);
                        }
                    }

                    GrantVO grantVO = new GrantVO();
                    grantVO = grantVO.build(grant, grantService.getGrantSections(grant), workflowPermissionService, user, appConfigService
                            .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                    AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
                    grant.setGrantDetails(grantVO.getGrantDetails());
                    grant.setGrantTemplate(granterGrantTemplateService.findByTemplateId(grant.getTemplateId()));
                    List<GrantAssignmentsVO> workflowAssignments = new ArrayList<>();
                    for (GrantAssignments assignment : grantService.getGrantWorkflowAssignments(grant)) {
                        GrantAssignmentsVO assignmentsVO = new GrantAssignmentsVO();
                        assignmentsVO.setId(assignment.getId());
                        assignmentsVO.setAnchor(assignment.isAnchor());
                        assignmentsVO.setAssignments(assignment.getAssignments());
                        if(assignment.getAssignments()!=null && assignment.getAssignments()>0) {
                            assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignments()));
                        }
                        assignmentsVO.setGrantId(assignment.getGrantId());
                        assignmentsVO.setStateId(assignment.getStateId());
                        assignmentsVO.setStateName(workflowStatusService.findById(assignment.getStateId()));
                        workflowAssignments.add(assignmentsVO);
                    }
                    grant.setWorkflowAssignment(workflowAssignments);

                    List<GrantAssignments> grantAssignments = grantService.getGrantCurrentAssignments(grant);
                    if (grantAssignments != null) {
                        for (GrantAssignments assignment : grantAssignments) {
                            if (grant.getCurrentAssignment() == null) {
                                List<AssignedTo> assignedToList = new ArrayList<>();
                                grant.setCurrentAssignment(assignedToList);
                            }
                            AssignedTo newAssignedTo = new AssignedTo();
                            if(assignment.getAssignments()!=null && assignment.getAssignments()>0) {
                                newAssignedTo.setUser(userService.getUserById(assignment.getAssignments()));
                            }
                            grant.getCurrentAssignment().add(newAssignedTo);
                        }
                    }
                    grant.getWorkflowAssignment().sort((a,b) -> a.getId().compareTo(b.getId()));
                    grant.getGrantDetails().getSections().sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
                    for (SectionVO section : grant.getGrantDetails().getSections()) {
                        if (section.getAttributes() != null) {
                            section.getAttributes().sort((a, b) -> Long.valueOf(a.getAttributeOrder()).compareTo(Long.valueOf(b.getAttributeOrder())));
                        }
                    }
                    grantList.add(grant);
                    tenant.setGrants(grantList);
                }
            }
        }
        return this;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }
}
