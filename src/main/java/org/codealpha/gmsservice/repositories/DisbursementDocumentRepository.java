package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.DisbursementDocument;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DisbursementDocumentRepository extends CrudRepository<DisbursementDocument, Long> {

    List<DisbursementDocument> findByDisbursementId(Long disbursementId);
}