package org.codealpha.gmsservice.repositories;

import java.util.List;
import org.codealpha.gmsservice.entities.Grant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Developer <developer@enstratify.com>
 **/
public interface GrantRepository extends CrudRepository<Grant, Long> {

  @Query(value = "select A.* from grants A inner join organizations B on A.grantor_org_id=B.id where B.id=?2 and A.organization_id=?1",nativeQuery = true)
  public List<Grant> findGrantsOfGranteeForTenantOrg(Long granteeOrgId, Long grantorOrgId);

  @Query(value = "select A.* from grants A where A.organization_id=?1",nativeQuery = true)
  public List<Grant> findAllGrantsOfGrantee(Long granteeOrgId);


}
