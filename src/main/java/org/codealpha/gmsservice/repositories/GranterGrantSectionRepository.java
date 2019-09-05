package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterGrantSectionRepository extends CrudRepository<GranterGrantSection,Long> {

    public GranterGrantSection findByGranterAndSectionName(Granter granter, String sectionName);

    List<GranterGrantSection> findByGranter(Granter granter);
}
