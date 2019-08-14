package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.springframework.data.repository.CrudRepository;

public interface GranterGrantSectionRepository extends CrudRepository<GranterGrantSection,Long> {

    public GranterGrantSection findByGranterAndSectionName(Granter granter, String sectionName);
}
