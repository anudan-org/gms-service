package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantSpecificSection;
import org.codealpha.gmsservice.entities.GrantSpecificSectionAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantSpecificSectionAttributeRepository extends CrudRepository<GrantSpecificSectionAttribute, Long> {

  public GrantSpecificSectionAttribute findBySectionAndFieldName(GrantSpecificSection section, String fieldName);

  public List<GrantSpecificSectionAttribute> findBySection(GrantSpecificSection section);

  @Query(value = "select case when max(attribute_order)+1 is null then 1 else max(attribute_order)+1 end from grant_specific_section_attributes where granter_id=?1 and section_id=?2",nativeQuery = true)
  public int getNextAttributeOrder(Long granterId, Long sectionId);
}
