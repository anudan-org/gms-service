package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantTypeWorkflowMapping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantTypeWorkflowMappingRepository extends CrudRepository<GrantTypeWorkflowMapping,Long> {

    @Query(value = "select * from grant_type_workflow_mapping where grant_type_id=?1 and workflow_id=?2",nativeQuery = true)
    GrantTypeWorkflowMapping findByGrantTypeAndWorkflow(Long grantTypeId,Long workflowId);

    @Query(value = "select * from grant_type_workflow_mapping where workflow_id=?1",nativeQuery = true)
    List<GrantTypeWorkflowMapping> findByWorkflow(Long workflowId);
}
