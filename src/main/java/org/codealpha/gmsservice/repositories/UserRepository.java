package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Repository
public interface UserRepository extends CrudRepository<User, Long> {


}
