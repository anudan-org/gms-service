package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface WorkflowStatusRepository extends CrudRepository<WorkflowStatus,Long> {

  @Query(value = "select ws.* from workflows w inner join workflow_statuses ws on w.id = ws.workflow_id where ws.initial=true and w.object=?1 and w.granter_id=?2", nativeQuery = true)
  public WorkflowStatus getInitialStatusByObjectAndGranterOrg(String object, Long granterOrgId);

}
