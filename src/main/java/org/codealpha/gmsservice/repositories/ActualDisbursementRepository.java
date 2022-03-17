package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ActualDisbursement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActualDisbursementRepository extends CrudRepository<ActualDisbursement,Long>{
    
    public List<ActualDisbursement> findByDisbursementId(Long disbursementId);

    @Query(value="select max(C.order_position) from disbursements A inner join workflow_statuses B on B.id=A.status_id inner join actual_disbursements C on C.disbursement_id=A.id where grant_id=?1",nativeQuery = true)
    public Integer getMaxOrderPositionForClosedDisbursementOfGrant(Long grantId);
}