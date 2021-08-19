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

    @Query(value="select row_number() OVER () as id,* from ( \n" +
            "            select P.granter_id,P.internal_status,'Due' as status,count(P.*) from (select distinct Z.organization_id granter_id,D.internal_status,'Due' as status \n" +
            "            from \n" +
            "            reports A inner join grants Z on Z.id=A.grant_id \n" +
            "            inner join workflow_statuses D on D.id=Z.grant_status_id \n" +
            "            inner join workflow_statuses C on C.id=A.status_id \n" +
            "            where Z.deleted=false and (C.internal_status ='ACTIVE') \n" +
            "            and  (A.end_date between now() and (now()+ INTERVAL '15 day') and now()<A.due_date)) P group by P.granter_id,P.internal_status union \n" +
            "            select P.granter_id,P.internal_status,'Overdue' as status,count(P.*) from (select distinct Z.organization_id granter_id,D.internal_status,'Overdue' as status \n" +
            "            from \n" +
            "            reports A inner join grants Z on Z.id=A.grant_id \n" +
            "            inner join workflow_statuses D on D.id=Z.grant_status_id \n" +
            "            inner join workflow_statuses C on C.id=A.status_id \n" +
            "            where Z.deleted=false and (C.internal_status ='ACTIVE') \n" +
            "            and  (A.due_date<now())) P group by P.granter_id,P.internal_status \n" +
            "\t\t\tunion\n" +
            "\t\t\tselect P.granter_id,P.internal_status,'Submitted' as status,count(P.*) from (select distinct Z.organization_id granter_id,D.internal_status,'Submitted' as status \n" +
            "            from \n" +
            "            reports A inner join grants Z on Z.id=A.grant_id \n" +
            "            inner join workflow_statuses D on D.id=Z.grant_status_id \n" +
            "            inner join workflow_statuses C on C.id=A.status_id \n" +
            "            where Z.deleted=false and (C.internal_status ='REVIEW') \n" +
            "            ) P group by P.granter_id,P.internal_status \n" +
            "            ) X where X.granter_id=?1 and X.internal_status=?2",nativeQuery = true)
    List<GranterReportStatus> getReportStatusesForGrantee(Long granteeId,String status);

    @Query(value="select row_number() OVER () as id,* from (select 1 granter_id,'' internal_status,'Due' as status,count(*) from grants a\n" +
            "inner join grant_assignments b on b.state_id=a.grant_status_id and b.grant_id=a.id\n" +
            "inner join workflow_statuses c on c.id=a.grant_status_id\n" +
            "inner join reports d on d.grant_id=a.id\n" +
            "inner join workflow_statuses e on e.id=d.status_id\n" +
            "where c.internal_status=?2 and b.assignments=?1 and a.deleted=false\n" +
            "and e.internal_status!='CLOSED' and d.deleted=false\n" +
            "and (\n" +
            "\t\t(d.end_date between now() and (now()+ INTERVAL '15 day')) or\n" +
            "\t\t(now()>d.end_date and now()<d.due_date)\n" +
            "\t)\n" +
            "union\n" +
            "select 1 granter_id,'' internal_status,'Overdue' as status,count(*) from grants a\n" +
            "inner join grant_assignments b on b.state_id=a.grant_status_id and b.grant_id=a.id\n" +
            "inner join workflow_statuses c on c.id=a.grant_status_id\n" +
            "inner join reports d on d.grant_id=a.id\n" +
            "inner join workflow_statuses e on e.id=d.status_id\n" +
            "where c.internal_status=?2 and b.assignments=?1 and a.deleted=false\n" +
            "and e.internal_status!='CLOSED' and d.deleted=false\n" +
            "and (\n" +
            "\t\t(now()>d.due_date)\n" +
            "\t)\n" +
            ") X",nativeQuery = true)
    List<GranterReportStatus> getReportStatusesForUser(Long userId,String status);

    @Query(value = "select row_number() over () as id,* from (select Z.grantor_org_id granter_id, C.name status, '' internal_status, count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=Z.grant_status_id inner join workflow_statuses C on C.id=A.status_id where Z.deleted=false  and A.deleted=false group by Z.grantor_org_id,C.name) X where X.granter_id=?1",nativeQuery = true)
    List<GranterReportStatus> getReportsByStatusForGranter(Long granterId);
}
