package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.GranterGrantSectionAttribute;
import org.springframework.data.repository.CrudRepository;

public interface GrantDocumentAttributesRepository extends CrudRepository<GrantDocumentAttributes, Long> {

  public GrantDocumentAttributes findByGrantAndName(Grant grant, String name);
  public GrantDocumentAttributes findBySectionAndSectionAttributeAndGrant(GranterGrantSection section, GranterGrantSectionAttribute attribute, Grant grant);
}
