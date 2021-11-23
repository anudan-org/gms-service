package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureStringAttributeRepository extends CrudRepository<ClosureStringAttribute,Long> {

    public List<ClosureStringAttribute> findByClosure(GrantClosure closure);
    public ClosureStringAttribute findBySectionAttributeAndSection(ClosureSpecificSectionAttribute sectionAttribute, ClosureSpecificSection section);

    List<ClosureStringAttribute> findBySectionAttribute(ClosureSpecificSectionAttribute attrib);
}
