package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.WorkflowTransitionModel;
import org.codealpha.gmsservice.repositories.WorkflowTransitionModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowTransitionModelService {
    @Autowired
    private WorkflowTransitionModelRepository workflowTransitionModelRepository;

    public List<WorkflowTransitionModel> getWorkflowsByGranterAndType(Long granterId, String type) {
        return workflowTransitionModelRepository.getWorkflowsByGranterAndObject(granterId, type);
    }
}
