package org.codealpha.gmsservice.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.services.GrantService;
import org.codealpha.gmsservice.services.GranteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
public class Dashboard {

  private User user;

  @Autowired
  private GrantService grantService;

  List<Tenant> tenants;


  public Dashboard build(User user, List<Grant> grants) {
    this.user=user;
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
          grantList.add(new GrantVO().build(grant));
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
