package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.Configuration;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author Developer code-alpha.org
 **/
@RestController
@RequestMapping(value = "/app")
@Api(value = "Application Configuration", tags = {"Application Configuration"})
public class ApplicationController {

    public static final String REPORT = "REPORT";
    public static final String GRANTCLOSURE = "GRANTCLOSURE";
    public static final String LOGO_URL = "/public/images/ANUDAN/anudan.png";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String FILE = "file:";
    public static final String ATTACHMENT_FILENAME = "attachment; filename=";
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
    @Autowired private ReportService reportService;
    @Autowired private DisbursementService disbursementService;
    @Autowired private GrantClosureService closureService;

    @GetMapping(value = {"/config/user/{userId}/{host}", "/config"})
    @ApiOperation(value = "Application Configuration for tenant and Anudan platform.",notes = "Publicly available application configuration for tenant.\nIf host is passed then tenant specific configuration is retrieved. If tenant is not passed then Anudan platform level configuration is retrieved.",response = UIConfig.class)
    public UIConfig config(@ApiParam(name="host",value = "Sub-domain of tenant in url. <Blank> for Anudan platform") @PathVariable(name = "host", required = false) String host,
                           HttpServletRequest request,@PathVariable("userId") Long userId) {

        UIConfig config;

        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if(!environment.equalsIgnoreCase("local")){
            url = url.replace("http","https");
        }else{
            url = url.replace("http","http");
        }
        if(host.equalsIgnoreCase("anudan")){
            host=null;
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
                config.setWorkflowStatuses(workflowStatusService.getTenantWorkflowStatuses("GRANT", org.getId()));
                config.setTenantUsers(userService.getAllTenantUsers(org));
                config.setReportWorkflowStatuses(workflowStatusService.getTenantWorkflowStatuses(REPORT, org.getId()));
                config.setClosureWorkflowStatuses(workflowStatusService.getTenantWorkflowStatuses(GRANTCLOSURE, org.getId()));
                config.setGranteeOrgs(organizationService.getAssociatedGranteesForTenant(org));
                config.setDaysBeforePublishingReport(Integer.valueOf(appConfigService.getAppConfigForGranterOrg(orgId, AppConfiguration.REPORT_SETUP_INTERVAL).getConfigValue()));
                config.setTemplateLibrary(templateLibraryService.getTemplateLibraryForOrganization(org.getId()));
            } else {
                org = organizationService.getPlatformOrg();
                config = new UIConfig();
                config.setLogoUrl(url.concat(LOGO_URL));
                config.setNavbarColor("#e3f2fd");
                config.setTenantCode(org.getCode());
                config.setTemplateLibrary(templateLibraryService.getTemplateLibraryForOrganization(org.getId()));
            }

        } else {
            Organization org = organizationService.getPlatformOrg();
            config = new UIConfig();
            config.setLogoUrl(url.concat(LOGO_URL));
            config.setNavbarColor("#e3f2fd");
            User user = userService.getUserById(userId);
            config.setTenantCode(org.getCode());
            config.setTemplateLibrary(templateLibraryService.getTemplateLibraryForOrganization(user.getOrganization().getId()));
        }
        return config;
    }



    @GetMapping("/grants/{grantId}/kpi-templates/{fileName}")
    @ApiIgnore
    public void getTemplate(@PathVariable("grantId") Long grantId,@PathVariable("fileName") String fileName,
                            HttpServletResponse servletResponse) {

        Resource file = resourceLoader
                .getResource(FILE + uploadLocation + grantService.getById(grantId).getGrantorOrganization().getCode() + "/grants/" + grantId + "/kpi-templates/" + fileName);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
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
                break;
            default:
                servletResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);
        }
        servletResponse
                .setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName);

        try {
            StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    @GetMapping("/grants/{grantId}/kpi-documents/{fileName}")
    @ApiIgnore
    public void getKpiDataDoc(@PathVariable("grantId") Long grantId, @PathVariable("fileName") String fileName, HttpServletResponse servletResponse) {

        Resource file = resourceLoader
                .getResource(FILE + uploadLocation + grantService.getById(grantId).getGrantorOrganization().getCode() + "/grants/" + grantId + "/kpi-documents/" + fileName);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);

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
                break;
            default:
                servletResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);
        }
        servletResponse
                .setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName);

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
                    .getResource(FILE + uploadLocation + URLDecoder.decode(templateLibrary.getLocation(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
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
                break;
            default:
                servletResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);
        }
        servletResponse
                .setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + templateLibrary.getName() + "." + fileType);

        try {
            if (file != null) {
                StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());
            }

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @GetMapping("/config/{type}/{id}")
    public Configuration getConfigs(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("type") String type, @PathVariable("id") Long id){

        Configuration config = new Configuration();
        if ("grant".equalsIgnoreCase(type)) {
            Grant grant = grantService.getById(id);
            config.setTenantUsers(userService.getAllTenantUsers(grant.getGrantorOrganization()));
            config.setGrantWorkflowStatuses(workflowStatusService.getTenantWorkflowStatuses("GRANT", grant.getGrantorOrganization().getId()));

        } else if ("report".equalsIgnoreCase(type)) {
            Report report = reportService.getReportById(id);
            config.setTenantUsers(userService.getAllTenantUsers(report.getGrant().getGrantorOrganization()));
            config.setReportWorkflowStatuses(workflowStatusService.getTenantWorkflowStatuses(REPORT, report.getGrant().getGrantorOrganization().getId()));
            config.setReportTransitions(workflowTransitionModelService.getWorkflowsByGranterAndType(report.getGrant().getGrantorOrganization().getId(), REPORT, report.getGrant().getGrantTypeId()));
        } else if ("disbursement".equalsIgnoreCase(type)) {
            Disbursement disbursement = disbursementService.getDisbursementById(id);
            config.setTenantUsers(userService.getAllTenantUsers(disbursement.getGrant().getGrantorOrganization()));
            config.setDisbursementWorkflowStatuses(workflowStatusService.getTenantWorkflowStatuses("DISBURSEMENT", disbursement.getGrant().getGrantorOrganization().getId()));
        } else if ("closure".equalsIgnoreCase(type)) {
            GrantClosure closure = closureService.getClosureById(id);
            config.setTenantUsers(userService.getAllTenantUsers(closure.getGrant().getGrantorOrganization()));
            config.setClosureWorkflowStatuses(workflowStatusService.getTenantWorkflowStatuses(GRANTCLOSURE, closure.getGrant().getGrantorOrganization().getId()));
        }
        return config;
    }

}
