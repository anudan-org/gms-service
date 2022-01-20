package org.codealpha.gmsservice.repositories;

import java.util.List;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface WorkflowPermissionRepository extends CrudRepository<WorkFlowPermission, Long> {

  @Query(value = "WITH RECURSIVE wst AS (\n" +
          "\tSELECT\n" +
          "\t\tid,\n" +
          "\t\tfrom_state_id,\n" +
          "\t\t(select name from workflow_statuses where id = c.from_state_id) from_name,\n" +
          "\t\tto_state_id,\n" +
          "\t\t(select name from workflow_statuses where id = c.to_state_id) to_name,\n" +
          "\t\tc.action,\n" +
          "\t\tc.note_required,\n" +
          "\t\tc.seq_order,\n" +
          "\t\tc.is_forward_direction\n" +
          "\tFROM\n" +
          "\t\tworkflow_status_transitions c\n" +
          "\tWHERE\n" +
          "\t\tfrom_state_id = ?1\n" +
          "\tUNION\n" +
          "\t\tSELECT\n" +
          "\t\t\te.id,\n" +
          "\t\t\te.from_state_id,\n" +
          "\t\t\t(select name from workflow_statuses where id = e.from_state_id) from_name,\n" +
          "\t\t\te.to_state_id,\n" +
          "\t\t\t(select name from workflow_statuses where id = e.to_state_id) to_name,\n" +
          "\t\t\te.action,\n" +
          "\t\t\te.note_required,\n" +
          "\t\t\te.seq_order,\n" +
          "\t\t\tfalse as is_forward_direction\n" +
          "\t\tFROM\n" +
          "\t\t\tworkflow_status_transitions e\n" +
          "\t\tINNER JOIN wst s ON s.from_state_id = e.to_state_id\n" +
          ") SELECT\n" +
          "\twst.*\n" +
          "FROM\n" +
          "\twst\n" +
          "left join grant_assignments c on c.state_id=wst.from_state_id \n" +
          "where \n" +
          "c.grant_id=?3 and ((wst.is_forward_direction=true and c.assignments=?2) or (wst.is_forward_direction = false))\n" +
          "order by wst.is_forward_direction desc", nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForGrantFlow(Long grantStatusId,Long userId,Long grantId);

  @Query(value = "select wst.id, wst.from_state_id, (select name from workflow_statuses where id = wst.from_state_id) from_name, wst.to_state_id, (select name from workflow_statuses where id = wst.to_state_id) to_name, wst.action, wst.note_required,wst.seq_order from workflow_status_transitions wst inner join workflows B on B.id = wst.workflow_id inner join grants G on G.grantor_org_id = B.granter_id where B.object = 'GRANT' and B.granter_id =?1 and wst.role_id in (?2) and wst.id = ?3 group by (wst.id, wst.from_state_id, wst.to_state_id, wst.action) order by wst.seq_order asc",nativeQuery = true)
  public List<WorkFlowPermission> getFlowPermisionsOfRoleForStateTransition(Long granterOrgId, List<Long> roleIds, Long grantStatusId);

  @Query(value = "select wst.id, wst.from_state_id, (select name from workflow_statuses where id = wst.from_state_id) from_name, wst.to_state_id, (select name from workflow_statuses where id = wst.to_state_id) to_name, wst.action,wst.note_required,wst.seq_order from workflow_status_transitions wst inner join workflows B on B.id = wst.workflow_id inner join submissions G on G.submission_status_id = wst.from_state_id where B.object = 'SUBMISSION' and B.granter_id =?1 and wst.role_id  in (?2) and G.submission_status_id =?3 group by (wst.id, wst.from_state_id, wst.to_state_id, wst.action) order by wst.seq_order asc", nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForSubmissionFlow(Long granterOrgId, List<Long> roleIds, Long statusId);

  @Query(value = "select wst.id, wst.from_state_id, (select name from workflow_statuses where id = wst.from_state_id) from_name, wst.to_state_id, (select name from workflow_statuses where id = wst.to_state_id) to_name, wst.action, wst.note_required,wst.seq_order from workflow_status_transitions wst inner join disbursement_assignments c on c.state_id=wst.from_state_id where wst.from_state_id=?1 and c.owner=?2 and c.disbursement_id=?3 group by (wst.id, wst.from_state_id, wst.to_state_id, wst.action) order by wst.seq_order asc",nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForDisbursementFlow(Long statusId,Long userId,Long disbursementId);
}
