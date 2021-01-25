package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterReportStatus;
import org.codealpha.gmsservice.entities.dashboard.GranterReportSummaryStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Cacheable;
import java.util.List;

@Cacheable(value = false)
public interface GranterReportSummaryStatusRepository extends CrudRepository<GranterReportSummaryStatus,Long> {

    @Query(value = "select row_number() over () as id,* from (select Z.grantor_org_id granter_id, C.name status, D.internal_status internal_status, count(A.*) from reports A inner join grants Z on Z.id=A.grant_id inner join workflow_statuses D on D.id=A.status_id inner join workflow_statuses C on C.id=A.status_id where Z.deleted=false  and A.deleted=false group by Z.grantor_org_id,C.name,D.internal_status) X where X.granter_id=?1",nativeQuery = true)
    List<GranterReportSummaryStatus> getReportsByStatusForGranter(Long granterId);
}
