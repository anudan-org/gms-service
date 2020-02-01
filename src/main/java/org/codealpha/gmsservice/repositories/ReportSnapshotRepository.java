package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantSnapshot;
import org.codealpha.gmsservice.entities.ReportSnapshot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReportSnapshotRepository extends CrudRepository<ReportSnapshot,Long> {

    @Query(value = "Select * from report_snapshot where report_id=?1 and assigned_to_id=?2 and status_id=?3 order by id desc limit 1",nativeQuery = true)
    public ReportSnapshot findByReportIdAndAssignedToAndStatusId(Long grantId, Long assignedToId, Long statusId);
}
