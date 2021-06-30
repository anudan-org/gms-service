package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.WorkflowValidation;
import org.codealpha.gmsservice.repositories.WorkflowValidationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowValidationService {
    @Autowired
    private WorkflowValidationRepository workflowValidationRepository;

    public List<WorkflowValidation> getActiveValidationsByObject(String _for){
        return workflowValidationRepository.getActiveValidationsByObject(_for);
    }
}
