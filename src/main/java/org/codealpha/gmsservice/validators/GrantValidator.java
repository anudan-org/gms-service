package org.codealpha.gmsservice.validators;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.repositories.*;
import org.codealpha.gmsservice.services.GrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GrantValidator {
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    GrantRepository grantRepository;
    @Autowired
    GrantAssignmentRepository grantAssignmentRepository;
    @Autowired
    GrantSpecificSectionRepository grantSpecificSectionRepository;
    @Autowired
    GrantSpecificSectionAttributeRepository grantSpecificSectionAttributeRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;

    public void validate(GrantService grantService, Long grantId, Grant grantToSave, Long userId, String tenantCode) {
        //Do nothing
    }

    public void validateTemplateExists(GrantService grantService, Grant grantToSave, Long templateId) {
        //Do nothing
    }

    public void validateSectionExists(GrantService grantService, Grant grantToSave, Long sectionId) {
        //Do nothing
    }

    public void validateFieldExists(GrantService grantService, Grant grant, Long sectionId, Long fieldId) {
        //Do nothing
    }

    public void validateFlow(GrantService grantService, Grant grantToSave,Long grantId,Long userId, Long fromStateId, Long toStateId) {
        //Do nothing
    }

    public void validateFiles(MultipartFile[] files, String[] supportedFileTypes) {
        //Do nothing
    }
}
