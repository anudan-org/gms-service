package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GranterClosureSection;
import org.codealpha.gmsservice.entities.GranterClosureTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterClosureSectionRepository extends CrudRepository<GranterClosureSection,Long> {
    public List<GranterClosureSection> findByClosureTemplate(GranterClosureTemplate template);
}
