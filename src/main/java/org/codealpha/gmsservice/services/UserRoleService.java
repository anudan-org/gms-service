package org.codealpha.gmsservice.services;

import java.util.List;
import java.util.stream.Collectors;
import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.UserRole;
import org.codealpha.gmsservice.repositories.RoleRepository;
import org.codealpha.gmsservice.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

  @Autowired
  private UserRoleRepository userRoleRepository;
  @Autowired
  private RoleRepository roleRepository;

  public List<Role> findRolesForUser(User user) {
    List<UserRole> userRoles = userRoleRepository.findByUser(user);
    List<Role> roles = userRoles.stream()
        .map(e -> roleRepository.findById(e.getRole().getId()).get())
        .collect(Collectors.toList());
    return roles;
  }

  public UserRole saveUserRole(UserRole userRole){
    return userRoleRepository.save(userRole);
  }
}
