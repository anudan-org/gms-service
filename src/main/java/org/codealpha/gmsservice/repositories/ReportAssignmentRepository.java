package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportAssignment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportAssignmentRepository extends CrudRepository<ReportAssignment,Long> {
    List<ReportAssignment> findByReportId(Long id);

    @Query(value = "select distinct B.*,A.moved_on from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.state_id=A.status_id) and ( (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) or (C.internal_status='REVIEW' ) ) ) and  now()>A.moved_on and Z.grantor_org_id not in(?1) and Z.deleted=false and A.deleted=false",nativeQuery = true)
    List<ReportAssignment> getActionDueReportsForPlatform(List<Long> granterIds);

    @Query(value = "select distinct B.* from reports A inner join grants Z on Z.id=A.grant_id inner join report_assignments B on B.report_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.state_id=A.status_id) and ( (C.internal_status='DRAFT' and (select count(*) from report_history where id=A.id)>0 ) or (C.internal_status='REVIEW' )) ) and   now()>A.moved_on and Z.grantor_org_id =?1 and Z.deleted=false and A.deleted=false",nativeQuery = true)
    List<ReportAssignment> getActionDueReportsForGranterOrg(Long granterId);
}
