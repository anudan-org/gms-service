package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.WorkflowStatusTransition;
import org.codealpha.gmsservice.repositories.WorkflowStatusTransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStatusTransitionService {
    @Autowired
    private WorkflowStatusTransitionRepository workflowStatusTransitionRepository;

    public WorkflowStatusTransition saveStatusTransition(WorkflowStatusTransition transition){
        return workflowStatusTransitionRepository.save(transition);
    }
}
