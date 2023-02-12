package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantClosure;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface GrantClosureRepository extends CrudRepository<GrantClosure, Long> {
    @Query(value = "select distinct A.id, A.reason, A.template_id, A.grant_id, A.moved_on, A.create_by, A.created_at, A.updated_by, A.updated_at, A.status_id, A.note_added_by, A.note, A.deleted, A.closure_detail, A.linked_approved_reports, A.description, A.note_added,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name, A.refund_amount, A.refund_reason, A.actual_spent, A.interest_earned, A.covernote_attributes, A.covernote_content from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.organization_id=?2 and Z.deleted=false and A.deleted=false order by A.updated_at desc", nativeQuery = true)
    List<GrantClosure> findAllAssignedClosuresForGranteeUser(Long id, Long granteeOrgId, String status);

    @Query(value = "select distinct A.id, A.reason, A.template_id, A.grant_id, A.moved_on, A.create_by, A.created_at, A.updated_by, A.updated_at, A.status_id, A.note_added_by, A.note, A.deleted, A.closure_detail, A.linked_approved_reports, A.description, A.note_added,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name,A.refund_amount, A.refund_reason, A.actual_spent, A.interest_earned, A.covernote_attributes, A.covernote_content from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where C.internal_status!='CLOSED' and ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id))) " + 
    " ) and Z.grantor_org_id=?2 and Z.deleted=false and A.deleted=false", nativeQuery = true)
    List<GrantClosure> findAllAssignedClosuresForGranterUser(Long id, Long granterOrgId);

    @Query(value = "select distinct A.id, A.reason, A.template_id, A.grant_id, A.moved_on, A.create_by, A.created_at, A.updated_by, A.updated_at, A.status_id, A.note_added_by, A.note, A.deleted, A.closure_detail, A.linked_approved_reports, A.description, A.note_added,get_owner_closure(A.id) owner_id, get_owner_closure_name(A.id) owner_name, A.refund_amount, A.refund_reason, A.actual_spent, A.interest_earned, A.covernote_attributes, A.covernote_content from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where C.internal_status!='CLOSED' and ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='REVIEW') or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and A.deleted=false", nativeQuery = true)
    List<GrantClosure> findAllAssignedClosuresForGranterAdmin(Long id, Long granterOrgId);


    @Query(value = "select distinct A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.end_date asc", nativeQuery = true)
    List<GrantClosure> findUpcomingClosuresForGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.end_date asc", nativeQuery = true)
    List<GrantClosure> findUpcomingClosuresForAdminGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and A.end_date > ?3 and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.grant_id=?4 and A.deleted=false order by A.due_date desc", nativeQuery = true)
    List<GrantClosure> findFutureClosuresToSubmitForGranterUserByDateRangeAndGrant(Long id, Long granterOrgId, Date end,
            Long grantId);

    @Query(value = "select distinct A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id  inner join workflow_statuses D on D.id=A.status_id where ( (B.anchor=true and B.assignment = ?1)  or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id))) or (D.internal_status='ACTIVE' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id))) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status ='ACTIVE') and A.deleted=false order by A.due_date asc", nativeQuery = true)
    List<GrantClosure> findReadyToSubmitClosuresForGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id inner join workflow_statuses D on D.id=A.status_id where ( (B.anchor=true and B.assignment = ?1)  or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 ) or (C.internal_status='REVIEW' ) or (D.internal_status='ACTIVE' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status ='ACTIVE') and A.deleted=false order by A.due_date asc", nativeQuery = true)
    List<GrantClosure> findReadyToSubmitClosuresForAdminGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);


    @Query(value = "select distinct C.internal_status,A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id))) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='REVIEW') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<GrantClosure> findSubmittedClosuresForGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='REVIEW' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='REVIEW') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<GrantClosure> findSubmittedClosuresForAdminGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='CLOSED') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<GrantClosure> findApprovedClosuresForGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='CLOSED') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<GrantClosure> findApprovedClosuresForAdminGranterUserByDateRange(Long id, Long granterOrgId);


    @Query(value = "select sum(c.actual_target) from grant_closure A inner join workflow_statuses B on B.id=A.status_id inner join report_string_attributes C on C.closure_id=A.id inner join report_specific_section_attributes D on D.id=C.section_attribute_id where A. grant_id=?1 and B.internal_status='CLOSED' and D.field_name=?2 and A.deleted=false group by D.field_name;", nativeQuery = true)
    Long getApprovedClosuresActualSumForGrantAndAttribute(Long id, String attributeName);

    @Query(value = "select *,get_owner_closure(r.id) owner_id,get_owner_closure_name(r.id) owner_name from grant_closure r inner join grants g on g.id=r.grant_id inner join workflow_statuses wf on wf.id=r.status_id where r.due_date=?1 and wf.internal_status='ACTIVE' and g.grantor_org_id not in (?2) and g.deleted=false and r.deleted=false order by r.due_date", nativeQuery = true)
    List<GrantClosure> getDueClosuresForPlatform(Date dueDate, List<Long> granterIds);

    @Query(value = "select *,get_owner_closure(r.id) owner_id,get_owner_closure_name(r.id) owner_name from grant_closure r inner join grants g on g.id=r.grant_id inner join workflow_statuses wf on wf.id=r.status_id where r.due_date=?1 and wf.internal_status='ACTIVE' and g.grantor_org_id = ?2 and g.deleted=false and r.deleted=false order by r.due_date", nativeQuery = true)
    List<GrantClosure> getDueClosuresForGranter(Date dueDate, Long granterId);

    @Query(value = "select *,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join workflow_statuses B on B.id=A.status_id where A.grant_id=?1 and B.internal_status=?2 and A.id!=?3 and A.deleted=false", nativeQuery = true)
    public List<GrantClosure> findByGrantAndStatus(Long grantId, String statusName, Long currentReportId);

    @Query(value = "select *,get_owner_closure(id) owner_id,get_owner_closure_name(id) owner_name from grant_closure where id in (?1) and deleted=false", nativeQuery = true)
    public List<GrantClosure> findClosuresByIds(List<Long> reportIds);

    @Query(value = "select *,get_owner_closure(a.id) owner_id,get_owner_closure_name(a.id) owner_name from grant_closure a where a.status_id=?1 and a.grant_id=?2",nativeQuery = true)
    public List<GrantClosure> findByStatusAndGrant(Long statusId, Long grantId);

    @Query(value="select *,get_owner_closure(id) owner_id,get_owner_closure_name(id) owner_name from grant_closure where grant_id=? and deleted=false",nativeQuery = true)
    List<GrantClosure> getClosuresByGrant(Long grantId);

    @Query(value = "select A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join workflow_statuses B on B.id=A.status_id where ( (B.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id) >0   ) or B.internal_status!='DRAFT') and A.id=?1  and A.deleted=false", nativeQuery = true)
    List<GrantClosure> findClosuresThatMovedAtleastOnce(Long reportId);

    @Query(value = "select  A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED')  and A.deleted=false order by A.grant_id,A.end_date asc", nativeQuery = true)
    List<GrantClosure> findUpcomingFutureClosures(Long userId, Long id);

    @Query(value = "select  A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A inner join grants Z on Z.id=A.grant_id inner join closure_assignments B on B.closure_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=A.id)>0 ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.grant_id,A.end_date asc", nativeQuery = true)
    List<GrantClosure> findUpcomingFutureAdminClosures(Long userId, Long id);

    @Query(value = "select count(distinct(a.id)) from grant_closure a\n" +
            " inner join closure_assignments b on b.closure_id=a.id and b.state_id=a.status_id\n" +
            " inner join workflow_statuses c on c.id = a.status_id " +
            " where b.assignment=?1 \n" +
            " and a.deleted=false and c.internal_status!='CLOSED' group by b.assignment",nativeQuery = true)
    Long getActionDueClosuresForUser(Long userId);

    @Query(value = "select distinct a.id, a.reason, a.template_id, a.grant_id, a.moved_on, a.create_by, a.created_at, a.updated_by, a.updated_at, a.status_id, a.note_added_by, a.note, a.deleted, a.closure_detail, a.linked_approved_reports, a.description, a.note_added,get_owner_closure(a.id) owner_id,get_owner_closure_name(a.id) owner_name,a.refund_amount, a.refund_reason, a.actual_spent, a.interest_earned, A.covernote_attributes, A.covernote_content  " +
     " from grant_closure a\n" +
            "inner join closure_assignments b on b.closure_id=a.id and b.state_id=a.status_id\n" +
            " inner join workflow_statuses c on c.id=a.status_id " +
            " where b.assignment=?1\n" +
            " and a.deleted=false and c.internal_status!='CLOSED' ",nativeQuery = true)
    List<GrantClosure> getDetailedActionDueClosuresForUser(Long userId);

    @Query(value = "select count(distinct(d.id)) from grants a\n" +
            "            inner join grant_assignments b on b.state_id=a.grant_status_id and b.grant_id=a.id\n" +
            "            inner join workflow_statuses c on c.id=a.grant_status_id\n" +
            "            inner join grant_closure d on d.grant_id=a.id\n" +
            "            inner join workflow_statuses e on e.id=d.status_id\n" +
            "            inner join report_snapshot f on f.closure_id=d.id and f.to_state_id=d.status_id\n" +
            "            where c.internal_status='ACTIVE' and e.internal_status='CLOSED' and b.assignments=?1\n" +
            "            and f.moved_on>d.due_date and a.deleted=false and d.deleted=false",nativeQuery = true)
    Long approvedClosuresNotInTimeForUser(Long userId);

    @Query(value = "select count(distinct(d.id)) from grants a\n" +
            "inner join grant_assignments b on b.state_id=a.grant_status_id and b.grant_id=a.id\n" +
            "inner join workflow_statuses c on c.id=a.grant_status_id\n" +
            "inner join grant_closure d on d.grant_id=a.id\n" +
            "inner join workflow_statuses e on e.id=d.status_id\n" +
            "inner join report_snapshot f on f.closure_id=d.id and f.to_state_id=d.status_id\n" +
            "where c.internal_status='ACTIVE' and e.internal_status='CLOSED' and b.assignments=?1\n" +
            "and f.moved_on<=d.due_date and a.deleted=false",nativeQuery = true)
    Long approvedClosuresInTimeForUser(Long userId);

    @Query(value = "select count(distinct(a.id)) from grant_closure a \n" +
            "inner join closure_assignments b on b.closure_id=a.id and b.state_id=a.status_id \n" +
            "inner join workflow_statuses c on c.id=a.status_id \n" +
            "inner join grants d on d.id=a.grant_id\n" +
            "where b.assignment=?1 and c.internal_status='DRAFT' and d.deleted=false and a.deleted=false",nativeQuery = true)
    Long getUpComingDraftClosures(Long userId);

    @Query(value = "select distinct a.id, a.reason, a.template_id, a.grant_id, a.moved_on, a.create_by, a.created_at, a.updated_by, a.updated_at, a.status_id, a.note_added_by, a.note, a.deleted, a.closure_detail, a.linked_approved_reports, a.description, a.note_added,get_owner_closure(a.id) owner_id,get_owner_closure_name(a.id) owner_name,a.refund_amount, a.refund_reason, a.actual_spent, a.interest_earned, A.covernote_attributes, A.covernote_content " +
        " from grant_closure a \n" +
            "inner join closure_assignments b on b.closure_id=a.id and b.state_id=a.status_id \n" +
            "inner join workflow_statuses c on c.id=a.status_id \n" +
            "where b.assignment=?1 \n" +
            "and c.internal_status='DRAFT' \n" +
            "and a.deleted=false",nativeQuery = true)
    List<GrantClosure> getDetailedUpComingDraftClosures(Long userId);

    @Query(value = "select count(distinct(a.id)) \n" +
            "            from grant_closure a \n" +
            "            inner join closure_assignments b on b.closure_id=a.id \n" +
            "            inner join workflow_statuses c on c.id=a.status_id \n" +
            "            where b.assignment=?1 and (\n" +
            "\t\t\t\t\t\t\t((select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id))) or \n" +
            "\t\t\t\t\t\t\t(C.internal_status='REVIEW' and ?1 = any( array(select assignment from closure_assignments where closure_id=A.id)))\n" +
            "\t\t\t\t\t\t) and a.deleted=false and c.internal_status !='CLOSED' ",nativeQuery = true)
    Long getClosuresInWorkflow(Long userId);

    @Query(value = "select (sum(committed) - sum(disbursed)) as pending_commitments from (\n" +
            "\t\t\tselect distinct b.assignment,d.amount as committed,disbursed_amount_for_grant(d.id) disbursed\n" +
            "\t\t\t\t\t\tfrom grant_closure a \n" +
            "\t\t\t\t\t\tinner join closure_assignments b on b.closure_id=a.id \n" +
            "\t\t\t\t\t\tinner join workflow_statuses c on c.id=a.status_id\n" +
            "\t\t\t\t\t\tinner join grants d on d.id=a.grant_id\n" +
            "\t\t\t\t\t\twhere b.assignment=?1 and (\n" +
            "((select count(*) from grant_closure_history where id=A.id)>0 and ?1 = any (array(select assignment from closure_assignments where closure_id=A.id)) and C.internal_status not in ('ACTIVE','CLOSED')) or \n" +
            "(C.internal_status not in ('ACTIVE','CLOSED') and ?1 = b.assignment and b.state_id=a.status_id)\n" +
            ")  and a.deleted=false and d.deleted=false  \n" +
            "\t\t\t) X group by X.assignment",nativeQuery = true)
    Long getUpcomingClosuresDisbursementAmount(Long userId);


    @Query(value = "select A.*,get_owner_closure(A.id) owner_id,get_owner_closure_name(A.id) owner_name from grant_closure A where A.id=?1",nativeQuery = true)
    public GrantClosure findByClosureId(Long closureId);

    @Query(value = "select * from grant_closure where grant_id=?1",nativeQuery = true)
    List<GrantClosure> findByGrant(Long grantId);

    @Query(value = "  select sum(a.actual_spent)  from grant_closure a, workflow_statuses s where s.id = a.status_id "
    + " and exists (select 1 from closure_assignments b  "
    + " inner join workflow_statuses c on c.id=a.status_id "
    + " where  b.closure_id =a.id and b.assignment=?1 and "
    + " (b.assignment=?1 and c.internal_status='DRAFT' and b.state_id=a.status_id) or "
    + " (b.assignment=?1 and c.internal_status!='DRAFT') or "
    + " (c.internal_status='DRAFT' and (select count(*) from grant_closure_history where id=a.id)>0)) "
    + " and a.deleted=false and s.internal_status not in ( 'CLOSED')",nativeQuery = true)
    Long getUpcomingClosuresActualSpentAmount(Long userId);
   
   @Query(value="select count(*) from grant_closure a\n" +
   " inner join closure_assignments b on b.state_id=a.status_id and b.closure_id=a.id\n" +
   " inner join workflow_statuses c on c.id=a.status_id\n" +
   " where b.assignment=?1 and c.internal_status!='CLOSED' and a.deleted=false",nativeQuery = true)
Long getActionDueClsouresForUser(Long userId);



   
}
