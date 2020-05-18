package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportsCountPerGrant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportsCountPerGrantRepository extends CrudRepository<ReportsCountPerGrant,Long> {

    @Query(value = "select a.id,count(c.*) from grants a inner join workflow_statuses b on b.id=a.grant_status_id inner join reports c on c.grant_id=a.id inner join workflow_statuses d on d.id=c.status_id where b.internal_status=?2 and d.internal_status='CLOSED' and a.grantor_org_id=?1 group by a.id",nativeQuery = true)
    public List<ReportsCountPerGrant> findGrantCountsByReportNumbersAndStatusForGranter(Long granterId, String status);
}
