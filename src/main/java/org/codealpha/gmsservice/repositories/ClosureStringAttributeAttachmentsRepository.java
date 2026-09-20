package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ClosureStringAttribute;
import org.codealpha.gmsservice.entities.ClosureStringAttributeAttachments;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClosureStringAttributeAttachmentsRepository extends CrudRepository<ClosureStringAttributeAttachments,Long> {

    public List<ClosureStringAttributeAttachments> findByClosureStringAttribute(ClosureStringAttribute closureStringAttribute);

    @Modifying
    @Transactional
    @Query(value = "delete from closure_string_attribute_attachments where id = :attachmentId", nativeQuery = true)
    int hardDeleteById(@Param("attachmentId") Long attachmentId);

    @Modifying
    @Transactional
    @Query(value = """
            delete from closure_string_attribute_attachments
            where closure_string_attribute_id = :stringAttributeId
              and name = :name
              and type = :type
              and location = :location
            """, nativeQuery = true)
    int hardDeleteByFileKey(@Param("stringAttributeId") Long stringAttributeId,
                            @Param("name") String name,
                            @Param("type") String type,
                            @Param("location") String location);
}
