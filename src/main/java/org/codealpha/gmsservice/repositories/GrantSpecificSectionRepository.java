package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantSpecificSection;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantSpecificSectionRepository extends CrudRepository<GrantSpecificSection,Long> {

    public GrantSpecificSection findByGranterAndSectionName(Granter granter, String sectionName);

    List<GrantSpecificSection> findByGranterAndGrantId(Granter granter,Long grantId);
}
