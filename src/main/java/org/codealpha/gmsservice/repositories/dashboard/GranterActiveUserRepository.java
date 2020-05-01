package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterActiveUser;
import org.codealpha.gmsservice.entities.dashboard.GranterGrantee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GranterActiveUserRepository extends CrudRepository<GranterActiveUser,Long> {

    @Query(value="select row_number() OVER () as id,* from granter_active_users A where granter_id=?1",nativeQuery = true)
    public GranterActiveUser getActiveUserSummaryForGranter(Long granterId);
}
