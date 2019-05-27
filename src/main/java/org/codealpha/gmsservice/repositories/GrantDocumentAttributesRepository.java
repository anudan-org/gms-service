package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.springframework.data.repository.CrudRepository;

public interface GrantDocumentAttributesRepository extends CrudRepository<GrantDocumentAttributes, Long> {

  public GrantDocumentAttributes findByGrantAndName(Grant grant, String name);
}
