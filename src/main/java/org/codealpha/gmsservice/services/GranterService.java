package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.List;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.entities.UserRole;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.codealpha.gmsservice.repositories.GranterGrantSectionRepository;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
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
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserRoleService userRoleService;
  @Autowired
  private RoleService roleService;

  public List<Granter> getAllGranters() {
    return (List<Granter>) granterRepository.findAll();
  }

  public List<Grant> getGrantsOfGranterForGrantor(Long granterOrgId, Organization tenantOrg, Long userId) {

    List<Grant> allGrants = new ArrayList<>();

    if ("GRANTER".equalsIgnoreCase(tenantOrg.getType())) {
      boolean isAdmin = false;

      for (Role role : userRoleService.findRolesForUser(userRepository.findById(userId).get())) {
        if (role.getName().equalsIgnoreCase("ADMIN")) {
          isAdmin = true;
          break;
        }
      }
      if (!isAdmin) {
        allGrants.addAll(grantRepository.findAssignedGrantsOfGranter(granterOrgId, userId));
      } else {
        allGrants.addAll(grantRepository.findGrantsForAdmin(granterOrgId));
      }
    } else if ("PLATFORM".equalsIgnoreCase(tenantOrg.getType())) {
      allGrants.addAll(grantRepository.findGrantsOfGranter(granterOrgId));
    }
    return allGrants;
  }

  public Organization createGranter(Granter granterOrg, MultipartFile image) {
    // granterOrg = organizationRepository.save(granterOrg);
    // Granter granter = new Granter();

    granterOrg.setHostUrl(granterOrg.getCode().toLowerCase());
    granterOrg.setId(granterOrg.getId());
    granterOrg.setImageName(image.getOriginalFilename());
    granterOrg.setNavbarColor("#232323");
    granterOrg.setNavbarTextColor("#fff");
    return granterRepository.save(granterOrg);
  }

  public Granter getGranterById(Long grnaterId) {
    return granterRepository.findById(grnaterId).get();
  }

  public Long getInProgressGrantsOfGranterForGrantor(Long granterOrgId, Organization tenantOrg, Long userId) {

    if ("GRANTER".equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfInprogressGrantsForGrantor(granterOrgId, userId);
    } else if ("PLATFORM".equalsIgnoreCase(tenantOrg.getType())) {
      return 0l;
    }
    return null;
  }

  public Long getActiveGrantsOfGranteeForGrantor(Long granterOrgId, Organization tenantOrg, Long userId) {

    if ("GRANTER".equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfInprogressGrantsForGrantor(granterOrgId, userId);
    } else if ("PLATFORM".equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfActiveGrantsForGrantee(granterOrgId);
    }
    return null;
  }

  public Long getActiveGrantsOfGranterForGrantor(Long userOrgId, Organization tenantOrg, Long userId) {
    if ("GRANTER".equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfActiveGrantsForGrantor(userOrgId);
    } else if ("PLATFORM".equalsIgnoreCase(tenantOrg.getType())) {
      return 0l;
    }
    return null;
  }

  public Long getClosedGrantsOfGranteeForGrantor(Long granterOrgId, Organization tenantOrg, Long userId) {

    if ("GRANTER".equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfInprogressGrantsForGrantor(granterOrgId, userId);
    } else if ("PLATFORM".equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfClosedGrantsForGrantee(granterOrgId);
    }
    return null;
  }

  public Long getClosedGrantsOfGranterForGrantor(Long userOrgId, Organization tenantOrg, Long userId) {
    if ("GRANTER".equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfClosedGrantsForGrantor(userOrgId);
    } else if ("PLATFORM".equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfClosedGrantsForGrantor(userOrgId);
    }
    return null;
  }
}
