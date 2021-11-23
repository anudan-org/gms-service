package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.ClosureSnapshot;
import org.codealpha.gmsservice.repositories.ClosureSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClosureSnapshotService {

    @Autowired
    private ClosureSnapshotRepository closureSnapshotRepository;

    public ClosureSnapshot getSnapshotByClosureIdAndAssignedToIdAndStatusId(Long reportId, Long assignedToId,
                                                                            Long statusId) {
        return closureSnapshotRepository.findByClosureIdAndAssignedToAndStatusId(reportId, assignedToId, statusId);
    }

    public ClosureSnapshot getMostRecentSnapshotByClosureId(Long reportId) {
        return closureSnapshotRepository.findByMostRecentByClosureId(reportId);
    }

    public ClosureSnapshot saveClosureSnapshot(ClosureSnapshot snapshot) {
        return closureSnapshotRepository.save(snapshot);
    }

    public List<ClosureSnapshot> getClosureSnapshotForClosure(Long closureId) {
        return closureSnapshotRepository.getClosureSnapshotsForReport(closureId);
    }
}
