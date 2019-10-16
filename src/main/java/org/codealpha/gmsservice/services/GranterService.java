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
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GranterService {

  @Autowired
  private GranterRepository granterRepository;
  @Autowired
  private GrantRepository grantRepository;
  @Autowired
  private OrganizationRepository organizationRepository;



  public List<Granter> getAllGranters() {
    return (List<Granter>) granterRepository.findAll();
  }

  public List<Grant> getGrantsOfGranterForGrantor(Long granterOrgId, Organization tenantOrg, Long userId) {

    List<Grant> allGrants = new ArrayList<>();

    if("GRANTER".equalsIgnoreCase(tenantOrg.getType())){
      allGrants
          .addAll(grantRepository.findAssignedGrantsOfGranter(granterOrgId,userId));
    }else if("PLATFORM".equalsIgnoreCase(tenantOrg.getType())){
      allGrants.addAll(grantRepository.findGrantsOfGranter(granterOrgId));
    }
    return allGrants;
  }

  public Organization createGranter(Granter granterOrg, MultipartFile image){
    //granterOrg = organizationRepository.save(granterOrg);
    //Granter granter = new Granter();

      granterOrg.setHostUrl(granterOrg.getCode().toLowerCase());
      granterOrg.setId(granterOrg.getId());
      granterOrg.setImageName(image.getOriginalFilename());
      granterOrg.setNavbarColor("#232323");
      granterOrg.setNavbarTextColor("#fff");
    return granterRepository.save(granterOrg);
  }

  public Granter getGranterById(Long grnaterId){
    return granterRepository.findById(grnaterId).get();
  }

}
