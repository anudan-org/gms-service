package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ActualRefund;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ActualRefundRepository extends CrudRepository<ActualRefund,Long> {

    @Query(value = "select * from actual_refunds where id=?1",nativeQuery = true)
    public ActualRefund getById(Long actualRefundId);
}
