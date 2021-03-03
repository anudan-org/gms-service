package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Workflow;
import org.codealpha.gmsservice.repositories.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowService {

    @Autowired
    private WorkflowRepository workflowRepository;

    public Workflow saveWorkflow(Workflow flow){
        return workflowRepository.save(flow);
    }

    public Workflow findDefaultByGranterAndObjectAndType(Organization granter, String object, Long grantTypeId){
        return workflowRepository.findByGranterAndObjectAndType(granter.getId(),object,grantTypeId).get(0);
    }

    public Workflow findWorkflowByGrantTypeAndObject(Long grantTypeId,String object){
        return workflowRepository.findWorkflowByGrantTypeAndObject(grantTypeId,object);
    }

    public List<Workflow> getAllWorkflowsForGranterByType(Long granterId, String object){
        return workflowRepository.getAllWorkflowsForGranterByType(granterId,object);
    }

    public Workflow findDefaultByGranterAndObject(Long granterId, String object){
        return workflowRepository.findDefaultByGranterAndObject(granterId, object);
    }
}
