package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureSpecificSection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureSpecificSectionRepository extends CrudRepository<ClosureSpecificSection,Long> {
    List<ClosureSpecificSection> findByClosureId(Long id);

    @Query(value = "select max(section_order)+1 from closure_specific_sections where granter_id=?1 and closure_template_id=?2",nativeQuery = true)
    public int getNextSectionOrder(Long granterId, Long templateId);

}
