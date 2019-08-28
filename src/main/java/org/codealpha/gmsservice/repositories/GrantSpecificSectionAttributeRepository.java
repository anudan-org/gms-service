package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantSpecificSection;
import org.codealpha.gmsservice.entities.GrantSpecificSectionAttribute;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.GranterGrantSectionAttribute;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantSpecificSectionAttributeRepository extends CrudRepository<GrantSpecificSectionAttribute, Long> {

  public GrantSpecificSectionAttribute findBySectionAndFieldName(GrantSpecificSection section, String fieldName);

  public List<GrantSpecificSectionAttribute> findBySection(GrantSpecificSection section);
}
