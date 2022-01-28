package org.codealpha.gmsservice.services;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.controllers.AdiminstrativeController;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.UserRole;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Developer code-alpha.org
 **/
@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository repository;

	public Organization get(Long organizationId) {
		Optional<Organization> optionalOrganization = repository.findById(organizationId);

		if (optionalOrganization.isPresent()) {
			return optionalOrganization.get();
		}
		// TODO - Replace with specific exception
		throw new ResourceNotFoundException("Organization with id [" + organizationId + "] not found.");
	}

	public Organization findOrganizationByTenantCode(String code) {
		return repository.findByCode(code);
	}

	public Organization getPlatformOrg() {
		return repository.findByOrganizationTypeEquals("PLATFORM");
	}

	public Organization save(Organization organization) {
		return repository.save(organization);
	}

	public List<Organization> getGranteeOrgs() {
		return repository.getGranteeOrgs();
	}

	public List<Organization> getGranterOrgs() {
		return repository.getGranterOrgs();
	}

	public Organization findByNameAndOrganizationType(String name, String type) {
		return repository.findByNameAndOrganizationType(name, type);
	}

	public List<Organization> getAssociatedGranteesForTenant(Organization tenantOrg) {
		return repository.getAssociatedGranteesForTenant(tenantOrg.getId());
	}

	public void buildInviteUrlAndSendMail(UserService userService, AppConfigService appConfigService,
			CommonEmailSevice commonEmailSevice, ReleaseService releaseService, User adminUser, Organization org,
			User user, List<UserRole> userRoles) {
		UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
		String host = null;
		if (org.getOrganizationType().equalsIgnoreCase("GRANTEE")) {
			host = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
		} else if (org.getOrganizationType().equalsIgnoreCase("GRANTER")) {
			host = uriComponents.getHost();
			if (adminUser.getOrganization().getOrganizationType().equals("PLATFORM")) {
				host = ((Granter) org).getHostUrl() + "." + host;
			}
		}
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme())
				.host(host).port(uriComponents.getPort());
		String url = uriBuilder.toUriString();
		url = url + "/home/?action=registration&org="
				+ StringEscapeUtils.escapeHtml4(user.getOrganization().getName()).replaceAll(" ", "%20") + "&email="
				+ user.getEmailId() + "&type=join";
		String[] notifications = userService.buildJoiningInvitationContent(user.getOrganization(),
				userRoles.get(0).getRole(), adminUser,
				appConfigService
						.getAppConfigForGranterOrg(user.getOrganization().getId(), AppConfiguration.INVITE_SUBJECT)
						.getConfigValue(),
				appConfigService
						.getAppConfigForGranterOrg(user.getOrganization().getId(), AppConfiguration.INVITE_MESSAGE)
						.getConfigValue(),
				url);
		commonEmailSevice.sendMail(new String[] { !user.isDeleted() ? user.getEmailId() : null }, null,
				notifications[0], notifications[1],
				new String[] { appConfigService
						.getAppConfigForGranterOrg(user.getOrganization().getId(),
								AppConfiguration.PLATFORM_EMAIL_FOOTER)
						.getConfigValue()
						.replaceAll("%RELEASE_VERSION%", releaseService.getCurrentRelease().getVersion()).replace("%TENANT%",user.getOrganization()
						.getName()) });
	}

	public Organization findByName(String grantee) {
		return repository.findByName(grantee);
	}
}
