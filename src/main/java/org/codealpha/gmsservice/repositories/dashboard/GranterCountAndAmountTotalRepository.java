package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterCountAndAmountTotal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GranterCountAndAmountTotalRepository extends CrudRepository<GranterCountAndAmountTotal,Long> {

    @Query(value="select row_number() OVER () as id,* from granter_count_and_amount_totals A where granter_id=?1",nativeQuery = true)
    public GranterCountAndAmountTotal getSummaryForGranter(Long granterId);
}
