package org.codealpha.gmsservice.validators;

import org.apache.commons.io.FilenameUtils;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.exceptions.InvalidFileTypeException;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.models.SecureEntity;
import org.codealpha.gmsservice.repositories.*;
import org.codealpha.gmsservice.services.GrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public void validate(GrantService grantService,Long grantId,Grant grantToSave, Long userId, String tenantCode) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String[] authTokens = auth.getPrincipal().toString().split("\\^");
        Organization tenant = organizationRepository.findByCode(authTokens[1]);
        User user = userRepository.findByEmailIdAndOrganization(authTokens[0],tenant);
        Grant grant = grantRepository.findById(grantToSave.getId()).get();
        try {
            _validateUser(userId, user);
            _validateTenant(tenantCode, tenant);
            _validateGrant(grantService,grantId,grant,grantToSave);
            _validUserCanModifyGrant(user, grant);
        }catch (Exception e){
            throw new ResourceNotFoundException("Invalid authorization for this user");
        }
    }

    private void _validateGrant(GrantService grantService,Long grantId,Grant grant, Grant grantToSave) {

        SecureEntity secureEntity = grantService.unBuildGrantHashCode(grantToSave);
        if(grantId.longValue()!=secureEntity.getGrantId()){
            throw new ResourceNotFoundException("Invalid grant being request for modification");
        }
        String existingHas = grantService.buildHashCode(grantToSave);
        if(!existingHas.equalsIgnoreCase(grantToSave.getSecurityCode())){
            throw new ResourceNotFoundException("The grant data seems to have been tampered.");
        }

    }

    private void _validUserCanModifyGrant(User user, Grant grant) {
        GrantAssignments assignment = grantAssignmentRepository.findByGrantIdAndStateId(grant.getId(),grant.getGrantStatus().getId()).get(0);
        if(assignment.getAssignments()!=user.getId()){
            throw new ResourceNotFoundException("User not authorized to modify this grant");
        }
    }

    private void _validateUser(Long userId, User user) {
        if(userId!=user.getId()){
            throw new ResourceNotFoundException("The user is not valid");
        }
    }

    private void _validateTenant(String tenantCode, Organization tenant){
        if(!tenantCode.equalsIgnoreCase(tenant.getCode())){
            throw new ResourceNotFoundException("Invalid authorization");
        }
    }

    public void validateTemplateExists(GrantService grantService, Grant grantToSave, Long templateId) {
        SecureEntity secureEntity = grantService.unBuildGrantHashCode(grantToSave);
        if(!secureEntity.getGrantTemplateIds().stream().filter(l -> l.longValue()==templateId.longValue()).findFirst().isPresent()){
            throw new ResourceNotFoundException("The template is in use is not valid");
        }
    }

    public void validateSectionExists(GrantService grantService, Grant grantToSave, Long sectionId) {
        SecureEntity secureEntity = grantService.unBuildGrantHashCode(grantToSave);
        if(!secureEntity.getSectionAndAtrribIds().containsKey(sectionId)){
            throw new ResourceNotFoundException("Grant sections seem to have been tampered");
        }
    }

    public void validateFieldExists(GrantService grantService, Grant grant, Long sectionId, Long fieldId) {
        SecureEntity secureEntity = grantService.unBuildGrantHashCode(grant);
        if(!secureEntity.getSectionAndAtrribIds().get(sectionId).stream().filter(i -> i.longValue()==fieldId.longValue()).findFirst().isPresent()){
            throw new ResourceNotFoundException("Field does not exist in this grant");
        }
    }

    public void validateFlow(GrantService grantService, Grant grantToSave,Long grantId,Long userId, Long fromStateId, Long toStateId) {
        SecureEntity secureEntity = grantService.unBuildGrantHashCode(grantToSave);

        if(grantId.longValue()!=secureEntity.getGrantId().longValue()){
            throw new ResourceNotFoundException("Invalid grant flow requested");
        }

        if(!secureEntity.getGrantWorkflowIds().stream().filter(wf -> wf.longValue()==workflowStatusRepository.getById(grantToSave.getGrantStatus().getId()).getWorkflow().getId().longValue()).findFirst().isPresent()){
            throw new ResourceNotFoundException("Invalid grant flow requested");
        }

        if(!grantAssignmentRepository.findByGrantIdAndStateId(grantToSave.getId(),fromStateId).stream().filter(ass -> ass.getAssignments().longValue()==userId).findFirst().isPresent()){
            throw new ResourceNotFoundException("Grant is not in the right state");
        }

        Long[][] attribs = secureEntity.getWorkflowStatusTransitionIds().get(workflowStatusRepository.getById(grantToSave.getGrantStatus().getId()).getWorkflow().getId());
        boolean found = false;
        for(int i=0;i<attribs.length;i++){
            if(attribs[i][0].longValue()==fromStateId.longValue() && attribs[i][1].longValue()==toStateId.longValue()){
                found=true;
                break;
            }
        }
        if(!found) {
            throw new ResourceNotFoundException("Invalid workflow requested for this grant");
        }

    }

    public void validateFiles(MultipartFile[] files, String[] supportedFileTypes) {
        for (int i = 0; i < files.length; i++) {
            int finalI = i;
            if(!Arrays.stream(supportedFileTypes).filter(type -> type.equalsIgnoreCase(FilenameUtils.getExtension(files[finalI].getOriginalFilename()))).findAny().isPresent()){
                throw new InvalidFileTypeException("The requested file type extension is not supported");
            }
        }
    }
}
