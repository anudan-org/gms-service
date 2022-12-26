package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportsCountPerGrant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportsCountPerGrantRepository extends CrudRepository<ReportsCountPerGrant,Long> {

   @Query(value = "select a.id,count(r.id) from grants a inner join workflow_statuses b on b.id=a.grant_status_id  left outer join  ( select c.* from reports c   inner join workflow_statuses d on d.id=c.status_id and d.internal_status='CLOSED' and c.deleted =false) r on r.grant_id = a.id where b.internal_status='CLOSED' and a.amend_grant_id is null and a.deleted =false and a.grantor_org_id=?1 group by a.id",nativeQuery = true)
    public List<ReportsCountPerGrant> findGrantCountsByReportNumbersAndStatusForGranter(Long granterId, String status);
}
