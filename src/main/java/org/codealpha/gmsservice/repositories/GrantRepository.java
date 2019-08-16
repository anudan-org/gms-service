package org.codealpha.gmsservice.repositories;

import java.util.List;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Granter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Developer <developer@enstratify.com>
 **/
public interface GrantRepository extends CrudRepository<Grant, Long> {

  @Query(value = "select A.* from grants A inner join organizations B on A.grantor_org_id = B.id inner join workflows w on B.id = w.granter_id inner join workflow_statuses ws on w.id = ws.workflow_id inner join workflow_state_permissions wsp on ws.id = wsp.workflow_status_id where B.id =?2 and A.organization_id =?1 and wsp.role_id in (?3) and A.grant_status_id = ws.id", nativeQuery = true)
  public List<Grant> findGrantsOfGranteeForTenantOrg(Long granteeOrgId, Long grantorOrgId,
      List<Long> roleIds);

  @Query(value = "select A.* from grants A where A.grantor_org_id=?1", nativeQuery = true)
  public List<Grant> findGrantsOfGranter(Long grantorOrgId);

  @Query(value = "select A.* from grants A where A.organization_id=?1", nativeQuery = true)
  public List<Grant> findAllGrantsOfGrantee(Long granteeOrgId);

  public Grant findByNameAndGrantorOrganization(String name, Granter granter);

}
