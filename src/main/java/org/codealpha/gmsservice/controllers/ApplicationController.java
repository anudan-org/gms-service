package org.codealpha.gmsservice.controllers;

import javax.servlet.http.HttpServletRequest;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.models.UIConfig;
import org.codealpha.gmsservice.services.GranterConfigurationService;
import org.codealpha.gmsservice.services.OrganizationResolver;
import org.codealpha.gmsservice.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping(value = "/app")
public class ApplicationController {

	@Autowired
	private OrganizationResolver organizationResolver;

	@Autowired
  private OrganizationService organizationService;

	@Autowired
	private GranterConfigurationService service;

	@GetMapping(value = {"/config/{host}","/config"})
	public UIConfig config(@PathVariable(name = "host", required = false) String host, HttpServletRequest request) {

		UIConfig config;


		String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		if (null != host) {
			Organization org = organizationResolver.getOrganizationByHostedDomain(host);
			if(null != org) {
				long orgId = org.getId();
				config = service.getUiConfiguration(orgId);

				config.setLogoUrl(url.concat("/images/")
						.concat(config.getLogoUrl()));
				config.setTenantCode(org.getCode());
			}else{
				config = new UIConfig();
				config.setLogoUrl(url.concat("/images/anudan.png"));
				config.setNavbarColor("#e3f2fd");
			}
		} else {
		  Organization org = organizationService.getPlatformOrg();
			config = new UIConfig();
			config.setLogoUrl(url.concat("/images/anudan.png"));
			config.setNavbarColor("#e3f2fd");
			config.setTenantCode(org.getCode());
		}
		return config;
	}

}
