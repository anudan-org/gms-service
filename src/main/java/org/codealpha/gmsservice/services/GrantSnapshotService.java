package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantSnapshot;
import org.codealpha.gmsservice.repositories.GrantSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrantSnapshotService {

    @Autowired
    private GrantSnapshotRepository grantSnapshotRepository;

    public GrantSnapshot getSnapshotByGrantIdAndAssignedToIdAndStatusId(Long grantId, Long assignedToId,
            Long statusId) {
        return grantSnapshotRepository.findByGrantIdAndAssignedToAndStatusId(grantId, assignedToId, statusId);
    }

    public GrantSnapshot getMostRecentSnapshotByGrantId(Long grantId) {
        return grantSnapshotRepository.findByMostRecentByGrantId(grantId);
    }

    public GrantSnapshot saveGrantSnapshot(GrantSnapshot snapshot) {
        return grantSnapshotRepository.save(snapshot);
    }

    public List<GrantSnapshot> getGrantSnapshotForGrant(Long grantId) {
        return grantSnapshotRepository.getGrantShanpshotsForGrant(grantId);
    }
}
