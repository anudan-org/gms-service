package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.GranterGrantSectionAttribute;
import org.springframework.data.repository.CrudRepository;

public interface GranterGrantSectionAttributeRepository extends CrudRepository<GranterGrantSectionAttribute, Long> {

  public GranterGrantSectionAttribute findBySectionAndFieldTypeAndFieldName(GranterGrantSection section, String fieldType,String fieldName );
}
