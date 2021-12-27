package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureStringAttribute;
import org.codealpha.gmsservice.entities.ClosureStringAttributeAttachments;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClosureStringAttributeAttachmentsRepository extends CrudRepository<ClosureStringAttributeAttachments,Long> {

    public List<ClosureStringAttributeAttachments> findByClosureStringAttribute(ClosureStringAttribute closureStringAttribute);
}
