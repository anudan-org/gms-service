package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Workflow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkflowRepository extends CrudRepository<Workflow, Long> {

    public List<Workflow> findByGranterAndObject(Organization granter, WorkflowObject object);

    @Query(value = "select a.* from workflows a inner join grant_type_workflow_mapping b on b.workflow_id=a.id where b.grant_type_id=?1 and a.object=?2",nativeQuery = true)
    Workflow findWorkflowByGrantTypeAndObject(Long grantTypeId,String object);
}
