package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Report;
import org.codealpha.gmsservice.entities.ReportCard;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ReportCardRepository extends CrudRepository<ReportCard, Long> {
    @Query(value = "select distinct A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and C.internal_status=?3 and Z.organization_id=?2 and Z.deleted=false and A.deleted=false order by A.due_date desc", nativeQuery = true)
    List<ReportCard> findAllAssignedReportsForGranteeUser(Long id, Long granteeOrgId, String status);

    @Query(value = "select distinct A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.end_date asc", nativeQuery = true)
    List<ReportCard> findUpcomingReportsForGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.end_date asc", nativeQuery = true)
    List<ReportCard> findUpcomingReportsForAdminGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and A.end_date > ?3 and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.grant_id=?4 and A.deleted=false order by A.due_date desc", nativeQuery = true)
    List<ReportCard> findFutureReportsToSubmitForGranterUserByDateRangeAndGrant(Long id, Long granterOrgId, Date end,
            Long grantId);

    @Query(value = "select distinct A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id  inner join workflow_statuses D on D.id=A.status_id where ( (B.anchor=true and B.assignment = ?1)  or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (D.internal_status='ACTIVE' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status ='ACTIVE') and A.deleted=false order by A.due_date asc", nativeQuery = true)
    List<ReportCard> findReadyToSubmitReportsForGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id inner join workflow_statuses D on D.id=A.status_id where ( (B.anchor=true and B.assignment = ?1)  or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) or (C.internal_status='REVIEW' ) or (D.internal_status='ACTIVE' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status ='ACTIVE') and A.deleted=false order by A.due_date asc", nativeQuery = true)
    List<ReportCard> findReadyToSubmitReportsForAdminGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);


    @Query(value = "select distinct C.internal_status,A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='REVIEW' OR C.internal_status ='ACTIVE') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<ReportCard> findSubmittedReportsForGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='REVIEW' or C.internal_status='ACTIVE' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='REVIEW' or C.internal_status='ACTIVE') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<ReportCard> findSubmittedReportsForAdminGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='CLOSED') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<ReportCard> findApprovedReportsForGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.*,false can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status ='CLOSED') and A.deleted=false order by A.moved_on desc", nativeQuery = true)
    List<ReportCard> findApprovedReportsForAdminGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select A.*,true as can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A where status_id=?1 and grant_id=?2",nativeQuery = true)
    public List<ReportCard> findByStatusAndGrant(Long statusId, Long grantId);

    @Query(value="select A.*,true can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A where grant_id=?1 and deleted=false",nativeQuery = true)
    List<ReportCard> getReportsByGrant(Long grantId);

    @Query(value = "select  A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED')  and A.deleted=false order by A.grant_id,A.end_date asc", nativeQuery = true)
    List<ReportCard> findUpcomingFutureReports(Long userId, Long id);

    @Query(value = "select  A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id  from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) ) and Z.grantor_org_id=?2 and Z.deleted=false and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.deleted=false order by A.grant_id,A.end_date asc", nativeQuery = true)
    List<ReportCard> findUpcomingFutureAdminReports(Long userId, Long id);

    @Query(value = "select distinct a.* ,case when (select assignment from report_assignments x where x.state_id=a.status_id and x.report_id=a.id)=?1 then true else false end can_manage,get_owner_report_name(a.id) owner_name,get_owner_report(a.id) owner_id from reports a\n" +
            "inner join report_assignments b on b.report_id=a.id and b.state_id=a.status_id\n" +
            "inner join grants c on c.id=a.grant_id\n" +
            "where b.assignment=?1\n" +
            "and (a.end_date between now() and (now()+ INTERVAL '15 day') \n" +
            "or a.due_date<now()\n" +
            "or (select count(*) from report_history where id=a.id )>0)\n" +
            "and c.deleted=false and a.deleted=false",nativeQuery = true)
    List<ReportCard> getDetailedActionDueReportsForUser(Long userId);

    @Query(value = "select distinct a.*,case when (select assignment from report_assignments x where x.state_id=a.status_id and x.report_id=a.id)=?1 then true else false end can_manage,get_owner_report_name(a.id) owner_name,get_owner_report(a.id) owner_id from reports a \n" +
            "inner join report_assignments b on b.report_id=a.id and b.state_id=a.status_id \n" +
            "inner join workflow_statuses c on c.id=a.status_id \n" +
            "inner join grants d on d.id=a.grant_id\n" +
            "where b.assignment=?1 \n" +
            "and c.internal_status='DRAFT' \n" +
            "and d.deleted=false and a.deleted=false",nativeQuery = true)
    List<ReportCard> getDetailedUpComingDraftReports(Long userId);

    @Query(value = "select distinct A.*,case when (select assignment from report_assignments x where x.state_id=A.status_id and x.report_id=A.id)=?1 then true else false end can_manage,get_owner_report_name(A.id) owner_name,get_owner_report(A.id) owner_id from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and Z.deleted=false and A.end_date > ?3 and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.grant_id=?4 and A.deleted=false order by A.due_date desc", nativeQuery = true)
    List<ReportCard> futureReportsToSubmitForGranterUserByDateRangeAndGrant(Long id, Long granterOrgId, Date end,
                                                                            Long grantId);

}
