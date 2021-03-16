package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantTag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantTagRepository extends CrudRepository<GrantTag,Long> {

    @Query(value = "select * from grant_tags where grant_id=?1",nativeQuery = true)
    List<GrantTag> getTagsForGrant(Long grantId);
}
