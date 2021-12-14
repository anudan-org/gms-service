package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.services.*;
import org.codealpha.gmsservice.validators.GrantValidator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/user/{userId}/grant")
public class GrantController {

    private static final Logger logger = LoggerFactory.getLogger(GrantController.class);
    public static final String GRANT = "GRANT";
    public static final String TABLE = "table";
    public static final String DISBURSEMENT = "disbursement";
    public static final String DD_MMM_YYYY = "dd-MMM-yyyy";
    public static final String GRANT_DOCUMENTS = "/grant-documents/";
    public static final String CLOSED = "CLOSED";
    public static final String ACTIVE = "ACTIVE";
    public static final String FILE = "file:";
    public static final String ATTACHMENT_FILENAME_TEST_ZIP = "attachment; filename=\"test.zip\"";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String PROJECT = "PROJECT";
    public static final String REPORT = "REPORT";
    public static final String FILE_SEPARATOR = "/";
    public static final String DOCUMENT = "document";
    public static final String RELEASE_VERSION = "%RELEASE_VERSION%";
    public static final String APPLICATION_ZIP = "application/zip";
    public static final String TENANT_ID = "%tenantId%";
    public static final String STRING_AGG = "string_agg";
    public static final String EMAIL = "&email=";
    public static final String TYPE_GRANT = "&type=grant";
    public static final String LIBRARY = "library";
    public static final String CLOSURE = "closure";

    @Autowired
    DataSource dataSource;
    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private WorkflowStatusTransitionService workflowStatusTransitionService;

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private GranteeService granteeService;
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private GranterGrantTemplateService granterGrantTemplateService;
    @Autowired
    private TemplateLibraryService templateLibraryService;
    @Autowired
    private GrantSnapshotService grantSnapshotService;
    @Autowired
    private GrantValidator grantValidator;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private OrgTagService orgTagService;

    @Autowired
    private ReportService reportService;


    @Value("${spring.upload-file-location}")
    private String uploadLocation;
    @Value("${spring.preview-file-location}")
    private String previewLocation;
    @Value("${spring.supported-file-types}")
    private String[] supportedFileTypes;
    @Value("${spring.timezone}")
    private String timezone;
    @Autowired
    private GrantClosureService grantClosureService;

    @Autowired
    private CommonEmailSevice commonEmailSevice;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private DisbursementService disbursementService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DataExportConfigService exportConfigService;
    @Autowired
    private WorkflowValidationService workflowValidationService;

    @GetMapping("/create/{templateId}/{grantTypeId}")
    @ApiOperation("Create new grant with a template")
    public Grant createGrant(
            @ApiParam(name = "templateId", value = "Unique identifier for the selected template") @PathVariable("templateId") Long templateId, @PathVariable("grantTypeId") Long grantTypeId,
            @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        Grant grant = new Grant();

        grant = createGrantBasicDetails(templateId, userId, tenantCode, grant, "", grantTypeId);

        createInitialAssignmentsPlaceholders(userId, grant);

        GranterGrantTemplate grantTemplate = granterGrantTemplateService.findByTemplateId(templateId);

        List<GrantSpecificSection> grantSpecificSections = new ArrayList<>();
        List<GranterGrantSection> granterGrantSections = grantTemplate.getSections();
        grant.setStringAttributes(new ArrayList<>());
        for (GranterGrantSection grantSection : granterGrantSections) {
            GrantSpecificSection specificSection = new GrantSpecificSection();
            specificSection.setDeletable(true);
            specificSection.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
            specificSection.setGrantTemplateId(grantTemplate.getId());
            specificSection.setSectionName(grantSection.getSectionName());
            specificSection.setSectionOrder(grantSection.getSectionOrder());
            specificSection.setGrantId(grant.getId());

            specificSection = grantService.saveSection(specificSection);
            for (GranterGrantSectionAttribute sectionAttribute : grantSection.getAttributes()) {
                GrantSpecificSectionAttribute specificSectionAttribute = new GrantSpecificSectionAttribute();
                specificSectionAttribute.setDeletable(true);
                specificSectionAttribute.setFieldName(sectionAttribute.getFieldName());
                specificSectionAttribute.setFieldType(sectionAttribute.getFieldType());
                specificSectionAttribute
                        .setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
                specificSectionAttribute.setRequired(false);
                specificSectionAttribute.setExtras(sectionAttribute.getExtras());
                specificSectionAttribute.setAttributeOrder(sectionAttribute.getAttributeOrder());
                specificSectionAttribute.setSection(specificSection);
                specificSectionAttribute = grantService.saveSectionAttribute(specificSectionAttribute);

                GrantStringAttribute stringAttribute = new GrantStringAttribute();

                stringAttribute.setSection(specificSection);
                stringAttribute.setGrant(grant);
                stringAttribute.setSectionAttribute(specificSectionAttribute);
                if ((specificSectionAttribute.getFieldType().equalsIgnoreCase(TABLE)
                        || specificSectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT))
                        && specificSectionAttribute.getExtras() != null && !"".equalsIgnoreCase(specificSectionAttribute.getExtras())) {
                    stringAttribute.setValue(sectionAttribute.getExtras());
                } else {
                    stringAttribute.setValue("");
                }
                stringAttribute.setFrequency("");
                stringAttribute.setTarget("");

                stringAttribute = grantService.saveStringAttribute(stringAttribute);
                grant.getStringAttributes().add(stringAttribute);
            }
            specificSection = grantService.saveSection(specificSection);
            grantSpecificSections.add(specificSection);
        }

        grant = grantService.grantToReturn(userId, grant);
        return grant;
    }

    private void createInitialAssignmentsPlaceholders(Long userId, Grant grant) {
        GrantAssignments assignment;
        List<WorkflowStatus> statuses = new ArrayList<>();
        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionService
                .getStatusTransitionsForWorkflow(
                        workflowService.findWorkflowByGrantTypeAndObject(grant.getGrantTypeId(), WorkflowObject.GRANT.name()));
        for (WorkflowStatusTransition supportedTransition : supportedTransitions) {
            if (statuses.stream()
                    .noneMatch(s -> s.getId().longValue() == supportedTransition.getFromState().getId().longValue())) {
                statuses.add(supportedTransition.getFromState());
            }
            if (statuses.stream()
                    .noneMatch(s -> s.getId().longValue() == supportedTransition.getToState().getId().longValue())) {
                statuses.add(supportedTransition.getToState());
            }
        }

        for (WorkflowStatus status : statuses) {

            assignment = new GrantAssignments();
            if (status.isInitial()) {
                assignment.setAnchor(true);
                assignment.setAssignments(userId);
            } else {
                assignment.setAnchor(false);
            }
            assignment.setGrant(grant);
            assignment.setStateId(status.getId());
            grantService.saveAssignmentForGrant(assignment);

        }
    }

    private Grant createGrantBasicDetails(Long templateId, Long userId, String tenantCode, Grant grant,
                                          String grantName, Long grantTypeId) {
        grant.setName(grantName);
        grant.setStartDate(null);
        grant.setStDate("");
        grant.setDescription("");
        grant.setGrantStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId(GRANT,
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), grantTypeId));
        grant.setStatusName(GrantStatus.DRAFT);
        grant.setEndDate(null);
        grant.setEnDate("");
        grant.setOrganization(null);
        grant.setCreatedAt(new Date());
        grant.setCreatedBy(userService.getUserById(userId).getEmailId());
        grant.setGrantorOrganization((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
        grant.setRepresentative("");
        grant.setTemplateId(templateId);
        grant.setGrantTypeId(grantTypeId);
        grant.setDeleted(false);
        grant.setGrantTemplate(granterGrantTemplateService.findByTemplateId(templateId));

        grant = grantService.saveGrant(grant);
        return grant;
    }

    private Grant copyGrantBasicDetails(Long templateId, Long userId, String tenantCode, Grant grant,
                                        Grant originalGrant) {
        grant.setName(originalGrant.getName());
        grant.setStartDate(originalGrant.getStartDate());
        grant.setStDate(originalGrant.getStDate());
        grant.setAmount(originalGrant.getAmount());
        grant.setDescription(originalGrant.getDescription());
        grant.setGrantStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId(GRANT,
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), originalGrant.getGrantTypeId()));
        grant.setStatusName(GrantStatus.DRAFT);
        grant.setEndDate(originalGrant.getEndDate());
        grant.setEnDate(originalGrant.getEnDate());
        grant.setOrganization(originalGrant.getOrganization());
        grant.setCreatedAt(new Date());
        grant.setCreatedBy(userService.getUserById(userId).getEmailId());
        grant.setGrantorOrganization((Granter) originalGrant.getGrantorOrganization());
        grant.setRepresentative(originalGrant.getRepresentative());
        grant.setTemplateId(templateId);
        grant.setDeleted(false);
        grant.setGrantTypeId(originalGrant.getGrantTypeId());
        grant.setGrantTemplate(granterGrantTemplateService.findByTemplateId(templateId));
        grant.setOrigGrantId(originalGrant.getId());

        grant = grantService.saveGrant(grant);
        return grant;
    }

    @GetMapping("/{grantId}/copy/{grantTypeId}")
    @ApiOperation("Create copy of an existing grant")
    public Grant copyGrant(
            @ApiParam(name = "grantId", value = "Unique identifier for the selected grant") @PathVariable("grantId") Long grantId,
            @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("grantTypeId") Long grantTypeId) {

        Grant existingGrant = grantService.getById(grantId);
        Grant grant = new Grant();

        grant = createGrantBasicDetails(existingGrant.getTemplateId(), userId, tenantCode, grant,
                "< New Draft Grant based on " + existingGrant.getName() + " >", grantTypeId);


        createInitialAssignmentsPlaceholders(userId, grant);

        granterGrantTemplateService
                .findByTemplateId(existingGrant.getTemplateId());


        for (GrantSpecificSection sec : grantService.getGrantSections(existingGrant)) {
            GrantSpecificSection section = new GrantSpecificSection();
            section.setSectionOrder(sec.getSectionOrder());
            section.setGrantTemplateId(existingGrant.getTemplateId());
            section.setGrantId(grant.getId());
            section.setDeletable(sec.getDeletable());
            section.setSectionName(sec.getSectionName());
            section.setGranter(sec.getGranter());

            section = grantService.saveSection(section);

            List<GrantSpecificSectionAttribute> attrs = grantService.getAttributesBySection(sec);
            if (attrs != null) {
                for (GrantSpecificSectionAttribute attr : attrs) {
                    GrantSpecificSectionAttribute attribute = new GrantSpecificSectionAttribute();
                    attribute.setAttributeOrder(attr.getAttributeOrder());
                    attribute.setExtras(attr.getExtras());
                    attribute.setFieldType(attr.getFieldType());
                    attribute.setFieldName(attr.getFieldName());
                    attribute.setGranter((Granter) attr.getGranter());
                    attribute.setDeletable(attr.getDeletable());
                    attribute.setRequired(attr.getRequired());
                    attribute.setSection(section);
                    attribute = grantService.saveSectionAttribute(attribute);

                    List<GrantStringAttribute> stringAttrs = grantService.getStringAttributesByAttribute(attr);
                    if (stringAttrs != null) {
                        for (GrantStringAttribute stringAttr : stringAttrs) {
                            GrantStringAttribute stringAttrubute = new GrantStringAttribute();
                            stringAttrubute.setTarget(stringAttr.getTarget());
                            stringAttrubute.setFrequency(stringAttr.getFrequency());
                            stringAttrubute.setSection(section);
                            stringAttrubute.setGrant(grant);
                            stringAttrubute.setSectionAttribute(attribute);
                            stringAttrubute = grantService.saveStringAttribute(stringAttrubute);

                            List<GrantStringAttributeAttachments> attmnts = stringAttr.getAttachments();
                            List<GrantStringAttributeAttachments> allAttachments = new ArrayList<>();

                            if (attmnts != null) {
                                for (GrantStringAttributeAttachments attmnt : attmnts) {
                                    GrantStringAttributeAttachments attachment = new GrantStringAttributeAttachments();
                                    attachment.setName(attmnt.getName());
                                    attachment.setCreatedBy(userService.getUserById(userId).getEmailId());
                                    attachment.setCreatedOn(DateTime.now().toDate());
                                    attachment.setDescription(attmnt.getDescription());
                                    attachment.setGrantStringAttribute(stringAttrubute);
                                    String filePath = uploadLocation + tenantCode + GRANT_DOCUMENTS + grant.getId()
                                            + FILE_SEPARATOR + stringAttrubute.getSection().getId() + FILE_SEPARATOR
                                            + stringAttrubute.getSectionAttribute().getId() + FILE_SEPARATOR;
                                    attachment.setLocation(filePath);
                                    attachment.setTitle(attmnt.getTitle());
                                    attachment.setType(attmnt.getType());
                                    attachment.setVersion(attmnt.getVersion());
                                    attachment = grantService.saveGrantStringAttributeAttachment(attachment);
                                    allAttachments.add(attachment);

                                    try {
                                        File fileExisting = resourceLoader.getResource(FILE + attmnt.getLocation()
                                                + attachment.getName() + "." + attachment.getType()).getFile();
                                        File dir = new File(filePath);
                                        dir.mkdirs();
                                        File fileToCreate = new File(dir,
                                                attachment.getName() + "." + attachment.getType());
                                        FileCopyUtils.copy(fileExisting, fileToCreate);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            try {
                                if (stringAttrubute.getSectionAttribute().getFieldType().equalsIgnoreCase(DOCUMENT)) {
                                    stringAttrubute.setValue(new ObjectMapper().writeValueAsString(allAttachments));
                                } else if (stringAttrubute.getSectionAttribute().getFieldType()
                                        .equalsIgnoreCase(DISBURSEMENT)) {
                                    String[] colHeaders = new String[]{"Date/Period", "Amount",
                                            "Funds from other Sources", "Notes"};
                                    List<TableData> tableDataList = new ArrayList<>();
                                    TableData tableData = new TableData();
                                    tableData.setName("1");
                                    tableData.setHeader("Planned Installment #");
                                    tableData.setEnteredByGrantee(false);
                                    tableData.setColumns(new ColumnData[4]);
                                    for (int i = 0; i < tableData.getColumns().length; i++) {

                                        tableData.getColumns()[i] = new ColumnData(colHeaders[i], "",
                                                (i == 1 || i == 2) ? "currency" : null);
                                    }
                                    tableDataList.add(tableData);

                                    ObjectMapper mapper = new ObjectMapper();
                                    setStringAttribute(stringAttrubute, tableDataList, mapper);
                                } else {
                                    stringAttrubute.setValue(stringAttr.getValue());
                                }
                                grantService.saveStringAttribute(stringAttrubute);

                            } catch (JsonProcessingException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }

        grant = grantService.grantToReturn(userId, grant);
        return grant;

    }

    private void setStringAttribute(GrantStringAttribute stringAttrubute, List<TableData> tableDataList, ObjectMapper mapper) {
        try {
            stringAttrubute.setValue(mapper.writeValueAsString(tableDataList));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @GetMapping("/{grantId}/amend")
    @ApiOperation("Create amendment of an existing grant")
    public Grant amendGrant(
            @ApiParam(name = "grantId", value = "Unique identifier for the selected grant") @PathVariable("grantId") Long grantId,
            @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        Grant existingGrant = grantService.getById(grantId);
        Grant grant = new Grant();


        grant = copyGrantBasicDetails(existingGrant.getTemplateId(), userId, tenantCode, grant, existingGrant);

        organizationService.findOrganizationByTenantCode(tenantCode);
        createInitialAssignmentsPlaceholders(userId, grant);

        List<GrantStringAttribute> stringAttrsToSave = new ArrayList<>();

        for (GrantSpecificSection sec : grantService.getGrantSections(existingGrant)) {
            GrantSpecificSection section = new GrantSpecificSection();
            section.setSectionOrder(sec.getSectionOrder());
            section.setGrantTemplateId(existingGrant.getTemplateId());
            section.setGrantId(grant.getId());
            section.setDeletable(sec.getDeletable());
            section.setSectionName(sec.getSectionName());
            section.setGranter(sec.getGranter());

            section = grantService.saveSection(section);

            List<GrantSpecificSectionAttribute> attrs = grantService.getAttributesBySection(sec);
            if (attrs != null) {
                for (GrantSpecificSectionAttribute attr : attrs) {
                    GrantSpecificSectionAttribute attribute = new GrantSpecificSectionAttribute();
                    attribute.setAttributeOrder(attr.getAttributeOrder());
                    attribute.setExtras(attr.getExtras());
                    attribute.setFieldType(attr.getFieldType());
                    attribute.setFieldName(attr.getFieldName());
                    attribute.setGranter((Granter) attr.getGranter());
                    attribute.setDeletable(attr.getDeletable());
                    attribute.setRequired(attr.getRequired());
                    attribute.setSection(section);
                    attribute = grantService.saveSectionAttribute(attribute);


                    List<GrantStringAttribute> stringAttrs = grantService.getStringAttributesByAttribute(attr);
                    if (stringAttrs != null) {
                        for (GrantStringAttribute stringAttr : stringAttrs) {
                            GrantStringAttribute stringAttrubute = new GrantStringAttribute();
                            stringAttrubute.setTarget(stringAttr.getTarget());
                            stringAttrubute.setFrequency(stringAttr.getFrequency());
                            stringAttrubute.setSection(section);
                            stringAttrubute.setGrant(grant);
                            stringAttrubute.setSectionAttribute(attribute);
                            stringAttrubute = grantService.saveStringAttribute(stringAttrubute);
                            stringAttrsToSave.add(stringAttrubute);

                            List<GrantStringAttributeAttachments> attmnts = stringAttr.getAttachments();
                            List<GrantStringAttributeAttachments> allAttachments = new ArrayList<>();

                            if (attmnts != null) {
                                for (GrantStringAttributeAttachments attmnt : attmnts) {
                                    GrantStringAttributeAttachments attachment = new GrantStringAttributeAttachments();
                                    attachment.setName(attmnt.getName());
                                    attachment.setCreatedBy(userService.getUserById(userId).getEmailId());
                                    attachment.setCreatedOn(DateTime.now().toDate());
                                    attachment.setDescription(attmnt.getDescription());
                                    attachment.setGrantStringAttribute(stringAttrubute);
                                    String filePath = uploadLocation + tenantCode + GRANT_DOCUMENTS + grant.getId()
                                            + FILE_SEPARATOR + stringAttrubute.getSection().getId() + FILE_SEPARATOR
                                            + stringAttrubute.getSectionAttribute().getId() + FILE_SEPARATOR;
                                    attachment.setLocation(filePath);
                                    attachment.setTitle(attmnt.getTitle());
                                    attachment.setType(attmnt.getType());
                                    attachment.setVersion(attmnt.getVersion());
                                    attachment = grantService.saveGrantStringAttributeAttachment(attachment);
                                    allAttachments.add(attachment);

                                    try {
                                        File fileExisting = resourceLoader.getResource(FILE + attmnt.getLocation()
                                                + attachment.getName() + "." + attachment.getType()).getFile();
                                        File dir = new File(filePath);
                                        dir.mkdirs();
                                        File fileToCreate = new File(dir,
                                                attachment.getName() + "." + attachment.getType());
                                        FileCopyUtils.copy(fileExisting, fileToCreate);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            try {
                                if (stringAttrubute.getSectionAttribute().getFieldType().equalsIgnoreCase(DOCUMENT)) {
                                    stringAttrubute.setValue(new ObjectMapper().writeValueAsString(allAttachments));
                                } else {
                                    stringAttrubute.setValue(stringAttr.getValue());
                                }
                                grantService.saveStringAttribute(stringAttrubute);

                            } catch (JsonProcessingException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }

        existingGrant.setAmendGrantId(grant.getId());
        existingGrant.setAmended(true);
        grantService.saveGrant(existingGrant);

        grant.setStringAttributes(stringAttrsToSave);
        GrantVO grantVO = new GrantVO().build(grant, grantService.getGrantSections(grant), workflowPermissionService, userService.getUserById(userId),
                userService, grantService);
        grant.setGrantDetails(grantVO.getGrantDetails());
        grant = grantService.grantToReturn(userId, grant);

        try {
            grant.setAmendmentDetailsSnapshot(new ObjectMapper().writeValueAsString(grant.getGrantDetails()));
            grant = grantService.saveGrant(grant);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<WorkflowStatus> wfStatuses = workflowStatusService.findByWorkflow(existingGrant.getGrantStatus().getWorkflow());
        wfStatuses.removeIf(st -> st.getId().longValue() != existingGrant.getGrantStatus().getId().longValue());
        WorkflowStatus activeStatus = wfStatuses.get(0);

        User prevOwner = null;
        Optional<GrantAssignments> staeOwnerPrevTemp = grantService.getGrantCurrentAssignments(existingGrant).stream().filter(g -> g.getStateId().longValue() == activeStatus.getId().longValue()).findFirst();
        if (staeOwnerPrevTemp.isPresent()) {
            prevOwner = userService.getUserById(staeOwnerPrevTemp.get().getAssignments());
        }
        User currentOwner = userService.getUserById(userId);
        List<User> tenantUsers = userService.getAllTenantUsers(organizationService.findOrganizationByTenantCode(tenantCode));
        List<User> admins = tenantUsers.stream().filter(u ->
                u.getUserRoles().stream().anyMatch(r -> r.getRole().getName().equalsIgnoreCase("ADMIN"))).collect(Collectors.toList());

        List<User> ccList = new ArrayList<>();
        ccList.addAll(admins);
        ccList.removeIf(u -> u.getId().longValue() == currentOwner.getId());
        ccList.add(currentOwner);

        User user = userService.getUserById(userId);
        String[] emailNotificationContent = grantService.buildEmailNotificationContent(existingGrant, user,
                appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                        AppConfiguration.AMENDMENT_INIT_MAIL_SUBJECT).getConfigValue(),
                appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                        AppConfiguration.AMENDMENT_INIT_MAIL_MESSAGE).getConfigValue(),
                null,
                currentOwner.getFirstName().concat(" ").concat(currentOwner.getLastName()), null,
                null,
                null, "", "",
                "",
                "",
                "", null, null, null, null);


        commonEmailSevice
                .sendMail((prevOwner != null && !prevOwner.isDeleted()) ? new String[]{prevOwner.getEmailId()} : null,
                        ccList.stream().map(User::getEmailId).collect(Collectors.toList())
                                .toArray(new String[ccList.size()]),
                        emailNotificationContent[0], emailNotificationContent[1],
                        new String[]{appConfigService
                                .getAppConfigForGranterOrg(existingGrant.getGrantorOrganization().getId(),
                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                .getConfigValue().replace(RELEASE_VERSION,
                                releaseService.getCurrentRelease().getVersion())});

        final Grant finalGrant = existingGrant;
        User finalPrevOwner = prevOwner;
        ccList.removeIf(u -> u.getId().longValue() == finalPrevOwner.getId().longValue());
        ccList.add(prevOwner);
        ccList.stream().forEach(u -> notificationsService.saveNotification(emailNotificationContent, u.getId(),
                finalGrant.getId(), GRANT));

        return grant;

    }

    @DeleteMapping("/{grantId}")
    @ApiOperation("Delete grant")
    public void deleteGrant(
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code ") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        Grant grant = grantService.getById(grantId);
        if (grantService.checkIfGrantMovedThroughWFAtleastOnce(grantId)) {
            grant.setDeleted(true);
            grant.setNote("No note added.<br><i><b>System Note</b>: </i>Grant deleted after reviews.");
            grant.setNoteAdded(DateTime.now().toDate());
            grant.setNoteAddedBy(userService.getUserById(userId).getEmailId());
            grantService.saveGrant(grant);
        } else {
            for (GrantSpecificSection section : grantService.getGrantSections(grant)) {
                List<GrantSpecificSectionAttribute> attribs = grantService.getAttributesBySection(section);
                for (GrantSpecificSectionAttribute attribute : attribs) {
                    List<GrantStringAttribute> strAttribs = grantService.getStringAttributesByAttribute(attribute);
                    grantService.deleteStringAttributes(strAttribs);
                }
                grantService.deleteSectionAttributes(attribs);
                grantService.deleteSection(section);
            }
            if (grant.getOrigGrantId() != null) {
                Grant origGrant = grantService.getById(grant.getOrigGrantId());
                origGrant.setAmendGrantId(null);
                origGrant.setAmended(false);
                grantService.saveGrant(origGrant);
            }
            grantService.deleteGrant(grant);
            GranterGrantTemplate template = granterGrantTemplateService.findByTemplateId(grant.getTemplateId());
            if (!template.isPublished()) {
                grantService.deleteGrantTemplate(template);
            }
        }

    }

    @PostMapping("/{grantId}/section/{sectionId}/field")
    @ApiOperation("Added new field to section")
    public FieldInfo createFieldInSection(
            @ApiParam(name = "grantToSave", value = "Grant to save if in edit mode passed in Body of request") @RequestBody Grant grantToSave,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "sectionId", value = "Unique identifier of the section to which the field is being added") @PathVariable("sectionId") Long sectionId,
            @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        grantValidator.validate(grantService, grantId, grantToSave, userId, tenantCode);
        grantValidator.validateSectionExists(grantService, grantToSave, sectionId);
        saveGrant(grantId, grantToSave, userId, tenantCode);
        Grant grant = grantService.getById(grantId);
        GrantSpecificSection grantSection = grantService.getGrantSectionBySectionId(sectionId);

        GrantSpecificSectionAttribute newSectionAttribute = new GrantSpecificSectionAttribute();
        newSectionAttribute.setSection(grantSection);
        newSectionAttribute.setRequired(false);
        newSectionAttribute.setFieldType("multiline");
        newSectionAttribute.setFieldName("");
        newSectionAttribute.setDeletable(true);
        newSectionAttribute.setAttributeOrder(grantService.getNextAttributeOrder(
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), sectionId));
        newSectionAttribute.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
        newSectionAttribute = grantService.saveSectionAttribute(newSectionAttribute);
        GrantStringAttribute stringAttribute = new GrantStringAttribute();
        stringAttribute.setValue("");
        stringAttribute.setSectionAttribute(newSectionAttribute);
        stringAttribute.setSection(grantSection);
        stringAttribute.setTarget("");
        stringAttribute.setFrequency("");
        stringAttribute.setGrant(grant);

        stringAttribute = grantService.saveStringAttribute(stringAttribute);
        grant.getStringAttributes().add(stringAttribute);
        grant = grantService.saveGrant(grant);
        if (checkIfGrantTemplateChanged(grant, grantSection, newSectionAttribute)) {
            createNewGrantTemplateFromExisiting(grant);
        }

        grant = grantService.grantToReturn(userId, grant);
        return new FieldInfo(newSectionAttribute != null ? newSectionAttribute.getId() : 0L, stringAttribute.getId(), grant);
    }

    @PostMapping("/{grantId}/section/{sectionId}/field/{fieldId}")
    @ApiOperation("Delete field in a section")
    public Grant deleteField(
            @ApiParam(name = "grantToSave", value = "Grant to save if in edit mode, passed in Body of request") @RequestBody Grant grantToSave,
            @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "sectionId", value = "Unique identifier of the section being modified") @PathVariable("sectionId") Long sectionId,
            @ApiParam(name = "fieldId", value = "Unique identifier of the field being deleted") @PathVariable("fieldId") Long fieldId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        grantValidator.validate(grantService, grantId, grantToSave, userId, tenantCode);
        grantValidator.validateSectionExists(grantService, grantToSave, sectionId);
        grantValidator.validateFieldExists(grantService, grantToSave, sectionId, fieldId);
        Grant grant = saveGrant(grantId, grantToSave, userId, tenantCode);
        GrantSpecificSectionAttribute attribute = grantService.findGrantStringAttributeById(fieldId)
                .getSectionAttribute();

        GrantStringAttribute stringAttrib = grantService.findGrantStringAttributeById(fieldId);

        if (stringAttrib.getSectionAttribute().getFieldType().equalsIgnoreCase(DOCUMENT)) {
            List<GrantStringAttributeAttachments> attachments = grantService
                    .getStringAttributeAttachmentsByStringAttribute(stringAttrib);
            grantService.deleteStringAttributeAttachments(attachments);
        }
        grantService.deleteStringAttribute(stringAttrib);
        grantService.deleteAtttribute(attribute);
        Optional<GrantStringAttribute> first = grant.getStringAttributes().stream()
                .filter(g -> g.getId().longValue() == stringAttrib.getId().longValue()).findFirst();
        GrantStringAttribute gsa2Delete = first.isPresent() ? first.get() : null;
        grant.getStringAttributes().remove(gsa2Delete);
        grant = grantService.saveGrant(grant);

        if (checkIfGrantTemplateChanged(grant, attribute.getSection(), null)) {
            createNewGrantTemplateFromExisiting(grant);
        }
        grant = grantService.grantToReturn(userService.getUserById(userId).getId(), grant);
        return grant;
    }


    @ApiOperation("Update field information")
    @PutMapping("/{grantId}/section/{sectionId}/field/{fieldId}")
    public FieldInfo updateField(
            @ApiParam(name = "sectionId", value = "Unique identifier of section") @PathVariable("sectionId") Long sectionId,
            @ApiParam(name = "attributeToSave", value = "Updated attribute to be saved") @RequestBody AttributeToSaveVO attributeToSave,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "fieldId", value = "Unique identifier of the field being updated") @PathVariable("fieldId") Long fieldId,
            @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        grantValidator.validate(grantService, grantId, attributeToSave.getGrant(), userId, tenantCode);
        grantValidator.validateSectionExists(grantService, attributeToSave.getGrant(), sectionId);
        grantValidator.validateFieldExists(grantService, attributeToSave.getGrant(), sectionId, fieldId);
        saveGrant(grantId, attributeToSave.getGrant(), userId, tenantCode);
        GrantSpecificSectionAttribute currentAttribute = grantService.findGrantStringAttributeById(fieldId)
                .getSectionAttribute();
        currentAttribute.setFieldName(attributeToSave.getAttr().getFieldName());
        currentAttribute.setFieldType(attributeToSave.getAttr().getFieldType());
        currentAttribute = grantService.saveSectionAttribute(currentAttribute);
        GrantStringAttribute stringAttribute = grantService.findGrantStringBySectionIdAttribueIdAndGrantId(
                currentAttribute.getSection().getId(), currentAttribute.getId(), grantId);
        stringAttribute = grantService.saveStringAttribute(stringAttribute);

        Grant grant = grantService.getById(grantId);
        if (checkIfGrantTemplateChanged(grant, currentAttribute.getSection(), currentAttribute)) {
            createNewGrantTemplateFromExisiting(grant);
        }

        grant = grantService.grantToReturn(userId, grant);
        return new FieldInfo(currentAttribute.getId(), stringAttribute.getId(), grant);
    }

    @PostMapping("/{id}/template/{templateId}/section/{sectionName}")
    @ApiOperation("Create new section in grant")
    public SectionInfo createSection(@RequestBody Grant grantToSave,
                                     @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("id") Long grantId,
                                     @ApiParam(name = "temaplteId", value = "Unique identifier of the grant template") @PathVariable("templateId") Long templateId,
                                     @ApiParam(name = "sectionName", value = "Name of the new section") @PathVariable("sectionName") String sectionName,
                                     @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
                                     @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        grantValidator.validate(grantService, grantId, grantToSave, userId, tenantCode);
        grantValidator.validateTemplateExists(grantService, grantToSave, templateId);
        saveGrant(grantId, grantToSave, userId, tenantCode);
        Grant grant = grantService.getById(grantId);

        GrantSpecificSection specificSection = new GrantSpecificSection();
        specificSection.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
        specificSection.setSectionName(sectionName);

        specificSection.setGrantTemplateId(templateId);
        specificSection.setDeletable(true);
        specificSection.setGrantId(grantId);
        specificSection.setSectionOrder(grantService
                .getNextSectionOrder(organizationService.findOrganizationByTenantCode(tenantCode).getId(), templateId));
        specificSection = grantService.saveSection(specificSection);

        if (checkIfGrantTemplateChanged(grant, specificSection, null)) {
            createNewGrantTemplateFromExisiting(grant);
        }

        grant = grantService.grantToReturn(userId, grant);
        return new SectionInfo(specificSection.getId(), specificSection.getSectionName(), grant);

    }

    @PutMapping("/{id}/template/{templateId}/section/{sectionId}")
    @ApiOperation("Delete existing section in grant")
    public Grant deleteSection(@RequestBody Grant grantToSave,
                               @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("id") Long grantId,
                               @ApiParam(name = "templateId", value = "Unique identifier of the grant template") @PathVariable("templateId") Long templateId,
                               @ApiParam(name = "sectionId", value = "Unique identifier of the section being deleted") @PathVariable("sectionId") Long sectionId,
                               @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
                               @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        grantValidator.validate(grantService, grantId, grantToSave, userId, tenantCode);
        grantValidator.validateTemplateExists(grantService, grantToSave, templateId);
        grantValidator.validateSectionExists(grantService, grantToSave, sectionId);
        GrantSpecificSection section = grantService.getGrantSectionBySectionId(sectionId);
        Grant grant = grantService.getById(grantId);

        for (GrantSpecificSectionAttribute attrib : grantService.getAttributesBySection(section)) {
            for (GrantStringAttribute stringAttrib : grantService.getStringAttributesByAttribute(attrib)) {
                if (stringAttrib != null) {
                    grantService.deleteStringAttribute(stringAttrib);
                }
            }
        }
        grantService.deleteSectionAttributes(grantService.getAttributesBySection(section));
        grantService.deleteSection(section);

        if (checkIfGrantTemplateChanged(grant, section, null)) {
            createNewGrantTemplateFromExisiting(grant);
        }
        grant = grantService.grantToReturn(userId, grant);
        return grant;
    }

    private GranterGrantTemplate createNewGrantTemplateFromExisiting(Grant grant) {
        GranterGrantTemplate currentGrantTemplate = granterGrantTemplateService.findByTemplateId(grant.getTemplateId());
        GranterGrantTemplate newTemplate = new GranterGrantTemplate();
        if (!currentGrantTemplate.isPublished()) {
            grantService.deleteGrantTemplate(currentGrantTemplate);
        }
        newTemplate.setName("Custom Template");
        newTemplate.setGranterId(grant.getGrantorOrganization().getId());
        newTemplate.setPublished(false);
        newTemplate = grantService.saveGrantTemplate(newTemplate);

        List<GranterGrantSection> newSections = new ArrayList<>();
        for (GrantSpecificSection currentSection : grantService.getGrantSections(grant)) {
            GranterGrantSection newSection = new GranterGrantSection();
            newSection.setSectionOrder(currentSection.getSectionOrder());
            newSection.setSectionName(currentSection.getSectionName());
            newSection.setGrantTemplate(newTemplate);
            newSection.setGranter((Granter) grant.getGrantorOrganization());
            newSection.setDeletable(currentSection.getDeletable());

            newSection = grantService.saveGrantTemaplteSection(newSection);
            newSections.add(newSection);

            currentSection.setGrantTemplateId(newTemplate.getId());
            currentSection = grantService.saveSection(currentSection);

            for (GrantSpecificSectionAttribute currentAttribute : grantService.getAttributesBySection(currentSection)) {
                GranterGrantSectionAttribute newAttribute = new GranterGrantSectionAttribute();
                newAttribute.setDeletable(currentAttribute.getDeletable());
                newAttribute.setFieldName(currentAttribute.getFieldName());
                newAttribute.setFieldType(currentAttribute.getFieldType());
                newAttribute.setGranter((Granter) currentAttribute.getGranter());
                newAttribute.setRequired(currentAttribute.getRequired());
                newAttribute.setAttributeOrder(currentAttribute.getAttributeOrder());
                newAttribute.setSection(newSection);
                if (currentAttribute.getFieldType().equalsIgnoreCase(TABLE)
                        || currentAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                    GrantStringAttribute stringAttribute = grantService.findGrantStringBySectionIdAttribueIdAndGrantId(
                            currentSection.getId(), currentAttribute.getId(), grant.getId());

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        List<TableData> tableData = mapper.readValue(stringAttribute.getValue(),
                                new TypeReference<List<TableData>>() {
                                });
                        for (TableData data : tableData) {
                            for (ColumnData columnData : data.getColumns()) {
                                columnData.setValue("");
                            }
                        }
                        newAttribute.setExtras(mapper.writeValueAsString(tableData));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                grantService.saveGrantTemaplteSectionAttribute(newAttribute);

            }
        }

        newTemplate.setSections(newSections);
        newTemplate = grantService.saveGrantTemplate(newTemplate);
        grant.setTemplateId(newTemplate.getId());
        grantService.saveGrant(grant);
        return newTemplate;
    }

    private boolean checkIfGrantTemplateChanged(Grant grant, GrantSpecificSection newSection,
                                                GrantSpecificSectionAttribute newAttribute) {
        GranterGrantTemplate currentGrantTemplate = granterGrantTemplateService.findByTemplateId(grant.getTemplateId());
        for (GranterGrantSection grantSection : currentGrantTemplate.getSections()) {
            if (!grantSection.getSectionName().equalsIgnoreCase(newSection.getSectionName())) {
                return true;
            }
            if (newAttribute != null) {
                for (GranterGrantSectionAttribute sectionAttribute : grantSection.getAttributes()) {
                    if (!sectionAttribute.getFieldName().equalsIgnoreCase(newAttribute.getFieldName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @GetMapping("/{grantId}")
    @ApiOperation("Get grant details")
    public Grant getGrant(
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId) {

        Grant grant = grantService.getById(grantId);
        grant = grantService.grantToReturn(userId, grant);

        return grant;
    }

    @PutMapping("/{grantId}")
    @ApiOperation("Save grant")
    public Grant saveGrant(
            @ApiParam(name = "grantId", value = "Unique identifier of grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "grantToSave", value = "Grant to save in edit mode, passed in Body of request") @RequestBody Grant grantToSave,
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        return grantService.saveGrant(grantId,grantToSave,userId,tenantCode);
    }

    @PostMapping("/{grantId}/flow/{fromState}/{toState}")
    @ApiOperation("Move grant through workflow")
    public Grant moveGrantState(@RequestBody GrantWithNote grantwithNote,
                                @ApiParam(name = "userId", value = "Unique identified of logged in user") @PathVariable("userId") Long userId,
                                @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
                                @ApiParam(name = "fromStateId", value = "Unique identifier of the starting state of the grant in the workflow") @PathVariable("fromState") Long fromStateId,
                                @ApiParam(name = "toStateId", value = "Unique identifier of the ending state of the grant in the workflow") @PathVariable("toState") Long toStateId,
                                @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {


        Grant grant = grantService.moveToNewState(grantwithNote, userId, grantId, fromStateId, toStateId, tenantCode);
        if (grant.getOrigGrantId() != null
                && workflowStatusService.findById(toStateId).getInternalStatus().equalsIgnoreCase(ACTIVE)) {
            Grant origGrant = grantService.getById(grant.getOrigGrantId());


            grant = grantService.saveGrant(grant);
            final Grant finalGrant = grant;
            Optional<WorkflowStatus> first = workflowStatusService
                    .getTenantWorkflowStatuses(GRANT, grant.getGrantorOrganization().getId()).stream()
                    .filter(ws -> ws.getInternalStatus().equalsIgnoreCase(CLOSED) && ws.getWorkflow().getId().longValue() == finalGrant.getGrantStatus().getWorkflow().getId().longValue()).findFirst();
            WorkflowStatus statusClosed = first.isPresent() ? first.get() : null;
            Optional<WorkflowStatus> optionalWorkflowStatus = workflowStatusService
                    .getTenantWorkflowStatuses(GRANT, grant.getGrantorOrganization().getId()).stream()
                    .filter(ws -> ws.getInternalStatus().equalsIgnoreCase(ACTIVE) && ws.getId().longValue() == toStateId).findFirst();
            WorkflowStatus statusActive = optionalWorkflowStatus.isPresent() ? optionalWorkflowStatus.get() : null;

            Optional<GrantAssignments> optionalGrantAssignments = grantService.getGrantWorkflowAssignments(origGrant).stream()
                    .filter(wa -> wa.getStateId().longValue() == statusActive.getId().longValue()).findFirst();
            Long activeStateOwnerId = optionalGrantAssignments.isPresent() ? optionalGrantAssignments.get()
                    .getAssignments() : 0;
            origGrant = grantService.grantToReturn(activeStateOwnerId, origGrant);

            GrantWithNote gn = new GrantWithNote();
            gn.setGrant(origGrant);
            gn.setNote(
                    "No note added.<br><i><b>System Note</b>: </i>Closed because of an amendment. Amended grant reference no. is "
                            + grant.getReferenceNo());
            if (statusClosed != null) {
                grantService.moveToNewState(gn, activeStateOwnerId, origGrant.getId(), origGrant.getGrantStatus().getId(),
                        statusClosed.getId(), tenantCode);
            }
            List<Report> existingReports = reportService
                    .getReportsForGrant(grantService.getById(grant.getOrigGrantId()));
            if (existingReports != null && !existingReports.isEmpty()) {


                existingReports.stream().forEach(r -> {
                    if (!r.getStatus().getInternalStatus().equalsIgnoreCase(CLOSED)) {
                        r.setDisabledByAmendment(true);
                        r.setGrant(finalGrant); //Switch over to new grant happening here
                        reportService.saveReport(r);
                    }
                });
            }

            List<Disbursement> existingDisbursements = disbursementService
                    .getAllDisbursementsForGrant(grant.getOrigGrantId());

            if (existingDisbursements != null && !existingDisbursements.isEmpty()) {

                existingDisbursements.stream().forEach(r -> {
                    if (!r.getStatus().getInternalStatus().equalsIgnoreCase(CLOSED)) {
                        r.setGrant(finalGrant); //Switch over to new grant happening here
                        r.setDisabledByAmendment(true);
                        disbursementService.saveDisbursement(r);
                    }
                });
            }


            List<GrantDocument> projectDocuments = grantService.getGrantsDocuments(grant.getOrigGrantId());
            if (projectDocuments != null && !projectDocuments.isEmpty()) {
                for (GrantDocument doc : projectDocuments) {
                    GrantDocument newDoc = new GrantDocument();
                    newDoc.setGrantId(grant.getId());
                    newDoc.setExtension(doc.getExtension());
                    newDoc.setLocation(doc.getLocation().replaceFirst(grant.getOrigGrantId().toString(), grant.getId().toString()));
                    newDoc.setName(doc.getName());
                    newDoc.setUploadedBy(doc.getUploadedBy());
                    newDoc.setUploadedOn(doc.getUploadedOn());

                    grantService.saveGrantDocument(newDoc);

                    try {
                        FileCopyUtils.copy(new File(doc.getLocation()), new File(newDoc.getLocation()));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }
        }
        return grant;

    }

    @PutMapping("/{grantId}/template/{templateId}/{templateName}")
    @ApiOperation("Save custom grachangeHistorynt template with name and description")
    public Grant updateTemplateName(
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "templateId", value = "Unique identfier of the grant template") @PathVariable("templateId") Long templateId,
            @ApiParam(name = "templateName", value = "NName of the template to be saved") @PathVariable("templateName") String templateName,
            @ApiParam(name = "templateDate", value = "Additional information about the template such as descriptio, publish or save as private") @RequestBody TemplateMetaData templateData) {

        GranterGrantTemplate template = granterGrantTemplateService.findByTemplateId(templateId);
        template.setName(templateName);
        template.setDescription(templateData.getDescription());
        template.setPublished(templateData.isPublish());
        template.setPrivateToGrant(templateData.isPrivateToGrant());
        template.setPublished(true);
        grantService.saveGrantTemplate(template);

        Grant grant = grantService.getById(grantId);
        grant = grantService.grantToReturn(userId, grant);
        return grant;

    }

    @GetMapping("/templates")
    @ApiOperation("Get all published grant templates for tenant")
    public List<GranterGrantTemplate> getTenantPublishedGrantTemplates(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {
        return granterGrantTemplateService.findByGranterIdAndPublishedStatusAndPrivateStatus(
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), true, false);
    }

    @PostMapping("/{grantId}/assignment")
    @ApiOperation("Set owners for grant workflow states")
    public Grant saveGrantAssignments(
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "assignmentModel", value = "Set assignment for grant per workflow state") @RequestBody GrantAssignmentModel assignmentModel,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        Map<Long, Long> currentAssignments = new LinkedHashMap<>();
        if (grantService.checkIfGrantMovedThroughWFAtleastOnce(grantId)) {
            grantService.getGrantWorkflowAssignments(grantService.getById(grantId)).stream().forEach(a ->
                    currentAssignments.put(a.getStateId(), a.getAssignments()));

        }
        Grant grant = saveGrant(assignmentModel.getGrant().getId(), assignmentModel.getGrant(), userId, tenantCode);

        for (GrantAssignmentsVO assignmentsVO : assignmentModel.getAssignments()) {
            if (currentAssignments != null && currentAssignments.size() > 0 && currentAssignments.get(assignmentsVO.getStateId()).longValue() == assignmentsVO.getAssignments().longValue()) {
                continue;
            }
            GrantAssignments assignment = null;
            if (assignmentsVO.getId() == null) {
                assignment = new GrantAssignments();
                assignment.setStateId(assignmentsVO.getStateId());
                assignment.setGrant(grant);
            } else {
                assignment = grantService.getGrantAssignmentById(assignmentsVO.getId());
            }

            assignment.setAssignments(assignmentsVO.getAssignments());
            assignment.setUpdatedBy(userId);
            assignment.setAssignedOn(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());

            //Change the reports anchor to the new Grant Active State Owner
            if (grant.getGrantStatus().getInternalStatus().equalsIgnoreCase(ACTIVE) && assignmentsVO.getStateId().longValue() == grant.getGrantStatus().getId().longValue()) {
                List<Report> reports = reportService.getReportsForGrant(grant);

                for (Report report : reports) {
                    List<WorkflowStatus> workflowStatusList = workflowStatusService.findByWorkflow(report.getStatus().getWorkflow()).stream().filter(st -> st.getInternalStatus().equalsIgnoreCase(CLOSED)).collect(Collectors.toList());
                    Workflow currentReportWorkflow = workflowService.findWorkflowByGrantTypeAndObject(grant.getGrantTypeId(), REPORT);
                    workflowStatusList.removeIf(wf -> wf.getWorkflow().getId().longValue() != currentReportWorkflow.getId().longValue());
                    WorkflowStatus closedStatusForReport = workflowStatusList.get(0);
                    List<ReportAssignment> reportAssignments = reportService.getAssignmentsForReport(report);
                    List<ReportAssignment> tmpAssignments = new ArrayList<>();
                    for (ReportAssignment rAssignment : reportAssignments) {
                        if (rAssignment.isAnchor() || rAssignment.getStateId().longValue() == closedStatusForReport.getId().longValue()) {
                            tmpAssignments.add(rAssignment);
                        }
                    }
                    for (ReportAssignment rAssignment : tmpAssignments) {
                        rAssignment.setAssignment(assignmentsVO.getAssignments());
                        reportService.saveAssignmentForReport(rAssignment);
                    }
                }

                List<Disbursement> disbursements = disbursementService.getAllDisbursementsForGrant(grant.getId());
                for (Disbursement disbursement : disbursements) {
                    List<WorkflowStatus> workflowStatuses = workflowStatusService.findByWorkflow(disbursement.getStatus().getWorkflow()).stream().filter(st -> st.getInternalStatus().equalsIgnoreCase(CLOSED)).collect(Collectors.toList());
                    Workflow currentWorkflow = workflowService.findWorkflowByGrantTypeAndObject(grant.getGrantTypeId(), "DISBURSEMENT");
                    workflowStatuses.removeIf(wf -> wf.getWorkflow().getId().longValue() != currentWorkflow.getId().longValue());
                    WorkflowStatus closedStatusForDisbursement = workflowStatuses.get(0);

                    List<DisbursementAssignment> disbursementAssignments = disbursementService.getDisbursementAssignments(disbursement);
                    disbursementAssignments.removeIf(d -> d.getStateId().longValue() != closedStatusForDisbursement.getId().longValue());
                    for (DisbursementAssignment disbursementAssignment : disbursementAssignments) {
                        disbursementAssignment.setOwner(assignmentsVO.getAssignments());
                        disbursementService.saveAssignmentForDisbursement(disbursementAssignment);
                    }

                }
            }

            //Change the disbursements anchor to the new Grant Active State Owner
            if (grant.getGrantStatus().getInternalStatus().equalsIgnoreCase(ACTIVE) && assignmentsVO.getStateId().longValue() == grant.getGrantStatus().getId().longValue()) {
                List<Disbursement> disbursements = disbursementService.getAllDisbursementsForGrant(grant.getId());
                disbursements.removeIf(r -> r.getStatus().getInternalStatus().equalsIgnoreCase(CLOSED));
                for (Disbursement disbursement : disbursements) {
                    List<DisbursementAssignment> disbursementAssignments = disbursementService.getDisbursementAssignments(disbursement);
                    disbursementAssignments.removeIf(ass -> !ass.getAnchor());
                    for (DisbursementAssignment dAssignment : disbursementAssignments) {
                        dAssignment.setOwner(assignmentsVO.getAssignments());
                        disbursementService.saveAssignmentForDisbursement(dAssignment);
                    }
                }
            }
            grantService.saveAssignmentForGrant(assignment);
            if (workflowStatusService.findById(assignment.getStateId()).getInternalStatus().equalsIgnoreCase(ACTIVE)) {

                Optional<WorkflowStatus> optionalWorkflowStatus = workflowStatusService.findByWorkflow(workflowStatusService.findById(assignment.getStateId()).getWorkflow()).stream().filter(s -> s.getInternalStatus().equalsIgnoreCase(CLOSED)).findFirst();
                WorkflowStatus closedStatus = optionalWorkflowStatus.isPresent() ? optionalWorkflowStatus.get() : null;
                if (closedStatus != null) {
                    Optional<GrantAssignments> first = grantService.getGrantWorkflowAssignments(grant).stream().filter(ass -> ass.getStateId().longValue() == closedStatus.getId().longValue()).findFirst();
                    GrantAssignments closedAssignmentEntry = first.isPresent() ? first.get() : null;
                    if (closedAssignmentEntry != null) {
                        closedAssignmentEntry.setAssignments(assignment.getAssignments());
                        closedAssignmentEntry.setUpdatedBy(userId);
                        closedAssignmentEntry.setAssignedOn(DateTime.now().toDate());
                        grantService.saveAssignmentForGrant(closedAssignmentEntry);
                    }
                }

            }
        }

        if (currentAssignments.size() > 0) {

            List<GrantAssignments> newAssignments = grantService.getGrantWorkflowAssignments(grant);

            User currentUser = userService.getUserById(userId);
            String[] notifications = grantService.buildEmailNotificationContent(grant, currentUser,
                    appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_MESSAGE).getConfigValue(),
                    null,currentUser.getFirstName().concat(" ").concat(currentUser.getLastName()) , null, null, null, null, null, null, null, null, null, null, currentAssignments,
                    newAssignments);
            List<User> toUsers = newAssignments.stream().map(GrantAssignments::getAssignments)
                    .map(uid -> userService.getUserById(uid)).collect(Collectors.toList());
            toUsers.removeIf(User::isDeleted);
            toUsers.removeIf(Objects::isNull);
            List<User> ccUsers = currentAssignments.values().stream().map(uid -> userService.getUserById(uid))
                    .collect(Collectors.toList());
            ccUsers.removeIf(User::isDeleted);
            ccUsers.removeIf(Objects::isNull);
            commonEmailSevice
                    .sendMail(
                            toUsers.stream().map(User::getEmailId).collect(Collectors.toList())
                                    .toArray(new String[newAssignments.size()]),
                            ccUsers.stream().map(User::getEmailId).collect(
                                    Collectors.toList()).toArray(new String[currentAssignments.size()]),
                            notifications[0], notifications[1],
                            new String[]{appConfigService
                                    .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                            AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                    .getConfigValue().replace(RELEASE_VERSION,
                                    releaseService.getCurrentRelease().getVersion())});

            Map<Long, Long> cleanAsigneesList = new HashMap<>();
            for (Long ass : currentAssignments.values()) {
                cleanAsigneesList.put(ass, ass);
            }
            for (GrantAssignments ass : newAssignments) {
                cleanAsigneesList.put(ass.getAssignments(), ass.getAssignments());
            }

            final String[] finaNotifications = grantService.buildEmailNotificationContent(grant,
                    userService.getUserById(userId),
                    appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_MESSAGE).getConfigValue(),
                    null, null, null, null, null, null, null, null, null, null, null, null, currentAssignments,
                    newAssignments);

            final Grant finalGrant = grant;

            cleanAsigneesList.keySet().stream().forEach(
                    u -> notificationsService.saveNotification(finaNotifications, u, finalGrant.getId(), GRANT));

        }

        grant = grantService.getById(grantId);
        grant = grantService.grantToReturn(userId, grant);
        return grant;
    }

    @PostMapping("/{grantId}/field/{fieldId}/template/{templateId}")
    @ApiOperation(value = "Attach document to field", notes = "Valid for Document field types only")
    public DocInfo createDocumentForGrantSectionField(
            @ApiParam(name = "grantToSave", value = "Grant to save in edit mode, passed in Body of request") @RequestBody Grant grantToSave,
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "fieldId", value = "Unique identifier of the field to which document is being attached") @PathVariable("fieldId") Long fieldId,
            @ApiParam(name = "temaplteId", value = "Unique identified of the document template being attached") @PathVariable("templateId") Long templateId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        saveGrant(grantToSave.getId(), grantToSave, userId, tenantCode);
        TemplateLibrary libraryDoc = templateLibraryService.getTemplateLibraryDocumentById(templateId);

        GrantStringAttribute stringAttribute = grantService.findGrantStringAttributeById(fieldId);

        File file = null;
        String filePath = null;
        try {
            file = resourceLoader.getResource(FILE + uploadLocation + libraryDoc.getLocation()).getFile();
            filePath = uploadLocation + tenantCode + GRANT_DOCUMENTS + grantId + FILE_SEPARATOR
                    + stringAttribute.getSection().getId() + FILE_SEPARATOR + stringAttribute.getSectionAttribute().getId() + FILE_SEPARATOR;

            File dir = new File(filePath);
            dir.mkdirs();
            File fileToCreate = new File(dir, libraryDoc.getName() + "." + libraryDoc.getType());
            FileCopyUtils.copy(file, fileToCreate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GrantStringAttributeAttachments attachment = new GrantStringAttributeAttachments();
        attachment.setCreatedBy(userService.getUserById(userId).getEmailId());
        attachment.setCreatedOn(new Date());
        attachment.setDescription(libraryDoc.getDescription());
        attachment.setGrantStringAttribute(stringAttribute);
        attachment.setLocation(filePath);
        attachment.setName(libraryDoc.getName());
        attachment.setTitle("");
        attachment.setType(libraryDoc.getType());
        attachment.setVersion(1);
        attachment = grantService.saveGrantStringAttributeAttachment(attachment);

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<GrantStringAttributeAttachments> stringAttributeAttachments = grantService
                    .getStringAttributeAttachmentsByStringAttribute(stringAttribute);
            stringAttribute.setValue(mapper.writeValueAsString(stringAttributeAttachments));
            grantService.saveGrantStringAttribute(stringAttribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Grant grant = grantService.getById(grantId);
        grant = grantService.grantToReturn(userId, grant);
        return new DocInfo(attachment.getId(), grant);
    }

    @PostMapping("{grantId}/attribute/{attributeId}/attachment/{attachmentId}")
    @ApiOperation("Delete attachment from document field")
    public Grant deleteGrantStringAttributeAttachment(
            @ApiParam(name = "grantToSave", value = "Grant to save in edit mode, pass in Body of request") @RequestBody Grant grantToSave,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "userId", value = "Unique identifier og logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "attachmentId", value = "Unique identifier of the document attachment being deleted") @PathVariable("attachmentId") Long attachmentId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @ApiParam(name = "attributeId", value = "Unique identifier of the document field") @PathVariable("attributeId") Long attributeId) {
        saveGrant(grantToSave.getId(), grantToSave, userId, tenantCode);
        GrantStringAttributeAttachments attachment = grantService
                .getStringAttributeAttachmentsByAttachmentId(attachmentId);
        grantService.deleteStringAttributeAttachmentsByAttachmentId(attachmentId);

        File file = new File(attachment.getLocation() + attachment.getName() + "." + attachment.getType());
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        GrantStringAttribute stringAttribute = grantService.findGrantStringAttributeById(attributeId);
        List<GrantStringAttributeAttachments> stringAttributeAttachments = grantService
                .getStringAttributeAttachmentsByStringAttribute(stringAttribute);

        ObjectMapper mapper = new ObjectMapper();
        try {
            stringAttribute.setValue(mapper.writeValueAsString(stringAttributeAttachments));
            grantService.saveGrantStringAttribute(stringAttribute);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }

        Grant grant = grantService.getById(grantId);

        grant = grantService.grantToReturn(userId, grant);
        return grant;
    }

    @PostMapping(value = "/{grantId}/section/{sectionId}/attribute/{attributeId}/upload", consumes = {
            "multipart/form-data"})
    @ApiOperation("Upload and attach files to Document field from disk")
    public DocInfo saveUploadedFiles(
            @ApiParam(name = "sectionId", value = "Unique identifier of section") @PathVariable("sectionId") Long sectionId,
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "attributeId", value = "Unique identifier of the document field") @PathVariable("attributeId") Long attributeId,
            @ApiParam(name = "grantData", value = "Grant data") @RequestParam("grantToSave") String grantToSaveStr,
            @RequestParam("file") MultipartFile[] files,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Grant grantToSave = null;
        try {
            grantToSave = mapper.readValue(grantToSaveStr, Grant.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        grantValidator.validate(grantService, grantId, grantToSave, userId, tenantCode);
        grantValidator.validateSectionExists(grantService, grantToSave, sectionId);
        grantValidator.validateFieldExists(grantService, grantToSave, sectionId, attributeId);
        grantValidator.validateFiles(files, supportedFileTypes);

        Grant grant = grantService.getById(grantId);

        GrantStringAttribute attr = grantService.findGrantStringAttributeById(attributeId);
        String filePath = uploadLocation + tenantCode + GRANT_DOCUMENTS + grantId + FILE_SEPARATOR + attr.getSection().getId()
                + FILE_SEPARATOR + attr.getSectionAttribute().getId() + FILE_SEPARATOR;
        File dir = new File(filePath);
        dir.mkdirs();
        List<GrantStringAttributeAttachments> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && file.getOriginalFilename() != null) {
                String fileName = file.getOriginalFilename();

                File fileToCreate = new File(dir, fileName);
                try (FileOutputStream fos = new FileOutputStream(fileToCreate)) {
                    fos.write(file.getBytes());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                GrantStringAttributeAttachments attachment = new GrantStringAttributeAttachments();
                attachment.setVersion(1);
                attachment.setType(FilenameUtils.getExtension(file.getOriginalFilename()));
                attachment.setTitle(file.getOriginalFilename() != null ? file.getOriginalFilename().replace("." + FilenameUtils.getExtension(file.getOriginalFilename()), "") : "temp"
                        );
                attachment.setLocation(filePath);
                attachment.setName(file.getOriginalFilename() != null ? file.getOriginalFilename().replace("." + FilenameUtils.getExtension(file.getOriginalFilename()), "") : "temp"
                        );
                attachment.setGrantStringAttribute(attr);
                attachment.setDescription(file.getOriginalFilename() != null ? file.getOriginalFilename().replace("." + FilenameUtils.getExtension(file.getOriginalFilename()), "") : "temp"
                        );
                attachment.setCreatedOn(new Date());
                attachment.setCreatedBy(userService.getUserById(userId).getEmailId());
                attachment = grantService.saveGrantStringAttributeAttachment(attachment);
                attachments.add(attachment);
            }
        }

        mapper = new ObjectMapper();
        try {
            if (attr.getValue().equalsIgnoreCase("")) {
                attr.setValue("[]");
            }
            List<GrantStringAttributeAttachments> currentAttachments = mapper.readValue(attr.getValue(),
                    new TypeReference<List<GrantStringAttributeAttachments>>() {
                    });
            if (currentAttachments == null) {
                currentAttachments = new ArrayList<>();
            }
            currentAttachments.addAll(attachments);

            attr.setValue(mapper.writeValueAsString(currentAttachments));
            attr = grantService.saveStringAttribute(attr);
            GrantStringAttribute finalAttr = attr;
            GrantStringAttribute finalAttr1 = finalAttr;
            Optional<GrantStringAttribute> first = grant.getStringAttributes().stream().filter(g -> g.getId().longValue() == finalAttr1.getId().longValue()).findFirst();
            finalAttr = first.isPresent() ? first.get() : null;
            assert finalAttr != null;
            finalAttr.setValue(mapper.writeValueAsString(currentAttachments));
            grantService.saveGrant(grant);

        } catch (IOException e) {
            e.printStackTrace();
        }

        grant = grantService.getById(grantId);
        grant = grantService.grantToReturn(userId, grant);

        return new DocInfo(attachments.get(attachments.size() - 1).getId(), grant);
    }

    @PostMapping(value = "/{grantId}/attachments", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadSelectedAttachments(@PathVariable("userId") Long userId,
                                              @PathVariable("grantId") Long grantId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                                              @RequestBody AttachmentDownloadRequest downloadRequest, HttpServletResponse response) throws IOException {

        // setting headers
        response.setContentType(APPLICATION_ZIP);
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_TEST_ZIP);

        // creating byteArray stream, make it bufforable and passing this buffor to
        // ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        // simple file list, just for tests

        ArrayList<File> files = new ArrayList<>(2);
        files.add(new File("README.md"));

        // packing files
        for (Long attachmentId : downloadRequest.getAttachmentIds()) {
            Map<String, File> fileMap = getAttachment(grantId, tenantCode, attachmentId);
            File file = fileMap.values().stream().collect(Collectors.toList()).get(0);
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                IOUtils.copy(fileInputStream, zipOutputStream);
                zipOutputStream.closeEntry();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }


        zipOutputStream.finish();
        zipOutputStream.flush();
        zipOutputStream.close();
        bufferedOutputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private Map<String, File> getAttachment(Long grantId, String tenantCode, Long attachmentId) throws IOException {
        GrantStringAttributeAttachments attachment = grantService
                .getStringAttributeAttachmentsByAttachmentId(attachmentId);
        Long sectionId = attachment.getGrantStringAttribute().getSectionAttribute().getSection().getId();
        Long attributeId = attachment.getGrantStringAttribute().getSectionAttribute().getId();
        String fileName = attachment.getName();
        if(!fileName.contains(".".concat(attachment.getType()))){
            fileName=fileName.concat(".".concat(attachment.getType()));
        }
        File file = resourceLoader.getResource(FILE + attachment.getLocation() + fileName)
                .getFile();
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put(attachment.getType(), file);
        return fileMap;
    }

    @PostMapping(value = "/data/", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] exportDataForGrants(@PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                                      HttpServletResponse response) throws IOException {

        // setting headers
        response.setContentType(APPLICATION_ZIP);
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_TEST_ZIP);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOut = new ZipOutputStream(bufferedOutputStream);


        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            Long tenantId = organizationService.findOrganizationByTenantCode(tenantCode).getId();
            List<DataExportConfig> exportConfigs = exportConfigService.getDataExportConfigForTenantByCategory("ACTIVE_GRANTS_DETAILS", tenantId);

            List<String[]> summary = new ArrayList<>();
            summary.add(new String[]{"Summary for", "Extract Requested By", "Extract Requested On", "Records Retrieved"});
            String orgName = organizationService.get(tenantId).getName();
            Date extractDate = DateTime.now().toDate();
            String dt = new SimpleDateFormat("dd-MM-yyyy").format(extractDate);
            List<Long> summaryListIds = new ArrayList<>();
            for (DataExportConfig exportConfig : exportConfigs) {

                String q = exportConfig.getQuery().replace(TENANT_ID, String.valueOf(tenantId));
                if (q.indexOf("%grantTags%") >= 0) {
                    String grantsTagsQuery = "select string_agg(concat('\"',name,'\" as \"Tag - ',name,'\"'),',') from (select * from org_tags order by name) X where tenant=%tenantId% group by tenant";
                    grantsTagsQuery = grantsTagsQuery.replace(TENANT_ID, String.valueOf(tenantId));

                    ResultSet grantTagsSelectStatement = null;
                    q = buildGrantsTags(conn, q, grantsTagsQuery, grantTagsSelectStatement);


                }

                if (q.indexOf("%grantTagDefs%") >= 0) {
                    String grantsTagsDefQuery = "select string_agg(concat('\"',name,'\" text'),',') from (select * from org_tags order by name) X where tenant=%tenantId% group by tenant";
                    String grantsTagsSelectDefQuery = "select string_agg(concat('string_agg(\"',name,'\",'','') \"',name,'\"'),',') from (select * from org_tags order by name) X where tenant=%tenantId% group by tenant";
                    grantsTagsDefQuery = grantsTagsDefQuery.replace(TENANT_ID, String.valueOf(tenantId));
                    grantsTagsSelectDefQuery = grantsTagsSelectDefQuery.replace(TENANT_ID, String.valueOf(tenantId));

                    ResultSet grantTagsDefsStatement = null;
                    ResultSet grantTagsSelectDefsStatement = null;
                    q = buildDetailedGrantTags(conn, q, grantsTagsDefQuery, grantsTagsSelectDefQuery, grantTagsDefsStatement, grantTagsSelectDefsStatement);


                }
                ResultSet activeGrants = null;
                PreparedStatement activeGrantsStatement = null;
                activeGrantsStatement = prepareStatementForActiveGrants(conn, q, activeGrantsStatement);
                activeGrants = executeStatementForActiveGrants(activeGrants, activeGrantsStatement);

                String filename = orgName + "_" + exportConfig.getName() + "_" + dt + ".csv";
                ZipEntry entry = new ZipEntry(filename);


                zipOut.putNextEntry(entry);
                CSVWriter writer = new CSVWriter(new OutputStreamWriter(zipOut));
                int count = writer.writeAll(activeGrants, true);
                DataExportSummary exportSummary = new DataExportSummary(orgName + "_" + exportConfig.getName() + "_" + dt, userService.getUserById(userId).getFirstName().concat(" ").concat(userService.getUserById(userId).getLastName()), extractDate, count - 1);
                exportSummary = grantService.saveExportSummary(exportSummary);
                summaryListIds.add(exportSummary.getId());
                writer.flush();
                if (activeGrants != null) {
                    activeGrants.close();
                }
                if (activeGrantsStatement != null) {
                    activeGrantsStatement.close();
                }
            }

            String summaryQuery = "select summary_for as \"Summary For\",extract_request_by as \"Extract Requested By\",extract_requested_on as \"Extract Requested On\",records_retrieved as \"Records Retrieved\" from data_extract_logs where id in (%logIds%)";
            summaryQuery = summaryQuery.replace("%logIds%", StringUtils.join(summaryListIds.toArray(new Long[summaryListIds.size()]), ","));

            ResultSet summaryResult = null;
            createCsvExtract(zipOut, conn, orgName, dt, summaryQuery, summaryResult);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private ResultSet executeStatementForActiveGrants(ResultSet activeGrants, PreparedStatement activeGrantsStatement) {
        try {
            activeGrants = activeGrantsStatement.executeQuery();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return activeGrants;
    }

    private PreparedStatement prepareStatementForActiveGrants(Connection conn, String q, PreparedStatement activeGrantsStatement) {
        try {
            activeGrantsStatement = conn.prepareStatement(q);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return activeGrantsStatement;
    }

    private void createCsvExtract(ZipOutputStream zipOut, Connection conn, String orgName, String dt, String summaryQuery, ResultSet summaryResult) throws SQLException, IOException {
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(zipOut));
             PreparedStatement summaryStmnt = conn.prepareStatement(summaryQuery)) {

            summaryResult = summaryStmnt.executeQuery();
            String filename = orgName + "_Extract_Summary_" + dt + ".csv";
            ZipEntry entry = new ZipEntry(filename);
            zipOut.putNextEntry(entry);

            writer.writeAll(summaryResult, true);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (summaryResult != null) {
                summaryResult.close();
            }

            zipOut.closeEntry();
            zipOut.close();
            DataSourceUtils.doReleaseConnection(conn, dataSource);

        }
    }

    private String buildDetailedGrantTags(Connection conn, String q, String grantsTagsDefQuery, String grantsTagsSelectDefQuery, ResultSet grantTagsDefsStatement, ResultSet grantTagsSelectDefsStatement) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(grantsTagsDefQuery);
             PreparedStatement ps2 = conn.prepareStatement(grantsTagsSelectDefQuery);) {

            grantTagsDefsStatement = ps.executeQuery();
            grantTagsSelectDefsStatement = ps2.executeQuery();
            String tagsDefs = null;
            String tagsSelectDefs = null;
            while (grantTagsDefsStatement.next()) {
                tagsDefs = grantTagsDefsStatement.getString(STRING_AGG);
            }
            while (grantTagsSelectDefsStatement.next()) {
                tagsSelectDefs = grantTagsSelectDefsStatement.getString(STRING_AGG);
            }

            q = q.replace("%grantTagDefs%", tagsDefs == null ? "no_tag text" : tagsDefs);
            q = q.replace("%grantTagSelectDefs%", tagsSelectDefs == null ? "string_agg(no_tag,',') as no_tag" : tagsSelectDefs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (grantTagsDefsStatement != null) {
                grantTagsDefsStatement.close();
            }
            grantTagsSelectDefsStatement.close();
        }
        return q;
    }

    private String buildGrantsTags(Connection conn, String q, String grantsTagsQuery, ResultSet grantTagsSelectStatement) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(grantsTagsQuery);) {

            grantTagsSelectStatement = statement.executeQuery();

            String tagsSelect = null;
            while (grantTagsSelectStatement.next()) {
                tagsSelect = grantTagsSelectStatement.getString(STRING_AGG);
            }
            q = q.replace("%grantTags%", tagsSelect == null ? "'' as Tags" : tagsSelect);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            grantTagsSelectStatement.close();
        }
        return q;
    }

    @GetMapping("/{grantId}/history/")
    public List<GrantHistory> getGrantHistory(@PathVariable("grantId") Long grantId,
                                              @PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode) {

        List<GrantHistory> history = new ArrayList<>();
        List<GrantSnapshot> grantSnapshotHistory = grantSnapshotService.getGrantSnapshotForGrant(grantId);
        if (grantSnapshotHistory != null && grantSnapshotHistory.get(0).getFromStateId() == null) {
            history = grantService.getGrantHistory(grantId);
            for (GrantHistory historyEntry : history) {
                historyEntry.setNoteAddedByUser(userService.getUserByEmailAndOrg(historyEntry.getNoteAddedBy(),
                        historyEntry.getGrantorOrganization()));
            }
        } else {
            for (GrantSnapshot snapShot : grantSnapshotHistory) {
                GrantHistory hist = new GrantHistory();
                hist.setName(snapShot.getName());
                hist.setId(snapShot.getGrantId());
                hist.setNote(snapShot.getFromNote());
                hist.setNoteAdded(snapShot.getMovedOn());
                User assignedBy = userService.getUserById(snapShot.getAssignedBy());
                hist.setNoteAddedBy(assignedBy.getFirstName().concat(" ").concat(assignedBy.getLastName()));
                hist.setNoteAddedByUser(assignedBy);
                hist.setGrantStatus(workflowStatusService.findById(snapShot.getFromStateId()));
                history.add(hist);
            }
        }

        return history;
    }

    @GetMapping("{grantId}/changeHistory")
    public PlainGrant getGrantHistory(@PathVariable("grantId") Long grantId, @PathVariable("userId") Long userId) throws IOException {
        Grant grant = grantService.getById(grantId);
        GrantSnapshot snapshot = grantSnapshotService.getMostRecentSnapshotByGrantId(grantId);
        if (snapshot == null) {
            return null;
        }
        grant.setName(snapshot.getName());
        grant.setGrantStatus(workflowStatusService.findById(snapshot.getGrantStatusId()));
        grant.setOrganization(organizationService.findByName(snapshot.getGrantee()));
        grant.setAmount(snapshot.getAmount());
        grant.setStartDate(snapshot.getStartDate());
        grant.setEndDate(snapshot.getEndDate());
        grant.setRepresentative(snapshot.getRepresentative());
        GrantDetailVO details = new ObjectMapper().readValue(snapshot.getStringAttributes(), GrantDetailVO.class);
        grant.setGrantDetails(details);

        return grantService.grantToPlain(grant);
    }

    @GetMapping("/active")
    public List<Grant> getAllActiveGrants(@PathVariable("userId") Long userId,
                                          @RequestHeader("X-TENANT-CODE") String tenantCode) {
        return grantService
                .getActiveGrantsForTenant(organizationService.findOrganizationByTenantCode(tenantCode));
    }

    @PostMapping("{grantId}/invite")
    public Grant saveGrantInvites(@PathVariable("userId") Long userId,
                                  @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("grantId") Long grantId,
                                  @RequestBody GrantInvite grantInvite) {

        Grant grant = saveGrant(grantId, grantInvite.getGrant(), userId, tenantCode);
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
        String host = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme())
                .host(host).port(uriComponents.getPort());
        String url = uriBuilder.toUriString();
        for (InviteEntry invite : grantInvite.getInvites()) {
            User granteeUser = null;

            String code = null;
            try {
                User existingUser = userService.getUserByEmailAndOrg(invite.getName(), grant.getOrganization());
                code = Base64.getEncoder().encodeToString(String.valueOf(grant.getId()).getBytes());

                if (existingUser != null && existingUser.isActive()) {
                    granteeUser = existingUser;

                    url = new StringBuilder(url + "/home/?action=login&org="
                            + URLEncoder.encode(grant.getOrganization().getName(), StandardCharsets.UTF_8.toString())
                            + "&g=" + code + EMAIL + invite.getName() + TYPE_GRANT).toString();

                } else if (existingUser != null && !existingUser.isActive()) {
                    granteeUser = existingUser;
                    url = new StringBuilder(url + "/home/?action=registration&org="
                            + URLEncoder.encode(grant.getOrganization().getName(), StandardCharsets.UTF_8.toString())
                            + "&g=" + code + EMAIL + invite.getName() + TYPE_GRANT).toString();

                } else {
                    granteeUser = new User();
                    Role newRole = roleService.findByOrganizationAndName(grant.getOrganization(), "Admin");

                    UserRole userRole = new UserRole();
                    userRole.setRole(newRole);
                    userRole.setUser(granteeUser);

                    List<UserRole> userRoles = new ArrayList<>();
                    userRoles.add(userRole);
                    granteeUser.setUserRoles(userRoles);
                    granteeUser.setFirstName("");
                    granteeUser.setLastName("");
                    granteeUser.setEmailId(invite.getName());
                    granteeUser.setOrganization(grant.getOrganization());
                    granteeUser.setActive(false);
                    granteeUser = userService.save(granteeUser);
                    userRoleService.saveUserRole(userRole);
                    url = new StringBuilder(url + "/home/?action=registration&org="
                            + URLEncoder.encode(grant.getOrganization().getName(), StandardCharsets.UTF_8.toString())
                            + "&g=" + code + EMAIL + invite.getName() + TYPE_GRANT).toString();
                }

                String[] notifications = grantService.buildGrantInvitationContent(grant,
                        appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                AppConfiguration.GRANT_INVITE_SUBJECT).getConfigValue(),
                        appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                AppConfiguration.GRANT_INVITE_MESSAGE).getConfigValue(),
                        url);

                commonEmailSevice.sendMail(!granteeUser.isDeleted() ? new String[]{granteeUser.getEmailId()} : null,
                        null, notifications[0], notifications[1],
                        new String[]{appConfigService
                                .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                .getConfigValue()
                                .replace(RELEASE_VERSION, releaseService.getCurrentRelease().getVersion())});
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }

        grant = grantService.grantToReturn(userId, grant);
        return grant;
    }

    @GetMapping("/resolve")
    public Grant resolveGrant(@PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                              @RequestParam("g") String grantCode) {
        Long grantId = Long.valueOf(new String(Base64.getDecoder().decode(grantCode), StandardCharsets.UTF_8));

        Grant grant = grantService.getById(grantId);

        grant = grantService.grantToReturn(userId, grant);
        return grant;
    }

    @GetMapping("/{grantId}/file/{fileId}")
    @ApiOperation(value = "Get file for download")
    public ResponseEntity<Resource> getFileForDownload(HttpServletResponse servletResponse,
                                                       @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("grantId") Long grantId,
                                                       @PathVariable("fileId") Long fileId) {

        GrantStringAttributeAttachments attachment = grantService.getStringAttributeAttachmentsByAttachmentId(fileId);
        String filePath = attachment.getLocation() + attachment.getName() + "." + attachment.getType();

        try {
            File file = resourceLoader.getResource(FILE + filePath).getFile();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + attachment.getName() + "." + attachment.getType());
            servletResponse.setHeader("filename", attachment.getName() + "." + attachment.getType());
            return ResponseEntity.ok().headers(headers).contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    @GetMapping("{grantId}/documents")
    public List<GrantDocument> getDocumentForGrant(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                   @PathVariable("grantId") Long grantId, @PathVariable("userId") Long userId) {
        return grantService.getGrantsDocuments(grantId);
    }

    @PostMapping(value = "/{grantId}/documents/upload", consumes = {"multipart/form-data"})
    public List<GrantDocument> saveUploadedFiles(

            @PathVariable("userId") Long userId,
            @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("grantId") Long grantId,
            @RequestParam("file") MultipartFile[] files,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        String filePath = uploadLocation + tenantCode + GRANT_DOCUMENTS + grantId + FILE_SEPARATOR;
        File dir = new File(filePath);
        dir.mkdirs();
        List<GrantDocument> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            File fileToCreate = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(fileToCreate)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            GrantDocument attachment = new GrantDocument();
            attachment.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "temp";
            attachment.setName(file.getOriginalFilename() != null ? file.getOriginalFilename().replace("." + FilenameUtils.getExtension(filename), "") : "temp"
                    );
            attachment.setLocation(filePath + (file.getOriginalFilename() != null ? file.getOriginalFilename() : "temp"));
            attachment.setUploadedOn(new Date());
            attachment.setUploadedBy(userId);
            attachment.setGrantId(grantId);
            attachment = grantService.saveGrantDocument(attachment);
            attachments.add(attachment);
        }

        updateProjectDocuments(grantId, tenantCode, userId);

        return attachments;
    }

    private void updateProjectDocuments(Long grantId, String tenantCode, Long userId) {
        List<Long> projectGrantIds = grantService.getAllGrantIdsForProject(grantId);
        List<GrantDocument> projectDocs = grantService.getGrantsDocuments(grantId);
        if (projectGrantIds != null && !projectGrantIds.isEmpty()) {
            for (Long id : projectGrantIds) {
                List<GrantDocument> existingDocs = grantService.getGrantsDocuments(id);
                if (existingDocs != null && !existingDocs.isEmpty()) {
                    for (GrantDocument d : existingDocs) {
                        File file = new File(d.getLocation());
                        grantService.deleteGrantDocument(d);
                        try {
                            Files.delete(file.toPath());
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }

                    }
                }

                if (projectDocs != null && !projectDocs.isEmpty()) {
                    for (GrantDocument d : projectDocs) {
                        GrantDocument newDoc = new GrantDocument();
                        newDoc.setName(d.getName());
                        File dir = new File(uploadLocation + tenantCode + GRANT_DOCUMENTS + id);
                        dir.mkdirs();
                        newDoc.setLocation(dir.getPath() + FILE_SEPARATOR + d.getName() + "." + d.getExtension());
                        newDoc.setExtension(d.getExtension());
                        newDoc.setGrantId(id);
                        newDoc.setUploadedOn(DateTime.now().toDate());
                        newDoc.setUploadedBy(userId);
                        grantService.saveGrantDocument(newDoc);
                        File file = new File(d.getLocation());
                        File newFile = new File(newDoc.getLocation());

                        try {
                            FileCopyUtils.copy(file, newFile);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

    @PostMapping(value = "/{grantId}/documents/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadProjectDocuments(@PathVariable("userId") Long userId, @PathVariable("grantId") Long grantId,
                                           @RequestHeader("X-TENANT-CODE") String tenantCode, @RequestBody AttachmentDownloadRequest downloadRequest,
                                           HttpServletResponse response) throws IOException {

        // setting headers
        response.setContentType(APPLICATION_ZIP);
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_TEST_ZIP);

        // creating byteArray stream, make it bufforable and passing this buffor to
        // ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        // simple file list, just for tests

        ArrayList<File> files = new ArrayList<>(2);
        files.add(new File("README.md"));

        // packing files
        for (Long attachmentId : downloadRequest.getAttachmentIds()) {
            GrantDocument attachment = grantService.getGrantDocumentById(attachmentId);

            File file = resourceLoader.getResource(FILE + uploadLocation + tenantCode + GRANT_DOCUMENTS + grantId
                    + FILE_SEPARATOR + attachment.getName() + "." + attachment.getExtension()).getFile();
            // new zip entry and copying inputstream with file to zipOutputStream, after all
            // closing streams
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            try (FileInputStream fileInputStream = new FileInputStream(file)) {

                IOUtils.copy(fileInputStream, zipOutputStream);
            }
            zipOutputStream.closeEntry();
        }
        zipOutputStream.finish();
        zipOutputStream.flush();
        zipOutputStream.close();

        bufferedOutputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    @DeleteMapping(value = "/{grantId}/document/{documentId}")
    public void downloadProjectDocuments(@PathVariable("userId") Long userId, @PathVariable("grantId") Long grantId,
                                         @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("documentId") Long attachmentId) {

        GrantDocument doc = grantService.getGrantDocumentById(attachmentId);
        File file = new File(doc.getLocation());
        grantService.deleteGrantDocument(doc);
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        updateProjectDocuments(grantId, tenantCode, userId);

    }

    @GetMapping("/granteeOrgs")
    public List<Organization> getAssociatedGranteesForTenant(@RequestHeader("X-TENANT-CODE") String tenantCode) {
        return organizationService.getAssociatedGranteesForTenant(organizationService.findOrganizationByTenantCode(tenantCode));
    }


    @GetMapping("/grantTypes")
    public List<GrantType> getGrantTypes(@RequestHeader("X-TENANT-CODE") String tenantCode) {
        return grantService.getGrantTypesForTenantOrg(organizationService.findOrganizationByTenantCode(tenantCode).getId());
    }

    @PostMapping("/{grantId}/tags/{orgTagId}")
    public GrantTagVO attachTagToGrant(@PathVariable("userId") Long userId, @PathVariable("grantId") Long grantId,
                                       @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("orgTagId") Long orgTagId) {

        GrantTag tag = new GrantTag();
        tag.setGrant(grantService.getById(grantId));
        tag.setOrgTagId(orgTagId);

        tag = grantService.attachTagToGrant(tag);

        updateProjectTags(grantId);

        return new GrantTagVO(tag.getId(), orgTagService.getOrgTagById(orgTagId).getName(), grantId, orgTagId);

    }

    private void updateProjectTags(Long grantId) {
        List<Long> projectGrants = grantService.getAllGrantIdsForProject(grantId);
        if (projectGrants != null && !projectGrants.isEmpty()) {
            List<GrantTag> tagsToSet = grantService.getTagsForGrant(grantId);
            for (Long id : projectGrants) {
                List<GrantTag> tags = grantService.getTagsForGrant(id);
                if (tags != null && !tags.isEmpty()) {
                    for (GrantTag t : tags) {
                        grantService.detachTagToGrant(t);
                    }
                }
                for (GrantTag t : tagsToSet) {
                    GrantTag t1 = new GrantTag();
                    t1.setOrgTagId(t.getOrgTagId());
                    t1.setGrant(grantService.getById(id));
                    grantService.attachTagToGrant(t1);
                }
            }
        }
    }

    @DeleteMapping("/{grantId}/tags/{tagId}")
    public void detachTagFromGrant(@PathVariable("userId") Long userId, @PathVariable("grantId") Long grantId,
                                   @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("tagId") Long tagId) {
        GrantTag grantTag = grantService.getGrantTagById(tagId);
        grantService.detachTagToGrant(grantTag);

        updateProjectTags(grantId);

    }

    @GetMapping(value = "/compare/{currentGrantId}/{origGrantId}")
    public GrantCompareVO getGrantsToCompare(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                             @PathVariable("userId") Long userId,
                                             @PathVariable("currentGrantId") Long currentGrantId,
                                             @PathVariable("origGrantId") Long origGrantId) {

        List<PlainGrant> grantsToReturn = new ArrayList<>();
        String checkType = "strong";

        Grant currentGrant = grantService.getById(currentGrantId);
        currentGrant = grantService.grantToReturn(userId, currentGrant);

        Grant origGrant = grantService.getById(origGrantId);
        origGrant = grantService.grantToReturn(userId, origGrant);

        String amendGrantDetailsSnapshot = currentGrant.getAmendmentDetailsSnapshot();
        if (amendGrantDetailsSnapshot == null || amendGrantDetailsSnapshot.equalsIgnoreCase("")) {
            checkType = "weak";
        } else {

            try {
                origGrant.setGrantDetails(new ObjectMapper().readValue(amendGrantDetailsSnapshot, GrantDetailVO.class));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }


        try {
            grantsToReturn.add(grantService.grantToPlain(currentGrant));
            grantsToReturn.add(grantService.grantToPlain(origGrant));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new GrantCompareVO(checkType, grantsToReturn);
    }

    @GetMapping(value = "/compare/{currentGrantId}")
    public PlainGrant getPlainGrantForCompare(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                              @PathVariable("userId") Long userId,
                                              @PathVariable("currentGrantId") Long currentGrantId) throws IOException {
        Grant currentGrant = grantService.getById(currentGrantId);
        currentGrant = grantService.grantToReturn(userId, currentGrant);


        return grantService.grantToPlain(currentGrant);
    }

    @PostMapping(value = "/{for}/{id}/attachments/preview")
    public PreviewData prepareAttachmentForPreview(
            @PathVariable("for") String forEntity,
            @PathVariable("id") Long id,
            @RequestHeader("X-TENANT-CODE") String tenantCode,
            @RequestBody AttachmentDownloadRequest downloadRequest,
            HttpServletResponse response) throws IOException {

        Map<String, File> fileMap = null;
        if (GRANT.equalsIgnoreCase(forEntity)) {
            fileMap = getAttachment(id, tenantCode, downloadRequest.getAttachmentIds()[0]);
        } else if (PROJECT.equalsIgnoreCase(forEntity)) {
            GrantDocument doc = grantService.getGrantDocumentById(downloadRequest.getAttachmentIds()[0]);
            fileMap = new HashMap<>();
            String fileName = doc.getName();
            if(!fileName.contains(".".concat(doc.getExtension()))){
                fileName=fileName.concat(".".concat(doc.getExtension()));
            }
            File file = resourceLoader.getResource(FILE + doc.getLocation()).getFile();
            fileMap.put(doc.getExtension(), file);
        } else if (REPORT.equalsIgnoreCase(forEntity)) {
            ReportStringAttributeAttachments attachment = reportService.getStringAttributeAttachmentsByAttachmentId(downloadRequest.getAttachmentIds()[0]);
            Long sectionId = attachment.getReportStringAttribute().getSectionAttribute().getSection().getId();
            Long attributeId = attachment.getReportStringAttribute().getSectionAttribute().getId();

            String fileName = attachment.getName();
            if(!fileName.contains(".".concat(attachment.getType()))){
                fileName=fileName.concat(".".concat(attachment.getType()));
            }

            File file = resourceLoader.getResource(FILE + attachment.getLocation() + fileName)
                    .getFile();
            fileMap = new HashMap<>();
            fileMap.put(attachment.getType(), file);
        } else if (DISBURSEMENT.equalsIgnoreCase(forEntity)) {
            DisbursementDocument attachment = disbursementService.getDisbursementDocumentById(id);

            String fileName = attachment.getLocation();
            if(!fileName.contains(".".concat(attachment.getExtension()))){
                fileName=fileName.concat(".".concat(attachment.getExtension()));
            }

            File file = resourceLoader.getResource(FILE + fileName).getFile();
            fileMap = new HashMap<>();
            fileMap.put(attachment.getExtension(), file);
        }else if (LIBRARY.equalsIgnoreCase(forEntity)) {
            TemplateLibrary attachment = templateLibraryService.getTemplateLibraryDocumentById(id);

            String fileName = attachment.getLocation();
            if(!fileName.contains(".".concat(attachment.getType()))){
                fileName=fileName.concat(".".concat(attachment.getType()));
            }

            File file = resourceLoader.getResource(FILE +uploadLocation+ fileName).getFile();
            fileMap = new HashMap<>();
            fileMap.put(attachment.getType(), file);
        }else if (CLOSURE.equalsIgnoreCase(forEntity)) {
            ClosureStringAttributeAttachments attachment = grantClosureService.getStringAttributeAttachmentsByAttachmentId(downloadRequest.getAttachmentIds()[0]);
            Long sectionId = attachment.getClosureStringAttribute().getSectionAttribute().getSection().getId();
            Long attributeId = attachment.getClosureStringAttribute().getSectionAttribute().getId();

            String fileName = attachment.getName();
            if(!fileName.contains(".".concat(attachment.getType()))){
                fileName=fileName.concat(".".concat(attachment.getType()));
            }

            File file = resourceLoader.getResource(FILE + attachment.getLocation() + fileName)
                    .getFile();
            fileMap = new HashMap<>();
            fileMap.put(attachment.getType(), file);
        }

        if (fileMap == null) {
            return null;
        }
        File file = fileMap.values().stream().collect(Collectors.toList()).get(0);
        Optional<String> first = fileMap.keySet().stream().findFirst();
        String tempFileName = RandomStringUtils.randomAlphabetic(127) + "." + (first.isPresent() ? first.get() : "");
        File tempFile = new File(previewLocation + FILE_SEPARATOR + tempFileName);
        FileCopyUtils.copy(file, tempFile);
        return new PreviewData(tempFileName);
    }


}
