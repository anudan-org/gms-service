package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.TransitionStatusOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransitionStatusOrderRepository extends CrudRepository<TransitionStatusOrder,Long> {

    @Query(value = "select wf.id,wf.workflow_id,map.grant_type_id, (select name from workflow_statuses where from_state_id=id) state, from_state_id, (select internal_status from workflow_statuses where from_state_id=id) internal_status, seq_order from workflow_status_transitions wf inner join workflows w on w.id=wf.workflow_id inner join grant_type_workflow_mapping map on map.workflow_id=w.id where w.id=?1 and map.grant_type_id=?2 and w.object='REPORT' and seq_order<50 order by seq_order desc",nativeQuery = true)
    List<TransitionStatusOrder> getTransitionOrderByWorkflowAndGrantType(Long workflowId,Long grantTypeId);

    @Query(value = "select wf.id,name state, wf.workflow_id, m.grant_type_id, internal_status, wf.id from_state_id, 100 as seq_order from workflow_statuses wf inner join grant_type_workflow_mapping m on m.workflow_id=wf.workflow_id where wf.workflow_id=?1 and grant_type_id=?2 and terminal=true",nativeQuery = true)
    TransitionStatusOrder getTransitionOrderForTerminalState(Long workflowId,Long grantTypeId);
}
