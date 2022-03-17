package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantSpecificSection;
import org.codealpha.gmsservice.entities.Granter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantSpecificSectionRepository extends CrudRepository<GrantSpecificSection,Long> {

    public GrantSpecificSection findByGranterAndSectionName(Granter granter, String sectionName);

    List<GrantSpecificSection> findByGranterAndGrantId(Granter granter,Long grantId);

    @Query(value = "select max(section_order)+1 from grant_specific_sections where granter_id=?1 and grant_template_id=?2",nativeQuery = true)
    public int getNextSectionOrder(Long granterId, Long templateId);
}
