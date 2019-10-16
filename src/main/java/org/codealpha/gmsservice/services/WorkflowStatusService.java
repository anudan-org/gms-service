package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Workflow;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.repositories.WorkflowStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowStatusService {

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  public WorkflowStatus findById(Long id){
    return workflowStatusRepository.findById(id).get();
  }

  public WorkflowStatus findInitialStatusByObjectAndGranterOrgId(String object, Long orgid){
    return workflowStatusRepository.getInitialStatusByObjectAndGranterOrg(object,orgid);
  }

  public List<WorkflowStatus> getTenantWorkflowStatuses(String object, Long granterOrgId){
    return workflowStatusRepository.getAllTenantStatuses(object,granterOrgId);
  }
  public WorkflowStatus getById(Long id){
    return workflowStatusRepository.getById(id);
  }

  public WorkflowStatus saveWorkflowStatus(WorkflowStatus status){
    return workflowStatusRepository.save(status);
  }
}
