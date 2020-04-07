package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportAssignment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportAssignmentRepository extends CrudRepository<ReportAssignment,Long> {
    List<ReportAssignment> findByReportId(Long id);

    @Query(value = "select distinct B.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.state_id=A.status_id) and ( (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) or (C.internal_status='REVIEW' )) ) and  (DATE_PART('day', now()-A.updated_at ) * 24 + DATE_PART('hour', now()-A.updated_at ))*60 + DATE_PART('minute', now()-A.updated_at )=?1 and Z.grantor_org_id not in(?2) order by A.due_date desc",nativeQuery = true)
    List<ReportAssignment> getActionDueReportsForPlatform(Long noOfMinutes, List<Long> granterIds);
}
