package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureReason;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureReasonsRepository extends CrudRepository<ClosureReason,Long> {
    @Query(value = "select * from closure_reasons where organization_id=?1",nativeQuery = true)
    public List<ClosureReason> getClosureReasonsForOrg(Long organizationId);
}
