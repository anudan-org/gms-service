package org.codealpha.gmsservice.services;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.PasswordResetRequest;
import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.UserNotFoundException;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.UserRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private CommonEmailSevice commonEmailSevice;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetRequestService passwordResetRequestService;

    public User getUserByEmailAndOrg(String email, Organization org) {
        User user = userRepository.findByEmailIdAndOrganization(email, org);
        return user;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).get();
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
        List<User> users = userRepository.findByOrganization(org);
        users.removeIf(u -> u.isDeleted());
        return users;
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
            if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")) {
                // host = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".")
                // + 1);
                host = uriComponents.getHost();

            } else {
                host = uriComponents.getHost();
            }
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme())
                    .host(host).port(uriComponents.getPort());
            url = uriBuilder.toUriString();
            String key = RandomStringUtils.randomAlphabetic(40, 60);
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
                    + user.getOrganization().getName().replaceAll(" ", "%20");
            mailMessage = mailMessage.replaceAll("%RESET_LINK%", url);
            mailMessage = mailMessage.replaceAll("%USER_NAME%", user.getFirstName());
            mailMessage = mailMessage.replaceAll("%ORGANIZATION%", user.getOrganization().getName());
            commonEmailSevice.sendMail(new String[] { user.getEmailId() }, null, mailSubject, mailMessage,
                    new String[] { mailFooter });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resetRequest;
    }

}
