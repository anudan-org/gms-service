package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantStringAttribute;
import org.codealpha.gmsservice.entities.GrantStringAttributeAttachments;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantStringAttributeAttachmentRepository extends CrudRepository<GrantStringAttributeAttachments,Long> {

    public List<GrantStringAttributeAttachments> findByGrantStringAttribute(GrantStringAttribute attrib);
}
