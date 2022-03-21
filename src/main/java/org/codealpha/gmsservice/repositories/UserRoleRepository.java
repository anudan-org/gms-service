package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRoleRepository extends CrudRepository<UserRole,Long> {

  public List<UserRole> findByUser(User user);
  public List<UserRole> findByRole(Role role);
}
