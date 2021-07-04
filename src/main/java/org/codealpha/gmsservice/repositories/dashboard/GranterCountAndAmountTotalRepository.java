package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterCountAndAmountTotal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GranterCountAndAmountTotalRepository extends CrudRepository<GranterCountAndAmountTotal,Long> {

    @Query(value="select row_number() OVER () as id,X.* from (SELECT a.grantor_org_id AS granter_id, sum(a.amount) AS total_grant_amount, count(*) AS total_grants FROM grants a JOIN workflow_statuses b ON a.grant_status_id = b.id WHERE (b.internal_status = 'ACTIVE' OR (b.internal_status = 'CLOSED' and a.amend_grant_id is null)) and a.deleted=false  GROUP BY a.grantor_org_id) X where X.granter_id=?1",nativeQuery = true)
    public GranterCountAndAmountTotal getSummaryForGranter(Long granterId);

    @Query(value="select row_number() OVER () as id,X.* from (SELECT a.grantor_org_id AS granter_id, sum(a.amount) AS total_grant_amount, count(*) AS total_grants FROM grants a JOIN workflow_statuses b ON a.grant_status_id = b.id WHERE (b.internal_status = 'ACTIVE' OR (b.internal_status = 'CLOSED' and a.amend_grant_id is null)) and a.deleted=false  GROUP BY a.grantor_org_id) X where X.granter_id=?1",nativeQuery = true)
    public GranterCountAndAmountTotal getMySummaryForGranter(Long granterId);
}
