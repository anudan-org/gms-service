package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.GrantToFix;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GrantToFixRepository extends CrudRepository<GrantToFix, Long> {

    @Query(value = "select B.id,(select reference_no from grants where id=A.grant_id),A.value,B.string_attributes,B.grant_id,(select name from workflow_statuses where id=B.grant_status_id) status from grant_string_attributes A inner join grant_snapshot B on B.grant_id=A.grant_id where A.id in(8537,8445,8491,8583,8675,8017,9000) order by A.grant_id", nativeQuery = true)
    public List<GrantToFix> getGrantsToFix();

}
