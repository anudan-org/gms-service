package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantAssignments;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantAssignmentRepository  extends CrudRepository<GrantAssignments,Long> {

public List<GrantAssignments> findByGrantIdAndStateId(Long grantId,Long stateId);
}
