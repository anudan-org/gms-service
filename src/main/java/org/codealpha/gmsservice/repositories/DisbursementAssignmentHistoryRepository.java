package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.DisbursementAssignmentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DisbursementAssignmentHistoryRepository extends CrudRepository<DisbursementAssignmentHistory, Long> {

    @Query(value = "select * from disbursement_assignment_history where disbursement_id=?1 and state_id=?2 order by updated_on desc", nativeQuery = true)
    List<DisbursementAssignmentHistory> findByDisbursementIdAndStateIdOrderByUpdatedOnDesc(Long disbursementId,
            Long stateId);
}