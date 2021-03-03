package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantTypeWorkflowMapping;
import org.codealpha.gmsservice.repositories.GrantTypeWorkflowMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrantTypeWorkflowMappingService {

    @Autowired
    private GrantTypeWorkflowMappingRepository grantTypeWorkflowMappingRepository;

    public GrantTypeWorkflowMapping findByGrantTypeAndWorkflow(Long grantTypeId, Long workflowId){
        return  grantTypeWorkflowMappingRepository.findByGrantTypeAndWorkflow(grantTypeId, workflowId);
    }

    public GrantTypeWorkflowMapping save(GrantTypeWorkflowMapping gtm) {
        return grantTypeWorkflowMappingRepository.save(gtm);
    }

    public List<GrantTypeWorkflowMapping> findByWorkflow(Long workflowId){
        return grantTypeWorkflowMappingRepository.findByWorkflow(workflowId);
    }
}
