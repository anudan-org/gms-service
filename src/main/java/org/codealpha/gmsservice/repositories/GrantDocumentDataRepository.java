package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantDocumentKpiData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GrantDocumentDataRepository extends CrudRepository<GrantDocumentKpiData,Long> {

  @Query(value = "select * from grant_document_kpi_data where grant_kpi_id=?1 and submission_id=?2",nativeQuery = true)
  public GrantDocumentKpiData findByGrantKpiIdAndSubmissionId(Long kpiId, Long submissionId);
}
