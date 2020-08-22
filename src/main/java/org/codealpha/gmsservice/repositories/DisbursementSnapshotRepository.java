package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.DisbursementSnapshot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DisbursementSnapshotRepository extends CrudRepository<DisbursementSnapshot, Long> {

    public DisbursementSnapshot findByDisbursementIdAndStatusId(Long disbursementId, Long statusId);

    @Query(value = "select * from disbursement_snapshot where disbursement_id=?1 order by id desc limit 1", nativeQuery = true)
    public DisbursementSnapshot findByMostRecentByDisbursementId(Long disbursementId);
}