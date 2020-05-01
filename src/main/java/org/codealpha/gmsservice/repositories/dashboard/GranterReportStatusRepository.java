package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterReportStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterReportStatusRepository extends CrudRepository<GranterReportStatus,Long> {

    @Query(value="select row_number() OVER () as id,* from ( select Z.grantor_org_id granter_id,D.internal_status,'Due' as status,count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=Z.grant_status_id inner join workflow_statuses C on C.id=A.status_id where ((select count(*) from report_history where id=A.id)>1) and (C.internal_status !='CLOSED') and (A.end_date<now()) group by Z.grantor_org_id,D.internal_status union select Z.grantor_org_id granter_id,D.internal_status,'Approved' as status,count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=Z.grant_status_id inner join workflow_statuses C on C.id=A.status_id where (C.internal_status ='CLOSED') group by Z.grantor_org_id,C.internal_status,D.internal_status union select Z.grantor_org_id,D.internal_status,'Unapproved' as status,count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=Z.grant_status_id inner join workflow_statuses C on C.id=A.status_id inner join report_history E on E.id=A.id inner join workflow_statuses F on F.id=E.status_id where C.internal_status='REVIEW' and F.internal_status='ACTIVE' group by Z.grantor_org_id,D.internal_status union select Z.grantor_org_id granter_id,D.internal_status,'Overdue' as status,count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=Z.grant_status_id inner join workflow_statuses C on C.id=A.status_id where ((select count(*) from report_history where id=A.id)>1) and (C.internal_status !='CLOSED') and (A.end_date<now()) and (A.due_date<now()) group by Z.grantor_org_id,D.internal_status ) X where X.granter_id=?1 and X.internal_status=?2",nativeQuery = true)
    List<GranterReportStatus> getReportStatusesForGranter(Long granterId,String status);
}
