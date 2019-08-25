package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantStringAttribute;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.GranterGrantSectionAttribute;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantStringAttributeRepository extends CrudRepository<GrantStringAttribute, Long> {

    public List<GrantStringAttribute> findBySectionAttribute(GranterGrantSectionAttribute granterGrantSectionAttribute);


    public GrantStringAttribute findBySectionAndSectionAttributeAndGrant(GranterGrantSection granterGrantSection, GranterGrantSectionAttribute granterGrantSectionAttribute, Grant grant);
}
