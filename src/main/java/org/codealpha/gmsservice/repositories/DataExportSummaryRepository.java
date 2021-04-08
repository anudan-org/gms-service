package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.DataExportSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DataExportSummaryRepository extends CrudRepository<DataExportSummary,Long> {
}
