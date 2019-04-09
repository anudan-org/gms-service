package org.codealpha.gmsservice.services;

import java.util.List;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.codealpha.gmsservice.repositories.WorkflowActionPermissionRepository;
import org.codealpha.gmsservice.repositories.WorkflowPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowPermissionService {

  @Autowired
  private WorkflowPermissionRepository workflowPermissionRepository;
  @Autowired
  private WorkflowActionPermissionRepository workflowActionPermissionRepository;

  public List<WorkFlowPermission> getGrantFlowPermissions(Long granterOrgId, Long userRoleId) {

    return workflowPermissionRepository.getPermissionsForGrantFlow(granterOrgId, userRoleId);
  }

  public WorkflowActionPermission getGrantActionPermissions(Long granterOrgId, Long userRoleId) {

    return workflowActionPermissionRepository.getActionPermissionsForGrant(granterOrgId, userRoleId);
  }


}
