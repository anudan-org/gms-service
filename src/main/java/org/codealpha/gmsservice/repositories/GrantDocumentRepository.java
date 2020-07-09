package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.GrantDocument;
import org.springframework.data.repository.CrudRepository;

public interface GrantDocumentRepository extends CrudRepository<GrantDocument, Long> {

    List<GrantDocument> findByGrantId(Long grantId);
}