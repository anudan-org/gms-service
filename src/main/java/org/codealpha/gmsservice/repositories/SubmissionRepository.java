package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Submission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SubmissionRepository extends CrudRepository<Submission,Long> {

  // CHANGED: fetch submissions by grant id for dashboard serialization
  List<Submission> findByGrantId(Long grantId);

  @Query(value = "select * from submissions where grant_id in (?1) order by submit_by asc", nativeQuery = true)
  List<Submission> findByGrantIdInOrderBySubmitBy(List<Long> grantIds);
}
