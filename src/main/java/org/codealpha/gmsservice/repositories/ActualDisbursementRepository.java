package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.ActualDisbursement;
import org.springframework.data.repository.CrudRepository;

public interface ActualDisbursementRepository extends CrudRepository<ActualDisbursement,Long>{
    
    public List<ActualDisbursement> findByDisbursementId(Long disbursementId);
}