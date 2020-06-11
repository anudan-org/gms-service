package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.DisbursementAssignment;
import org.springframework.data.repository.CrudRepository;

public interface DisbursementAssignmentRepository extends CrudRepository<DisbursementAssignment,Long> {

    public List<DisbursementAssignment> findByDisbursementId(Long disbursementId);
}
