package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Report;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportRepository extends CrudRepository<Report,Long> {
    @Query(value = "select distinct A.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.assignment = ?1) or (B.assignment=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 and ?1 = any (array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='REVIEW' and ?1 = any( array(select assignment from report_assignments where report_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) order by A.due_date desc",nativeQuery = true)
    List<Report> findAllAssignedReportsForUser(Long id, Long granterOrgId);

    @Query(value = "select sum(cast(c.actual_target as bigint)) from reports A inner join workflow_statuses B on B.id=A.status_id inner join report_string_attributes C on C.report_id=A.id inner join report_specific_section_attributes D on D.id=C.section_attribute_id where A. grant_id=?1 and B.internal_status='CLOSED' and D.field_name=?2 group by D.field_name;",nativeQuery = true)
    Long getApprovedReportsActualSumForGrantAndAttribute(Long id,String attributeName);
}
