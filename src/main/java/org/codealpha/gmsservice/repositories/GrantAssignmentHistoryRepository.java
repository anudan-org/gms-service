package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.GrantAssignmentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GrantAssignmentHistoryRepository extends CrudRepository<GrantAssignmentHistory, Long> {

    @Query(value = "select * from grant_assignment_history where grant_id=?1 and state_id=?2 order by updated_on desc", nativeQuery = true)
    List<GrantAssignmentHistory> findByGrantIdAndStateIdOrderByUpdatedOnDesc(Long grantId, Long stateId);

}