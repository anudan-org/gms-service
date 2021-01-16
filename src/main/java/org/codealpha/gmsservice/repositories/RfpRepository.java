package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Rfp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer code-alpha.org
 **/
@Repository
public interface RfpRepository extends CrudRepository<Rfp,Long> {

}
