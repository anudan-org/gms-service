package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantSnapshot;
import org.codealpha.gmsservice.repositories.GrantSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantSnapshotService {

    @Autowired
    private GrantSnapshotRepository grantSnapshotRepository;

    public GrantSnapshot getSnapshotByGrantIdAndAssignedToIdAndStatusId(Long grantId, Long assignedToId,Long statusId){
        return grantSnapshotRepository.findByGrantIdAndAssignedToAndStatusId(grantId,assignedToId,statusId);
    }

    public GrantSnapshot saveGrantSnapshot(GrantSnapshot snapshot){
        return grantSnapshotRepository.save(snapshot);
    }
}
