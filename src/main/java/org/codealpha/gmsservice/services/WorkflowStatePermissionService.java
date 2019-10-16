package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.WorkflowStatePermission;
import org.codealpha.gmsservice.repositories.WorkflowStatePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStatePermissionService {
    @Autowired
    private WorkflowStatePermissionRepository workflowStatePermissionRepository;

    public WorkflowStatePermission saveWorkflowStatePermission(WorkflowStatePermission permission){
        return workflowStatePermissionRepository.save(permission);
    }
}
