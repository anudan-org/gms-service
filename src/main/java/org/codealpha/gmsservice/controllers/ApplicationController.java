package org.codealpha.gmsservice.controllers;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Template;
import org.codealpha.gmsservice.models.UIConfig;
import org.codealpha.gmsservice.services.GrantDocumentAttributesService;
import org.codealpha.gmsservice.services.GrantSectionService;
import org.codealpha.gmsservice.services.GrantService;
import org.codealpha.gmsservice.services.GranterConfigurationService;
import org.codealpha.gmsservice.services.OrganizationResolver;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.TemplateService;
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
@RequestMapping(value = "/public")
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

  @Autowired
  private TemplateService templateService;

  @Autowired
  private GrantService grantService;

  @Autowired
  private GrantDocumentAttributesService grantDocumentAttributesService;

  @Value("${spring.upload-file-location}")
  private String uploadLocation;

  @Autowired
  private GrantSectionService grantSectionService;

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

        config.setLogoUrl(url.concat("/public/images/")
            .concat(config.getLogoUrl()));
        config.setTenantCode(org.getCode());
        config.setNavbarTextColor(config.getNavbarTextColor());
      } else {
        config = new UIConfig();
        config.setLogoUrl(url.concat("/public/images/anudan.png"));
        config.setNavbarColor("#e3f2fd");
      }
    } else {
      Organization org = organizationService.getPlatformOrg();
      config = new UIConfig();
      config.setLogoUrl(url.concat("/public/images/anudan.png"));
      config.setNavbarColor("#e3f2fd");
      config.setTenantCode(org.getCode());
    }
    config.setDefaultSections(grantSectionService.getAllDefaultSections());
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
      logger.error(ex.getMessage(), ex);
    }
  }

  @GetMapping("/templates/kpi/{templateId}")
  public void getTemplate(@PathVariable("templateId") Long templateId,
      HttpServletResponse servletResponse) {

    Template template = templateService.findByTemplateId(templateId);
    Resource file = resourceLoader
        .getResource("file:" + uploadLocation + template.getLocation() + template.getName());
    switch (template.getFileType()) {
      case "png":
        servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        break;
      case "jpg":
        servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
        break;
      case "jpeg":
        servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
        break;
      case "pdf":
        servletResponse.setContentType(MediaType.APPLICATION_PDF_VALUE);
        break;
      case "doc":
        servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        break;
      case "xls":
        servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }
    servletResponse
        .setHeader("Content-Disposition", "attachment; filename=" + template.getName());

    try {
      StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());

    } catch (IOException ex) {
      logger.error(ex.getMessage(), ex);
    }
  }

  @GetMapping("/grants/{grantId}/file/{fileName}")
  public void getGrantFile(@PathVariable("grantId") Long grantId,
      @PathVariable("fileName") String fileName,
      HttpServletResponse servletResponse) {

    GrantDocumentAttributes documentAttributes = grantDocumentAttributesService
        .findByGrantAndName(grantService.getById(grantId), fileName);

    Resource file = resourceLoader
        .getResource("file:" + uploadLocation + documentAttributes.getLocation() + documentAttributes.getName());
    String fileType = documentAttributes.getName().substring(documentAttributes.getName().lastIndexOf(".")+1);
    switch (fileType) {
      case "png":
        servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        break;
      case "jpg":
        servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
        break;
      case "jpeg":
        servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
        break;
      case "pdf":
        servletResponse.setContentType(MediaType.APPLICATION_PDF_VALUE);
        break;
      case "doc":
        servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        break;
      case "xls":
        servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }
    servletResponse
        .setHeader("Content-Disposition", "attachment; filename=" + documentAttributes.getName());

    try {
      StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());

    } catch (IOException ex) {
      logger.error(ex.getMessage(), ex);
    }
  }
}
