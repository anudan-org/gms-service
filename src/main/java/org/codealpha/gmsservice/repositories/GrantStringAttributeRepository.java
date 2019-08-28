package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantStringAttributeRepository extends CrudRepository<GrantStringAttribute, Long> {

    public List<GrantStringAttribute> findBySectionAttribute(GrantSpecificSectionAttribute granterGrantSectionAttribute);


    public GrantStringAttribute findBySectionAndSectionAttributeAndGrant(GrantSpecificSection granterGrantSection, GrantSpecificSectionAttribute granterGrantSectionAttribute, Grant grant);
}
