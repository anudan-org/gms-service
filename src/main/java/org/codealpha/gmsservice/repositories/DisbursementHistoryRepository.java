package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.DisbursementHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface DisbursementHistoryRepository extends CrudRepository<DisbursementHistory,Long> {
    @Query(value = "select * from disbursement_history where id=?1 and (note is not null or note!='') order by seqid desc",nativeQuery = true)
    public List<DisbursementHistory> findByDisbursementId(Long disbursementId);
}