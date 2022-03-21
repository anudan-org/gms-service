package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.UserRole;
import org.codealpha.gmsservice.repositories.RoleRepository;
import org.codealpha.gmsservice.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

  @Autowired
  private UserRoleRepository userRoleRepository;
  @Autowired
  private RoleRepository roleRepository;

  public List<Role> findRolesForUser(User user) {
    List<UserRole> userRoles = userRoleRepository.findByUser(user);
    return userRoles.stream().map(e -> roleRepository.findById(e.getRole().getId()).get())
        .collect(Collectors.toList());
  }

  public UserRole saveUserRole(UserRole userRole) {
    return userRoleRepository.save(userRole);
  }

  public List<UserRole> saveUserRoles(List<UserRole> userRoles) {
    userRoles.stream().forEach(ur -> userRoleRepository.save(ur));
    return userRoles;
  }

  public List<UserRole> findUsersForRole(Role role) {
    return userRoleRepository.findByRole(role);
  }

  public void deleteUserRole(UserRole roleToDelete) {

    userRoleRepository.delete(roleToDelete);
  }
}
