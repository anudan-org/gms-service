package org.codealpha.gmsservice.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.PasswordResetRequest;
import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.UserNotFoundException;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static  final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private CommonEmailService commonEmailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetRequestService passwordResetRequestService;

    public User getUserByEmailAndOrg(String email, Organization org) {
        return userRepository.findByEmailAndOrg(email, org.getId());
    }

    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent()?user.get():null;
    }

    public User getUserByEmailAndTenant(String email, String tenant) {
        User user = userRepository.findByEmailIdAndOrganization(email, organizationRepository.findByCode(tenant));
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> usersToNotifyOnWorkflowSateChangeTo(Long toStateId) {
        return userRepository.usersToNotifyOnWorkflowSateChangeTo(toStateId);
    }

    public List<User> getUsersByEmail(String email) {
        return userRepository.findByEmailId(email);
    }

    public List<User> getAllTenantUsers(Organization org) {
        return userRepository.findByOrganizationAndActive(org, true);
    }

    public List<User> findByOrganization(Organization org) {
        return userRepository.findByOrganization(org);
    }

    public List<User> getGranteeUsers(Organization org) {
        return userRepository.findByOrganizationAndActive(org, true);
    }

    public List<User> getAllGranteeUsers(Organization org) {
        return userRepository.findByOrganization(org);
    }

    public String[] buildJoiningInvitationContent(Organization org, Role role, User inviter, String sub, String msg,
            String url) {
        sub = sub.replace("%ORG_NAME%", org.getName());

        msg = msg.replace("%ROLE_NAME%", role.getName()).replace("%ORG_NAME%", org.getName())
                .replace("%INVITE_FROM%", inviter.getFirstName().concat(" ").concat(inviter.getLastName()))
                .replace("%LINK%", url);
        return new String[] { sub, msg };
    }

    public PasswordResetRequest sendPasswordResetMail(User user, String mailSubject, String mailMessage,
            String mailFooter) {
        PasswordResetRequest resetRequest = null;
        UriComponents uriComponents = null;
        String host = "";
        String url = "";
        try {
            uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
            host = uriComponents.getHost();
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme())
                    .host(host).port(uriComponents.getPort());
            url = uriBuilder.toUriString();
            String key = RandomStringUtils.random(50, 0, 0, true, true, null, new SecureRandom());
            String code = passwordEncoder
                    .encode(key.concat(user.getEmailId().concat(String.valueOf(user.getOrganization().getId()))));
            resetRequest = new PasswordResetRequest();
            resetRequest.setKey(key);
            resetRequest.setRequestedOn(DateTime.now().toDate());
            resetRequest.setUserId(user.getId());
            resetRequest.setCode(code);
            resetRequest.setValidated(false);
            resetRequest.setOrgId(user.getOrganization().getId());
            resetRequest = passwordResetRequestService.savePasswordResetRequest(resetRequest);

            url = url + "/home?action=reset-password&email=" + user.getEmailId() + "&key=" + key + "&org="
                    + user.getOrganization().getName().replace(" ", "%20");
            mailMessage = mailMessage.replace("%RESET_LINK%", url);
            mailMessage = mailMessage.replace("%USER_NAME%", user.getFirstName());
            mailMessage = mailMessage.replace("%ORGANIZATION%", user.getOrganization().getName());
            commonEmailService.sendMail(new String[] { !user.isDeleted() ? user.getEmailId() : null }, null, mailSubject,
                    mailMessage, new String[] { mailFooter });

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return resetRequest;
    }

    public List<User> getAdminUsersForTenant(long grantorOrg) {
        return userRepository.findAdminUsersForTenant(grantorOrg);
    }
}
