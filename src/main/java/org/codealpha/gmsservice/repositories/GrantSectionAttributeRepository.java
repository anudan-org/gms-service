package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantSectionAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantSectionAttributeRepository extends CrudRepository<GrantSectionAttribute, Long> {

    @Query(value = "select * from grant_section_attributes", nativeQuery = true)
    public List<GrantSectionAttribute> getAllDefaultSectionAttributes();

    @Query(value = "select * from grant_section_attributes where section_id=?1", nativeQuery = true)
    public List<GrantSectionAttribute> getAllDefaultSectionAttributesForSection(Long sectionId);


}
