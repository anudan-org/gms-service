package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public Role saveRole(Role role){
    return roleRepository.save(role);
  }
}
