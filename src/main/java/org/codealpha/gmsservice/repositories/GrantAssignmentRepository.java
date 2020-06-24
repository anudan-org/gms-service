package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantAssignments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantAssignmentRepository extends CrudRepository<GrantAssignments, Long> {

    public List<GrantAssignments> findByGrantIdAndStateId(Long grantId, Long stateId);

    public List<GrantAssignments> findByGrantId(Long grantId);

    public GrantAssignments findByGrantIdAndStateIdAndAssignments(Long grantId, Long stateId, Long userId);

    public GrantAssignments findByGrantIdAndAnchor(Long grantId, boolean anchor);

    @Query(value = "select distinct B.* from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where ( (B.state_id=A.grant_status_id) and ( (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 ) or (C.internal_status='REVIEW' ) ) ) and now()>A.moved_on and A.grantor_org_id not in(?1)", nativeQuery = true)
    public List<GrantAssignments> getActionDueGrantsForPlatform(List<Long> granterIds);

    @Query(value = "select distinct B.* from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where ( (B.state_id=A.grant_status_id) and ( (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 ) or (C.internal_status='REVIEW' )) ) and  now()>A.moved_on and A.grantor_org_id =?1", nativeQuery = true)
    public List<GrantAssignments> getActionDueGrantsForGranterOrg(Long granterId);

}
