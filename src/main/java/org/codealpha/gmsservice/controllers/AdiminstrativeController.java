package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.UserRoleRepository;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import springfox.documentation.annotations.ApiIgnore;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

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
    public UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    public AppConfigService appConfigService;
    @Autowired
    public CommonEmailSevice commonEmailSevice;
    @Autowired
    private ReportService reportService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DisbursementService disbursementService;
    @Autowired
    public ReleaseService releaseService;
    @Autowired
    public TemplateLibraryService templateLibraryService;
    @Value("${spring.upload-file-location}")
    private String uploadLocation;
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/workflow/grant/{grantId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for grant")
    public List<WorkflowTransitionModel> getGrantWorkflows(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @ApiParam(name = "grantId", value = "Unique identifier of grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        Organization org = null;
        if ("ANUDAN".equalsIgnoreCase(tenantCode)) {
            org = grantService.getById(grantId).getGrantorOrganization();
        } else {
            org = organizationService.findOrganizationByTenantCode(tenantCode);
        }

        return workflowTransitionModelService.getWorkflowsByGranterAndType(org.getId(), "GRANT");
    }

    @GetMapping("/workflow/report/{reportId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for report")
    public List<WorkflowTransitionModel> getReportWorkflows(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @ApiParam(name = "reportId", value = "Unique identifier of Report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        Organization org = null;
        if ("ANUDAN".equalsIgnoreCase(tenantCode)) {
            org = reportService.getReportById(reportId).getGrant().getGrantorOrganization();
        } else {
            org = organizationService.findOrganizationByTenantCode(tenantCode);
        }
        organizationService.findOrganizationByTenantCode(tenantCode);

        return workflowTransitionModelService.getWorkflowsByGranterAndType(org.getId(), "REPORT");
    }

    @GetMapping("/workflow/disbursement/{disbursementId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for disbursement")
    public List<WorkflowTransitionModel> getDisbursementWorkflows(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @ApiParam(name = "disbursementId", value = "Unique identifier of Disbursement") @PathVariable("disbursementId") Long disbursementId,
            @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        Organization org = null;
        if ("ANUDAN".equalsIgnoreCase(tenantCode)) {
            org = disbursementService.getDisbursementById(disbursementId).getGrant().getGrantorOrganization();
        } else {
            org = organizationService.findOrganizationByTenantCode(tenantCode);
        }
        organizationService.findOrganizationByTenantCode(tenantCode);

        return workflowTransitionModelService.getWorkflowsByGranterAndType(org.getId(), "DISBURSEMENT");
    }

    @PostMapping("/workflow")
    public void createBasicWorkflow(@RequestBody WorkFlowDataModel workFlowDataModel,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {
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

    private boolean _checkIfTerminal(WorkflowTransitionDataModel transition,
            List<WorkflowTransitionDataModel> transitions) {
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
                        if (a.getType().equalsIgnoreCase("table") || a.getType().equalsIgnoreCase("disbursement")) {
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
    public List<Role> getRolesForOrg(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {

        User user = userService.getUserById(userId);

        List<Role> roles = getCurrentOrgRoles(user);
        return roles;
    }

    @GetMapping("/user/{userId}/user")
    public List<User> getUsersForOrg(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {

        User user = userService.getUserById(userId);

        List<User> users = getCurrentUsers(user);
        return users;
    }

    private List<Role> getCurrentSystemRoles(User user) {
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
        roles.sort(Comparator.comparing(Role::getName));
        return roles;
    }

    private List<User> getCurrentUsers(User user) {
        List<User> users = userService.findByOrganization(user.getOrganization());
        users.forEach(u -> {
            u.getUserRoles().forEach(ur -> {
                if (ur.getRole().isInternal()) {
                    u.setAdmin(true);
                }
            });
            u.getUserRoles().removeIf(ur -> ur.getRole().isInternal());

        });
        List<User> unRegisteredUsers = users.stream().filter(u -> (u.isActive() == false)).collect(Collectors.toList());
        users.removeIf(u -> u.isActive() == false);
        users.sort(Comparator.comparing(User::getFirstName));
        users.addAll(unRegisteredUsers);
        return users;
    }

    @PutMapping("/user/{userId}/role")
    public Role saveRole(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId,
            @RequestBody Role newRole) {
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
    public List<Role> deleteRole(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId,
            @PathVariable("roleId") Long roleId) {

        Role role = roleService.getById(roleId);
        roleService.deleteRole(role);

        List<Role> roles = getCurrentOrgRoles(userService.getUserById(userId));
        return roles;
    }

    @PostMapping("/user/{userId}/user")
    public User createUser(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId,
            @RequestBody User newUser) {

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

        organizationService.buildInviteUrlAndSendMail(userService, appConfigService, commonEmailSevice, releaseService,
                adminUser, org, user, userRoles);
        return user;
    }

    @DeleteMapping("/user/{userId}/user/{userIdToDelete}")
    public List<User> deleteyUser(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId, @PathVariable("userIdToDelete") Long userIdToDelete) {

        User userToDelete = userService.getUserById(userIdToDelete);
        userToDelete.setDeleted(true);
        userService.save(userToDelete);

        return getUsersForOrg(tenantCode, userId);

    }

    @GetMapping("/user/{userId}/user/{userIdToUndelete}/undelete")
    public List<User> unDeleteUser(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId, @PathVariable("userIdToUndelete") Long userIdToUndelete) {

        User userToUndelete = userService.getUserById(userIdToUndelete);
        userToUndelete.setDeleted(false);
        userService.save(userToUndelete);

        return getUsersForOrg(tenantCode, userId);

    }

    @PutMapping("/user/{userId}/user")
    public User modifyUser(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId,
            @RequestBody User newUser) {

        Organization org = userService.getUserById(userId).getOrganization();

        User existingUser = userService.getUserById(newUser.getId());
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());
        existingUser.setAdmin(newUser.isAdmin());
        existingUser.setEmailId(newUser.getEmailId());
        existingUser.setUpdatedAt(DateTime.now().toDate());
        existingUser.setUpdatedBy(userService.getUserById(userId).getEmailId());
        Role adminRole = roleService.findByOrganizationAndName(org, "Admin");

        if (newUser.isAdmin()) {
            if (existingUser.getUserRoles().stream()
                    .anyMatch(ur -> ur.getRole().getId().longValue() == adminRole.getId().longValue())) {

            } else {
                UserRole newAdminRole = new UserRole();
                newAdminRole.setUser(existingUser);
                newAdminRole.setRole(adminRole);
                newAdminRole = userRoleService.saveUserRole(newAdminRole);
                existingUser.getUserRoles().add(newAdminRole);
            }
        } else {
            if (existingUser.getUserRoles().stream()
                    .anyMatch(ur -> ur.getRole().getId().longValue() == adminRole.getId().longValue())) {
                UserRole roleToDelete = existingUser.getUserRoles().stream()
                        .filter(ur -> ur.getRole().getId().longValue() == adminRole.getId().longValue()).findAny()
                        .get();
                // userRoleService.saveUserRoles(existingUser.getUserRoles());
                userRoleService.deleteUserRole(roleToDelete);
                existingUser.getUserRoles()
                        .removeIf(ur -> ur.getRole().getId().longValue() == roleToDelete.getRole().getId().longValue());

            } else {
            }
        }

        Optional<UserRole> optionalNonAdminRole = existingUser.getUserRoles().stream()
                .filter(ur -> ur.getRole().getId().longValue() != adminRole.getId().longValue()).findAny();
        UserRole nonAdminRole = optionalNonAdminRole.isPresent() ? optionalNonAdminRole.get() : null;
        Optional<UserRole> optionalNewNonAdminRole = newUser.getUserRoles().stream()
                .filter(ur -> ur.getRole().getId().longValue() != adminRole.getId().longValue()).findAny();
        UserRole newNonAdminRole = optionalNewNonAdminRole.isPresent() ? optionalNewNonAdminRole.get() : null;
        if (nonAdminRole != null && newNonAdminRole != null
                && nonAdminRole.getRole().getId().longValue() != newNonAdminRole.getRole().getId()) {
            nonAdminRole.setRole(newNonAdminRole.getRole());
            userRoleService.saveUserRole(nonAdminRole);
        } else if (nonAdminRole == null && newNonAdminRole != null && newNonAdminRole.getRole() != null) {
            nonAdminRole = new UserRole();
            nonAdminRole.setUser(existingUser);
            nonAdminRole.setRole(newNonAdminRole.getRole());
            userRoleService.saveUserRole(nonAdminRole);
        }

        existingUser = userService.save(existingUser);
        existingUser.getUserRoles().removeIf(ur -> ur.getRole().isInternal());
        return existingUser;
    }

    @GetMapping("/settings/{userId}/{orgId}")
    public List<AppConfigVO> getAppConfigsForOrg(@PathVariable("orgId") Long orgId,
            @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {
        Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
        if (org.getOrganizationType().equalsIgnoreCase("PLATFORM")) {
            List<AppConfig> configs = appConfigService.getAllAppConfigForGrantorOrg(0L);
            List<AppConfigVO> configVOs = new ArrayList<>();
            buildConfigVOList(configs, configVOs);
            return configVOs;
        } else if (org.getOrganizationType().equalsIgnoreCase("GRANTER")) {
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
        if (config.getConfigName().equalsIgnoreCase(AppConfiguration.DUE_REPORTS_REMINDER_SETTINGS.toString()) || config
                .getConfigName().equalsIgnoreCase(AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS.toString())) {
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
        if (config.getConfigName().equalsIgnoreCase(AppConfiguration.DUE_REPORTS_REMINDER_SETTINGS.toString()) || config
                .getConfigName().equalsIgnoreCase(AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS.toString())) {
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
    public AppConfigVO saveSetting(@RequestBody AppConfigVO config, @PathVariable("orgId") Long orgId,
            @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {

        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        if (config.getType().equalsIgnoreCase("app") && tenantOrg.getOrganizationType().equalsIgnoreCase("PLATFORM")) {
            AppConfig existingConfig = appConfigService.getAppConfigById(config.getKey());
            if (config.getConfigName().equalsIgnoreCase("DUE_REPORTS_REMINDER_SETTINGS")
                    || config.getConfigName().equalsIgnoreCase("ACTION_DUE_REPORTS_REMINDER_SETTINGS")) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    existingConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                existingConfig.setConfigValue(config.getConfigValue());
            }

            existingConfig = appConfigService.saveAppConfig(existingConfig);
        } else if (config.getType().equalsIgnoreCase("app")
                && tenantOrg.getOrganizationType().equalsIgnoreCase("GRANTER")) {
            AppConfig existingConfig = appConfigService.getAppConfigById(config.getKey());
            OrgConfig orgConfig = new OrgConfig();
            orgConfig.setConfigName(existingConfig.getConfigName());
            orgConfig.setConfigurable(existingConfig.getConfigurable());
            orgConfig.setDescription(existingConfig.getDescription());
            orgConfig.setGranterId(orgId);

            if (config.getConfigName().equalsIgnoreCase("DUE_REPORTS_REMINDER_SETTINGS")
                    || config.getConfigName().equalsIgnoreCase("ACTION_DUE_REPORTS_REMINDER_SETTINGS")) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    orgConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                existingConfig.setConfigValue(config.getConfigValue());
            }

            orgConfig = appConfigService.saveOrgConfig(orgConfig);
            config = convertOrgConfigToVO(orgConfig);
            config.setType("org");
            config.setKey(orgConfig.getId());
        } else if (config.getType().equalsIgnoreCase("org")) {
            OrgConfig existingConfig = appConfigService.getOrgConfigById(config.getKey());
            if (config.getConfigName().equalsIgnoreCase("DUE_REPORTS_REMINDER_SETTINGS")
                    || config.getConfigName().equalsIgnoreCase("ACTION_DUE_REPORTS_REMINDER_SETTINGS")) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    existingConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                existingConfig.setConfigValue(config.getConfigValue());
            }

            existingConfig = appConfigService.saveOrgConfig(existingConfig);

        }

        return config;
    }

    @PostMapping("/{userId}/encryptpasswords/{tenant}")
    public HttpStatus encryptAllPasswords(@PathVariable("tenant") String tenant,
            @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {

        List<User> tenantUsers = userService
                .getAllTenantUsers(organizationService.findOrganizationByTenantCode(tenant));
        for (User tenantUser : tenantUsers) {
            if (!tenantUser.isPlain()) {
                tenantUser.setPassword(passwordEncoder.encode(tenantUser.getPassword()));
                userService.save(tenantUser);
            }
        }

        return HttpStatus.OK;
    }

    @GetMapping("/user/{userId}/validate/{emailId}")
    public EmailValidationReponse validateEmail(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId, @PathVariable("emailId") String emailIdToValidate) {
        Organization userOrg = userService.getUserById(userId).getOrganization();

        User existingUser = userService.getUserByEmailAndOrg(emailIdToValidate, userOrg);
        return existingUser == null ? new EmailValidationReponse(false) : new EmailValidationReponse(true);
    }

    @PostMapping("/user/{userId}/role/validate")
    public EmailValidationReponse validateRole(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId, @RequestBody NewRoleModel roleToValidate) {
        Organization userOrg = userService.getUserById(userId).getOrganization();

        Role existingRole = roleService.findByNameAndOrganization(userOrg, roleToValidate.getRoleName());
        if (existingRole == null) {
            return new EmailValidationReponse(false);
        } else {
            return new EmailValidationReponse(true);
        }
    }

    @GetMapping("/user/{userId}/reinvite/{newUserId}")
    public User reSendInvite(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId,
            @PathVariable("newUserId") Long newUserId) {
        User adminUser = userService.getUserById(userId);
        Organization org = adminUser.getOrganization();
        User userToReinvite = userService.getUserById(newUserId);

        organizationService.buildInviteUrlAndSendMail(userService, appConfigService, commonEmailSevice, releaseService,
                adminUser, org, userToReinvite, userToReinvite.getUserRoles());
        return userToReinvite;
    }

    @GetMapping("/user/{userId}/document-library")
    public List<TemplateLibrary> getLibraryDocuments(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);

        return templateLibraryService.getTemplateLibraryForOrganization(user.getOrganization().getId());

    }

    @PostMapping(value = "/user/{userId}/document-library", consumes = { "multipart/form-data" })
    public TemplateLibrary saveLibraryDocuments(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId, @RequestParam("file") MultipartFile[] files,
            @RequestParam("docName") String docName, @RequestParam("docDescription") String docDescription) {
        User user = userService.getUserById(userId);

        String filePath = uploadLocation + tenantCode + "/template-library";

        TemplateLibrary libraryDoc = new TemplateLibrary();
        libraryDoc.setName(FilenameUtils.getBaseName(files[0].getOriginalFilename()));
        libraryDoc.setDescription(FilenameUtils.getBaseName(files[0].getOriginalFilename()));
        libraryDoc.setFileType(FilenameUtils.getExtension(files[0].getOriginalFilename()));
        libraryDoc.setType(FilenameUtils.getExtension(files[0].getOriginalFilename()));
        libraryDoc.setLocation(tenantCode + "/template-library/" + files[0].getOriginalFilename());
        libraryDoc.setGranterId(user.getOrganization().getId());
        libraryDoc = templateLibraryService.saveLibraryDoc(libraryDoc);

        File fileToCreate = new File(new File(filePath), files[0].getOriginalFilename());
        try {
            FileOutputStream fos = new FileOutputStream(fileToCreate);
            fos.write(files[0].getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return libraryDoc;
    }

    @PostMapping(value = "/user/{userId}/document-library/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadSelectedAttachments(@PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode, @RequestBody AttachmentDownloadRequest downloadRequest,
            HttpServletResponse response) throws IOException {

        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
        // setting headers
        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"test.zip\"");

        // creating byteArray stream, make it bufforable and passing this buffor to
        // ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        // simple file list, just for tests

        ArrayList<File> files = new ArrayList<>(2);

        // packing files
        for (Long attachmentId : downloadRequest.getAttachmentIds()) {
            TemplateLibrary attachment = templateLibraryService.getTemplateLibraryDocumentById(attachmentId);

            File file = resourceLoader.getResource("file:" + uploadLocation + attachment.getLocation() + "/"
                    + attachment.getName() + "." + attachment.getFileType()).getFile();
            // new zip entry and copying inputstream with file to zipOutputStream, after all
            // closing streams
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @PostMapping(value = "/user/{userId}/document-library/delete")
    public void downloadSelectedAttachments(@PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode, @RequestBody AttachmentDownloadRequest downloadRequest) {

        for (Long attachmentId : downloadRequest.getAttachmentIds()) {
            TemplateLibrary attachment = templateLibraryService.getTemplateLibraryDocumentById(attachmentId);
            templateLibraryService.deleteTemplateLibraryDoc(attachment);
        }
    }

    @GetMapping(value = "/user/{userId}/reference-orgs")
    public List<Organization> getReferenceOrgs(@PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {
        AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(
                userService.getUserById(userId).getOrganization().getId(), AppConfiguration.REFERENCE_ORG);

        List<Organization> orgs = new ArrayList<>();
        String[] orgCodes = config.getConfigValue().split(",");
        for (String code : orgCodes) {
            orgs.add(organizationService.findOrganizationByTenantCode(code));
        }

        return orgs;
    }
}
