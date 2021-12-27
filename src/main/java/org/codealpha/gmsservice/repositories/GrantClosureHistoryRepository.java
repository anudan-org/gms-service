package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantClosureHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantClosureHistoryRepository extends CrudRepository<GrantClosureHistory,Long> {

    @Query(value = "select * from grant_closure_history where id=?1 and (note is not null or note!='') order by seqid desc",nativeQuery = true)
    public List<GrantClosureHistory> findByClosureId(Long reportId);

    @Query(value="select A.* from grant_closure_history A inner join workflow_statuses B on B.id=A.status_id where A.id=?1 and (note is not null or note!='') and (A.note_added_by= ?2 or B.internal_status in ('DRAFT','ACTIVE','CLOSED')) order by seqid desc",nativeQuery = true)
    public List<GrantClosureHistory> findClosureHistoryForGranteeByClosureId(Long closureId, Long granteeUserId);

    @Query(value = "select A.* from grant_closure_history A inner join workflow_statuses B on B.id=A.status_id where B.internal_status=?1 and A.id=?2 order by moved_on desc limit 1",nativeQuery = true)
    GrantClosureHistory getSingleClosureHistoryByStatusAndClosureId(String status, Long closureId);
}
