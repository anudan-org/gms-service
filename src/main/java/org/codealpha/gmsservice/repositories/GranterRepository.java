package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Granter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer code-alpha.org
 **/
@Repository
public interface GranterRepository extends CrudRepository<Granter, Long> {

	Granter findByHostUrl(String url);

}
