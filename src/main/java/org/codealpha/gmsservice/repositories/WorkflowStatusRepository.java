package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Workflow;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkflowStatusRepository extends CrudRepository<WorkflowStatus,Long> {

  @Query(value = "select ws.* from workflows w inner join workflow_statuses ws on w.id = ws.workflow_id inner join grant_type_workflow_mapping m on m.workflow_id=w.id where ws.initial=true and w.object=?1 and m.grant_type_id=?3 and w.granter_id=?2", nativeQuery = true)
  public WorkflowStatus getInitialStatusByObjectAndGranterOrg(String object, Long granterOrgId, Long grantType);

  @Query(value = "select ws.* from workflows w inner join workflow_statuses ws on w.id = ws.workflow_id where w.object=?1 and w.granter_id=?2",nativeQuery = true)
  public List<WorkflowStatus> getAllTenantStatuses(String object, Long granterOrgId);

  @Query(value = "select * from workflow_statuses where id=?1", nativeQuery = true)
  public WorkflowStatus getById(Long statusId);

  public List<WorkflowStatus> findByWorkflow(Workflow workflow);


}
