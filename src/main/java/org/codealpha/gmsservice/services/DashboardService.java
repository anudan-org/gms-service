package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.List;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.models.Tenant;
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
      grant.getKpis();
      if (!tenantNames.contains(grant.getGrantorOrganization().getCode())) {
        tenantNames.add(grant.getGrantorOrganization().getCode());
      }
    }

    tenants = new ArrayList<>();
    for (String name : tenantNames) {
      Tenant tenant = new Tenant();
      tenant.setName(name);
      List<GrantVO> grantsList = new ArrayList<>();
      tenant.setGrants(grantsList);
      tenants.add(tenant);
    }

    for (Grant grant : grants) {
      for (Tenant tenant : tenants) {
        if (tenant.getName().equalsIgnoreCase(grant.getGrantorOrganization().getCode())) {
          List<GrantVO> grantList = tenant.getGrants();
          GrantVO grantVO = new GrantVO().build(grant, workflowPermissionService, user,appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
              AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
          grantList.add(grantVO);
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
