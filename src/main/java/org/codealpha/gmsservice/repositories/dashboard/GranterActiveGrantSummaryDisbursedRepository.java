package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterActiveGrantSummaryCommitted;
import org.codealpha.gmsservice.entities.dashboard.GranterActiveGrantSummaryDisbursed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterActiveGrantSummaryDisbursedRepository extends CrudRepository<GranterActiveGrantSummaryDisbursed,Long> {

    @Query(value="select row_number() OVER () as id,* from granter_active_grants_summary_disbursed A where granter_id=?1",nativeQuery = true)
    public List<GranterActiveGrantSummaryDisbursed> getActiveGrantDisbursedSummaryForGranter(Long granterId);
}
