package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Workflow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkflowRepository extends CrudRepository<Workflow, Long> {

    @Query(value = "select w.* from workflows w inner join grant_type_workflow_mapping m on m.workflow_id=w.id where w.object=?2 and w.granter_id=?1 and m.grant_type_id=?3",nativeQuery = true)
    public List<Workflow> findByGranterAndObjectAndType(Long granterId, String object, Long grantTypeId);

    @Query(value = "select a.* from workflows a inner join grant_type_workflow_mapping b on b.workflow_id=a.id where b.grant_type_id=?1 and a.object=?2 and b._default=true",nativeQuery = true)
    Workflow findWorkflowByGrantTypeAndObject(Long grantTypeId,String object);

    @Query(value = "select a.* from workflows a where  a.object=?2 and a.granter_id=?1",nativeQuery = true)
    public List<Workflow> getAllWorkflowsForGranterByType(Long granterId, String object);

    @Query(value="select a.* from workflows a where  a.object=?2 and a.granter_id=?1 and a._default=true",nativeQuery = true)
    public Workflow findDefaultByGranterAndObject(Long granterId, String object);
}
