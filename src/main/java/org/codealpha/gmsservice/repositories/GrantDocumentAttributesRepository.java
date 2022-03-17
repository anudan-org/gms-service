package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.entities.GrantSpecificSection;
import org.codealpha.gmsservice.entities.GrantSpecificSectionAttribute;
import org.springframework.data.repository.CrudRepository;

public interface GrantDocumentAttributesRepository extends CrudRepository<GrantDocumentAttributes, Long> {

  public GrantDocumentAttributes findByGrantAndName(Grant grant, String name);
  public GrantDocumentAttributes findBySectionAndSectionAttributeAndGrant(GrantSpecificSection section, GrantSpecificSectionAttribute attribute, Grant grant);
}
