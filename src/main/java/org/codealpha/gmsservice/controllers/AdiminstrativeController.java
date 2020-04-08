package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@ApiIgnore
public class AdiminstrativeController {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private WorkflowTransitionModelService workflowTransitionModelService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private GranterReportTemplateService reportTemplateService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private CommonEmailSevice commonEmailSevice;
    @Autowired private ReportService reportService;
    @Autowired private GrantService grantService;

    @GetMapping("/workflow/grant/{grantId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for grant")
    public List<WorkflowTransitionModel> getGrantWorkflows(@ApiParam(name = "X-TENANT-CODE", value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode, @ApiParam(name = "grantId", value = "Unique identifier of grant") @PathVariable("grantId") Long grantId, @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        Organization org = null;
        if("ANUDAN".equalsIgnoreCase(tenantCode)){
            org = grantService.getById(grantId).getGrantorOrganization();
        }else{
            org = organizationService.findOrganizationByTenantCode(tenantCode);
        }

        return workflowTransitionModelService.getWorkflowsByGranterAndType(org.getId(), "GRANT");
    }

    @GetMapping("/workflow/report/{reportId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for report")
    public List<WorkflowTransitionModel> getReportWorkflows(@ApiParam(name = "X-TENANT-CODE", value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode, @ApiParam(name = "reportId", value = "Unique identifier of Report") @PathVariable("reportId") Long reportId, @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        Organization org = null;
        if("ANUDAN".equalsIgnoreCase(tenantCode)){
            org = reportService.getReportById(reportId).getGrant().getGrantorOrganization();
        }else{
            org = organizationService.findOrganizationByTenantCode(tenantCode);
        }
        organizationService.findOrganizationByTenantCode(tenantCode);


        return workflowTransitionModelService.getWorkflowsByGranterAndType(org.getId(), "REPORT");
    }

    @PostMapping("/workflow")
    public void createBasicWorkflow(@RequestBody WorkFlowDataModel workFlowDataModel, @RequestHeader("X-TENANT-CODE") String tenantCode) {
        Workflow workflow = new Workflow();
        workflow.setObject(WorkflowObject.valueOf(workFlowDataModel.getType()));
        workflow.setName(workFlowDataModel.getName());
        workflow.setGranter(organizationService.findOrganizationByTenantCode(tenantCode));
        workflow.setCreatedBy("System");
        workflow.setCreatedAt(DateTime.now().toDate());

        workflow = workflowService.saveWorkflow(workflow);

        Map<String, WorkflowStatus> statuses = new HashMap<>();
        int index = 0;
        for (WorkflowTransitionDataModel transition : workFlowDataModel.getTransitions()) {
            WorkflowStatus status = new WorkflowStatus();
            status.setWorkflow(workflow);
            status.setVerb(transition.getAction());
            if (_checkIfTerminal(transition, workFlowDataModel.getTransitions())) {
                status.setTerminal(true);
            }
        }


    }

    private boolean _checkIfTerminal(WorkflowTransitionDataModel transition, List<WorkflowTransitionDataModel> transitions) {
        transitions.stream().filter(t -> t.getFrom().equalsIgnoreCase(transition.getFrom()));
        return true;
    }

    @PostMapping("/template")
    public void createTemplate(@RequestBody templateVO template, @RequestHeader("X-TENANT-CODE") String tenantCode) {

        Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
        switch (template.getType()) {
            case "report":
                GranterReportTemplate reportTemplate = _extractReportTemplate(org, template);
                reportTemplateService.saveReportTemplate(reportTemplate);
        }


    }

    private GranterReportTemplate _extractReportTemplate(Organization granter, templateVO template) {

        reportTemplateService.markAllAsNotDefault();
        GranterReportTemplate reportTemplate = new GranterReportTemplate();
        reportTemplate.setDefaultTemplate(template.is_default());
        reportTemplate.setDescription(template.getDescription());
        reportTemplate.setGranterId(granter.getId());
        reportTemplate.setName(template.getName());
        reportTemplate.setPrivateToReport(false);
        reportTemplate.setPublished(true);
        if (template.getSections() != null) {
            List<GranterReportSection> sectionsList = new ArrayList<>();
            template.getSections().forEach(s -> {
                GranterReportSection section = new GranterReportSection();
                section.setReportTemplate(reportTemplate);
                section.setDeletable(true);
                section.setGranter((Granter) granter);
                section.setSectionName(s.getName());
                section.setSectionOrder(s.getOrder());
                sectionsList.add(section);
                if (s.getAttributes() != null) {
                    List<GranterReportSectionAttribute> attributesList = new ArrayList<>();
                    s.getAttributes().forEach(a -> {
                        GranterReportSectionAttribute attribute = new GranterReportSectionAttribute();
                        attribute.setAttributeOrder(a.getOrder());
                        attribute.setDeletable(true);
                        if (a.getType().equalsIgnoreCase("table")) {
                            try {
                                attribute.setExtras(new ObjectMapper().writeValueAsString(a.getTableValue()));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                        attribute.setFieldName(a.getName());
                        attribute.setFieldType(a.getType());
                        attribute.setGranter((Granter) granter);
                        attribute.setRequired(false);
                        attribute.setSection(section);
                        attributesList.add(attribute);
                    });
                    section.setAttributes(attributesList);
                }

            });

            reportTemplate.setSections(sectionsList);
        }
        return reportTemplate;
    }

    @GetMapping("/user/{userId}/role")
    public List<Role> getRolesForOrg(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {

        User user = userService.getUserById(userId);

        List<Role> roles = getCurrentOrgRoles(user);
        return roles;
    }

    @GetMapping("/user/{userId}/user")
    public List<User> getUsersForOrg(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {

        User user = userService.getUserById(userId);

        List<User> users = getCurrentUsers(user);
        return users;
    }

    private List<Role> getCurrentSystemRoles(User user){
        List<Role> roles = roleService.getInternalRolesForOrganization(user.getOrganization());
        for (Role role : roles) {
            List<UserRole> userRoles = userRoleService.findUsersForRole(role);
            if (userRoles != null && userRoles.size() > 0) {
                role.setHasUsers(true);
                role.setLinkedUsers(userRoles.size());
            }
        }
        return roles;
    }

    private List<Role> getCurrentOrgRoles(User user) {
        List<Role> roles = roleService.getPublicRolesForOrganization(user.getOrganization());
        for (Role role : roles) {
            List<UserRole> userRoles = userRoleService.findUsersForRole(role);
            if (userRoles != null && userRoles.size() > 0) {
                role.setHasUsers(true);
                role.setLinkedUsers(userRoles.size());
            }
        }
        return roles;
    }

    private List<User> getCurrentUsers(User user) {
        List<User> users = userService.findByOrganization(user.getOrganization());
        users.forEach(u -> {
            u.getUserRoles().forEach(ur -> {
                if(ur.getRole().isInternal()){
                    u.setAdmin(true);
                }
            });
            u.getUserRoles().removeIf(ur -> ur.getRole().isInternal());

        });
        return  users;
    }

    @PutMapping("/user/{userId}/role")
    public Role saveRole(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId, @RequestBody Role newRole) {
        Role role = null;
        if (newRole.getId() == 0) {
            role = new Role();
            role.setLinkedUsers(0);
            role.setHasUsers(false);
            User user = userService.getUserById(userId);
            role.setOrganization(user.getOrganization());
            role.setCreatedAt(DateTime.now().toDate());
            role.setCreatedBy(user.getEmailId());
        } else {
            role = roleService.getById(newRole.getId());
            role.setUpdatedAt(DateTime.now().toDate());
            role.setUpdatedBy(userService.getUserById(userId).getEmailId());
        }

        role.setName(newRole.getName());
        role.setDescription(newRole.getDescription());

        role = roleService.saveRole(role);
        return role;
    }

    @DeleteMapping("/user/{userId}/role/{roleId}")
    public List<Role> deleteRole(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId, @PathVariable("roleId") Long roleId) {

        Role role = roleService.getById(roleId);
        roleService.deleteRole(role);

        List<Role> roles = getCurrentOrgRoles(userService.getUserById(userId));
        return roles;
    }

    @PostMapping("/user/{userId}/user")
    public User createUser(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId, @RequestBody User newUser) {

        User adminUser = userService.getUserById(userId);
        Organization org = adminUser.getOrganization();

        User user = new User();
        user.setActive(false);
        user.setOrganization(org);
        user.setEmailId(newUser.getEmailId());
        user.setCreatedAt(DateTime.now().toDate());
        user.setCreatedBy(adminUser.getEmailId());
        user = userService.save(user);

        List<UserRole> userRoles = new ArrayList<>();
        for (UserRole userRole : newUser.getUserRoles()) {
            UserRole userRole1 = new UserRole();
            userRole1.setRole(userRole.getRole());
            userRole1.setUser(user);
            userRole1 = userRoleService.saveUserRole(userRole1);
            userRoles.add(userRole1);
        }

       user.setUserRoles(userRoles);

        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
        String host = null;
        if(org.getOrganizationType().equalsIgnoreCase("GRANTEE")) {
            host = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
        }else if(org.getOrganizationType().equalsIgnoreCase("GRANTER")){
            host = uriComponents.getHost();
        }
        UriComponentsBuilder uriBuilder =  UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme()).host(host).port(uriComponents.getPort());
        String url = uriBuilder.toUriString();
        url = url+"/home/?action=registration&org="+ StringEscapeUtils.escapeHtml4(user.getOrganization().getName())+"&email="+user.getEmailId()+"&type=join";
        String[] notifications = userService.buildJoiningInvitationContent(
                user.getOrganization(),userRoles.get(0).getRole(),adminUser,
                appConfigService.getAppConfigForGranterOrg(user.getOrganization().getId(), AppConfiguration.INVITE_SUBJECT).getConfigValue(),
                appConfigService.getAppConfigForGranterOrg(user.getOrganization().getId(), AppConfiguration.INVITE_MESSAGE).getConfigValue(),
                url);
        commonEmailSevice.sendMail(user.getEmailId(),notifications[0],notifications[1],new String[]{appConfigService.getAppConfigForGranterOrg(user.getOrganization().getId(),AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
        return user;
    }

    @GetMapping("/settings/{userId}/{orgId}")
    public List<AppConfigVO> getAppConfigsForOrg(@PathVariable("orgId") Long orgId,@RequestHeader("X-TENANT-CODE") String tenantCode,@PathVariable("userId") Long userId){
        Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
        if(org.getOrganizationType().equalsIgnoreCase("PLATFORM")){
            List<AppConfig> configs = appConfigService.getAllAppConfigForGrantorOrg(0L);
            List<AppConfigVO> configVOs = new ArrayList<>();
            buildConfigVOList(configs, configVOs);
            return configVOs;
        }else if(org.getOrganizationType().equalsIgnoreCase("GRANTER")){
            List<AppConfig> configs = appConfigService.getAllAppConfigForGrantorOrg(orgId);
            List<AppConfigVO> configVOs = new ArrayList<>();
            buildConfigVOList(configs, configVOs);
            return configVOs;
        }
        return null;
    }

    private void buildConfigVOList(List<AppConfig> configs, List<AppConfigVO> configVOs) {
        for (AppConfig config : configs) {
            AppConfigVO configVO = convertAppConfigToVO(config);
            configVOs.add(configVO);
        }
    }

    private AppConfigVO convertAppConfigToVO(AppConfig config) {
        AppConfigVO configVO = new AppConfigVO();
        configVO.setConfigName(config.getConfigName());
        configVO.setId(config.getId());
        configVO.setConfigValue(config.getConfigValue());
        configVO.setDescription(config.getDescription());
        configVO.setConfigurable(config.getConfigurable());
        configVO.setKey(config.getKey());
        configVO.setType(config.getType());
        if(config.getConfigName().equalsIgnoreCase(AppConfiguration.DUE_REPORTS_REMINDER_SETTINGS.toString()) || config.getConfigName().equalsIgnoreCase(AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS.toString()) ){
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(config.getConfigValue(), ScheduledTaskVO.class);
                configVO.setScheduledTaskConfiguration(taskConfiguration);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configVO;
    }

    private AppConfigVO convertOrgConfigToVO(OrgConfig config) {
        AppConfigVO configVO = new AppConfigVO();
        configVO.setConfigName(config.getConfigName());
        configVO.setId(config.getId());
        configVO.setConfigValue(config.getConfigValue());
        configVO.setDescription(config.getDescription());
        configVO.setConfigurable(config.getConfigurable());
        configVO.setKey(config.getKey());
        configVO.setType(config.getType());
        if(config.getConfigName().equalsIgnoreCase(AppConfiguration.DUE_REPORTS_REMINDER_SETTINGS.toString()) || config.getConfigName().equalsIgnoreCase(AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS.toString()) ){
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(config.getConfigValue(), ScheduledTaskVO.class);
                configVO.setScheduledTaskConfiguration(taskConfiguration);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configVO;
    }


    @PostMapping("/settings/{userId}/{orgId}")
    public AppConfigVO saveSetting(@RequestBody AppConfigVO config, @PathVariable("orgId") Long orgId,@RequestHeader("X-TENANT-CODE") String tenantCode,@PathVariable("userId") Long userId){

        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        if(config.getType().equalsIgnoreCase("app") && tenantOrg.getOrganizationType().equalsIgnoreCase("PLATFORM")){
            AppConfig existingConfig = appConfigService.getAppConfigById(config.getKey());
            if(config.getConfigName().equalsIgnoreCase("DUE_REPORTS_REMINDER_SETTINGS") || config.getConfigName().equalsIgnoreCase("ACTION_DUE_REPORTS_REMINDER_SETTINGS")){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    existingConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else{
                existingConfig.setConfigValue(config.getConfigValue());
            }

            existingConfig = appConfigService.saveAppConfig(existingConfig);
        } else if(config.getType().equalsIgnoreCase("app") && tenantOrg.getOrganizationType().equalsIgnoreCase("GRANTER")){
            AppConfig existingConfig = appConfigService.getAppConfigById(config.getKey());
            OrgConfig orgConfig = new OrgConfig();
            orgConfig.setConfigName(existingConfig.getConfigName());
            orgConfig.setConfigurable(existingConfig.getConfigurable());
            orgConfig.setDescription(existingConfig.getDescription());
            orgConfig.setGranterId(orgId);

            if(config.getConfigName().equalsIgnoreCase("DUE_REPORTS_REMINDER_SETTINGS") || config.getConfigName().equalsIgnoreCase("ACTION_DUE_REPORTS_REMINDER_SETTINGS")){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    orgConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else{
                existingConfig.setConfigValue(config.getConfigValue());
            }

            orgConfig = appConfigService.saveOrgConfig(orgConfig);
            config = convertOrgConfigToVO(orgConfig);
            config.setType("org");
            config.setKey(orgConfig.getId());
        } else if(config.getType().equalsIgnoreCase("org")){
            OrgConfig existingConfig = appConfigService.getOrgConfigById(config.getKey());
            if(config.getConfigName().equalsIgnoreCase("DUE_REPORTS_REMINDER_SETTINGS") || config.getConfigName().equalsIgnoreCase("ACTION_DUE_REPORTS_REMINDER_SETTINGS")){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    existingConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else{
                existingConfig.setConfigValue(config.getConfigValue());
            }

            existingConfig = appConfigService.saveOrgConfig(existingConfig);

        }

        return config;
    }
}
