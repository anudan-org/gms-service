package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureSpecificSection;
import org.codealpha.gmsservice.entities.ClosureSpecificSectionAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureSpecificSectionAttributeRepository extends CrudRepository<ClosureSpecificSectionAttribute,Long> {

    @Query(value = "select case when max(attribute_order)+1 is null then 1 else max(attribute_order)+1 end from closure_specific_section_attributes where granter_id=?1 and section_id=?2",nativeQuery = true)
    public int getNextAttributeOrder(Long granterId, Long sectionId);

    public List<ClosureSpecificSectionAttribute> findBySection(ClosureSpecificSection section);
}
