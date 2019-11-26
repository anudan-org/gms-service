package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportAssignment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportAssignmentRepository extends CrudRepository<ReportAssignment,Long> {
    List<ReportAssignment> findByReportId(Long id);
}
