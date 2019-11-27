package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantSnapshot;
import org.codealpha.gmsservice.entities.Report;
import org.codealpha.gmsservice.entities.ReportSnapshot;
import org.codealpha.gmsservice.repositories.GrantSnapshotRepository;
import org.codealpha.gmsservice.repositories.ReportSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportSnapshotService {

    @Autowired
    private ReportSnapshotRepository reportSnapshotRepository;

    public ReportSnapshot getSnapshotByReportIdAndAssignedToIdAndStatusId(Long reportId, Long assignedToId, Long statusId){
        return reportSnapshotRepository.findByReportIdAndAssignedToAndStatusId(reportId,assignedToId,statusId);
    }

    public ReportSnapshot saveReportSnapshot(ReportSnapshot snapshot){
        return reportSnapshotRepository.save(snapshot);
    }
}
