package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<Role,Long> {

    public List<Role> findByOrganizationAndName(Organization org, String name);

    List<Role> findByOrganization(Organization organization);

    List<Role> findByOrganizationAndInternal(Organization org, boolean internalStatus);
}
