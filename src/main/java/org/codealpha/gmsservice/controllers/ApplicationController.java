package org.codealpha.gmsservice.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.*;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Template;
import org.codealpha.gmsservice.entities.TemplateLibrary;
import org.codealpha.gmsservice.models.UIConfig;
import org.codealpha.gmsservice.services.*;
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
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping(value = "/public")
@Api(value="Application Configuration",tags = {"Application Configuration"})
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

    @Autowired
    private WorkflowStatusService workflowStatusService;

    @Autowired
    private UserService userService;

    @Value("${spring.upload-file-location}")
    private String uploadLocation;

    @Autowired
    private GrantSectionService grantSectionService;

    @Autowired
    private TemplateLibraryService templateLibraryService;

    @Autowired
    private WorkflowTransitionModelService workflowTransitionModelService;

    @Value("${spring.profiles.active}")
    private String environment;

    @Autowired private AppConfigService appConfigService;

    @GetMapping(value = {"/config/{host}", "/config"})
    @ApiOperation(value = "Application Configuration for tenant and Anudan platform.",notes = "Publicly available application configuration for tenant.\nIf host is passed then tenant specific configuration is retrieved. If tenant is not passed then Anudan platform level configuration is retrieved.",response = UIConfig.class)
    public UIConfig config(@ApiParam(name="host",value = "Sub-domain of tenant in url. <Blank> for Anudan platform") @PathVariable(name = "host", required = false) String host,
                           HttpServletRequest request) {

        UIConfig config;

        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if(!environment.equalsIgnoreCase("local")){
            url = url.replace("http","https");
        }else{
            url = url.replace("http","http");
        }
        if (null != host) {
            Organization org = organizationResolver.getOrganizationByHostedDomain(host);
            if (null != org) {
                long orgId = org.getId();
                config = service.getUiConfiguration(orgId);

                config.setLogoUrl(url.concat("/public/images/").concat(org.getCode()).concat("/")
                        .concat(config.getLogoUrl()));
                config.setTenantCode(org.getCode());
                config.setNavbarTextColor(config.getNavbarTextColor());
                config.setGrantInitialStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("GRANT", org.getId()));
                config.setSubmissionInitialStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("SUBMISSION", org.getId()));
                config.setWorkflowStatuses(workflowStatusService.getTenantWorkflowStatuses("GRANT",org.getId()));
                config.setTenantUsers(userService.getAllTenantUsers(org));
                config.setTransitions(workflowTransitionModelService.getWorkflowsByGranterAndType(org.getId(),"GRANT"));
                config.setGranteeOrgs(organizationService.getAssociatedGranteesForTenant(org));
                config.setDaysBeforePublishingReport(Integer.valueOf(appConfigService.getAppConfigForGranterOrg(orgId, AppConfiguration.REPORT_SETUP_INTERVAL).getConfigValue()));
            } else {
                org = organizationService.getPlatformOrg();
                config = new UIConfig();
                config.setLogoUrl(url.concat("/public/images/ANUDAN/anudan.png"));
                config.setNavbarColor("#e3f2fd");
                config.setTenantCode(org.getCode());
            }

        } else {
            Organization org = organizationService.getPlatformOrg();
            config = new UIConfig();
            config.setLogoUrl(url.concat("/public/images/ANUDAN/anudan.png"));
            config.setNavbarColor("#e3f2fd");
            config.setTenantCode(org.getCode());
        }
        //config.setDefaultSections(grantSectionService.getAllDefaultSections());
        return config;
    }

    @GetMapping("/images/{tenant}/{img}")
    @ApiOperation(value = "Get tenant logo image for <img> tag 'src' property")
    public void getLogoImage(@ApiParam(name="imageName",value="Name of the image name as returned from Application configuration") @PathVariable("img") String imageName,
                             HttpServletResponse servletResponse,@ApiParam(name="tenant",value="Tenant code")@PathVariable("tenant") String tenant) {

        Resource image = resourceLoader.getResource("file:" + uploadLocation + "/" + tenant + "/logo/"+imageName);
        servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        try {
            StreamUtils.copy(image.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @GetMapping("/grants/{grantId}/kpi-templates/{fileName}")
    @ApiIgnore
    public void getTemplate(@PathVariable("grantId") Long grantId,@PathVariable("fileName") String fileName,
                            HttpServletResponse servletResponse) {

        Resource file = resourceLoader
                .getResource("file:" + uploadLocation + grantService.getById(grantId).getGrantorOrganization().getCode()+ "/grants/"+ grantId+"/kpi-templates/" + fileName);
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
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
                .setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try {
            StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    @GetMapping("/grants/{grantId}/kpi-documents/{fileName}")
    @ApiIgnore
    public void getKpiDataDoc(@PathVariable("grantId") Long grantId, @PathVariable("fileName") String fileName, HttpServletResponse servletResponse){

      Resource file = resourceLoader
              .getResource("file:" + uploadLocation +grantService.getById(grantId).getGrantorOrganization().getCode()+"/grants/"+grantId+"/kpi-documents/"+fileName);
      String fileType = fileName.substring(fileName.lastIndexOf(".")+1);

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
              .setHeader("Content-Disposition", "attachment; filename=" + fileName);

      try {
        StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());

      } catch (IOException ex) {
        logger.error(ex.getMessage(), ex);
      }
    }

    @GetMapping("/grants/{grantId}/file/{fileId}")
    @ApiOperation("Download template library document")
    public void getGrantFile(@ApiParam(name="grantId",value="Unique identifier of the selected grant")@PathVariable("grantId") Long grantId,
                             @ApiParam(name="fileId",value="Unique identifier of file to be downloaded")@PathVariable("fileId") Long fileId,
                             HttpServletResponse servletResponse) {

        TemplateLibrary templateLibrary = templateLibraryService.getTemplateLibraryDocumentById(fileId);

        Resource file = null;
        try {
            file = resourceLoader
                    .getResource("file:" + uploadLocation + URLDecoder.decode(templateLibrary.getLocation(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String fileType = templateLibrary.getType();
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
            case "docx":
                servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                break;
            case "xls":
                servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                break;
            case "xlsx":
                servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        }
        servletResponse
                .setHeader("Content-Disposition", "attachment; filename=" + templateLibrary.getName()+"."+fileType);

        try {
            StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
