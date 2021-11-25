package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureSnapshot;
import org.codealpha.gmsservice.entities.ClosureSnapshot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureSnapshotRepository extends CrudRepository<ClosureSnapshot, Long> {

    @Query(value = "Select * from closure_snapshot where closure_id=?1 and assigned_to_id=?2 and status_id=?3 order by id desc limit 1", nativeQuery = true)
    public ClosureSnapshot findByClosureIdAndAssignedToAndStatusId(Long grantId, Long assignedToId, Long statusId);

    @Query(value = "select * from closure_snapshot where closure_id=?1 order by id desc limit 1", nativeQuery = true)
    public ClosureSnapshot findByMostRecentByClosureId(Long closureId);

    @Query(value = "select * from closure_snapshot where closure_id=?1 order by id DESC", nativeQuery = true)
    List<ClosureSnapshot> getClosureSnapshotsForReport(Long closureId);
}
