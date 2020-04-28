package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Report;
import org.codealpha.gmsservice.entities.ReportAssignment;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ReportRepository extends CrudRepository<Report,Long> {
    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and C.internal_status=?3 and Z.organization_id=?2 order by A.due_date desc",nativeQuery = true)
    List<Report> findAllAssignedReportsForGranteeUser(Long id, Long granteeOrgId,String status);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 order by A.due_date desc",nativeQuery = true)
    List<Report> findAllAssignedReportsForGranterUser(Long id, Long granterOrgId);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') order by A.due_date desc",nativeQuery = true)
    List<Report> findUpcomingReportsForGranterUserByDateRange(Long id, Long granterOrgId, Date start, Date end);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and A.end_date > ?3 and (C.internal_status !='ACTIVE' and C.internal_status !='REVIEW' and C.internal_status !='CLOSED') and A.grant_id=?4 order by A.due_date desc",nativeQuery = true)
    List<Report> findFutureReportsToSubmitForGranterUserByDateRangeAndGrant(Long id, Long granterOrgId,Date end, Long grantId);

    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and (A.end_date < ?3 or (A.end_date between ?3 and ?4)) and (C.internal_status ='ACTIVE') order by A.due_date desc",nativeQuery = true)
    List<Report> findReadyToSubmitReportsForGranterUserByDateRange(Long id, Long granterOrgId,Date start, Date end);

    @Query(value = "select distinct C.internal_status,A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) ) and Z.grantor_org_id=?2 and (C.internal_status ='REVIEW') order by A.due_date desc",nativeQuery = true)
    List<Report> findSubmittedReportsForGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select distinct C.internal_status,A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='CLOSED' ) ) and Z.grantor_org_id=?2 and (C.internal_status ='CLOSED') order by A.due_date desc",nativeQuery = true)
    List<Report> findApprovedReportsForGranterUserByDateRange(Long id, Long granterOrgId);

    @Query(value = "select sum(cast(c.actual_target as bigint)) from reports A inner join workflow_statuses B on B.id=A.status_id inner join report_string_attributes C on C.report_id=A.id inner join report_specific_section_attributes D on D.id=C.section_attribute_id where A. grant_id=?1 and B.internal_status='CLOSED' and D.field_name=?2 group by D.field_name;",nativeQuery = true)
    Long getApprovedReportsActualSumForGrantAndAttribute(Long id,String attributeName);

    @Query(value = "select * from reports r inner join grants g on g.id=r.grant_id inner join workflow_statuses wf on wf.id=r.status_id where r.due_date=?1 and wf.internal_status='ACTIVE' and g.grantor_org_id not in (?2) order by r.due_date",nativeQuery = true)
    List<Report> getDueReportsForPlatform(Date dueDate,List<Long> granterIds);

    @Query(value = "select * from reports r inner join grants g on g.id=r.grant_id inner join workflow_statuses wf on wf.id=r.status_id where r.due_date=?1 and wf.internal_status='ACTIVE' and g.grantor_org_id = ?2 order by r.due_date",nativeQuery = true)
    List<Report> getDueReportsForGranter(Date dueDate,Long granterId);

    @Query(value = "select * from reports A inner join workflow_statuses B on B.id=A.status_id where A.grant_id=?1 and B.internal_status=?2 and A.id!=?3",nativeQuery = true)
    public List<Report> findByGrantAndStatus(Long grantId, String statusName, Long currentReportId);

    @Query(value = "select * from reports where id in (?1)",nativeQuery = true)
    public List<Report> findReportsByIds(List<Long> reportIds);

}
