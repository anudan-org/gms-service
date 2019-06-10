package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.List;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.codealpha.gmsservice.repositories.GranterGrantSectionRepository;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GranterService {

  @Autowired
  private GranterRepository granterRepository;
  @Autowired
  private GrantRepository grantRepository;


  public List<Granter> getAllGranters() {
    return (List<Granter>) granterRepository.findAll();
  }

  public List<Grant> getGrantsOfGranterForGrantor(Long granterOrgId, Organization tenantOrg) {

    List<Grant> allGrants = new ArrayList<>();

    if("GRANTER".equalsIgnoreCase(tenantOrg.getType())){
      allGrants
          .addAll(grantRepository.findGrantsOfGranter(granterOrgId));
    }else if("PLATFORM".equalsIgnoreCase(tenantOrg.getType())){
      allGrants.addAll(grantRepository.findGrantsOfGranter(granterOrgId));
    }
    return allGrants;
  }

}
