package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureReason;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureReasonRepository extends CrudRepository<ClosureReason,Long> {
    @Query(value = "select * from closure_reasons where organization_id=?1",nativeQuery = true)
    public List<ClosureReason> getClosureReasonsForOrg(Long organizationId);

    @Query(value = "select  count(*) from grant_closure c, closure_reasons r where c.reason = r.id and r.organization_id  =?1 and c.reason =?2 and c.deleted=false group by c.reason",nativeQuery = true)
    public Long getReasonUsageCount(Long organizationId, Long reasonId);

    
}
