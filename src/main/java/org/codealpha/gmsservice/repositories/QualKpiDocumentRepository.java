package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.QualKpiDataDocument;
import org.codealpha.gmsservice.entities.QuantKpiDataDocument;
import org.springframework.data.repository.CrudRepository;

public interface QualKpiDocumentRepository extends CrudRepository<QualKpiDataDocument, Long> {

}
