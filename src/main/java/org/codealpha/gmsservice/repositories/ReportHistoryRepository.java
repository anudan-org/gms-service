package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantHistory;
import org.codealpha.gmsservice.entities.ReportHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportHistoryRepository extends CrudRepository<ReportHistory,Long> {

    @Query(value = "select * from report_history where id=?1 and (note is not null or note!='') order by seqid desc",nativeQuery = true)
    public List<ReportHistory> findByReportId(Long reportId);
}
