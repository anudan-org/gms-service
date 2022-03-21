package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantDocument;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantDocumentRepository extends CrudRepository<GrantDocument, Long> {

    List<GrantDocument> findByGrantId(Long grantId);
}