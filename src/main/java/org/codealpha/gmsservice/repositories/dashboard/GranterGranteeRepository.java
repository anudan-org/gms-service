package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterCountAndAmountTotal;
import org.codealpha.gmsservice.entities.dashboard.GranterGrantee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GranterGranteeRepository extends CrudRepository<GranterGrantee,Long> {

    @Query(value="select row_number() OVER () as id,* from granter_grantees A where granter_id=?1",nativeQuery = true)
    public GranterGrantee getGranteeSummaryForGranter(Long granterId);
}
