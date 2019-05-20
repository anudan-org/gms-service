package org.codealpha.gmsservice.repositories;

import java.util.Optional;
import org.codealpha.gmsservice.entities.Granter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Repository
public interface GranterRepository extends CrudRepository<Granter, Long> {

	Granter findByHostUrl(String url);

}
