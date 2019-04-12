package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.repositories.WorkflowStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStatusService {

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  public WorkflowStatus findById(Long id){
    return workflowStatusRepository.findById(id).get();
  }
}
