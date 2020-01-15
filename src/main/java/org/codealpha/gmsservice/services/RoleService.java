package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public Role saveRole(Role role){
    return roleRepository.save(role);
  }

  public Role findByOrganizationAndName(Organization org, String name){
    List<Role> roles = roleRepository.findByOrganizationAndName(org, name);

    return roles.get(0);
  }

  public List<Role> getByOrganization(Organization organization){
    return roleRepository.findByOrganization(organization);
  }

  public Role getById(Long roleId){
    return roleRepository.findById(roleId).get();
  }

  public void deleteRole(Role role){
    roleRepository.delete(role);
  }
}
