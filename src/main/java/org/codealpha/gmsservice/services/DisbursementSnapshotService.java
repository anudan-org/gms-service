package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.DisbursementSnapshot;
import org.codealpha.gmsservice.repositories.DisbursementSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisbursementSnapshotService {

    @Autowired
    private DisbursementSnapshotRepository disbursementSnapshotRepository;
    
    public DisbursementSnapshot getSnapshotByDisbursementIdAndAssignedToIdAndStatusId(Long disbursementId, Long assignedToId, Long statusId){
        return disbursementSnapshotRepository.findByDisbursementIdAndAssignedToIdAndStatusId(disbursementId,assignedToId,statusId);
    }

    public DisbursementSnapshot saveSnapShot(DisbursementSnapshot snapshot){
        return disbursementSnapshotRepository.save(snapshot);
    }
}