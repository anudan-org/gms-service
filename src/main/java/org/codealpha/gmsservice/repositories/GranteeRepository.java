package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grantee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Repository
public interface GranteeRepository extends CrudRepository<Grantee, Long> {

}
