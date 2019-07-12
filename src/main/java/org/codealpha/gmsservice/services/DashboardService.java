package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.models.GrantDetailVO;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.models.Tenant;
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


  List<Tenant> tenants;


  public DashboardService build(User user, List<Grant> grants) {
    this.user = user;
    List<String> tenantNames = new ArrayList<>();
    for (Grant grant : grants) {
      if (!tenantNames.contains(grant.getGrantorOrganization().getCode())) {
        tenantNames.add(grant.getGrantorOrganization().getCode());
      }
    }

    tenants = new ArrayList<>();
    for (String name : tenantNames) {
      Tenant tenant = new Tenant();
      tenant.setName(name);
      List<Grant> grantsList = new ArrayList<>();
      tenant.setGrants(grantsList);
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
                  user.getUserRoles(),grant.getGrantStatus().getId()));

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
            }else{
              submission.setOpenForReporting(false);
            }
          }

          GrantVO grantVO = new GrantVO();
          grantVO = grantVO.build(grant, workflowPermissionService, user, appConfigService
              .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                  AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
          grant.setGrantDetails(grantVO.getGrantDetails());
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
