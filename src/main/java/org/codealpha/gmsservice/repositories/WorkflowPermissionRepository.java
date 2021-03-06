package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

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
          "c.grant_id=?2 and ((wst.is_forward_direction=true) or (wst.is_forward_direction = false and  exists (select * from grant_assignments where grant_id=?2 and state_id=?1) ))\n" +
          "order by wst.is_forward_direction desc", nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForGrantFlow(Long grantStatusId,Long grantId);

  @Query(value = "select wst.id, wst.from_state_id, (select name from workflow_statuses where id = wst.from_state_id) from_name, wst.to_state_id, (select name from workflow_statuses where id = wst.to_state_id) to_name, wst.action, wst.note_required,wst.seq_order from workflow_status_transitions wst inner join workflows B on B.id = wst.workflow_id inner join grants G on G.grantor_org_id = B.granter_id where B.object = 'GRANT' and B.granter_id =?1 and wst.role_id in (?2) and wst.id = ?3 group by (wst.id, wst.from_state_id, wst.to_state_id, wst.action) order by wst.seq_order asc",nativeQuery = true)
  public List<WorkFlowPermission> getFlowPermisionsOfRoleForStateTransition(Long granterOrgId, List<Long> roleIds, Long grantStatusId);

  @Query(value = "select wst.id, wst.from_state_id, (select name from workflow_statuses where id = wst.from_state_id) from_name, wst.to_state_id, (select name from workflow_statuses where id = wst.to_state_id) to_name, wst.action,wst.note_required,wst.seq_order from workflow_status_transitions wst inner join workflows B on B.id = wst.workflow_id inner join submissions G on G.submission_status_id = wst.from_state_id where B.object = 'SUBMISSION' and B.granter_id =?1 and wst.role_id  in (?2) and G.submission_status_id =?3 group by (wst.id, wst.from_state_id, wst.to_state_id, wst.action) order by wst.seq_order asc", nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForSubmissionFlow(Long granterOrgId, List<Long> roleIds, Long statusId);

  @Query(value = "WITH RECURSIVE wst AS ( \n" +
          "          SELECT \n" +
          "          id, \n" +
          "          from_state_id, \n" +
          "          (select name from workflow_statuses where id = c.from_state_id) from_name, \n" +
          "          to_state_id, \n" +
          "          (select name from workflow_statuses where id = c.to_state_id) to_name, \n" +
          "          c.action, \n" +
          "          c.note_required, \n" +
          "          c.seq_order, \n" +
          "          c.is_forward_direction \n" +
          "          FROM \n" +
          "          workflow_status_transitions c \n" +
          "          WHERE \n" +
          "          from_state_id = ?1 \n" +
          "          UNION \n" +
          "          SELECT \n" +
          "          e.id, \n" +
          "          e.from_state_id, \n" +
          "          (select name from workflow_statuses where id = e.from_state_id) from_name, \n" +
          "          e.to_state_id, \n" +
          "          (select name from workflow_statuses where id = e.to_state_id) to_name, \n" +
          "          e.action, \n" +
          "          e.note_required, \n" +
          "          e.seq_order, \n" +
          "          false as is_forward_direction \n" +
          "          FROM \n" +
          "          workflow_status_transitions e \n" +
          "          INNER JOIN wst s ON s.from_state_id = e.to_state_id \n" +
          "          ) SELECT \n" +
          "          wst.* \n" +
          "          FROM \n" +
          "          wst \n" +
          "          left join disbursement_assignments c on c.state_id=wst.from_state_id  \n" +
          "          where  \n" +
          "          c.disbursement_id=?2 and ((wst.is_forward_direction=true) or (wst.is_forward_direction = false and  exists (select * from disbursement_assignments where disbursement_id=?2 and state_id=?1) )) \n" +
          "          order by wst.is_forward_direction desc",nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForDisbursementFlow(Long statusId,Long disbursementId);

  @Query(value = "WITH RECURSIVE wst AS ( \n" +
          "          SELECT \n" +
          "          id, \n" +
          "          from_state_id, \n" +
          "          (select name from workflow_statuses where id = c.from_state_id) from_name, \n" +
          "          to_state_id, \n" +
          "          (select name from workflow_statuses where id = c.to_state_id) to_name, \n" +
          "          c.action, \n" +
          "          c.note_required, \n" +
          "          c.seq_order, \n" +
          "          c.is_forward_direction \n" +
          "          FROM \n" +
          "          workflow_status_transitions c \n" +
          "          WHERE \n" +
          "          from_state_id = ?1 \n" +
          "          UNION \n" +
          "          SELECT \n" +
          "          e.id, \n" +
          "          e.from_state_id, \n" +
          "          (select name from workflow_statuses where id = e.from_state_id) from_name, \n" +
          "          e.to_state_id, \n" +
          "          (select name from workflow_statuses where id = e.to_state_id) to_name, \n" +
          "          e.action, \n" +
          "          e.note_required, \n" +
          "          e.seq_order, \n" +
          "          false as is_forward_direction \n" +
          "          FROM \n" +
          "          workflow_status_transitions e \n" +
          "          INNER JOIN wst s ON s.from_state_id = e.to_state_id\n" +
          "          ) SELECT \n" +
          "          wst.* \n" +
          "          FROM \n" +
          "          wst \n" +
          "          inner join report_assignments c on c.state_id=wst.from_state_id  \n" +
          "          where  \n" +
          "          c.report_id=?2 and ((wst.is_forward_direction=true) or (wst.is_forward_direction = false and  exists (select * from report_assignments where report_id=?2 and state_id=?1) )) \n" +
          "          order by wst.is_forward_direction desc",nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForReportFlow(Long statusId,Long reportId);

  @Query(value = "WITH RECURSIVE wst AS ( \n" +
          "                    SELECT \n" +
          "                    id, \n" +
          "                    from_state_id, \n" +
          "                    (select name from workflow_statuses where id = c.from_state_id) from_name, \n" +
          "                    to_state_id, \n" +
          "                    (select name from workflow_statuses where id = c.to_state_id) to_name, \n" +
          "                    c.action, \n" +
          "                    c.note_required, \n" +
          "                    c.seq_order, \n" +
          "                    c.is_forward_direction \n" +
          "                    FROM \n" +
          "                    workflow_status_transitions c \n" +
          "                    WHERE \n" +
          "                    from_state_id = ?1 \n" +
          "                    UNION \n" +
          "                    SELECT \n" +
          "                    e.id, \n" +
          "                    e.from_state_id, \n" +
          "                    (select name from workflow_statuses where id = e.from_state_id) from_name, \n" +
          "                    e.to_state_id, \n" +
          "                    (select name from workflow_statuses where id = e.to_state_id) to_name, \n" +
          "                    e.action, \n" +
          "                    e.note_required, \n" +
          "                    e.seq_order, \n" +
          "                    false as is_forward_direction \n" +
          "                    FROM \n" +
          "                    workflow_status_transitions e \n" +
          "                    INNER JOIN wst s ON s.from_state_id = e.to_state_id\n" +
          "                    ) SELECT \n" +
          "                    wst.* \n" +
          "                    FROM \n" +
          "                    wst \n" +
          "                    inner join closure_assignments c on c.state_id=wst.from_state_id  \n" +
          "                    where  \n" +
          "                    c.closure_id=?3 and ((wst.is_forward_direction=true and c.assignment=?2) or (wst.is_forward_direction = false and  exists (select * from closure_assignments where assignment=?2 and closure_id=?3 and state_id=?1) )) \n" +
          "                    order by wst.is_forward_direction desc",nativeQuery = true)
  public List<WorkFlowPermission> getPermissionsForClosureFlow(Long statusId,Long userId,Long closureId);


  @Query(value = "select * from (select * from (WITH RECURSIVE wst AS ( \n" +
          "                              SELECT \n" +
          "                              id, \n" +
          "                              from_state_id, \n" +
          "                              (select name from workflow_statuses where id = c.from_state_id) from_name, \n" +
          "                              to_state_id, \n" +
          "                              (select name from workflow_statuses where id = c.to_state_id) to_name, \n" +
          "                              c.action, \n" +
          "                              c.note_required, \n" +
          "                              c.seq_order, \n" +
          "                              false as is_forward_direction \n" +
          "                              FROM \n" +
          "                              workflow_status_transitions c \n" +
          "                              WHERE \n" +
          "                              from_state_id in(?4) \n" +
          "                              UNION \n" +
          "                              SELECT \n" +
          "                              e.id, \n" +
          "                              e.from_state_id, \n" +
          "                              (select name from workflow_statuses where id = e.from_state_id) from_name, \n" +
          "                              e.to_state_id, \n" +
          "                              (select name from workflow_statuses where id = e.to_state_id) to_name, \n" +
          "                              e.action, \n" +
          "                              e.note_required, \n" +
          "                              e.seq_order, \n" +
          "                              false as is_forward_direction \n" +
          "                              FROM \n" +
          "                              workflow_status_transitions e \n" +
          "                              INNER JOIN wst s ON s.from_state_id = e.to_state_id\n" +
          "                              ) SELECT \n" +
          "                              wst.*,c.assignment\n" +
          "                              FROM \n" +
          "                              wst \n" +
          "                              inner join closure_assignments c on c.state_id=wst.from_state_id  \n" +
          "                              where  \n" +
          "                              c.closure_id=?3 and ((wst.is_forward_direction=true and  exists (select * from closure_assignments where assignment=?2 and closure_id=?3 and state_id=?1)) or (wst.is_forward_direction = false and  exists (select * from closure_assignments where assignment=?2 and closure_id=?3) )) \n" +
          "                              order by wst.is_forward_direction desc) X where is_forward_direction=false\n" +
          "union\n" +
          "select id, from_state_id,(select name from workflow_statuses where id=from_state_id),\n" +
          " to_state_id,(select name from workflow_statuses where id=to_state_id) to_name,action,true note_required,seq_order,true is_forward_direction,0 as assignment from workflow_status_transitions where from_state_id=?1 and exists (select * from closure_assignments where state_id=?1 and assignment=?2 and closure_id=?3)) Y order by is_forward_direction desc, seq_order desc",nativeQuery = true)
  public List<WorkFlowPermission> getPermissionfForClosureWorkflowAtBranchMergeState(Long statusId,Long userId,Long closureId,Long prevStatusId);

  @Query(value = "select * from (WITH RECURSIVE wst AS ( \n" +
          "                                        SELECT \n" +
          "                                        id, \n" +
          "                                        from_state_id, \n" +
          "                                        (select name from workflow_statuses where id = c.from_state_id) from_name, \n" +
          "                                        to_state_id, \n" +
          "                                        (select name from workflow_statuses where id = c.to_state_id) to_name, \n" +
          "                                        c.action, \n" +
          "                                        c.note_required, \n" +
          "                                        c.seq_order, \n" +
          "                                        is_forward_direction \n" +
          "                                        FROM \n" +
          "                                        workflow_status_transitions c \n" +
          "                                        WHERE \n" +
          "                                        from_state_id in(?1) \n" +
          "                                        UNION \n" +
          "                                        SELECT \n" +
          "                                        e.id, \n" +
          "                                        e.from_state_id, \n" +
          "                                        (select name from workflow_statuses where id = e.from_state_id) from_name, \n" +
          "                                        e.to_state_id, \n" +
          "                                        (select name from workflow_statuses where id = e.to_state_id) to_name, \n" +
          "                                        e.action, \n" +
          "                                        e.note_required, \n" +
          "                                        e.seq_order, \n" +
          "                                        false as is_forward_direction \n" +
          "                                        FROM \n" +
          "                                        workflow_status_transitions e \n" +
          "                                        INNER JOIN wst s ON s.from_state_id = e.to_state_id\n" +
          "                                        ) SELECT \n" +
          "                                        wst.*\n" +
          "                                        FROM \n" +
          "                                        wst ) X where X.is_forward_direction=false",nativeQuery = true)
  public List<WorkFlowPermission> simpleBackFlow(Long statusId);
}
