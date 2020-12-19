package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterActiveUser;
import org.codealpha.gmsservice.entities.dashboard.GranterGrantee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GranterActiveUserRepository extends CrudRepository<GranterActiveUser,Long> {

    @Query(value="select row_number() OVER () as id,X.* from (SELECT b.id AS granter_id, count(*) AS active_users FROM users a JOIN organizations b ON b.id = a.organization_id WHERE a.active = true and a.deleted=false AND b.organization_type = 'GRANTER' GROUP BY b.id) X where X.granter_id=?1",nativeQuery = true)
    public GranterActiveUser getActiveUserSummaryForGranter(Long granterId);
}
