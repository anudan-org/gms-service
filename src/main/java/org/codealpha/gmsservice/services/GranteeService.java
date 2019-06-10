package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.UserRole;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GranteeService {

  @Autowired
  private GrantRepository grantRepository;

  public List<Grant> getGrantsOfGranteeForGrantor(Long granteeOrgId, Organization tenantOrg, List<UserRole> userRoles){
    List<Grant> allGrants = new ArrayList<>();

    if("GRANTER".equalsIgnoreCase(tenantOrg.getOrganizationType())){
      List<Long> userRoleIds = userRoles.stream().map(e->new Long(e.getRole().getId())).collect(
          Collectors.toList());
      allGrants
          .addAll(grantRepository.findGrantsOfGranteeForTenantOrg(granteeOrgId, tenantOrg.getId(),userRoleIds));
    }else if("PLATFORM".equalsIgnoreCase(tenantOrg.getOrganizationType())){
      allGrants.addAll(grantRepository.findAllGrantsOfGrantee(granteeOrgId));
    }
    return allGrants;
  }
}
