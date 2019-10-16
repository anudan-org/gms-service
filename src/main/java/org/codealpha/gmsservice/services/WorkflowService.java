package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Workflow;
import org.codealpha.gmsservice.repositories.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    @Autowired
    private WorkflowRepository workflowRepository;

    public Workflow saveWorkflow(Workflow flow){
        return workflowRepository.save(flow);
    }
}
