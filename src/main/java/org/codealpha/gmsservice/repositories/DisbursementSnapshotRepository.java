package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.DisbursementSnapshot;
import org.springframework.data.repository.CrudRepository;

public interface DisbursementSnapshotRepository extends CrudRepository<DisbursementSnapshot,Long> {
    
    public DisbursementSnapshot findByDisbursementIdAndStatusId(Long disbursementId,Long statusId);
}