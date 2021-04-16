package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantTag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantTagRepository extends CrudRepository<GrantTag,Long> {

    @Query(value = "select * from grant_tags where grant_id=?1",nativeQuery = true)
    List<GrantTag> getTagsForGrant(Long grantId);

    @Query(value = "select case when count(*)>0 then true else false end from grant_tags where org_tag_id=?1",nativeQuery = true)
    boolean isTagInUse(Long orgTagId);

    @Query(value = "select * from grant_tags where id=?1",nativeQuery = true)
    GrantTag getTagById(Long id);
}
