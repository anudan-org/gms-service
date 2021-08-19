package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grantee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer code-alpha.org
 **/
@Repository
public interface GranteeRepository extends CrudRepository<Grantee, Long> {

    @Query(value = "select count(distinct grantor_org_id) from grants a\n" +
            "inner join workflow_statuses b on a.grant_status_id=b.id\n" +
            "where b.internal_status=?2 and a.deleted=false\n" +
            "and a.organization_id=?1",nativeQuery = true)
    Long getDonorsByStatus(Long id, String status);
}
