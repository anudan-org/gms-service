package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterActiveGrantSummaryCommitted;
import org.codealpha.gmsservice.entities.dashboard.GranterGrantee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GranterActiveGrantSummaryCommittedRepository extends CrudRepository<GranterActiveGrantSummaryCommitted,Long> {

    @Query(value="select row_number() OVER () as id,* from granter_active_grants_summary_committed A where granter_id=?1",nativeQuery = true)
    public GranterActiveGrantSummaryCommitted getActiveGrantCommittedSummaryForGranter(Long granterId);
}
