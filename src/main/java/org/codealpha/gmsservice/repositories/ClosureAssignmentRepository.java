package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureAssignments;
import org.codealpha.gmsservice.entities.ReportAssignment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureAssignmentRepository extends CrudRepository<ClosureAssignments,Long> {
    List<ClosureAssignments> findByClosureId(Long id);

    @Query(value = "select distinct B.*,A.moved_on from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.state_id=A.status_id) and ( (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 ) or (C.internal_status='REVIEW' ) ) ) and  now()>A.moved_on and Z.grantor_org_id not in(?1) and Z.deleted=false",nativeQuery = true)
    List<ClosureAssignments> getActionDueClosuresForPlatform(List<Long> granterIds);

    @Query(value = "select distinct B.* from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.state_id=A.status_id) and ( (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 ) or (C.internal_status='REVIEW' )) ) and   now()>A.moved_on and Z.grantor_org_id =?1 and Z.deleted=false",nativeQuery = true)
    List<ClosureAssignments> getActionDueClosuresForGranterOrg(Long granterId);
}
