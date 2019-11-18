package org.codealpha.gmsservice.validators;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DashboardValidator {
    @Autowired UserRepository userRepository;
    @Autowired OrganizationRepository organizationRepository;

    public void validate(Long userId,String tenantCode){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String[] authTokens = auth.getPrincipal().toString().split("\\^");
        Organization tenant = organizationRepository.findByCode(authTokens[1]);
        User user = userRepository.findByEmailIdAndOrganization(authTokens[0],tenant);
        try{
        _validateUser(userId, user);
        _validateTenant(tenantCode,tenant);
        }catch (Exception e){
            throw new ResourceNotFoundException("Invalid authorization");
        }
    }

    private void _validateUser(Long userId, User user) {
        if(userId!=user.getId()){
            throw new ResourceNotFoundException("Invalid authorization");
        }
    }

    private void _validateTenant(String tenantCode, Organization tenant){
        if(!tenantCode.equalsIgnoreCase(tenant.getCode())){
            throw new ResourceNotFoundException("Invalid authorization");
        }
    }
}
