package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportAssignmentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportAssignmentHistoryRepository extends CrudRepository<ReportAssignmentHistory, Long> {

    @Query(value = "select * from report_assignment_history where report_id=?1 and state_id=?2 order by updated_on desc", nativeQuery = true)
    List<ReportAssignmentHistory> findByReportIdAndStateIdOrderByUpdatedOnDesc(Long reportId, Long stateId);

}