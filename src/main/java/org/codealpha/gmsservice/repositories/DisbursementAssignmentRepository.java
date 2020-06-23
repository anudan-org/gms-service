package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.DisbursementAssignment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DisbursementAssignmentRepository extends CrudRepository<DisbursementAssignment, Long> {

    public List<DisbursementAssignment> findByDisbursementId(Long disbursementId);

    @Query(value = "select distinct B.* from disbursements A inner join disbursement_assignments B on B.disbursement_id=A.id inner join workflow_statuses C on C.id=A.status_id inner join grants D on D.id=A.grant_id where ( (B.state_id=A.status_id) and ( (C.internal_status='DRAFT' and (select count(*) from disbursement_history where id=A.id)>0 ) or (C.internal_status='REVIEW' ) ) ) and now()>A.moved_on and D.grantor_org_id not in(?1)", nativeQuery = true)

    public List<DisbursementAssignment> getActionDueDisbursementsForPlatform(List<Long> granterIds);

    @Query(value = "select distinct B.* from disbursements A inner join disbursement_assignments B on B.disbursement_id=A.id inner join workflow_statuses C on C.id=A.status_id inner join grants D on D.id=A.grant_id where ( (B.state_id=A.status_id) and ( (C.internal_status='DRAFT' and (select count(*) from disbursement_history where id=A.id)>0 ) or (C.internal_status='REVIEW' )) ) and  now()>A.moved_on and D.grantor_org_id =?1", nativeQuery = true)
    public List<DisbursementAssignment> getActionDueDisbursementsForOrg(Long granterId);

}
