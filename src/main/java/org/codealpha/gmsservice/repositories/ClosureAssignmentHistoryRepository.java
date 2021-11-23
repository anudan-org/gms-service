package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureAssignmentHistory;
import org.codealpha.gmsservice.entities.ReportAssignmentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureAssignmentHistoryRepository extends CrudRepository<ClosureAssignmentHistory, Long> {

    @Query(value = "select * from closure_assignment_history where closure_id=?1 and state_id=?2 order by updated_on desc", nativeQuery = true)
    List<ClosureAssignmentHistory> findByClosureIdAndStateIdOrderByUpdatedOnDesc(Long closureId, Long stateId);

}