package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class GranterService {

  public static final String GRANTER = "GRANTER";
  public static final String PLATFORM = "PLATFORM";
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
  @PersistenceContext
  private EntityManager entityManager;

  private final Logger logger = LoggerFactory.getLogger(GranterService.class);

  public List<Granter> getAllGranters() {
    return (List<Granter>) granterRepository.findAll();
  }

  public List<GrantCard> getGrantsOfGranterForGrantor(Long granterOrgId, Organization tenantOrg, Long userId, String forStatus) {

    List<GrantCard> allGrants = new ArrayList<>();

    if (GRANTER.equalsIgnoreCase(tenantOrg.getType())) {
      boolean isAdmin = false;

      User user = userRepository.findById(userId).orElse(null);
      if (user != null) {
        for (Role role : userRoleService.findRolesForUser(user)) {
          if (role.getName().equalsIgnoreCase("ADMIN")) {
            isAdmin = true;
            break;
          }
        }
      }
      if (!isAdmin) {
        allGrants.addAll(getGrantsForUser("LISTNONADMINGRANTS", granterOrgId, userId));
      } else {
        if (forStatus.equalsIgnoreCase("inprogress")) {
          allGrants.addAll(getGrantsForUser("LISTINPROGRESSGRANTS", granterOrgId, userId));
        } else if (forStatus.equalsIgnoreCase("active")) {
          allGrants.addAll(getGrantsForUser("LISTACTIVEGRANTS", granterOrgId, userId));
        } else if (forStatus.equalsIgnoreCase("closed")) {
          allGrants.addAll(getGrantsForUser("LISTCLOSEDGRANTS", granterOrgId, userId));
        }
      }
    } else if (PLATFORM.equalsIgnoreCase(tenantOrg.getType())) {
      //Do nothin
    }
    return allGrants;
  }



  private List<GrantCard> getGrantsForUser(String forQuery, Long granterId, Long userId) {


    try {
      Query q = entityManager.createNamedQuery(forQuery);
      Set<Parameter<?>> params =  q.getParameters();

      for (Parameter<?> p : params) {
        if (p.getName().equalsIgnoreCase("granterId")) {
          q.setParameter(p.getName(), granterId);
        } else if (p.getName().equalsIgnoreCase("userId")) {
          q.setParameter(p.getName(), userId);
        }
      }
      return Collections.checkedList(q.getResultList(), GrantCard.class);
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
    }
    return Collections.emptyList();
  }

  public Organization createGranter(Granter granterOrg, MultipartFile image) {
    granterOrg.setHostUrl(granterOrg.getCode().toLowerCase());
    granterOrg.setId(granterOrg.getId());
    granterOrg.setImageName(image.getOriginalFilename());
    granterOrg.setNavbarColor("#232323");
    granterOrg.setNavbarTextColor("#fff");
    granterOrg = granterRepository.save(granterOrg);
    return granterOrg;
  }

  public Granter getGranterById(Long granterId) {
    return granterRepository.findById(granterId).orElse(null);
  }

  public Long getInProgressGrantsOfGranterForGrantor(Long granterOrgId, Organization tenantOrg, Long userId) {

    if (GRANTER.equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfInprogressGrantsForGrantor(granterOrgId, userId);
    } else if (PLATFORM.equalsIgnoreCase(tenantOrg.getType())) {
      return 0l;
    }
    return null;
  }

  public Long getActiveGrantsOfGranteeForGrantor(Long granterOrgId, Organization tenantOrg, Long userId) {

    if (GRANTER.equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfInprogressGrantsForGrantor(granterOrgId, userId);
    } else if (PLATFORM.equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfActiveGrantsForGrantee(granterOrgId);
    }
    return null;
  }

  public Long getActiveGrantsOfGranterForGrantor(Long userOrgId, Organization tenantOrg) {
    if (GRANTER.equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfActiveGrantsForGrantor(userOrgId);
    } else if (PLATFORM.equalsIgnoreCase(tenantOrg.getType())) {
      return 0l;
    }
    return null;
  }

  public Long getClosedGrantsOfGranteeForGrantor(Long granterOrgId, Organization tenantOrg, Long userId) {

    if (GRANTER.equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfInprogressGrantsForGrantor(granterOrgId, userId);
    } else if (PLATFORM.equalsIgnoreCase(tenantOrg.getType())) {
      return grantRepository.countOfClosedGrantsForGrantee(granterOrgId);
    }
    return null;
  }

  public Long getClosedGrantsOfGranterForGrantor(Long userOrgId) {
      return grantRepository.countOfClosedGrantsForGrantor(userOrgId);
  }
}
