package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Report;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ReportRepository extends CrudRepository<Report, Long> {
    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and C.internal_status=?3 and Z.organization_id=?2 and Z.deleted=false and A.deleted=false order by A.due_date desc", nativeQuery = true)
    List<Report> findAllAssignedReportsForGranteeUser(Long id, Long granteeOrgId, String status);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and A.deleted=false order by A.due_date desc", nativeQuery = true)
    List<Report> findAllAssignedReportsForGranterUser(Long id, Long granterOrgId);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.end_date asc", nativeQuery = true)
    List<Report> findUpcomingReportsForGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.end_date asc", nativeQuery = true)
    List<Report> findUpcomingReportsForAdminGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and A.end_date > ?3 and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.grant_id=?4 and A.deleted=false order by A.due_date desc", nativeQuery = true)
    List<Report> findFutureReportsToSubmitForGranterUserByDateRangeAndGrant(Long id, Long granterOrgId, Date end,
            Long grantId);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id  inner join workflow_statuses D on D.id=A.status_id where ( (B.anchor=true and B.assignment = ?1)  or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (D.internal_status='ACTIVE' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status ='ACTIVE') and A.deleted=false order by A.due_date asc", nativeQuery = true)
    List<Report> findReadyToSubmitReportsForGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id inner join workflow_statuses D on D.id=A.status_id where ( (B.anchor=true and B.assignment = ?1)  or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) or (C.internal_status='REVIEW' ) or (D.internal_status='ACTIVE' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status ='ACTIVE') and A.deleted=false order by A.due_date asc", nativeQuery = true)
    List<Report> findReadyToSubmitReportsForAdminGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);


    @Query(value = "select distinct C.internal_status,A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='REVIEW') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<Report> findSubmittedReportsForGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='REVIEW' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='REVIEW') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<Report> findSubmittedReportsForAdminGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='CLOSED') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<Report> findApprovedReportsForGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='CLOSED') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<Report> findApprovedReportsForAdminGranterUserByDateRange(Long id, Long granterOrgId);


    @Query(value = "select sum(c.actual_target) from reports A inner join workflow_statuses B on B.id=A.status_id inner join report_string_attributes C on C.report_id=A.id inner join report_specific_section_attributes D on D.id=C.section_attribute_id where A. grant_id=?1 and B.internal_status='CLOSED' and D.field_name=?2 and A.deleted=false group by D.field_name;", nativeQuery = true)
    Long getApprovedReportsActualSumForGrantAndAttribute(Long id, String attributeName);

    @Query(value = "select * from reports r inner join grants g on g.id=r.grant_id inner join workflow_statuses wf on wf.id=r.status_id where r.due_date=?1 and wf.internal_status='ACTIVE' and g.grantor_org_id not in (?2) and g.deleted=false and r.deleted=false order by r.due_date", nativeQuery = true)
    List<Report> getDueReportsForPlatform(Date dueDate, List<Long> granterIds);

    @Query(value = "select * from reports r inner join grants g on g.id=r.grant_id inner join workflow_statuses wf on wf.id=r.status_id where r.due_date=?1 and wf.internal_status='ACTIVE' and g.grantor_org_id = ?2 and g.deleted=false and r.deleted=false order by r.due_date", nativeQuery = true)
    List<Report> getDueReportsForGranter(Date dueDate, Long granterId);

    @Query(value = "select * from reports A inner join workflow_statuses B on B.id=A.status_id where A.grant_id=?1 and B.internal_status=?2 and A.id!=?3 and A.deleted=false", nativeQuery = true)
    public List<Report> findByGrantAndStatus(Long grantId, String statusName, Long currentReportId);

    @Query(value = "select * from reports where id in (?1) and deleted=false", nativeQuery = true)
    public List<Report> findReportsByIds(List<Long> reportIds);

    @Query(value = "select * from reports a where a.status_id=?1 and a.grant_id=?2",nativeQuery = true)
    public List<Report> findByStatusAndGrant(Long statusId, Long grantId);

    @Query(value="select * from reports where grant_id=? and deleted=false",nativeQuery = true)
    List<Report> getReportsByGrant(Long grantId);

    @Query(value = "select A.* from reports A inner join workflow_statuses B on B.id=A.status_id where ( (B.internal_status='DRAFT' and (select count(*) from report_history where id=A.id) >0   ) or B.internal_status!='DRAFT') and A.id=?1  and A.deleted=false", nativeQuery = true)
    List<Report> findReportsThatMovedAtleastOnce(Long reportId);

    @Query(value = "select  A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED')  and A.deleted=false order by A.grant_id,A.end_date asc", nativeQuery = true)
    List<Report> findUpcomingFutureReports(Long userId, Long id);

    @Query(value = "select  A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.grant_id,A.end_date asc", nativeQuery = true)
    List<Report> findUpcomingFutureAdminReports(Long userId, Long id);

    @Query(value = "select count(distinct(a.id)) from reports a\n" +
            "            inner join report_assignments b on b.report_id=a.id and b.state_id=a.status_id\n" +
            "            inner join grants c on c.id=a.grant_id\n" +
            "            where b.assignment=?1 \n" +
            "\t\t\tand (a.end_date between now() and (now()+ INTERVAL '15 day') \n" +
            "\t\t\t\tor a.due_date<now()\n" +
            "\t\t\t\tor (select count(*) from report_history where id=a.id )>0)\n" +
            "\t\t\tand c.deleted=false group by b.assignment",nativeQuery = true)
    Long getActionDueReportsForUser(Long userId);

    @Query(value = "select count(distinct(d.id)) from grants a\n" +
            "            inner join grant_assignments b on b.state_id=a.grant_status_id and b.grant_id=a.id\n" +
            "            inner join workflow_statuses c on c.id=a.grant_status_id\n" +
            "            inner join reports d on d.grant_id=a.id\n" +
            "            inner join workflow_statuses e on e.id=d.status_id\n" +
            "            inner join report_snapshot f on f.report_id=d.id and f.to_state_id=d.status_id\n" +
            "            where c.internal_status='ACTIVE' and e.internal_status='CLOSED' and b.assignments=?1\n" +
            "            and f.moved_on>d.due_date and a.deleted=false and d.deleted=false",nativeQuery = true)
    Long approvedReportsNotInTimeForUser(Long userId);

    @Query(value = "select count(distinct(d.id)) from grants a\n" +
            "inner join grant_assignments b on b.state_id=a.grant_status_id and b.grant_id=a.id\n" +
            "inner join workflow_statuses c on c.id=a.grant_status_id\n" +
            "inner join reports d on d.grant_id=a.id\n" +
            "inner join workflow_statuses e on e.id=d.status_id\n" +
            "inner join report_snapshot f on f.report_id=d.id and f.to_state_id=d.status_id\n" +
            "where c.internal_status='ACTIVE' and e.internal_status='CLOSED' and b.assignments=?1\n" +
            "and f.moved_on<=d.due_date and a.deleted=false",nativeQuery = true)
    Long approvedReportsInTimeForUser(Long userId);

    @Query(value = "select count(distinct(a.id)) from reports a \n" +
            "inner join report_assignments b on b.report_id=a.id and b.state_id=a.status_id \n" +
            "inner join workflow_statuses c on c.id=a.status_id \n" +
            "inner join grants d on d.id=a.grant_id\n" +
            "where b.assignment=?1 and c.internal_status='DRAFT' and d.deleted=false and a.deleted=false",nativeQuery = true)
    Long getUpComingDraftReports(Long userId);

    @Query(value = "select count(distinct(a.id)) \n" +
            "            from reports a \n" +
            "            inner join report_assignments b on b.report_id=a.id \n" +
            "            inner join workflow_statuses c on c.id=a.status_id \n" +
            "            inner join grants d on d.id=a.grant_id\n" +
            "            where b.assignment=?1 and (\n" +
            "\t\t\t\t\t\t\t((select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or \n" +
            "\t\t\t\t\t\t\t(C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id)))\n" +
            "\t\t\t\t\t\t) and a.deleted=false and d.deleted=false",nativeQuery = true)
    Long getReportsInWorkflow(Long userId);

    @Query(value = "select (sum(committed) - sum(disbursed)) as pending_commitments from (\n" +
            "            select distinct b.assignment,d.amount as committed,disbursed_amount_for_grant(d.id) disbursed\n" +
            "                        from reports a \n" +
            "                        inner join report_assignments b on b.report_id=a.id \n" +
            "                        inner join workflow_statuses c on c.id=a.status_id\n" +
            "                        inner join grants d on d.id=a.grant_id\n" +
            "                        where b.assignment=?1 and (\n" +
            "\t\t\t\t\t\t\t((select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or \n" +
            "\t\t\t\t\t\t\t(C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id)))\n" +
            "\t\t\t\t\t\t)  and a.deleted=false and d.deleted=false  \t\n" +
            "            ) X group by X.assignment",nativeQuery = true)
    Long getUpcomingReportsDisbursementAmount(Long userId);
}
