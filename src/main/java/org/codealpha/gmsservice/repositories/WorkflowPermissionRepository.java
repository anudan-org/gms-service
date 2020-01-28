package org.codealpha.gmsservice.repositories;

import java.util.List;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface WorkflowPermissionRepository extends CrudRepository<WorkFlowPermission, Long> {

  @Query(value = "select wst.id, wst.from_state_id, (select name from workflow_statuses where id = wst.from_state_id) from_name, wst.to_state_id, (select name from workflow_statuses where id = wst.to_state_id) to_name, wst.action, wst.note_required from workflow_status_transitions wst where wst.from_state_id=?1 group by (wst.id, wst.from_state_id, wst.to_state_id, wst.action)", nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForGrantFlow(Long grantStatusId);

  @Query(value = "select wst.id, wst.from_state_id, (select name from workflow_statuses where id = wst.from_state_id) from_name, wst.to_state_id, (select name from workflow_statuses where id = wst.to_state_id) to_name, wst.action, wst.note_required from workflow_status_transitions wst inner join workflows B on B.id = wst.workflow_id inner join grants G on G.grantor_org_id = B.granter_id where B.object = 'GRANT' and B.granter_id =?1 and wst.role_id in (?2) and wst.id = ?3 group by (wst.id, wst.from_state_id, wst.to_state_id, wst.action)",nativeQuery = true)
  public List<WorkFlowPermission> getFlowPermisionsOfRoleForStateTransition(Long granterOrgId, List<Long> roleIds, Long grantStatusId);

  @Query(value = "select wst.id, wst.from_state_id, (select name from workflow_statuses where id = wst.from_state_id) from_name, wst.to_state_id, (select name from workflow_statuses where id = wst.to_state_id) to_name, wst.action,wst.note_required from workflow_status_transitions wst inner join workflows B on B.id = wst.workflow_id inner join submissions G on G.submission_status_id = wst.from_state_id where B.object = 'SUBMISSION' and B.granter_id =?1 and wst.role_id  in (?2) and G.submission_status_id =?3 group by (wst.id, wst.from_state_id, wst.to_state_id, wst.action)", nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForSubmissionFlow(Long granterOrgId, List<Long> roleIds, Long statusId);
}
