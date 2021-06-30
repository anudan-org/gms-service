package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantTypeRepository extends CrudRepository<GrantType,Long> {

    @Query(value = "select * from grant_types where granter_id=?1",nativeQuery = true)
    List<GrantType> findGrantTypesForTenant(Long granterId);
}
