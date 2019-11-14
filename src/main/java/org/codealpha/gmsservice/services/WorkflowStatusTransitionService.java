package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Workflow;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowStatusTransition;
import org.codealpha.gmsservice.repositories.WorkflowStatusTransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowStatusTransitionService {
    @Autowired
    private WorkflowStatusTransitionRepository workflowStatusTransitionRepository;

    public WorkflowStatusTransition saveStatusTransition(WorkflowStatusTransition transition){
        return workflowStatusTransitionRepository.save(transition);
    }

    public WorkflowStatusTransition findByFromAndToStates(WorkflowStatus from, WorkflowStatus to){
        return workflowStatusTransitionRepository.findByFromStateAndToState(from, to);
    }

    public List<WorkflowStatusTransition> getStatusTransitionsForWorkflow(Workflow workflow){
        return workflowStatusTransitionRepository.findByWorkflow(workflow);
    }
}
