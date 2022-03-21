package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantSpecificSection;
import org.codealpha.gmsservice.entities.GrantSpecificSectionAttribute;
import org.codealpha.gmsservice.entities.GrantStringAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantStringAttributeRepository extends CrudRepository<GrantStringAttribute, Long> {

    public List<GrantStringAttribute> findBySectionAttribute(GrantSpecificSectionAttribute granterGrantSectionAttribute);


    public GrantStringAttribute findBySectionAndSectionAttributeAndGrant(GrantSpecificSection granterGrantSection, GrantSpecificSectionAttribute granterGrantSectionAttribute, Grant grant);

    @Query(value = "select * from grant_string_attributes where section_id=?1 and section_attribute_id=?2 and grant_id=?3",nativeQuery = true)
    public GrantStringAttribute findBySectionAndSectionIdAttributeIdAndGrantId(Long granterGrantSectionId, Long granterGrantSectionAttributeId, Long grantId);

    @Query(value="select * from grant_string_attributes where id = ?1",nativeQuery = true)
    public GrantStringAttribute getGrantStringAttributeById(Long id);

}
