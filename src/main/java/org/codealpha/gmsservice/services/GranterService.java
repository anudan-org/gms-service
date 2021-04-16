package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

import javax.persistence.*;

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
  @PersistenceContext
  private EntityManager entityManager;

  private final Logger logger = LoggerFactory.getLogger(GranterService.class);

  private static String INPROGRESS_GRANTS_FOR_ADMIN = "select distinct A.id,A.name, approved_reports_for_grant(a.id) approved_reports_for_grant, disbursed_amount_for_grant(a.id) approved_disbursements_total, project_documents_for_grant(a.id) project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=%1 and A.deleted=false and ( (B.anchor=true and B.assignments=%2) or (B.assignments=%2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 ) or (C.internal_status='REVIEW') ) order by A.updated_at desc";
  private static String ACTIVE_GRANTS_FOR_ADMIN = "select distinct A.*,approved_reports_for_grant(a.id) approved_reports_for_grant, disbursed_amount_for_grant(a.id) approved_disbursements_total, project_documents_for_grant(a.id) project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=%1 and A.deleted=false and ( (C.internal_status='ACTIVE') ) order by A.updated_at desc";

  public List<Granter> getAllGranters() {
    return (List<Granter>) granterRepository.findAll();
  }

  public List<GrantCard> getGrantsOfGranterForGrantor(Long granterOrgId, Organization tenantOrg, Long userId, String forStatus) {

    List<GrantCard> allGrants = new ArrayList<>();

    if ("GRANTER".equalsIgnoreCase(tenantOrg.getType())) {
      boolean isAdmin = false;

      for (Role role : userRoleService.findRolesForUser(userRepository.findById(userId).get())) {
        if (role.getName().equalsIgnoreCase("ADMIN")) {
          isAdmin = true;
          break;
        }
      }
      if (!isAdmin) {
        allGrants.addAll(getGrantsForUser("LISTNONADMINGRANTS",granterOrgId, userId));
      } else {
        if(forStatus.equalsIgnoreCase("inprogress")) {
          allGrants.addAll(getGrantsForUser("LISTINPROGRESSGRANTS",granterOrgId,userId));
        }else if(forStatus.equalsIgnoreCase("active")) {
          allGrants.addAll(getGrantsForUser("LISTACTIVEGRANTS",granterOrgId,userId));
        }else if(forStatus.equalsIgnoreCase("closed")) {
          allGrants.addAll(getGrantsForUser("LISTCLOSEDGRANTS",granterOrgId,userId));
        }
      }
    } else if ("PLATFORM".equalsIgnoreCase(tenantOrg.getType())) {
      //allGrants.addAll(grantRepository.findGrantsOfGranter(granterOrgId));
    }
    return allGrants;
  }



  private List<GrantCard> getGrantsForUser(String _for, Long granterId, Long userId) {


    try {
      Query q = entityManager.createNamedQuery(_for);
      Set<Parameter<?>> params =  q.getParameters();

      for(Parameter p : params){
        if(p.getName().equalsIgnoreCase("granterId")){
          q.setParameter(p.getName(),granterId);
        }else if(p.getName().equalsIgnoreCase("userId")){
          q.setParameter(p.getName(),userId);
        }
      }
     List<GrantCard> grants = Collections.checkedList(q.getResultList(), GrantCard.class);
     return grants;
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
    }
    return null;
  }

  public Organization createGranter(Granter granterOrg, MultipartFile image) {
    // granterOrg = organizationRepository.save(granterOrg);
    // Granter granter = new Granter();

    granterOrg.setHostUrl(granterOrg.getCode().toLowerCase());
    granterOrg.setId(granterOrg.getId());
    granterOrg.setImageName(image.getOriginalFilename());
    granterOrg.setNavbarColor("#232323");
    granterOrg.setNavbarTextColor("#fff");
    granterOrg = granterRepository.save(granterOrg);
    return (Organization) granterOrg;
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
