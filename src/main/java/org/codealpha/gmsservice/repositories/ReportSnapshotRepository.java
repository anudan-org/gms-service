package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportSnapshot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportSnapshotRepository extends CrudRepository<ReportSnapshot, Long> {

    @Query(value = "Select * from report_snapshot where report_id=?1 and assigned_to_id=?2 and status_id=?3 order by id desc limit 1", nativeQuery = true)
    public ReportSnapshot findByReportIdAndAssignedToAndStatusId(Long grantId, Long assignedToId, Long statusId);

    @Query(value = "select * from report_snapshot where report_id=?1 order by id desc limit 1", nativeQuery = true)
    public ReportSnapshot findByMostRecentByReportId(Long reportId);

    @Query(value = "select * from report_snapshot where report_id=?1 order by id DESC ,id desc", nativeQuery = true)
    List<ReportSnapshot> getReportShanpshotsForReport(Long reportId);
}
