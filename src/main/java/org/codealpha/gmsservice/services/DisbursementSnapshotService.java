package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.DisbursementSnapshot;
import org.codealpha.gmsservice.repositories.DisbursementSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisbursementSnapshotService {

    @Autowired
    private DisbursementSnapshotRepository disbursementSnapshotRepository;

    public DisbursementSnapshot getSnapshotByDisbursementIdAndStatusId(Long disbursementId, Long statusId) {
        return disbursementSnapshotRepository.findByDisbursementIdAndStatusId(disbursementId, statusId);
    }

    public DisbursementSnapshot getMostRecentSnapshotByDisbursementId(Long disbursementId) {
        return disbursementSnapshotRepository.findByMostRecentByDisbursementId(disbursementId);
    }

    public DisbursementSnapshot saveSnapShot(DisbursementSnapshot snapshot) {
        return disbursementSnapshotRepository.save(snapshot);
    }
}