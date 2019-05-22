package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.List;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GranteeService {

  @Autowired
  private GrantRepository grantRepository;

  public List<Grant> getGrantsOfGranteeForGrantor(Long granteeOrgId, Organization tenantOrg, Long userRoleId){
    List<Grant> allGrants = new ArrayList<>();

    if("GRANTER".equalsIgnoreCase(tenantOrg.getOrganizationType())){
      allGrants
          .addAll(grantRepository.findGrantsOfGranteeForTenantOrg(granteeOrgId, tenantOrg.getId(),userRoleId));
    }else if("PLATFORM".equalsIgnoreCase(tenantOrg.getOrganizationType())){
      allGrants.addAll(grantRepository.findAllGrantsOfGrantee(granteeOrgId));
    }
    return allGrants;
  }
}
