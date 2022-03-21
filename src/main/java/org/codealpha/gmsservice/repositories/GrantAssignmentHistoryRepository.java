package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantAssignmentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantAssignmentHistoryRepository extends CrudRepository<GrantAssignmentHistory, Long> {

    @Query(value = "select * from grant_assignment_history where grant_id=?1 and state_id=?2 order by updated_on desc", nativeQuery = true)
    List<GrantAssignmentHistory> findByGrantIdAndStateIdOrderByUpdatedOnDesc(Long grantId, Long stateId);

}