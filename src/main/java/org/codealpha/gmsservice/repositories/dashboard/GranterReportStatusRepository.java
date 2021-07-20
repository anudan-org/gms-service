package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterReportStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Cacheable;
import java.util.List;
@Cacheable(value = false)
public interface GranterReportStatusRepository extends CrudRepository<GranterReportStatus,Long> {

    @Query(value="select row_number() OVER () as id,* from ( select Z.grantor_org_id granter_id,D.internal_status,'Due' as status,count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=Z.grant_status_id inner join workflow_statuses C on C.id=A.status_id where Z.deleted=false and (C.internal_status !='CLOSED') and (now() between A.end_date and A.due_date) group by Z.grantor_org_id,D.internal_status union select Z.grantor_org_id granter_id,D.internal_status,'Overdue' as status,count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=Z.grant_status_id inner join workflow_statuses C on C.id=A.status_id where Z.deleted=false and (C.internal_status !='CLOSED') and  (A.due_date<now()) group by Z.grantor_org_id,D.internal_status ) X where X.granter_id=?1 and X.internal_status=?2",nativeQuery = true)
    List<GranterReportStatus> getReportStatusesForGranter(Long granterId,String status);

    @Query(value="select row_number() OVER () as id,* from ( select p.assignment granter_id,D.internal_status,'Due' as status,count(A.*) \n" +
            "\t\t\t\t\t\t\t\t\t\t  from reports A \n" +
            "\t\t\t\t\t\t\t\t\t\t  inner join grants Z on Z.id=A.grant_id \n" +
            "\t\t\t\t\t\t\t\t\t\t  inner join workflow_statuses D on D.id=Z.grant_status_id \n" +
            "\t\t\t\t\t\t\t\t\t\t  inner join report_assignments p on p.state_id=A.status_id and p.report_id=A.id\n" +
            "\t\t\t\t\t\t\t\t\t\t  inner join workflow_statuses C on C.id=A.status_id \n" +
            "\t\t\t\t\t\t\t\t\t\t  where Z.deleted=false and (C.internal_status !='CLOSED') \n" +
            "\t\t\t\t\t\t\t\t\t\t  and (now() between A.end_date and A.due_date) \n" +
            "\t\t\t\t\t\t\t\t\t\t  and p.assignment=?1\n" +
            "\t\t\t\t\t\t\t\t\t\t  group by p.assignment,D.internal_status \n" +
            "\t\t\t\t\t\t\t\tunion \n" +
            "\t\t\t\t\t\t\t\t\t\t  select  p.assignment granter_id,D.internal_status,'Overdue' as status,count(A.*) \n" +
            "\t\t\t\t\t\t\t\t\t\t  from reports A \n" +
            "\t\t\t\t\t\t\t\t\t\t  inner join grants Z on Z.id=A.grant_id \n" +
            "\t\t\t\t\t\t\t\t\t\t  inner join workflow_statuses D on D.id=Z.grant_status_id\n" +
            "\t\t\t\t\t\t\t\t\t\t  inner join report_assignments p on p.state_id=A.status_id and p.report_id=A.id\n" +
            "\t\t\t\t\t\t\t\t\t\t  inner join workflow_statuses C on C.id=A.status_id \n" +
            "\t\t\t\t\t\t\t\t\t\t  where Z.deleted=false and (C.internal_status !='CLOSED') \n" +
            "\t\t\t\t\t\t\t\t\t\t  and  (A.due_date<now()) and p.assignment=?1 group by p.assignment,D.internal_status ) X \n" +
            "where X.granter_id=?1 and X.internal_status=?2",nativeQuery = true)
    List<GranterReportStatus> getReportStatusesForUser(Long userId,String status);

    @Query(value = "select row_number() over () as id,* from (select Z.grantor_org_id granter_id, C.name status, '' internal_status, count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=Z.grant_status_id inner join workflow_statuses C on C.id=A.status_id where Z.deleted=false  and A.deleted=false group by Z.grantor_org_id,C.name) X where X.granter_id=?1",nativeQuery = true)
    List<GranterReportStatus> getReportsByStatusForGranter(Long granterId);
}
