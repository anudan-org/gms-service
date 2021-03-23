package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.OrgTag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrgTagRepository extends CrudRepository<OrgTag,Long> {

    @Query(value = "select * from org_tags where tenant=?1 order by id asc",nativeQuery = true)
    List<OrgTag> getOrgTags(Long orgId);
}
