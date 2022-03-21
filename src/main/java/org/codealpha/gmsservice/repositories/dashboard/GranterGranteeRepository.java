package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterGrantee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GranterGranteeRepository extends CrudRepository<GranterGrantee,Long> {

    @Query(value="select row_number() OVER () as id,X.* from (SELECT DISTINCT a.grantor_org_id AS granter_id, count(DISTINCT a.organization_id) AS grantee_totals FROM grants a JOIN workflow_statuses b ON a.grant_status_id = b.id WHERE (b.internal_status = 'ACTIVE' OR b.internal_status = 'CLOSED') and a.deleted=false GROUP BY a.grantor_org_id) X where granter_id=?1",nativeQuery = true)
    public GranterGrantee getGranteeSummaryForGranter(Long granterId);
    @Query(value="select row_number() OVER () as id,X.* from (select count(distinct(a.organization_id)) grantee_totals from grants a\n" +
            "inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id\n" +
            "inner join workflow_statuses c on c.id=a.grant_status_id\n" +
            "where b.assignments=?1 and (c.internal_status=?2)) X",nativeQuery = true)
    public GranterGrantee getMyGranteeSummaryForGranter(Long userId,String status);
}
