package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureDocument;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureDocumentRepository extends CrudRepository<ClosureDocument, Long> {

    List<ClosureDocument> findByClosureId(Long closureId);

    @Query(value = "select * from closure_documents where id=?1",nativeQuery = true)
    ClosureDocument findByDocId(Long docId);
}