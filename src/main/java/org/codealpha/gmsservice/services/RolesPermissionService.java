package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.RolesPermission;
import org.codealpha.gmsservice.repositories.RolesPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesPermissionService {

    @Autowired
    private RolesPermissionRepository rolesPermissionRepository;

    public List<RolesPermission> saveRolePermissions(List<RolesPermission> rolePermissions){
        return (List<RolesPermission>) rolesPermissionRepository.saveAll(rolePermissions);

    }
}
