package org.codealpha.gmsservice.controllers;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.models.UIConfig;
import org.codealpha.gmsservice.services.GranterConfigurationService;
import org.codealpha.gmsservice.services.OrganizationResolver;
import org.codealpha.gmsservice.services.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
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
  private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

  @Autowired
  private OrganizationResolver organizationResolver;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private GranterConfigurationService service;

  @Autowired
  private ResourceLoader resourceLoader;

  @Value("${spring.upload-file-location}")
  private String uploadLocation;

  @GetMapping(value = {"/config/{host}", "/config"})
  public UIConfig config(@PathVariable(name = "host", required = false) String host,
      HttpServletRequest request) {

    UIConfig config;

    String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    if (null != host) {
      Organization org = organizationResolver.getOrganizationByHostedDomain(host);
      if (null != org) {
        long orgId = org.getId();
        config = service.getUiConfiguration(orgId);

        config.setLogoUrl(url.concat("/app/images/")
            .concat(config.getLogoUrl()));
        config.setTenantCode(org.getCode());
        config.setNavbarTextColor(config.getNavbarTextColor());
      } else {
        config = new UIConfig();
        config.setLogoUrl(url.concat("/app/images/anudan.png"));
        config.setNavbarColor("#e3f2fd");
      }
    } else {
      Organization org = organizationService.getPlatformOrg();
      config = new UIConfig();
      config.setLogoUrl(url.concat("/app/images/anudan.png"));
      config.setNavbarColor("#e3f2fd");
      config.setTenantCode(org.getCode());
    }
    return config;
  }

  @GetMapping("/images/{img}")
  public void getLogoImage(@PathVariable("img") String imageName,
      HttpServletResponse servletResponse) {

    Resource image = resourceLoader.getResource("classpath:static/images/" + imageName);
    servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
    try {
      StreamUtils.copy(image.getInputStream(), servletResponse.getOutputStream());

    } catch (IOException ex) {
      logger.error(ex.getMessage(),ex);
    }
  }

}
