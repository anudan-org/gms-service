package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantHistoryRepository extends CrudRepository<GrantHistory,Long> {

    @Query(value = "select * from grant_history where id=?1 and (note is not null or note!='') order by seqid desc",nativeQuery = true)
    public List<GrantHistory> findByGrantId(Long grantId);
}
