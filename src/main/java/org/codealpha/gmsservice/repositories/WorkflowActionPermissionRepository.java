package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface WorkflowActionPermissionRepository extends CrudRepository<WorkflowActionPermission,Long> {

  @Query(value = "select A.workflow_status_id as id, string_agg(A.permission,',') as permissions_string from workflow_state_permissions A inner join workflow_statuses ws on A.workflow_status_id = ws.id inner join workflows w on ws.workflow_id = w.id where w.granter_id=?1 and A.role_id=?2 and w.object='GRANT' group by A.workflow_status_id",nativeQuery = true)
  public WorkflowActionPermission getActionPermissionsForGrant(Long granterOrgId, Long roleId);
}
