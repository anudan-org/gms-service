package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranteeReportStatus;
import org.codealpha.gmsservice.entities.dashboard.GranterReportStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Cacheable;
import java.util.List;

@Cacheable(value = false)
public interface GranteeReportStatusRepository extends CrudRepository<GranteeReportStatus,Long> {


    @Query(value = "select row_number() OVER () as id,c.id granter_id,c.name as internal_status,'' as status,count(d.*) as count from grants a\n" +
            "inner join workflow_statuses b on b.id=a.grant_status_id\n" +
            "inner join organizations c on c.id=a.grantor_org_id\n" +
            "inner join reports d on d.grant_id=a.id\n" +
            "inner join workflow_statuses e on e.id=d.status_id\n" +
            "where b.internal_status=?2 and a.organization_id=?1 and a.deleted=false\n" +
            "and e.internal_status='CLOSED'\n" +
            "group by c.id",nativeQuery = true)
    List<GranteeReportStatus> getReportApprovedStatusSummaryForGranteeAndStatusByGranter(Long id, String status);
}
