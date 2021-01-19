package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.Organization;
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

    public Workflow findDefaultByGranterAndObject(Organization granter, WorkflowObject object){
        return workflowRepository.findByGranterAndObject(granter,object).get(0);
    }
}
