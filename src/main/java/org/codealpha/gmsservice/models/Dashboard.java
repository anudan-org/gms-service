package org.codealpha.gmsservice.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codealpha.gmsservice.entities.Grant;

public class Dashboard {

  List<Tenant> tenants;


  public Dashboard build(List<Grant> grants){
    List<String> tenantNames = new ArrayList<>();
    for(Grant grant: grants) {
      if(!tenantNames.contains(grant.getGrantorOrganization().getCode())){
        tenantNames.add(grant.getGrantorOrganization().getCode());
      }
    }

    tenants= new ArrayList<>();
    for(String name:tenantNames){
      Tenant tenant = new Tenant();
      tenant.setName(name);
      List<Grant> grantsList = new ArrayList<>();
      tenant.setGrants(grantsList);
      tenants.add(tenant);
    }

    for(Grant grant:grants){
      for(Tenant tenant:tenants){
        if(tenant.getName().equalsIgnoreCase(grant.getGrantorOrganization().getCode())){
          List<Grant> grantList = tenant.getGrants();
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
