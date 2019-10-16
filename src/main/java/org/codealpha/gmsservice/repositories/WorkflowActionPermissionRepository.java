package org.codealpha.gmsservice.repositories;

import java.util.List;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface WorkflowActionPermissionRepository extends CrudRepository<WorkflowActionPermission,Long> {

  @Query(value = "select A.workflow_status_id as id, string_agg(A.permission, ',') as permissions_string from workflow_state_permissions A inner join workflow_statuses ws on A.workflow_status_id = ws.id inner join workflows w on ws.workflow_id = w.id inner join grants G on G.grantor_org_id = w.granter_id inner join grant_assignments X on X.state_id=G.grant_status_id and X.grant_id=G.id where w.granter_id =?1 and A.role_id in (?2) and w.object = 'GRANT' and G.grant_status_id=?3 and X.assignments=?4 and X.grant_id=?5 and G.grant_status_id = ws.id group by A.workflow_status_id",nativeQuery = true)
  public WorkflowActionPermission getActionPermissionsForGrant(Long granterOrgId, List<Long> roleIds, Long grantStatusId, Long userId,Long grantId);

  @Query(value = "select X.workflow_status_id as id,string_agg(X.permission,',') as permissions_string from (select distinct A.workflow_status_id,permission from workflow_state_permissions A inner join workflow_statuses ws on A.workflow_status_id = ws.id inner join workflows w on ws.workflow_id = w.id inner join submissions G on G.submission_status_id =ws.id where w.granter_id =?1 and A.role_id in (?2) and w.object = 'SUBMISSION') X group by X.workflow_status_id",nativeQuery = true)
  public WorkflowActionPermission getActionPermissionsForSubmission(Long granterOrgId, List<Long> roleIds);
}
