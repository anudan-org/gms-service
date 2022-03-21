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
public class NotificationValidator {

    public static final String INVALID_AUTHORIZATION = "Invalid authorization";
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrganizationRepository organizationRepository;

    public void validate(Long userId, String tenantCode) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String[] authTokens = auth.getPrincipal().toString().split("\\^");

        Organization tenant = organizationRepository.findByCode(authTokens[1]);
        User user = null;
        if (!"ANUDAN".equalsIgnoreCase(tenantCode)){
            user = userRepository.findByEmailIdAndOrganization(authTokens[0], tenant);
        }else if ("ANUDAN".equalsIgnoreCase(tenantCode)){
            user = userRepository.findById(userId).orElse(null);
        }

        try {
            validateUser(userId, user);
            validateTenant(tenantCode, tenant);
        } catch (Exception e) {
            throw new ResourceNotFoundException(INVALID_AUTHORIZATION);
        }
    }

    private void validateUser(Long userId, User user) {
        if (user==null || userId.longValue() != user.getId().longValue()) {
            throw new ResourceNotFoundException(INVALID_AUTHORIZATION);
        }
    }

    private void validateTenant(String tenantCode, Organization tenant) {
        if (!tenantCode.equalsIgnoreCase(tenant.getCode())) {
            throw new ResourceNotFoundException(INVALID_AUTHORIZATION);
        }
    }
}
