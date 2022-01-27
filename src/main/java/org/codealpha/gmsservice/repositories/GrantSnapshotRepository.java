package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.GrantSnapshot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GrantSnapshotRepository extends CrudRepository<GrantSnapshot, Long> {

    @Query(value = "Select * from grant_snapshot where grant_id=?1 and assigned_to_id=?2 and grant_status_id=?3 order by id desc limit 1", nativeQuery = true)
    public GrantSnapshot findByGrantIdAndAssignedToAndStatusId(Long grantId, Long assignedToId, Long statusId);

    @Query(value = "select * from grant_snapshot where grant_id=?1 order by id desc limit 1", nativeQuery = true)
    public GrantSnapshot findByMostRecentByGrantId(Long grantId);

    @Query(value = "select * from grant_snapshot where grant_id=?1 order by moved_on DESC ,id desc", nativeQuery = true)
    public List<GrantSnapshot> getGrantShanpshotsForGrant(Long grantId);
}
