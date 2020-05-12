package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.PasswordResetRequest;
import org.codealpha.gmsservice.repositories.PasswordResetRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetRequestService {
    @Autowired
    private PasswordResetRequestRepository passwordResetRequestRepository;

    public PasswordResetRequest savePasswordResetRequest(PasswordResetRequest passwordResetRequest){
        return passwordResetRequestRepository.save(passwordResetRequest);
    }

    public PasswordResetRequest findByUnvalidatedUserIdAndKeyAndOrgId(Long userId,String key,Long orgId){
        return passwordResetRequestRepository.findByUnvalidatedUserIdAndKeyAndOrgId(userId, key,orgId);
    }
}
