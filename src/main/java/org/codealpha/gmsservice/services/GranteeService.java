package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.repositories.GrantCardRepository;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.codealpha.gmsservice.repositories.GranteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GranteeService {

  @Autowired
  private GrantRepository grantRepository;

  @Autowired
  private GrantCardRepository grantCardRepository;

  @Autowired
  private GranteeRepository granteeRepository;

  public List<Grant> getGrantsOfGranteeForGrantor(Long granteeOrgId, Organization tenantOrg, List<UserRole> userRoles){
    List<Grant> allGrants = new ArrayList<>();

    if("GRANTER".equalsIgnoreCase(tenantOrg.getOrganizationType())){
      List<Long> userRoleIds = userRoles.stream().map(e->e.getRole().getId()).collect(
          Collectors.toList());
      allGrants
          .addAll(grantRepository.findGrantsOfGranteeForTenantOrg(granteeOrgId, tenantOrg.getId(),userRoleIds));
    }else if("PLATFORM".equalsIgnoreCase(tenantOrg.getOrganizationType())){
      allGrants.addAll(grantRepository.findAllGrantsOfGrantee(granteeOrgId));
    }
    return allGrants;
  }

  public List<GrantCard> getGrantCardsOfGranteeForGrantor(Long granteeOrgId, Organization tenantOrg, List<UserRole> userRoles){
    List<GrantCard> allGrants = new ArrayList<>();

    if("GRANTER".equalsIgnoreCase(tenantOrg.getOrganizationType())){
      List<Long> userRoleIds = userRoles.stream().map(e->e.getRole().getId()).collect(
              Collectors.toList());
      allGrants
              .addAll(grantCardRepository.findGrantsOfGranteeForTenantOrg(granteeOrgId, tenantOrg.getId(),userRoleIds));
    }else if("PLATFORM".equalsIgnoreCase(tenantOrg.getOrganizationType())){
      allGrants.addAll(grantCardRepository.findAllGrantsOfGrantee(granteeOrgId));
    }
    return allGrants;
  }

  public Grantee saveGrantee(Grantee org){
    return granteeRepository.save(org);

  }

  public Long getDonorsByState(Long id, String status) {
    return granteeRepository.getDonorsByStatus(id,status);
  }
}
