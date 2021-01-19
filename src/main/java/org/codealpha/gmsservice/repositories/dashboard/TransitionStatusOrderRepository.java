package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.TransitionStatusOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransitionStatusOrderRepository extends CrudRepository<TransitionStatusOrder,Long> {

    @Query(value = "select row_number() over () as id,* from (select * from (select (select name from workflow_statuses where from_state_id=id) state,seq_order from workflow_status_transitions where workflow_id=?1 and seq_order<50 order by seq_order) X union select name state,100 as seq_order from workflow_statuses where workflow_id=?1 and terminal=true) Y order by seq_order desc",nativeQuery = true)
    List<TransitionStatusOrder> getTransitionOrder(Long workflowId);
}
