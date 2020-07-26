package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.repositories.RoleRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public Role saveRole(Role role) {
    return roleRepository.save(role);
  }

  public Role findByNameAndOrganization(Organization org, String name) {
    List<Role> roles = roleRepository.findByOrganizationAndName(org, name);
    if (roles.size() > 0) {
      return roles.get(0);
    } else {
      return null;
    }

  }

  public Role findByOrganizationAndName(Organization org, String name) {
    List<Role> roles = roleRepository.findByOrganizationAndName(org, name);

    if (roles != null && roles.size() == 0) {
      Role newRole = new Role();
      newRole.setCreatedAt(DateTime.now().toDate());
      newRole.setCreatedBy("System");
      newRole.setName(name);
      newRole.setOrganization(org);
      newRole = roleRepository.save(newRole);
      roles.add(newRole);
    }
    return roles.get(0);
  }

  public List<Role> getByOrganization(Organization organization) {
    return roleRepository.findByOrganization(organization);
  }

  public Role getById(Long roleId) {
    return roleRepository.findById(roleId).get();
  }

  public void deleteRole(Role role) {
    roleRepository.delete(role);
  }

  public List<Role> getPublicRolesForOrganization(Organization org) {
    return roleRepository.findByOrganizationAndInternal(org, false);
  }

  public List<Role> getInternalRolesForOrganization(Organization org) {
    return roleRepository.findByOrganizationAndInternal(org, true);
  }
}
