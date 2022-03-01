package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ActualRefund;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActualRefundRepository extends CrudRepository<ActualRefund,Long> {

    @Query(value = "select * from actual_refunds where id=?1",nativeQuery = true)
    public ActualRefund getById(Long actualRefundId);

    @Query(value="select * from actual_refunds where associated_grant_id=?1",nativeQuery = true)
    List<ActualRefund> getActualRefundsForGrant(Long grantId);
}
