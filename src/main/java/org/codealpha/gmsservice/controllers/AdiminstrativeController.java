package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.GrantSnapshotRepository;
import org.codealpha.gmsservice.repositories.GrantToFixRepository;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/admin")
@ApiIgnore
public class AdiminstrativeController {

    public static final String DISBURSEMENT = "DISBURSEMENT";
    public static final String ANUDAN = "ANUDAN";
    public static final String DUE_REPORTS_REMINDER_SETTINGS = "DUE_REPORTS_REMINDER_SETTINGS";
    public static final String ACTION_DUE_REPORTS_REMINDER_SETTINGS = "ACTION_DUE_REPORTS_REMINDER_SETTINGS";
    public static final String COLOR_CODE = "#fdf6ff";
    public static final String IMPLEMENTED_VIA_EXTERNAL_PARTNER = "Implemented via external partner";
    public static final String EXTERNAL_IMPLEMENTATION = "External Implementation";
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
    @Autowired
    private GrantToFixRepository grantToFixRepository;
    @Autowired
    private GrantSnapshotRepository grantSnapshotRepository;
    @Autowired
    private GrantTypeService grantTypeService;
    @Autowired
    private GrantTypeWorkflowMappingService grantTypeWorkflowMappingService;
    @Autowired
    private GranterService granterService;
    @Autowired
    private OrgTagService orgTagService;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private WorkflowValidationService workflowValidationService;
    @Autowired
    private WorkflowStatusTransitionService workflowStatusTransitionService;
    @Autowired
    private GrantClosureService closureService;

    private static Logger logger = LoggerFactory.getLogger(AdiminstrativeController.class);

    @GetMapping("/workflow/grant/{grantId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for grant")
    public List<WorkflowTransitionModel> getGrantWorkflows(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @ApiParam(name = "grantId", value = "Unique identifier of grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        WorkflowStatus grantStatus = workflowStatusService.findById(grantService.getById(grantId).getGrantStatus().getId());
        return workflowTransitionModelService.getWorkflowsByWorkflowStatusId(grantStatus.getWorkflow().getId());
    }

    @GetMapping("/workflow/report/{reportId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for report")
    public List<WorkflowTransitionModel> getReportWorkflows(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @ApiParam(name = "reportId", value = "Unique identifier of Report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId) {

        organizationService.findOrganizationByTenantCode(tenantCode);

        WorkflowStatus reportStatus = workflowStatusService.findById(reportService.getReportById(reportId).getStatus().getId());

        return workflowTransitionModelService.getWorkflowsByWorkflowStatusId(reportStatus.getWorkflow().getId());
    }

    @GetMapping("/workflow/closure/{closureId}/user/{userId}")
    public List<WorkflowTransitionModel> getCLosureWorkflows(
            @RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("closureId") Long closureId,
            @PathVariable("userId") Long userId) {
        organizationService.findOrganizationByTenantCode(tenantCode);

        WorkflowStatus reportStatus = workflowStatusService.findById(closureService.getClosureById(closureId).getStatus().getId());

        return workflowTransitionModelService.getWorkflowsByWorkflowStatusId(reportStatus.getWorkflow().getId());
    }

    @GetMapping("/workflow/disbursement/{disbursementId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for disbursement")
    public List<WorkflowTransitionModel> getDisbursementWorkflows(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @ApiParam(name = "disbursementId", value = "Unique identifier of Disbursement") @PathVariable("disbursementId") Long disbursementId,
            @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId) {

        organizationService.findOrganizationByTenantCode(tenantCode);

        WorkflowStatus disbursementStatus = workflowStatusService.findById(disbursementService.getDisbursementById(disbursementId).getStatus().getId());
        return workflowTransitionModelService.getWorkflowsByWorkflowStatusId(disbursementStatus.getWorkflow().getId());
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

        for (WorkflowTransitionDataModel transition : workFlowDataModel.getTransitions()) {
            WorkflowStatus status = new WorkflowStatus();
            status.setWorkflow(workflow);
            status.setVerb(transition.getAction());
                status.setTerminal(true);
        }
    }


    @PostMapping("/template")
    public void createTemplate(@RequestBody templateVO template, @RequestHeader("X-TENANT-CODE") String tenantCode) {

        Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
        if ("report".equals(template.getType())) {
            GranterReportTemplate reportTemplate = extractReportTemplate(org, template);
            reportTemplateService.saveReportTemplate(reportTemplate);
        }

    }

    private GranterReportTemplate extractReportTemplate(Organization granter, templateVO template) {

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
                                logger.error(e.getMessage(),e);
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

        return getCurrentOrgRoles(user);
    }

    @GetMapping("/user/{userId}/user")
    public List<User> getUsersForOrg(@RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {

        User user = userService.getUserById(userId);

        return getCurrentUsers(user);
    }

    private List<Role> getCurrentOrgRoles(User user) {
        List<Role> roles = roleService.getPublicRolesForOrganization(user.getOrganization());
        for (Role role : roles) {
            List<UserRole> userRoles = userRoleService.findUsersForRole(role);
            if (userRoles != null && !userRoles.isEmpty()) {
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
        List<User> unRegisteredUsers = users.stream().filter(u -> (!u.isActive())).collect(Collectors.toList());
        users.removeIf(u -> !u.isActive());
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
            role.setDescription(newRole.getDescription());
            role.setName(newRole.getName());
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

        return getCurrentOrgRoles(userService.getUserById(userId));
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
                //Do nothing
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
                Optional<UserRole> optionalUserRole = existingUser.getUserRoles().stream()
                        .filter(ur -> ur.getRole().getId().longValue() == adminRole.getId().longValue()).findAny();
                if(optionalUserRole.isPresent()){
                    userRoleService.deleteUserRole(optionalUserRole.get());
                    existingUser.getUserRoles()
                            .removeIf(ur -> ur.getRole().getId().longValue() == optionalUserRole.get().getRole().getId().longValue());
                }
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
        return Collections.emptyList();
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
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
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
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
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
            if (config.getConfigName().equalsIgnoreCase(DUE_REPORTS_REMINDER_SETTINGS)
                    || config.getConfigName().equalsIgnoreCase(ACTION_DUE_REPORTS_REMINDER_SETTINGS)) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    existingConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(),e);
                }
            } else {
                existingConfig.setConfigValue(config.getConfigValue());
            }

            appConfigService.saveAppConfig(existingConfig);
        } else if (config.getType().equalsIgnoreCase("app")
                && tenantOrg.getOrganizationType().equalsIgnoreCase("GRANTER")) {
            AppConfig existingConfig = appConfigService.getAppConfigById(config.getKey());
            OrgConfig orgConfig = new OrgConfig();
            orgConfig.setConfigName(existingConfig.getConfigName());
            orgConfig.setConfigurable(existingConfig.getConfigurable());
            orgConfig.setDescription(existingConfig.getDescription());
            orgConfig.setGranterId(orgId);

            if (config.getConfigName().equalsIgnoreCase(DUE_REPORTS_REMINDER_SETTINGS)
                    || config.getConfigName().equalsIgnoreCase(ACTION_DUE_REPORTS_REMINDER_SETTINGS)) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    orgConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(),e);
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
            if (config.getConfigName().equalsIgnoreCase(DUE_REPORTS_REMINDER_SETTINGS)
                    || config.getConfigName().equalsIgnoreCase(ACTION_DUE_REPORTS_REMINDER_SETTINGS)) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    existingConfig.setConfigValue(mapper.writeValueAsString(config.getScheduledTaskConfiguration()));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(),e);
                }
            } else {
                existingConfig.setConfigValue(config.getConfigValue());
            }

            appConfigService.saveOrgConfig(existingConfig);

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

        String filePath = uploadLocation + user.getOrganization().getCode() + "/template-library";
        new File(filePath).mkdirs();



        File fileToCreate = new File(new File(filePath), files[0].getOriginalFilename());
        TemplateLibrary libraryDoc = new TemplateLibrary();
        libraryDoc.setName(FilenameUtils.getBaseName(files[0].getOriginalFilename()));
        libraryDoc.setDescription(FilenameUtils.getBaseName(files[0].getOriginalFilename()));
        libraryDoc.setFileType(FilenameUtils.getExtension(files[0].getOriginalFilename()));
        libraryDoc.setType(FilenameUtils.getExtension(files[0].getOriginalFilename()));
        libraryDoc.setLocation(fileToCreate.getPath());
        libraryDoc.setGranterId(user.getOrganization().getId());
        libraryDoc = templateLibraryService.saveLibraryDoc(libraryDoc);
        try(FileOutputStream fos = new FileOutputStream(fileToCreate)) {
            fos.write(files[0].getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return libraryDoc;
    }

    @PostMapping(value = "/user/{userId}/document-library/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadSelectedAttachments(@PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode, @RequestBody AttachmentDownloadRequest downloadRequest,
            HttpServletResponse response) throws IOException {

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
        // packing files
        for (Long attachmentId : downloadRequest.getAttachmentIds()) {
            TemplateLibrary attachment = templateLibraryService.getTemplateLibraryDocumentById(attachmentId);

            File file = resourceLoader.getResource("file:" + uploadLocation + attachment.getLocation()).getFile();
            // new zip entry and copying inputstream with file to zipOutputStream, after all
            // closing streams
            try(FileInputStream fileInputStream = new FileInputStream(file)) {
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));

                IOUtils.copy(fileInputStream, zipOutputStream);
                zipOutputStream.closeEntry();
            }catch(Exception e){
                logger.error(e.getMessage(),e);
            }
        }

        zipOutputStream.finish();
        zipOutputStream.flush();
        IOUtils.closeQuietly(zipOutputStream);
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

    @PostMapping("/fixgrants")
    public HttpStatus fixSustainPlusGrants()
            throws IOException {
        List<GrantToFix> grants2Fix = grantToFixRepository.getGrantsToFix();
        FileWriter fw = new FileWriter("c:\\sustainplus\\snapshot_fix.sql", true);
        BufferedWriter bw = new BufferedWriter(fw);

        for (GrantToFix toFix : grants2Fix) {
            ObjectMapper mapper = new ObjectMapper();
            List<TableData> disbsToSet = mapper.readValue(toFix.getValue(), new TypeReference<List<TableData>>() {
            });

            GrantDetailVO detail = mapper.readValue(toFix.getStringAttributes(), GrantDetailVO.class);
            for (SectionVO section : detail.getSections()) {
                section.getAttributes().removeIf(at -> at.getId().longValue() == 8665);
                List<SectionAttributesVO> attributes = section.getAttributes();
                if (attributes != null && !attributes.isEmpty()) {
                    for (SectionAttributesVO attribute : attributes) {
                        if (attribute.getId() == 8655) {
                            attribute.setFieldValue(
                                    "PRADAN, 3 Community Shopping Centre, Niti Bagh, New Delhi - 110049");//
                        }
                        if (attribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                            attribute.setFieldTableValue(disbsToSet);
                            attribute.setFieldValue(mapper.writeValueAsString(disbsToSet));
                        }

                    }
                }

                String updateString = mapper.writeValueAsString(detail);
                updateString = updateString.replaceAll("'", "''");
                bw.write("update grant_snapshot set string_attributes='" + updateString + "' where id=" + toFix.getId()
                        + ";");
                bw.newLine();
            }
        }
        bw.close();

        return HttpStatus.OK;
    }

    @GetMapping("/create-grant-types")
    public boolean createGrantTypes(){

        List<Granter> granters = granterService.getAllGranters();
        granters.removeIf(g -> g.getCode().equalsIgnoreCase("TEMPORG"));
        for(Organization granter:granters){
            List<Workflow> grantWorkflows = workflowService.getAllWorkflowsForGranterByType(granter.getId(),"GRANT");
            List<GrantType> grantTypes = grantTypeService.findGrantTypesForTenant(granter.getId());
            for(Workflow grantWf: grantWorkflows){
                if(grantTypes.isEmpty()){
                    GrantType gt = new GrantType();
                    gt.setColorCode(COLOR_CODE);
                    gt.setDescription(IMPLEMENTED_VIA_EXTERNAL_PARTNER);
                    gt.setGranterId(granter.getId());
                    gt.setInternal(false);
                    gt.setName(EXTERNAL_IMPLEMENTATION);
                    gt = grantTypeService.save(gt);
                    grantTypes.add(gt);

                    List<Grant> grants = grantService.getAllGrantsForGranter(granter.getId());
                    for(Grant grant: grants){
                        grant.setGrantTypeId(gt.getId());
                        grantService.saveGrant(grant);
                    }
                }
                for(GrantType gt : grantTypes){
                    List<GrantTypeWorkflowMapping> typeWfMapping = grantTypeWorkflowMappingService.findByWorkflow(grantWf.getId());
                    if(typeWfMapping.isEmpty()){
                        GrantTypeWorkflowMapping gtm = new GrantTypeWorkflowMapping();
                        gtm.setGrantTypeId(gt.getId());
                        gtm.set_default(false);
                        gtm.setWorkflowId(grantWf.getId());
                        grantTypeWorkflowMappingService.save(gtm);
                    }
                }
            }

            List<Workflow> reportWorkflows = workflowService.getAllWorkflowsForGranterByType(granter.getId(),"REPORT");
            for(Workflow reportWf: reportWorkflows){
                if(grantTypes.isEmpty()){
                    GrantType gt = new GrantType();
                    gt.setColorCode(COLOR_CODE);
                    gt.setDescription(IMPLEMENTED_VIA_EXTERNAL_PARTNER);
                    gt.setGranterId(granter.getId());
                    gt.setInternal(false);
                    gt.setName(EXTERNAL_IMPLEMENTATION);
                    gt = grantTypeService.save(gt);
                    grantTypes.add(gt);

                    List<Grant> grants = grantService.getAllGrantsForGranter(granter.getId());
                    for(Grant grant: grants){
                        grant.setGrantTypeId(gt.getId());
                        grantService.saveGrant(grant);
                    }
                }
                for(GrantType gt : grantTypes){
                    List<GrantTypeWorkflowMapping> typeWfMapping = grantTypeWorkflowMappingService.findByWorkflow(reportWf.getId());
                    if(typeWfMapping.isEmpty()){
                        GrantTypeWorkflowMapping gtm = new GrantTypeWorkflowMapping();
                        gtm.setGrantTypeId(gt.getId());
                        gtm.set_default(false);
                        gtm.setWorkflowId(reportWf.getId());
                        grantTypeWorkflowMappingService.save(gtm);
                    }
                }
            }

            List<Workflow> disbursementWorkflows = workflowService.getAllWorkflowsForGranterByType(granter.getId(), DISBURSEMENT);
            for(Workflow disbWf: disbursementWorkflows){
                if(grantTypes.isEmpty()){
                    GrantType gt = new GrantType();
                    gt.setColorCode(COLOR_CODE);
                    gt.setDescription(IMPLEMENTED_VIA_EXTERNAL_PARTNER);
                    gt.setGranterId(granter.getId());
                    gt.setInternal(false);
                    gt.setName(EXTERNAL_IMPLEMENTATION);
                    gt = grantTypeService.save(gt);
                    grantTypes.add(gt);

                    List<Grant> grants = grantService.getAllGrantsForGranter(granter.getId());
                    for(Grant grant: grants){
                        grant.setGrantTypeId(gt.getId());
                        grantService.saveGrant(grant);
                    }
                }
                for(GrantType gt : grantTypes){
                    List<GrantTypeWorkflowMapping> typeWfMapping = grantTypeWorkflowMappingService.findByWorkflow(disbWf.getId());
                    if(typeWfMapping.isEmpty()){
                        GrantTypeWorkflowMapping gtm = new GrantTypeWorkflowMapping();
                        gtm.setGrantTypeId(gt.getId());
                        gtm.set_default(false);
                        gtm.setWorkflowId(disbWf.getId());
                        grantTypeWorkflowMappingService.save(gtm);
                    }
                }
            }
        }
        return true;
    }

    @PostMapping("/tags/{name}")
    public OrgTag createOrgTag(@PathVariable("name") String tagName,@RequestHeader("X-TENANT-CODE") String tenantCode){
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        OrgTag tag = new OrgTag();
        tag.setName(tagName);
        tag.setTenant(tenantOrg.getId());
        tag = orgTagService.createTag(tag);
        return tag;
    }

    @GetMapping("/user/{userId}/tags")
    public List<OrgTag> getOrgTags(@PathVariable("userId") Long userId,@RequestHeader("X-TENANT-CODE") String tenantCode){
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        List<OrgTag> tags = orgTagService.getOrgTags(tenantOrg.getId());
        for(OrgTag tag : tags){
            tag.setUsed(grantService.isTagInUse(tag.getId()));
        }
        return tags;
    }

    @PutMapping("/user/{userId}/tags")
    public OrgTag updateOrgTag(@PathVariable("userId") Long userId,@RequestHeader("X-TENANT-CODE") String tenantCode,@RequestBody OrgTag tag){
        OrgTag existingTag = orgTagService.getOrgTagById(tag.getId());
        existingTag.setUsed(tag.getUsed());
        existingTag.setName(tag.getName());
        existingTag.setDisabled(tag.getDisabled());

        existingTag = orgTagService.save(existingTag);
        return existingTag;
    }

    @DeleteMapping("/user/{userId}/tags/{tagId}")
    public void deleteOrgTag(@PathVariable("userId") Long userId,@RequestHeader("X-TENANT-CODE") String tenantCode,@PathVariable Long tagId){
        OrgTag existingTag = orgTagService.getOrgTagById(tagId);
        orgTagService.delete(existingTag);
    }

    @PostMapping("/{id}/workflow/validate/{object}/{fromStateId}/{toStateId}")
    public WorkflowValidationResult getGrantValidations(@PathVariable("id")Long objectId,
                                                        @PathVariable("object")String object,
                                                        @PathVariable("fromStateId")Long fromStateId,
                                                        @PathVariable("toStateId")Long toStateId,
                                                        @RequestBody(required = false) List<ColumnData> meta){
        WorkflowValidationResult validationResult = new WorkflowValidationResult();
        WorkflowStatusTransition transition = workflowStatusTransitionService.findByFromAndToStates(workflowStatusService.getById(fromStateId),workflowStatusService.getById(toStateId));

        if(transition==null){
            return validationResult;
        }
        List<WorkflowValidation> validationsToRun = workflowValidationService.getActiveValidationsByObject(object.toUpperCase());
        if(!validationsToRun.isEmpty()){



            List<WarningMessage> wfValidationMessages = new ArrayList<>();
            EntityManagerFactoryInfo info = (EntityManagerFactoryInfo) entityManager.getEntityManagerFactory();
            try(Connection conn=info.getDataSource().getConnection()) {
                for (WorkflowValidation validation : validationsToRun) {
                    String query = null;
                    if(object.equalsIgnoreCase("GRANT")){
                        query = validation.getValidationQuery().replaceAll("%grantId%",String.valueOf(objectId));
                    }else if(object.equalsIgnoreCase("REPORT")){
                        query = validation.getValidationQuery().replaceAll("%reportId%",String.valueOf(objectId));
                    }else if(object.equalsIgnoreCase(DISBURSEMENT)){
                        query = validation.getValidationQuery().replaceAll("%disbursementId%",String.valueOf(objectId));
                    }

                    try(PreparedStatement ps = conn.prepareStatement(query)){
                        ResultSet result = ps.executeQuery();
                        ResultSetMetaData rsMetaData = result.getMetaData();
                        while (result.next()){
                            if(result.getBoolean(1)){


                                String msg = validation.getMessage();
                                for(int i=1;i<=rsMetaData.getColumnCount();i++){
                                    msg = msg.replaceAll("%"+rsMetaData.getColumnName(i)+"%",result.getString(i));
                                }
                                wfValidationMessages.add(new WarningMessage(validation.getType(),msg));
                            }
                        }
                    }
                }
                boolean canMove = transition.getAllowTransitionOnValidationWarning() == null || transition.getAllowTransitionOnValidationWarning();
                if(!canMove && !wfValidationMessages.stream().filter(m -> m.getType().equalsIgnoreCase("warn")).collect(Collectors.toList()).isEmpty()){
                    canMove = false;
                }else if(!canMove && wfValidationMessages.stream().filter(m -> m.getType().equalsIgnoreCase("warn")).collect(Collectors.toList()).isEmpty()){
                    canMove = true;
                }
                validationResult.setCanMove(canMove);
                validationResult.setMessages(wfValidationMessages);

            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
        }

        return validationResult;
    }
}
