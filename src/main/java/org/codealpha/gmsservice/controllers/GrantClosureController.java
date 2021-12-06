package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/user/{userId}/closure")
public class GrantClosureController {


    public static final String STRNOSPACE = "";
    private static final Logger logger = LoggerFactory.getLogger(GrantClosureController.class);
    public static final String EMAIL = "&email=";
    public static final String DISBURSEMENT = "disbursement";
    public static final String ACTUAL_DISBURSEMENT = "Actual Disbursement";
    public static final String DISBURSEMENT_DATE = "Disbursement Date";
    public static final String FUNDS_FROM_OTHER_SOURCES = "Funds from other Sources";
    public static final String NOTES = "Notes";
    public static final String ONE = "1";
    public static final String PLANNED_INSTALLMENT = "Planned Installment #";
    public static final String CURRENCY = "currency";
    public static final String DATE = "date";
    public static final String REPORT = "REPORT";
    public static final String FILE = "file:";
    public static final String ACTIVE = "ACTIVE";
    public static final String GRANTEE = "GRANTEE";
    public static final String CLOSED = "CLOSED";
    public static final String DRAFT = "DRAFT";
    public static final String DD_MMM_YYYY = "dd-MMM-yyyy";
    public static final String CLOSURE_DOCUMENTS = "/closure-documents/";
    public static final String RELEASE_VERSION = "%RELEASE_VERSION%";
    public static final String PLEASE_REVIEW = "Please review.";
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String STREMPTY = " ";
    public static final String DISBURSEMENTCAPS = "DISBURSEMENT";

    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private GrantClosureService closureService;
    @Autowired
    private WorkflowStatusTransitionService workflowStatusTransitionService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private UserService userService;
    @Autowired
    private DisbursementService disbursementService;
    @Value("${spring.timezone}")
    private String timezone;
    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private GrantTypeService grantTypeService;
    @Autowired
    private TemplateLibraryService templateLibraryService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Value("${spring.upload-file-location}")
    private String uploadLocation;
    @Value("${spring.supported-file-types}")
    private String[] supportedFileTypes;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private CommonEmailSevice commonEmailSevice;
    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private ClosureSnapshotService closureSnapshotService;

    @GetMapping("/{closureId}")
    public GrantClosure getClosure(@PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                                @PathVariable("closureId") Long closureId) {

        GrantClosure closure = closureService.getClosureById(closureId);

        closure = closureToReturn(closure, userId);
        checkAndReturnHistoricalCLosure(userId, closure);
        return closure;
    }

    @DeleteMapping("/{closureId}")
    public void deleteClosure(
            @PathVariable("closureId") Long closureId,
            @PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {
        GrantClosure closure = closureService.getClosureById(closureId);

        closureService.deleteClosure(closure);
    }

    @GetMapping("/")
    public List<GrantClosure> getGrantClosuresForUser(
            @PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode
    ){

        List<GrantClosure> closures = closureService.getClosuresForUser(userId);

        for(GrantClosure closure : closures){
            closureToReturn(closure,userId);
        }
        return closures;
    }

    @GetMapping("/templates")
    @ApiOperation("Get all published closure templates for tenant")
    public List<GranterClosureTemplate> getTenantPublishedClosureTemplates(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {
        return closureService.findTemplatesAndPublishedStatusAndPrivateStatus(
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), true, false);
    }

    @GetMapping("/{grantId}/{templateId}")
    public GrantClosure createClosure(
            @ApiParam(name = "grantId", value = "Unique identifier for the selected grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "templateId", value = "Unique identifier for the selected template") @PathVariable("templateId") Long templateId,
            @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        Organization granterOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        GranterClosureTemplate closureTemplate = closureService.findByTemplateId(templateId);

        Grant grant = grantService.getById(grantId);
        GrantClosure closure = new GrantClosure();
        closure.setCreateBy(userId);
        closure.setCreatedAt(new Date());
        closure.setGrant(grant);
        closure.setTemplate(closureTemplate);
        closure.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("GRANTCLOSURE",
                organizationService.findOrganizationByTenantCode(tenantCode).getId(),grant.getGrantTypeId()));

        closure.getGrant().setClosureInProgress(true);
        grantService.saveGrant(closure.getGrant());

        closure = closureService.saveClosure(closure);



        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionService
                .getStatusTransitionsForWorkflow(
                        workflowService.findDefaultByGranterAndObjectAndType(granterOrg, "GRANTCLOSURE",grant.getGrantTypeId()));

        List<WorkflowStatus> statuses = new ArrayList<>();
        ClosureAssignments assignment = null;
        for (WorkflowStatusTransition supportedTransition : supportedTransitions) {
            if (statuses.stream()
                    .noneMatch(s -> s.getId().longValue() == supportedTransition.getFromState().getId().longValue())) {
                statuses.add(supportedTransition.getFromState());
            }
            if (statuses.stream().noneMatch(s -> s.getId().longValue() == supportedTransition.getToState().getId().longValue())) {
                statuses.add(supportedTransition.getToState());
            }
        }
        for (WorkflowStatus status : statuses) {

            assignment = new ClosureAssignments();
            if (status.isInitial()) {
                assignment.setAnchor(true);
                assignment.setAssignment(userId);
            } else {
                assignment.setAnchor(false);
            }
            assignment.setClosure(closure);
            assignment.setStateId(status.getId());

            if(Boolean.TRUE.equals(status.getTerminal())){
                final GrantClosure finalclosure = closure;

                Optional<GrantAssignments> optionalActiveStateOwner =  grantService.getGrantWorkflowAssignments(closure.getGrant()).stream().filter(ass -> ass.getStateId().longValue()==finalclosure.getGrant().getGrantStatus().getId().longValue()).findFirst();
                GrantAssignments activeStateOwner = optionalActiveStateOwner.isPresent()?optionalActiveStateOwner.get():null;

                if(activeStateOwner!=null) {
                    assignment.setAssignment(activeStateOwner.getAssignments());
                }
            }
            closureService.saveAssignmentForClosure(assignment);

        }

        List<GranterClosureSection> granterClosureSections = closureTemplate.getSections();
        closure.setStringAttributes(new ArrayList<>());
        AtomicBoolean closureTemplateHasDisbursement = new AtomicBoolean(false);
        AtomicReference<ClosureStringAttribute> disbursementAttributeValue = new AtomicReference<>(new ClosureStringAttribute());


        for (GranterClosureSection closureSection : granterClosureSections) {
            ClosureSpecificSection specificSection = new ClosureSpecificSection();
            specificSection.setDeletable(true);
            specificSection.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
            specificSection.setClosureId(closure.getId());
            specificSection.setClosureTemplateId(closureTemplate.getId());
            specificSection.setSectionName(closureSection.getSectionName());
            specificSection.setSectionOrder(closureSection.getSectionOrder());

            specificSection = closureService.saveClosureSpecificSection(specificSection);
            ClosureSpecificSection finalSpecificSection = specificSection;
            GrantClosure finalClosure = closure;
            final AtomicInteger[] attribVOOrder = { new AtomicInteger(1) };

            closureSection.getAttributes().forEach(a -> {
                ClosureSpecificSectionAttribute sectionAttribute = new ClosureSpecificSectionAttribute();
                sectionAttribute.setAttributeOrder(attribVOOrder[0].getAndIncrement());
                sectionAttribute.setDeletable(a.getDeletable());
                sectionAttribute.setFieldName(a.getFieldName());
                sectionAttribute.setFieldType(a.getFieldType());
                sectionAttribute.setGranter(finalSpecificSection.getGranter());
                sectionAttribute.setRequired(a.getRequired());
                sectionAttribute.setSection(finalSpecificSection);
                sectionAttribute.setCanEdit(true);
                sectionAttribute.setExtras(a.getExtras());
                sectionAttribute = closureService.saveClosureSpecificSectionAttribute(sectionAttribute);

                ClosureStringAttribute stringAttribute = new ClosureStringAttribute();

                stringAttribute.setSection(finalSpecificSection);
                stringAttribute.setClosure(finalClosure);
                stringAttribute.setSectionAttribute(sectionAttribute);
                if (sectionAttribute.getFieldType().equalsIgnoreCase("kpi")) {
                    stringAttribute.setGrantLevelTarget(null);

                } else if (sectionAttribute.getFieldType().equalsIgnoreCase("table")) {
                    stringAttribute.setValue(a.getExtras());
                }
                stringAttribute = closureService.saveClosureStringAttribute(stringAttribute);

                if (sectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {

                    closureTemplateHasDisbursement.set(true);
                    disbursementAttributeValue.set(stringAttribute);
                }
            });
        }

        // Handle logic for setting dibursement type in reports
        for (GrantSpecificSection grantSection : grantService.getGrantSections(closure.getGrant())) {
            for (GrantSpecificSectionAttribute specificSectionAttribute : grantService
                    .getAttributesBySection(grantSection)) {

                if (specificSectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                    if (closureTemplateHasDisbursement.get()) {
                        ObjectMapper mapper = new ObjectMapper();
                        String[] colHeaders = new String[] {DISBURSEMENT_DATE, ACTUAL_DISBURSEMENT,
                                FUNDS_FROM_OTHER_SOURCES, NOTES};
                        List<TableData> tableDataList = new ArrayList<>();
                        TableData tableData = new TableData();
                        tableData.setName(ONE);
                        tableData.setHeader(PLANNED_INSTALLMENT);
                        tableData.setEnteredByGrantee(false);
                        tableData.setColumns(new ColumnData[4]);
                        for (int i = 0; i < tableData.getColumns().length; i++) {


                            tableData.getColumns()[i] = new ColumnData(colHeaders[i], STRNOSPACE,getType(i));
                        }
                        tableDataList.add(tableData);

                        try {
                            disbursementAttributeValue.get().setValue(mapper.writeValueAsString(tableDataList));
                            closureService.saveClosureStringAttribute(disbursementAttributeValue.get());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ClosureSpecificSection specificSection = new ClosureSpecificSection();
                        specificSection.setDeletable(true);
                        specificSection.setGranter((Granter) closure.getGrant().getGrantorOrganization());
                        specificSection.setClosureId(closure.getId());
                        specificSection.setClosureTemplateId(closureTemplate.getId());
                        specificSection.setSectionName("Project Funds");
                        List<ClosureSpecificSection> closureSections = closureService.getClosureSections(closure);
                        specificSection.setSectionOrder(Collections.max(closureSections.stream()

                                .map(ClosureSpecificSection::getSectionOrder).collect(Collectors.toList())) + 1);
                        specificSection = closureService.saveClosureSpecificSection(specificSection);

                        ClosureSpecificSectionAttribute sectionAttribute = new ClosureSpecificSectionAttribute();
                        sectionAttribute.setAttributeOrder(1);
                        sectionAttribute.setDeletable(true);
                        sectionAttribute.setFieldName("Disbursement Details");

                        sectionAttribute.setFieldType(DISBURSEMENT);

                        sectionAttribute.setGranter((Granter) closure.getGrant().getGrantorOrganization());
                        sectionAttribute.setRequired(false);
                        sectionAttribute.setSection(specificSection);
                        sectionAttribute.setCanEdit(true);
                        sectionAttribute.setExtras(null);
                        sectionAttribute = closureService.saveClosureSpecificSectionAttribute(sectionAttribute);

                        ClosureStringAttribute stringAttribute = new ClosureStringAttribute();

                        stringAttribute.setSection(specificSection);
                        stringAttribute.setClosure(closure);
                        stringAttribute.setSectionAttribute(sectionAttribute);

                        closureService.saveClosureStringAttribute(stringAttribute);

                    }
                }
            }
        }

        closure = closureToReturn(closure, userId);
        return closure;
    }

    private String getType(int i) {
        if(i == 1 || i == 2) {
            return CURRENCY;
        }else if(i == 0){
            return DATE;
        }else{
            return null;
        }
    }


    private GrantClosure closureToReturn(GrantClosure closure, Long userId) {

        closure.setStringAttributes(closureService.getStringAttributesForClosure(closure));

        List<ClosureAssignmentsVO> workflowAssignments = new ArrayList<>();
        for (ClosureAssignments assignment : closureService.getAssignmentsForClosure(closure)) {
            ClosureAssignmentsVO assignmentsVO = new ClosureAssignmentsVO();
            assignmentsVO.setId(assignment.getId());
            assignmentsVO.setAnchor(assignment.isAnchor());
            assignmentsVO.setAssignmentId(assignment.getAssignment());
            if (assignment.getAssignment() != null && assignment.getAssignment() > 0) {
                assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignment()));
            }
            assignmentsVO.setClosureId(closure.getId());
            assignmentsVO.setStateId(assignment.getStateId());
            assignmentsVO.setStateName(workflowStatusService.findById(assignment.getStateId()));

            closureService.setAssignmentHistory(assignmentsVO);

            workflowAssignments.add(assignmentsVO);
        }
        closure.setWorkflowAssignment(workflowAssignments);
        List<ClosureAssignments> closureAssignments = determineCanManage(closure, userId);

        setGranteeUse(closure,userService.getUserById(userId).getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE));

        if (closureAssignments != null) {
            for (ClosureAssignments assignment : closureAssignments) {
                if (closure.getCurrentAssignment() == null) {
                    List<AssignedTo> assignedToList = new ArrayList<>();
                    closure.setCurrentAssignment(assignedToList);
                }
                AssignedTo newAssignedTo = new AssignedTo();
                if (assignment.getAssignment() != null && assignment.getAssignment() > 0) {
                    newAssignedTo.setUser(userService.getUserById(assignment.getAssignment()));
                }
                closure.getCurrentAssignment().add(newAssignedTo);
            }
        }

        GrantClosureVO closureVO = new GrantClosureVO().build(closure, closureService.getClosureSections(closure), userService,
                closureService);
        closure.setClosureDetails(closureVO.getClosureDetails());

        showDisbursementsForClosure(closure,userService.getUserById(userId));

        closure.setNoteAddedBy(closureVO.getNoteAddedBy());
        closure.setNoteAddedByUser(closureVO.getNoteAddedByUser());

        closure.getWorkflowAssignment().sort((a, b) -> a.getId().compareTo(b.getId()));
        closure.getClosureDetails().getSections()
                .sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
        for (SectionVO section : closure.getClosureDetails().getSections()) {
            if (section.getAttributes() != null) {
                section.getAttributes().sort(
                        (a, b) -> Long.valueOf(a.getAttributeOrder()).compareTo(Long.valueOf(b.getAttributeOrder())));
            }
        }

        closure.setGranteeUsers(userService.getAllGranteeUsers(closure.getGrant().getOrganization()));

        GrantVO grantVO = new GrantVO().build(closure.getGrant(), grantService.getGrantSections(closure.getGrant()),
                workflowPermissionService, userService.getUserById(userId),
                appConfigService.getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                        AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS),
                userService,grantService);

        ObjectMapper mapper = new ObjectMapper();
        closure.getGrant().setGrantDetails(grantVO.getGrantDetails());

        List<TableData> approvedDisbursements = new ArrayList<>();
        AtomicInteger installmentNumber = new AtomicInteger();


        closure.getGrant().setApprovedReportsDisbursements(approvedDisbursements);

        closure.getClosureDetails().getSections().forEach(sec -> {
            if (sec.getAttributes() != null) {
                sec.getAttributes().forEach(attr -> {

                    if (attr.getFieldType().equalsIgnoreCase(DISBURSEMENT) && attr.getFieldTableValue() != null) {
                        for (TableData data : attr.getFieldTableValue()) {
                            installmentNumber.getAndIncrement();
                            data.setName(String.valueOf(installmentNumber.get()));
                        }

                        try {
                            attr.setFieldValue(mapper.writeValueAsString(attr.getFieldTableValue()));
                        } catch (JsonProcessingException e) {
                            logger.error(e.getMessage(), e);
                        }

                    }
                });
            }
        });

        closure.setFlowAuthorities(closureService.getFlowAuthority(closure, userId));

        List<GrantTag> grantTags = grantService.getTagsForGrant(closure.getGrant().getId());

        closure.getGrant().setGrantTags(grantTags);

        return closure;
    }

    public void setGranteeUse(GrantClosure closure,boolean flag){
        closure.setForGranteeUse(flag);
    }

    private List<ClosureAssignments> determineCanManage(GrantClosure closure, Long userId) {
        List<ClosureAssignments> closureAssignments = closureService.getAssignmentsForClosure(closure);
        boolean canManageFlag = (closureAssignments.stream()
                .anyMatch(ass -> (ass.getAssignment() == null ? 0L : ass.getAssignment().longValue()) == userId
                        .longValue() && ass.getStateId().longValue() == closure.getStatus().getId().longValue()))

                || (closure.getStatus().getInternalStatus().equalsIgnoreCase(ACTIVE) && userService.getUserById(userId)
                .getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE));
        closure.setCanManage(canManageFlag);

        return closureAssignments;
    }

    private void showDisbursementsForClosure(GrantClosure closure, User currentUser) {
        List<WorkflowStatus> workflowStatuses = workflowStatusService.getTenantWorkflowStatuses(DISBURSEMENTCAPS,
                closure.getGrant().getGrantorOrganization().getId());

        List<WorkflowStatus> closedStatuses = workflowStatuses.stream()

                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase(CLOSED)).collect(Collectors.toList());
        List<Long> closedStatusIds = closedStatuses.stream().mapToLong(WorkflowStatus::getId).boxed()
                .collect(Collectors.toList());

        List<WorkflowStatus> draftStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase(DRAFT)).collect(Collectors.toList());
        List<Long> draftStatusIds = draftStatuses.stream().mapToLong(WorkflowStatus::getId).boxed()
                .collect(Collectors.toList());

        List<ActualDisbursement> finalActualDisbursements = new ArrayList<>();
        closure.getClosureDetails().getSections().forEach(s -> {
            if (s.getAttributes() != null && !s.getAttributes().isEmpty()) {
                s.getAttributes().forEach(a -> {
                    if (a.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                        List<Disbursement> closedDisbursements = getDisbursementsByStatusIds(closure.getGrant(),closedStatusIds); //disbursementService
                        List<Disbursement> draftDisbursements = getDisbursementsByStatusIds(closure.getGrant(), draftStatusIds);
                        if (!closure.getStatus().getInternalStatus().equalsIgnoreCase(CLOSED)) {
                            List<TableData> tableDataList = new ArrayList<>();
                            if (closedDisbursements != null) {
                                closedDisbursements.sort(Comparator.comparing(Disbursement::getCreatedAt));
                                closedDisbursements.forEach(cd -> {
                                    List<ActualDisbursement> ads = disbursementService
                                            .getActualDisbursementsForDisbursement(cd);
                                    if (ads != null && !ads.isEmpty()) {
                                        finalActualDisbursements.addAll(ads);
                                    }

                                });
                            }


                            if (draftDisbursements != null && !draftDisbursements.isEmpty()) {
                                if (!currentUser.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
                                    draftDisbursements.removeIf(dd -> ((dd.getReportId() != null
                                            && dd.getReportId().longValue() != closure.getId().longValue()  && dd.isGranteeEntry()) || (dd.getReportId() != null
                                            && dd.getReportId().longValue() == closure.getId().longValue()  && dd.isGranteeEntry() && closure.getStatus().getInternalStatus().equalsIgnoreCase(ACTIVE))));

                                }
                                if (draftDisbursements != null) {
                                    draftDisbursements.sort(Comparator.comparing(Disbursement::getCreatedAt));
                                    draftDisbursements.forEach(cd -> {
                                        List<ActualDisbursement> ads = disbursementService
                                                .getActualDisbursementsForDisbursement(cd);
                                        if (ads != null && !ads.isEmpty()) {
                                            finalActualDisbursements.addAll(ads);
                                        }

                                    });
                                }
                            }



                            finalActualDisbursements.sort(Comparator.comparing(ActualDisbursement::getId));
                            if (!finalActualDisbursements.isEmpty()) {
                                AtomicInteger index = new AtomicInteger(1);
                                finalActualDisbursements.forEach(ad -> {
                                    TableData td = new TableData();
                                    ColumnData[] colDataList = new ColumnData[4];
                                    td.setName(String.valueOf(index.getAndIncrement()));
                                    td.setHeader("#");
                                    td.setStatus(ad.getStatus());
                                    td.setSaved(ad.getSaved());
                                    td.setActualDisbursementId(ad.getId());
                                    td.setDisbursementId(ad.getDisbursementId());
                                    Long repId = disbursementService.getDisbursementById(ad.getDisbursementId()).getReportId();
                                    td.setReportId(repId);
                                    if (disbursementService.getDisbursementById(ad.getDisbursementId())
                                            .isGranteeEntry()) {
                                        td.setEnteredByGrantee(true);
                                    }

                                    if(currentUser.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE) && td.isEnteredByGrantee() && closure.getId().longValue()!=repId.longValue() && !disbursementService.getDisbursementById(ad.getDisbursementId()).getStatus().getInternalStatus().equalsIgnoreCase(CLOSED)){
                                        td.setShowForGrantee(false);
                                    }


                                    ColumnData cdDate = new ColumnData();
                                    cdDate.setDataType(DATE);
                                    cdDate.setName(DISBURSEMENT_DATE);
                                    cdDate.setValue(ad.getDisbursementDate() != null
                                            ? new SimpleDateFormat(DD_MMM_YYYY).format(ad.getDisbursementDate())
                                            : null);

                                    ColumnData cdDA = new ColumnData();
                                    cdDA.setDataType(CURRENCY);
                                    cdDA.setName(ACTUAL_DISBURSEMENT);
                                    cdDA.setValue(
                                            ad.getActualAmount() != null ? String.valueOf(ad.getActualAmount()) : null);

                                    ColumnData cdFOS = new ColumnData();
                                    cdFOS.setDataType(CURRENCY);
                                    cdFOS.setName("Funds from Other Sources");
                                    cdFOS.setValue(
                                            ad.getOtherSources() != null ? String.valueOf(ad.getOtherSources()) : null);

                                    ColumnData cdN = new ColumnData();
                                    cdN.setName(NOTES);
                                    cdN.setValue(ad.getNote());

                                    colDataList[0] = cdDate;
                                    colDataList[1] = cdDA;
                                    colDataList[2] = cdFOS;
                                    colDataList[3] = cdN;
                                    td.setColumns(colDataList);
                                    tableDataList.add(td);
                                });
                                a.setFieldTableValue(tableDataList);
                                try {
                                    a.setFieldValue(new ObjectMapper().writeValueAsString(tableDataList));
                                } catch (IOException e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        } else {
                            List<TableData> tableDataList = new ArrayList<>();

                            if (closedDisbursements != null) {
                                closedDisbursements.removeIf(
                                        cd -> new DateTime(cd.getMovedOn(), DateTimeZone.forID(timezone)).isAfter(
                                                new DateTime(closure.getMovedOn(), DateTimeZone.forID(timezone))));
                                if (closedDisbursements != null) {
                                    closedDisbursements.forEach(cd -> {

                                        List<ActualDisbursement> ads = disbursementService
                                                .getActualDisbursementsForDisbursement(cd);
                                        if (ads != null && !ads.isEmpty()) {
                                            finalActualDisbursements.addAll(ads);
                                        }
                                    });
                                }
                            }

                            finalActualDisbursements.sort(Comparator.comparing(ActualDisbursement::getOrderPosition));
                            if (!finalActualDisbursements.isEmpty()) {
                                AtomicInteger index = new AtomicInteger(1);
                                finalActualDisbursements.forEach(ad -> {
                                    TableData td = new TableData();
                                    ColumnData[] colDataList = new ColumnData[4];
                                    td.setName(String.valueOf(index.getAndIncrement()));
                                    td.setHeader("#");
                                    td.setStatus(ad.getStatus());
                                    td.setSaved(ad.getStatus());
                                    td.setActualDisbursementId(ad.getId());
                                    td.setDisbursementId(ad.getDisbursementId());
                                    td.setReportId(disbursementService.getDisbursementById(ad.getDisbursementId()).getReportId());
                                    if (disbursementService.getDisbursementById(ad.getDisbursementId())
                                            .isGranteeEntry()) {
                                        td.setEnteredByGrantee(true);
                                    }
                                    ColumnData cdDate = new ColumnData();
                                    cdDate.setDataType(DATE);
                                    cdDate.setName(DISBURSEMENT_DATE);
                                    cdDate.setValue(ad.getDisbursementDate() != null
                                            ? new SimpleDateFormat(DD_MMM_YYYY).format(ad.getDisbursementDate())
                                            : null);

                                    ColumnData cdDA = new ColumnData();
                                    cdDA.setDataType(CURRENCY);
                                    cdDA.setName(ACTUAL_DISBURSEMENT);
                                    cdDA.setValue(String.valueOf(ad.getActualAmount()));

                                    ColumnData cdFOS = new ColumnData();
                                    cdFOS.setDataType(CURRENCY);
                                    cdFOS.setName("Funds from Other Sources");
                                    cdFOS.setValue(String.valueOf(ad.getOtherSources()));

                                    ColumnData cdN = new ColumnData();
                                    cdN.setName(NOTES);
                                    cdN.setValue(ad.getNote());

                                    colDataList[0] = cdDate;
                                    colDataList[1] = cdDA;
                                    colDataList[2] = cdFOS;
                                    colDataList[3] = cdN;
                                    td.setColumns(colDataList);
                                    tableDataList.add(td);
                                });
                                a.setFieldTableValue(tableDataList);
                                try {
                                    a.setFieldValue(new ObjectMapper().writeValueAsString(tableDataList));
                                } catch (IOException e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }

                    }
                });
            }
        });

    }

    private List<Disbursement> getDisbursementsByStatusIds(Grant grant, List<Long> statusIds) {
        List<Disbursement> closedDisbursements = disbursementService.getDibursementsForGrantByStatuses(grant.getId(), statusIds);
        if(grant.getOrigGrantId()!=null){
            closedDisbursements.addAll(getDisbursementsByStatusIds(grantService.getById(grant.getOrigGrantId()),statusIds));
        }
        return closedDisbursements;
    }

    @PostMapping("/{closureId}/template/{templateId}/section/{sectionName}")
    @ApiOperation("Create new section in grant closure")
    public ClosureSectionInfo createSection(@RequestBody GrantClosure closureToSave,
                                           @PathVariable("closureId") Long closureId,
                                           @PathVariable("templateId") Long templateId,
                                           @PathVariable("sectionName") String sectionName,
                                           @PathVariable("userId") Long userId,
                                           @RequestHeader("X-TENANT-CODE") String tenantCode) {
        GrantClosure closure = saveClosure(closureId, closureToSave, userId, tenantCode);

        ClosureSpecificSection specificSection = new ClosureSpecificSection();
        specificSection.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
        specificSection.setSectionName(sectionName);

        specificSection.setClosureTemplateId(templateId);
        specificSection.setDeletable(true);
        specificSection.setClosureId(closureId);
        specificSection.setSectionOrder(closureService
                .getNextSectionOrder(organizationService.findOrganizationByTenantCode(tenantCode).getId(), templateId));
        specificSection = closureService.saveSection(specificSection);

        if (closureService.checkIfClosureTemplateChanged(closure, specificSection, null)) {
            closureService._createNewClosureTemplateFromExisiting(closure);
        }

        closure = closureToReturn(closure, userId);
        return new ClosureSectionInfo(specificSection.getId(), specificSection.getSectionName(), closure);

    }

    @PutMapping("/{closureId}")
    @ApiOperation("Save closure")
    public GrantClosure saveClosure(
            @PathVariable("closureId") Long closureId,
            @RequestBody GrantClosure closureToSave,
            @PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {


        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        User user = userService.getUserById(userId);
        GrantClosure closure = null;
        GrantClosure savedClosure = closureService.getClosureById(closureId);
        determineCanManage(savedClosure, userId);
        if (savedClosure.isCanManage())
            closure = processClosure(closureToSave, tenantOrg, user);

        if(closure!=null) {
            closure.getGrant().setClosureInProgress(true);
            grantService.saveGrant(closure.getGrant());
        }

        closure = closureToReturn(closure, userId);
        return closure;
    }

    private GrantClosure processClosure(GrantClosure closureToSave, Organization tenantOrg, User user) {
        GrantClosure closure = closureService.getClosureById(closureToSave.getId());

        closure.setReason(processNewReasonIfPresent(closureToSave));
        closure.setDescription(closureToSave.getDescription());
        closure.setUpdatedAt(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());
        closure.setUpdatedBy(user.getId());
        try {
            closure.setClosureDetail(new ObjectMapper().writeValueAsString(closureToSave.getClosureDetails()));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }

        List<Report> approvedReports = null;
        if (closure.getLinkedApprovedReports() == null || closure.getLinkedApprovedReports().isEmpty()) {
            Optional<WorkflowStatus> optionalWorkflowStatus = workflowStatusService
                    .getTenantWorkflowStatuses(REPORT, closure.getGrant().getGrantorOrganization().getId())
                    .stream().filter(s -> s.getInternalStatus().equalsIgnoreCase(CLOSED)).findFirst();
            approvedReports = reportService.findByGrantAndStatus(closure.getGrant(),
                    optionalWorkflowStatus.isPresent()?optionalWorkflowStatus.get():null,
                    closure.getId());
            if (approvedReports == null || approvedReports.isEmpty()) {
                try {
                    closure.setLinkedApprovedReports(
                            new ObjectMapper().writeValueAsString(Arrays.asList( 0l )));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                try {
                    closure.setLinkedApprovedReports(new ObjectMapper().writeValueAsString(
                            approvedReports.stream().map(Report::getId).collect(Collectors.toList())));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            closure = closureService.saveClosure(closure);
        }

        processStringAttributes(user, closure, closureToSave, tenantOrg);

        closure = closureService.saveClosure(closure);

        return closure;
    }

    private ClosureReason processNewReasonIfPresent(GrantClosure closureToSave) {
        ClosureReason newReason = null;
        if (closureToSave.getReason() != null) {
            if (closureToSave.getReason().getId() <= 0) {
                newReason = closureToSave.getReason();
                newReason = closureService.saveReason(newReason);

            } else {
                newReason = closureToSave.getReason();
            }
        }
        return newReason;
    }

    private void processStringAttributes(User user, GrantClosure closure, GrantClosure closureToSave, Organization tenant) {
        ClosureSpecificSection closureSpecificSection = null;

        for (SectionVO sectionVO : closureToSave.getClosureDetails().getSections()) {
            closureSpecificSection = closureService.getClosureSpecificSectionById(sectionVO.getId());

            closureSpecificSection.setSectionName(sectionVO.getName());
            closureSpecificSection.setSectionOrder(sectionVO.getOrder());
            if ("ANUDAN".equalsIgnoreCase(tenant.getCode())) {
                closureSpecificSection.setGranter((Granter) closure.getGrant().getGrantorOrganization());
            } else {
                closureSpecificSection.setGranter((Granter) tenant);
            }

            closureSpecificSection.setDeletable(true);

            closureSpecificSection = closureService.saveClosureSpecificSection(closureSpecificSection);

            ClosureSpecificSectionAttribute sectionAttribute = null;

            if (sectionVO.getAttributes() != null) {
                for (SectionAttributesVO sectionAttributesVO : sectionVO.getAttributes()) {

                    sectionAttribute = closureService.getClosureStringByStringAttributeId(sectionAttributesVO.getId())
                            .getSectionAttribute();

                    sectionAttribute.setFieldName(sectionAttributesVO.getFieldName());
                    sectionAttribute.setFieldType(sectionAttributesVO.getFieldType());
                    if ("ANUDAN".equalsIgnoreCase(tenant.getCode())) {
                        sectionAttribute.setGranter((Granter) closure.getGrant().getGrantorOrganization());
                    } else {
                        sectionAttribute.setGranter((Granter) tenant);
                    }

                    sectionAttribute.setAttributeOrder(sectionAttributesVO.getAttributeOrder());
                    sectionAttribute.setRequired(true);
                    sectionAttribute.setSection(closureSpecificSection);

                    sectionAttribute = closureService.saveClosureSpecificSectionAttribute(sectionAttribute);

                    ClosureStringAttribute closureStringAttribute = closureService
                            .getClosureStringAttributeBySectionAttributeAndSection(sectionAttribute,
                                    closureSpecificSection);

                    closureStringAttribute.setTarget(sectionAttributesVO.getTarget());
                    closureStringAttribute.setFrequency(sectionAttributesVO.getFrequency());
                    if ((user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE) && !grantTypeService.findById(closure.getGrant().getGrantTypeId()).isInternal()) || (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER") && grantTypeService.findById(closure.getGrant().getGrantTypeId()).isInternal())) {
                        closureStringAttribute.setActualTarget(sectionAttributesVO.getActualTarget());
                    }
                    if (sectionAttribute.getFieldType().equalsIgnoreCase("table")
                            || sectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                        List<TableData> tableData = sectionAttributesVO.getFieldTableValue();
                        // Do the below only if field type is Disbursement
                        // The idea is to create a real disbursement if a new row is added
                        if (sectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)
                                && user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
                            try {
                                List<TableData> newEntries = new ArrayList<>();
                                List<TableData> missingEntries = new ArrayList<>();

                                // Find out new entries
                                if (tableData != null) {
                                    for (TableData td : tableData) {
                                        if (td.isStatus() && !td.isSaved()) {
                                            newEntries.add(td);
                                        }
                                    }
                                }

                                if (tableData != null) {
                                    for (TableData et : tableData) {
                                        if (!et.isStatus()) {
                                            missingEntries.add(et);
                                        }
                                    }
                                }

                                if (tableData != null && !tableData.isEmpty()) {

                                    for (TableData nData : tableData) {

                                        if (!nData.isSaved()) {
                                            ActualDisbursement actualDisbursement = disbursementService
                                                    .getActualDisbursementById(nData.getActualDisbursementId());

                                            actualDisbursement.setOtherSources(
                                                    Double.valueOf(nData.getColumns()[2].getValue() == null ? "0d"
                                                            : nData.getColumns()[2].getValue()));
                                            actualDisbursement.setDisbursementDate(new SimpleDateFormat(DD_MMM_YYYY)
                                                    .parse(nData.getColumns()[0].getValue()));
                                            actualDisbursement.setNote(nData.getColumns()[3].getValue());
                                            actualDisbursement.setActualAmount(0d);
                                            actualDisbursement.setCreatedAt(DateTime.now().toDate());
                                            actualDisbursement.setCreatedBy(user.getId());
                                            actualDisbursement.setStatus(nData.isStatus());
                                            actualDisbursement.setSaved(false);
                                            actualDisbursement.setOrderPosition(
                                                    disbursementService.getNewOrderPositionForActualDisbursementOfGrant(
                                                            closure.getGrant().getId()));
                                            disbursementService.saveActualDisbursement(actualDisbursement);
                                        }
                                    }

                                }
                            } catch (ParseException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }

                    } else {
                        closureStringAttribute.setValue(sectionAttributesVO.getFieldValue());
                    }
                    closureService.saveClosureStringAttribute(closureStringAttribute);
                }
            }
        }
    }

    @PostMapping("/{closureId}/section/{sectionId}/field")
    public ClosureFieldInfo createFieldInSection(
            @RequestBody GrantClosure closureToSave,
            @PathVariable("closureId") Long closureId,
            @PathVariable("sectionId") Long sectionId,
            @PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {

        saveClosure(closureId, closureToSave, userId, tenantCode);
        GrantClosure closure = closureService.getClosureById(closureId);
        ClosureSpecificSection closureSection = closureService.getClosureSpecificSectionById(sectionId);

        ClosureSpecificSectionAttribute newSectionAttribute = new ClosureSpecificSectionAttribute();
        newSectionAttribute.setSection(closureSection);
        newSectionAttribute.setRequired(false);
        newSectionAttribute.setFieldType("multiline");
        newSectionAttribute.setFieldName(STRNOSPACE);
        newSectionAttribute.setDeletable(true);
        newSectionAttribute.setCanEdit(true);
        newSectionAttribute.setAttributeOrder(closureService.getNextAttributeOrder(
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), sectionId));
        newSectionAttribute.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
        newSectionAttribute = closureService.saveClosureSpecificSectionAttribute(newSectionAttribute);
        ClosureStringAttribute stringAttribute = new ClosureStringAttribute();
        stringAttribute.setValue(STRNOSPACE);
        stringAttribute.setSectionAttribute(newSectionAttribute);
        stringAttribute.setSection(closureSection);
        stringAttribute.setClosure(closure);

        stringAttribute = closureService.saveClosureStringAttribute(stringAttribute);

        if (closureService.checkIfClosureTemplateChanged(closure, closureSection, newSectionAttribute)) {
            closureService._createNewClosureTemplateFromExisiting(closure);
        }

        closure = closureToReturn(closure, userId);
        return new ClosureFieldInfo(newSectionAttribute.getId(), stringAttribute.getId(), closure);
    }

    @PutMapping("/{closureId}/template/{templateId}/section/{sectionId}")
    public GrantClosure deleteSection(@RequestBody GrantClosure closureToSave,
                                @PathVariable("closureId") Long closureId,
                                @PathVariable("templateId") Long templateId,
                                @PathVariable("sectionId") Long sectionId,
                                @PathVariable("userId") Long userId,
                                @RequestHeader("X-TENANT-CODE") String tenantCode) {

        ClosureSpecificSection section = closureService.getClosureSpecificSectionById(sectionId);
        GrantClosure closure = closureService.getClosureById(closureId);

        for (ClosureSpecificSectionAttribute attrib : closureService.getSpecificSectionAttributesBySection(section)) {
            for (ClosureStringAttribute stringAttrib : closureService.getClosureStringAttributesByAttribute(attrib)) {
                if (stringAttrib != null) {
                    closureService.deleteStringAttribute(stringAttrib);

                    closure.getStringAttributes().removeIf(e -> e.getId().longValue() == stringAttrib.getId().longValue());
                }
            }
        }

        closureService.deleteSectionAttributes(closureService.getSpecificSectionAttributesBySection(section));
        closureService.deleteSection(section);

        closure = closureService.getClosureById(closureId);
        if (closureService.checkIfClosureTemplateChanged(closure, section, null)) {
            GranterClosureTemplate newTemplate = closureService._createNewClosureTemplateFromExisiting(closure);
            newTemplate.getId();
        }
        closure = closureToReturn(closure, userId);
        return closure;
    }

    @PutMapping("/{closureId}/section/{sectionId}/field/{fieldId}")
    public ClosureFieldInfo updateField(
            @PathVariable("sectionId") Long sectionId,
            @RequestBody ClosureAttributeToSaveVO attributeToSave,
            @PathVariable("closureId") Long closureId,
            @PathVariable("fieldId") Long fieldId,
            @PathVariable("userId") Long userId,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {

        saveClosure(closureId, attributeToSave.getClosure(), userId, tenantCode);
        ClosureSpecificSectionAttribute currentAttribute = closureService.getClosureStringByStringAttributeId(fieldId)
                .getSectionAttribute();
        currentAttribute.setFieldName(attributeToSave.getAttr().getFieldName());
        currentAttribute.setFieldType(attributeToSave.getAttr().getFieldType());
        currentAttribute = closureService.saveClosureSpecificSectionAttribute(currentAttribute);
        ClosureStringAttribute stringAttribute = closureService
                .getClosureStringAttributeBySectionAttributeAndSection(currentAttribute, currentAttribute.getSection());
        if (currentAttribute.getFieldType().equalsIgnoreCase("kpi")) {
            stringAttribute.setFrequency("adhoc");
        }
        stringAttribute = closureService.saveClosureStringAttribute(stringAttribute);

        GrantClosure closure = closureService.getClosureById(closureId);
        if (closureService.checkIfClosureTemplateChanged(closure, currentAttribute.getSection(), currentAttribute)) {
            closureService._createNewClosureTemplateFromExisiting(closure);
        }

        closure = closureToReturn(closure, userId);
        return new ClosureFieldInfo(currentAttribute.getId(), stringAttribute.getId(), closure);
    }

    @PutMapping("/{closureId}/template/{templateId}/{templateName}")
    public GrantClosure updateTemplateName(
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "closureId", value = "Unique identifier of the report") @PathVariable("closureId") Long closureId,
            @ApiParam(name = "templateId", value = "Unique identfier of the grant template") @PathVariable("templateId") Long templateId,
            @ApiParam(name = "templateName", value = "NName of the template to be saved") @PathVariable("templateName") String templateName,
            @ApiParam(name = "templateDate", value = "Additional information about the template such as descriptio, publish or save as private") @RequestBody TemplateMetaData templateData) {

        GranterClosureTemplate template = closureService.findByTemplateId(templateId);
        template.setName(templateName);
        template.setDescription(templateData.getDescription());
        template.setPublished(templateData.isPublish());
        template.setPrivateToClosure(templateData.isPrivateToGrant());
        template.setPublished(true);
        closureService.saveClosureTemplate(template);

        GrantClosure closure = closureService.getClosureById(closureId);
        return closureToReturn(closure, userId);
    }

    @PostMapping("/{closureId}/field/{fieldId}/template/{templateId}")
    public ClosureDocInfo createDocumentForClosureSectionField(
            @RequestBody GrantClosure closureToSave,
            @PathVariable("userId") Long userId,
            @PathVariable("closureId") Long closureId,
            @PathVariable("fieldId") Long fieldId,
            @PathVariable("templateId") Long templateId,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {
        saveClosure(closureId, closureToSave, userId, tenantCode);
        TemplateLibrary libraryDoc = templateLibraryService.getTemplateLibraryDocumentById(templateId);

        ClosureStringAttribute stringAttribute = closureService.getClosureStringByStringAttributeId(fieldId);

        File file = null;
        String filePath = null;
        try {
            file = resourceLoader
                    .getResource(FILE + uploadLocation + URLDecoder.decode(libraryDoc.getLocation(), "UTF-8"))
                    .getFile();

            User user = userService.getUserById(userId);

            if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
                filePath = uploadLocation + user.getOrganization().getName().toUpperCase() + CLOSURE_DOCUMENTS
                        + closureId + File.pathSeparator + stringAttribute.getSection().getId() + File.pathSeparator
                        + stringAttribute.getSectionAttribute().getId() + File.pathSeparator;
            } else {
                filePath = uploadLocation + user.getOrganization().getCode().toUpperCase() + CLOSURE_DOCUMENTS + closureId + File.pathSeparator
                        + stringAttribute.getSection().getId() + File.pathSeparator + stringAttribute.getSectionAttribute().getId()
                        + File.pathSeparator;
            }

            File dir = new File(filePath);
            dir.mkdirs();
            File fileToCreate = new File(dir, libraryDoc.getName() + "." + libraryDoc.getType());
            FileCopyUtils.copy(file, fileToCreate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClosureStringAttributeAttachments attachment = new ClosureStringAttributeAttachments();
        attachment.setCreatedBy(userService.getUserById(userId).getEmailId());
        attachment.setCreatedOn(new Date());
        attachment.setDescription(libraryDoc.getDescription());
        attachment.setClosureStringAttribute(stringAttribute);
        attachment.setLocation(filePath);
        attachment.setName(libraryDoc.getName());
        attachment.setTitle(STRNOSPACE);
        attachment.setType(libraryDoc.getType());
        attachment.setVersion(1);
        attachment = closureService.saveClosureStringAttributeAttachment(attachment);

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ClosureStringAttributeAttachments> stringAttributeAttachments = closureService
                    .getStringAttributeAttachmentsByStringAttribute(stringAttribute);
            stringAttribute.setValue(mapper.writeValueAsString(stringAttributeAttachments));
            closureService.saveClosureStringAttribute(stringAttribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        GrantClosure closure = closureService.getClosureById(closureId);
        closure = closureToReturn(closure, userId);
        return new ClosureDocInfo(attachment.getId(), closure);
    }

    @PostMapping(value = "/{closureId}/section/{sectionId}/attribute/{attributeId}/upload", consumes = {
            "multipart/form-data" })
    public ClosureDocInfo saveUploadedFiles(
            @PathVariable("sectionId") Long sectionId,
            @PathVariable("userId") Long userId,
            @PathVariable("closureId") Long closureId,
            @PathVariable("attributeId") Long attributeId,
            @RequestParam("closureToSave") String closureToSaveStr,
            @RequestParam("file") MultipartFile[] files) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mapper.readValue(closureToSaveStr, GrantClosure.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        GrantClosure closure = closureService.getClosureById(closureId);

        ClosureStringAttribute attr = closureService.getClosureStringByStringAttributeId(attributeId);
        User user = userService.getUserById(userId);

        String filePath = STRNOSPACE;
            filePath = uploadLocation + user.getOrganization().getName().toUpperCase() + CLOSURE_DOCUMENTS + closureId
                    + File.pathSeparator + attr.getSection().getId() + File.pathSeparator + attr.getSectionAttribute().getId() + File.pathSeparator;
        File dir = new File(filePath);
        dir.mkdirs();
        List<ClosureStringAttributeAttachments> attachments = new ArrayList<>();



        for (MultipartFile file : files) {
            if(file.getOriginalFilename()!=null){
            String fileName = file.getOriginalFilename();
            File fileToCreate = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(fileToCreate)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            ClosureStringAttributeAttachments attachment = new ClosureStringAttributeAttachments();
            attachment.setVersion(1);
            attachment.setType(FilenameUtils.getExtension(file.getOriginalFilename()));
            attachment.setTitle(file.getOriginalFilename()!=null?file.getOriginalFilename():""
                    .replace("." + FilenameUtils.getExtension(file.getOriginalFilename()), STRNOSPACE));
            attachment.setLocation(filePath);
            attachment.setName(file.getOriginalFilename()!=null?file.getOriginalFilename():""
                    .replace("." + FilenameUtils.getExtension(file.getOriginalFilename()), STRNOSPACE));
            attachment.setClosureStringAttribute(attr);
            attachment.setDescription(file.getOriginalFilename()!=null?file.getOriginalFilename():""
                    .replace("." + FilenameUtils.getExtension(file.getOriginalFilename()), STRNOSPACE));
            attachment.setCreatedOn(new Date());
            attachment.setCreatedBy(userService.getUserById(userId).getEmailId());
            attachment = closureService.saveClosureStringAttributeAttachment(attachment);
            attachments.add(attachment);
        }
        }

        mapper = new ObjectMapper();
        try {
            if (attr.getValue() == null || attr.getValue().equalsIgnoreCase(STRNOSPACE)) {
                attr.setValue("[]");
            }
            List<ClosureStringAttributeAttachments> currentAttachments = mapper.readValue(attr.getValue(),
                    new TypeReference<List<ClosureStringAttributeAttachments>>() {
                    });
            if (currentAttachments == null) {
                currentAttachments = new ArrayList<>();
            }
            currentAttachments.addAll(attachments);

            attr.setValue(mapper.writeValueAsString(currentAttachments));
            attr = closureService.saveClosureStringAttribute(attr);
            ClosureStringAttribute finalAttr = attr;
            ClosureStringAttribute finalAttr1 = finalAttr;
            Optional<ClosureStringAttribute> optionalClosureStringAttribute = closure.getStringAttributes().stream().filter(g -> g.getId().longValue() == finalAttr1.getId().longValue()).findFirst();
            finalAttr = optionalClosureStringAttribute.isPresent()?
                    optionalClosureStringAttribute.get():null;
                if(finalAttr!=null){
                    finalAttr.setValue(mapper.writeValueAsString(currentAttachments));
                }

            closureService.saveClosure(closure);

        } catch (IOException e) {
            e.printStackTrace();
        }

        closure = closureService.getClosureById(closureId);
        closure = closureToReturn(closure, userId);

        return new ClosureDocInfo(attachments.get(attachments.size() - 1).getId(), closure);
    }

    @PostMapping(value = "/{closureId}/attachments", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadSelectedAttachments(@PathVariable("userId") Long userId,
                                              @PathVariable("closureId") Long closureId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                                              @RequestBody AttachmentDownloadRequest downloadRequest, HttpServletResponse response) throws IOException {

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
        files.add(new File("README.md"));

        User user = userService.getUserById(userId);

        // packing files
        for (Long attachmentId : downloadRequest.getAttachmentIds()) {
            ClosureStringAttributeAttachments attachment = closureService
                    .getStringAttributeAttachmentsByAttachmentId(attachmentId);
            Long sectionId = attachment.getClosureStringAttribute().getSectionAttribute().getSection().getId();
            Long attributeId = attachment.getClosureStringAttribute().getSectionAttribute().getId();
            File file = null;
            if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
                file = resourceLoader.getResource(FILE + uploadLocation
                        + user.getOrganization().getName().toUpperCase() + CLOSURE_DOCUMENTS + closureId + File.pathSeparator
                        + sectionId + File.pathSeparator + attributeId + File.pathSeparator + attachment.getName() + "." + attachment.getType())
                        .getFile();
                if (!file.exists()) {
                    file = resourceLoader
                            .getResource(FILE + uploadLocation
                                    + closureService.getClosureById(closureId).getGrant().getGrantorOrganization()
                                    .getCode().toUpperCase()
                                    + CLOSURE_DOCUMENTS + closureId + File.pathSeparator + sectionId + File.pathSeparator + attributeId + File.pathSeparator
                                    + attachment.getName() + "." + attachment.getType())
                            .getFile();
                }
            } else {
                file = resourceLoader.getResource(
                        FILE + uploadLocation + tenantCode + CLOSURE_DOCUMENTS + closureId + File.pathSeparator + sectionId + File.pathSeparator
                                + attributeId + File.pathSeparator + attachment.getName() + "." + attachment.getType())
                        .getFile();
                if (!file.exists()) {

                    file = resourceLoader
                            .getResource(FILE + uploadLocation
                                    + closureService.getClosureById(closureId).getGrant().getOrganization().getName()
                                    .toUpperCase()
                                    + CLOSURE_DOCUMENTS + closureId + File.pathSeparator + sectionId + File.pathSeparator + attributeId + File.pathSeparator
                                    + attachment.getName() + "." + attachment.getType())
                            .getFile();
                }
            }
            // new zip entry and copying inputstream with file to zipOutputStream, after all
            // closing streams
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            try(FileInputStream fileInputStream = new FileInputStream(file)){
                IOUtils.copy(fileInputStream, zipOutputStream);
                zipOutputStream.closeEntry();
            }
        }

            zipOutputStream.finish();
            zipOutputStream.flush();
            zipOutputStream.close();

        bufferedOutputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    @PostMapping("{closureId}/attribute/{attributeId}/attachment/{attachmentId}")
    public GrantClosure deleteClosureStringAttributeAttachment(
            @RequestBody GrantClosure closureToSave,
            @PathVariable("closureId") Long closureId,
            @PathVariable("userId") Long userId,
            @PathVariable("attachmentId") Long attachmentId,
            @RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("attributeId") Long attributeId) {
        saveClosure(closureId, closureToSave, userId, tenantCode);
        ClosureStringAttributeAttachments attch = closureService
                .getStringAttributeAttachmentsByAttachmentId(attachmentId);
        closureService.deleteStringAttributeAttachments(Arrays.asList(attch));

        File file = new File(attch.getLocation() + attch.getName() + "." + attch.getType());
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        ClosureStringAttribute stringAttribute = closureService.findClosureStringAttributeById(attributeId);
        List<ClosureStringAttributeAttachments> stringAttributeAttachments = closureService
                .getStringAttributeAttachmentsByStringAttribute(stringAttribute);
        ObjectMapper mapper = new ObjectMapper();
        try {
            stringAttribute.setValue(mapper.writeValueAsString(stringAttributeAttachments));
            closureService.saveClosureStringAttribute(stringAttribute);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }

        GrantClosure closure = closureService.getClosureById(closureId);

        closure = closureToReturn(closure, userId);
        return closure;
    }

    @PostMapping("/{closureId}/section/{sectionId}/field/{fieldId}")
    public GrantClosure deleteField(
            @RequestBody GrantClosure closureToSave,
            @PathVariable("userId") Long userId,
            @PathVariable("closureId") Long closureId,
            @PathVariable("sectionId") Long sectionId,
            @PathVariable("fieldId") Long fieldId,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {
        GrantClosure closure = saveClosure(closureId, closureToSave, userId, tenantCode);

        ClosureStringAttribute stringAttrib = closureService.getClosureStringByStringAttributeId(fieldId);
        ClosureSpecificSectionAttribute attribute = stringAttrib.getSectionAttribute();

        if (stringAttrib.getSectionAttribute().getFieldType().equalsIgnoreCase("document")) {
            List<ClosureStringAttributeAttachments> attachments = closureService
                    .getStringAttributeAttachmentsByStringAttribute(stringAttrib);
            closureService.deleteStringAttributeAttachments(attachments);
        }
        closureService.deleteStringAttribute(stringAttrib);
        closureService.deleteSectionAttribute(attribute);
        Optional<ClosureStringAttribute> optionalClosureStringAttribute = closure.getStringAttributes().stream()
                .filter(g -> g.getId().longValue() == stringAttrib.getId().longValue()).findFirst();
        ClosureStringAttribute rsa2Delete = optionalClosureStringAttribute.isPresent()?optionalClosureStringAttribute.get():null;
        closure.getStringAttributes().remove(rsa2Delete);
        closure = closureService.saveClosure(closure);

        if (closureService.checkIfClosureTemplateChanged(closure, attribute.getSection(), null)) {
            closureService._createNewClosureTemplateFromExisiting(closure);
        }
        closure = closureToReturn(closure, userId);
        return closure;
    }



    @PostMapping("/{closureId}/assignment")
    public GrantClosure saveReportAssignments(
            @PathVariable("userId") Long userId,
            @PathVariable("closureId") Long closureId,
            @RequestBody ClosureAssignmentModel assignmentModel,
            @RequestHeader("X-TENANT-CODE") String tenantCode) {
        GrantClosure closure = saveClosure(closureId, assignmentModel.getClosure(), userId, tenantCode);

        Map<Long, Long> currentAssignments = new LinkedHashMap<>();
        if (closureService.checkIfClosureMovedThroughWFAtleastOnce(closure.getId())) {

            closureService.getAssignmentsForClosure(closure).stream().forEach(a ->
                currentAssignments.put(a.getStateId(), a.getAssignment()));
        }
        String customAss = null;
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
        String host = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme())
                .host(host).port(uriComponents.getPort());
        String url = uriBuilder.toUriString();
        for (ClosureAssignmentsVO assignmentsVO : assignmentModel.getAssignments()) {
            if (customAss == null && assignmentsVO.getCustomAssignments() != null) {
                customAss = assignmentsVO.getCustomAssignments();
            }
            ClosureAssignments assignment = null;
            if (assignmentsVO.getId() == null) {
                assignment = new ClosureAssignments();
                assignment.setStateId(assignmentsVO.getStateId());
                assignment.setClosure(closure);
            } else {
                assignment = closureService.getClosureAssignmentById(assignmentsVO.getId());
            }

            assignment.setAssignment(assignmentsVO.getAssignmentId());
            assignment.setUpdatedBy(userId);
            assignment.setAssignedOn(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());

            if ((customAss != null && !STRNOSPACE.equalsIgnoreCase(customAss.trim())) && workflowStatusService
                    .getById(assignmentsVO.getStateId()).getInternalStatus().equalsIgnoreCase(ACTIVE)) {
                User granteeUser = null;
                User existingUser = userService.getUserByEmailAndOrg(customAss, closure.getGrant().getOrganization());
                String code = null;

                code = Base64.getEncoder().encodeToString(String.valueOf(closure.getId()).getBytes());

                if (existingUser != null && existingUser.isActive()) {
                    granteeUser = existingUser;
                    url = new StringBuilder(url + "/home/?action=login&org="
                            + URLEncoder.encode(closure.getGrant().getOrganization().getName()) + "&r=" + code
                            + EMAIL + granteeUser.getEmailId() + "&type=closure").toString();
                } else if (existingUser != null && !existingUser.isActive()) {
                    granteeUser = existingUser;
                    url = new StringBuilder(url + "/home/?action=registration&org="
                            + URLEncoder.encode(closure.getGrant().getOrganization().getName()) + "&r=" + code
                            + EMAIL + granteeUser.getEmailId() + "&type=closure").toString();

                } else {
                    granteeUser = new User();
                    Role newRole = roleService.findByOrganizationAndName(closure.getGrant().getOrganization(), "Admin");

                    UserRole userRole = new UserRole();
                    userRole.setRole(newRole);
                    userRole.setUser(granteeUser);

                    List<UserRole> userRoles = new ArrayList<>();
                    userRoles.add(userRole);
                    granteeUser.setUserRoles(userRoles);
                    granteeUser.setFirstName(STRNOSPACE);
                    granteeUser.setLastName(STRNOSPACE);
                    granteeUser.setEmailId(customAss);
                    granteeUser.setOrganization(closure.getGrant().getOrganization());
                    granteeUser.setActive(false);
                    granteeUser = userService.save(granteeUser);
                    userRoleService.saveUserRole(userRole);
                    url = new StringBuilder(url + "/home/?action=registration&org="
                            + URLEncoder.encode(closure.getGrant().getOrganization().getName()) + "&r=" + code
                            + EMAIL + granteeUser.getEmailId() + "&type=report").toString();
                }

                String[] notifications = closureService.buildClosureInvitationContent(closure,
                        appConfigService.getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.CLOSURE_INVITE_SUBJECT).getConfigValue(),
                        appConfigService.getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.CLOSURE_INVITE_MESSAGE).getConfigValue(),
                        url);
                commonEmailSevice.sendMail(new String[] { !granteeUser.isDeleted() ? granteeUser.getEmailId() : null },
                        null, notifications[0], notifications[1],
                        new String[] { appConfigService
                                .getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                .getConfigValue()
                                .replace(RELEASE_VERSION, releaseService.getCurrentRelease().getVersion()) });

                assignment.setAssignment(granteeUser.getId());
            }

            closureService.saveAssignmentForClosure(assignment);
        }

        if (currentAssignments.size() > 0) {

            List<ClosureAssignments> newAssignments = closureService.getAssignmentsForClosure(closure);

            String[] notifications = closureService.buildEmailNotificationContent(closure,
                    userService.getUserById(userId), null, null, null,
                    appConfigService.getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_MESSAGE).getConfigValue(),
                    null, null, null, null, null, null, null, null, null, null, null, null, currentAssignments,
                    newAssignments);
            List<User> toUsers = newAssignments.stream().map(ClosureAssignments::getAssignment)
                    .map(uid -> userService.getUserById(uid)).collect(Collectors.toList());
            toUsers.removeIf(User::isDeleted);
            List<User> ccUsers = currentAssignments.values().stream().map(uid -> userService.getUserById(uid))
                    .collect(Collectors.toList());
            ccUsers.removeIf(User::isDeleted);

            commonEmailSevice
                    .sendMail(
                            toUsers.stream().map(User::getEmailId).collect(Collectors.toList())
                                    .toArray(new String[toUsers.size()]),
                            ccUsers.stream().map(User::getEmailId).collect(
                                    Collectors.toList()).toArray(new String[ccUsers.size()]),
                            notifications[0], notifications[1],
                            new String[] { appConfigService
                                    .getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                                            AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                    .getConfigValue().replace(RELEASE_VERSION,
                                    releaseService.getCurrentRelease().getVersion()) });

            Map<Long, Long> cleanAsigneesList = new HashMap<>();
            for (Long ass : currentAssignments.values()) {
                cleanAsigneesList.put(ass, ass);
            }
            for (ClosureAssignments ass : newAssignments) {
                cleanAsigneesList.put(ass.getAssignment(), ass.getAssignment());
            }
            notifications = closureService.buildEmailNotificationContent(closure, userService.getUserById(userId), null,
                    null, null,
                    appConfigService.getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(closure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_MESSAGE).getConfigValue(),
                    null, null, null, null, null, null, null, null, null, null, null, null, currentAssignments,
                    newAssignments);

            final String[] finaNotifications = notifications;
            final GrantClosure finalClosure = closure;

            cleanAsigneesList.keySet().stream().forEach(u ->
                notificationsService.saveNotification(finaNotifications, u, finalClosure.getId(), REPORT)
            );

        }

        closure = closureToReturn(closure, userId);
        return closure;
    }

    @PostMapping("/{closureId}/flow/{fromState}/{toState}")
    public GrantClosure moveClosureState(@RequestBody ClosureWithNote closureWithNote,
                                        @PathVariable("userId") Long userId,
                                        @PathVariable("closureId") Long closureId,
                                        @PathVariable("fromState") Long fromStateId,
                                        @PathVariable("toState") Long toStateId,
                                        @RequestHeader("X-TENANT-CODE") String tenantCode) {

        saveClosure(closureId, closureWithNote.getClosure(), userId, tenantCode);

        GrantClosure closure = closureService.getClosureById(closureId);
        GrantClosure finalClosure = closure;
        WorkflowStatus previousState = closure.getStatus();

        User updatingUser = userService.getUserById(userId);
        if (updatingUser.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)
                && previousState.getInternalStatus().equalsIgnoreCase(ACTIVE)) {
            ClosureAssignments changedAssignment = closureService.getAssignmentsForClosure(closure).stream()
                    .filter(ass -> ass.getClosure().getId().longValue() == closureId.longValue()
                            && ass.getStateId().longValue() == finalClosure.getStatus().getId().longValue())
                    .collect(Collectors.toList()).get(0);
            changedAssignment.setAssignment(userId);
            closureService.saveAssignmentForClosure(changedAssignment);
        }
        ClosureAssignments currentAssignment = closureService.getAssignmentsForClosure(closure).stream()
                .filter(ass -> ass.getClosure().getId().longValue() == closureId.longValue()
                        && ass.getStateId().longValue() == finalClosure.getStatus().getId().longValue())
                .collect(Collectors.toList()).get(0);
        User previousOwner = userService.getUserById(currentAssignment.getAssignment());

        closure.setStatus(workflowStatusService.findById(toStateId));

        closure.setNote((closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE))
                ? closureWithNote.getNote()
                : "No note added");
        closure.setNoteAdded(new Date());
        closure.setNoteAddedBy(userId);

        Date currentDateTime = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate();
        closure.setUpdatedAt(currentDateTime);
        closure.setUpdatedBy(userId);
        closure.setMovedOn(currentDateTime);
        closure = closureService.saveClosure(closure);

        WorkflowStatus toStatus = workflowStatusService.findById(toStateId);

        List<User> usersToNotify = new ArrayList<>();
        List<ClosureAssignments> assigments = closureService.getAssignmentsForClosure(closure);
        assigments.forEach(ass -> {
            if (usersToNotify.stream().noneMatch(u -> u.getId().longValue() == ass.getAssignment().longValue())) {
                usersToNotify.add(userService.getUserById(ass.getAssignment()));
            }
        });

        Optional<ClosureAssignments> closureAss = closureService.getAssignmentsForClosure(closure).stream()
                .filter(ass -> ass.getClosure().getId().longValue() == closureId.longValue()
                        && ass.getStateId().longValue() == toStateId.longValue())
                .findAny();
        User currentOwner = null;
        String currentOwnerName = STRNOSPACE;
        if (closureAss.isPresent()) {
            currentOwner = userService.getUserById(closureAss.get().getAssignment());
            currentOwnerName = currentOwner.getFirstName().concat(STREMPTY).concat(currentOwner.getLastName());
        }

        WorkflowStatusTransition transition = workflowStatusTransitionService.findByFromAndToStates(previousState,
                toStatus);

        WorkflowStatus currentState = workflowStatusService.findById(toStateId);
        if (!updatingUser.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)
                && !currentState.getInternalStatus().equalsIgnoreCase(ACTIVE)
                && !currentState.getInternalStatus().equalsIgnoreCase(CLOSED)) {
            usersToNotify.removeIf(u -> u.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE));
        }

        String finalCurrentOwnerName = currentOwnerName;
        User finalCurrentOwner = currentOwner;
        if (toStatus.getInternalStatus().equalsIgnoreCase(ACTIVE)) {
            usersToNotify
                    .removeIf(u -> u.getId().longValue() == finalCurrentOwner.getId().longValue() || u.isDeleted());
            String[] emailNotificationContent = closureService.buildEmailNotificationContent(finalClosure,
                    finalCurrentOwner, currentOwner.getFirstName().concat(STREMPTY).concat(currentOwner.getLastName()), null,
                    new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                    appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                    workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                    previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                    transition.getAction(), YES, PLEASE_REVIEW,
                    closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE) ? YES
                            : NO,
                    closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                            ? PLEASE_REVIEW
                            : STRNOSPACE,
                    null, null, null, null, null);
            commonEmailSevice
                    .sendMail(new String[] { !currentOwner.isDeleted() ? currentOwner.getEmailId() : null },
                            usersToNotify.stream().map(User::getEmailId).collect(Collectors.toList())
                                    .toArray(new String[usersToNotify.size()]),
                            emailNotificationContent[0], emailNotificationContent[1],
                            new String[] { appConfigService
                                    .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                            AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                    .getConfigValue().replace(RELEASE_VERSION,
                                    releaseService.getCurrentRelease().getVersion()) });

            String[] notificationContent = closureService.buildEmailNotificationContent(finalClosure, currentOwner,
                    currentOwner.getFirstName().concat(STREMPTY).concat(currentOwner.getLastName()), toStatus.getVerb(),
                    new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                    appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                    workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                    previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                    transition.getAction(), YES, PLEASE_REVIEW,
                    closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE) ? YES
                            : NO,
                    closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                            ? PLEASE_REVIEW
                            : STRNOSPACE,
                    null, null, null, null, null);

            notificationsService.saveNotification(notificationContent, currentOwner.getId(), finalClosure.getId(),
                    REPORT);

            usersToNotify.stream().forEach(u -> {
                final String[] nc = closureService.buildEmailNotificationContent(finalClosure, u,
                        u.getFirstName().concat(STREMPTY).concat(u.getLastName()), toStatus.getVerb(),
                        new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                        appConfigService
                                .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_SUBJECT)
                                .getConfigValue(),
                        appConfigService
                                .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_MESSAGE)
                                .getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName,
                        previousState.getName(),
                        previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                        transition.getAction(), YES, PLEASE_REVIEW,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                                ? YES
                                : NO,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                                ? PLEASE_REVIEW
                                : STRNOSPACE,
                        null, null, null, null, null);

                notificationsService.saveNotification(nc, u.getId(), finalClosure.getId(), REPORT);
            });
        } else if (!toStatus.getInternalStatus().equalsIgnoreCase(CLOSED)) {
            usersToNotify
                    .removeIf(u -> u.getId().longValue() == finalCurrentOwner.getId().longValue() || u.isDeleted());
            if (!workflowStatusService.findById(fromStateId).getInternalStatus().equalsIgnoreCase(ACTIVE)) {
                usersToNotify.removeIf(u -> u.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE));
            }

            String[] emailNotificationContent = closureService.buildEmailNotificationContent(finalClosure,
                    finalCurrentOwner, currentOwner.getFirstName().concat(STREMPTY).concat(currentOwner.getLastName()), null,
                    new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                    appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                    workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                    previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                    transition.getAction(), YES, PLEASE_REVIEW,
                    closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE) ? YES
                            : NO,
                    closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                            ? PLEASE_REVIEW
                            : STRNOSPACE,
                    null, null, null, null, null);
            commonEmailSevice
                    .sendMail(new String[] { !currentOwner.isDeleted() ? currentOwner.getEmailId() : null },
                            usersToNotify.stream().map(User::getEmailId).collect(Collectors.toList())
                                    .toArray(new String[usersToNotify.size()]),
                            emailNotificationContent[0], emailNotificationContent[1],
                            new String[] { appConfigService
                                    .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                            AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                    .getConfigValue().replace(RELEASE_VERSION,
                                    releaseService.getCurrentRelease().getVersion()) });

            String[] notificationContent = closureService.buildEmailNotificationContent(finalClosure, currentOwner,
                    currentOwner.getFirstName().concat(STREMPTY).concat(currentOwner.getLastName()), toStatus.getVerb(),
                    new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                    appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                    workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                    previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                    transition.getAction(), YES, PLEASE_REVIEW,
                    closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE) ? YES
                            : NO,
                    closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                            ? PLEASE_REVIEW
                            : STRNOSPACE,
                    null, null, null, null, null);

            notificationsService.saveNotification(notificationContent, currentOwner.getId(), finalClosure.getId(),
                    REPORT);

            usersToNotify.stream().forEach(u -> {
                final String[] nc = closureService.buildEmailNotificationContent(finalClosure, u,
                        u.getFirstName().concat(STREMPTY).concat(u.getLastName()), toStatus.getVerb(),
                        new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                        appConfigService
                                .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_SUBJECT)
                                .getConfigValue(),
                        appConfigService
                                .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_MESSAGE)
                                .getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName,
                        previousState.getName(),
                        previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                        transition.getAction(), YES, PLEASE_REVIEW,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                                ? YES
                                : NO,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                                ? PLEASE_REVIEW
                                : STRNOSPACE,
                        null, null, null, null, null);

                notificationsService.saveNotification(nc, u.getId(), finalClosure.getId(), REPORT);
            });
        } else {

            Optional<User> granteeUsr =  usersToNotify.stream()
                    .filter(u -> u.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)).findFirst();
            if(granteeUsr.isPresent()){
                User granteeUser = granteeUsr.get();
                usersToNotify.removeIf(u -> u.getId().longValue() == granteeUser.getId().longValue() || u.isDeleted());

                String[] emailNotificationContent = closureService.buildEmailNotificationContent(finalClosure, granteeUser,
                        granteeUser.getFirstName().concat(STREMPTY).concat(granteeUser.getLastName()), null,
                        new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                        appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                        appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                        previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                        transition.getAction(), YES, PLEASE_REVIEW,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE) ? YES
                                : NO,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                                ? PLEASE_REVIEW
                                : STRNOSPACE,
                        null, null, null, null, null);
                commonEmailSevice
                        .sendMail(new String[] { !granteeUser.isDeleted() ? granteeUser.getEmailId() : null },
                                usersToNotify.stream().map(User::getEmailId).collect(Collectors.toList())
                                        .toArray(new String[usersToNotify.size()]),
                                emailNotificationContent[0], emailNotificationContent[1],
                                new String[] { appConfigService
                                        .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                        .getConfigValue().replace(RELEASE_VERSION,
                                        releaseService.getCurrentRelease().getVersion()) });

                String[] notificationContent = closureService.buildEmailNotificationContent(finalClosure, granteeUser,
                        granteeUser.getFirstName().concat(STREMPTY).concat(granteeUser.getLastName()), toStatus.getVerb(),
                        new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                        appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                        appConfigService.getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                        previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                        transition.getAction(), YES, PLEASE_REVIEW,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE) ? YES
                                : NO,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                                ? PLEASE_REVIEW
                                : STRNOSPACE,
                        null, null, null, null, null);

                notificationsService.saveNotification(notificationContent, granteeUser.getId(), finalClosure.getId(),
                        REPORT);
            }


            usersToNotify.stream().forEach(u -> {
                final String[] nc = closureService.buildEmailNotificationContent(finalClosure, u,
                        u.getFirstName().concat(STREMPTY).concat(u.getLastName()), toStatus.getVerb(),
                        new SimpleDateFormat(DD_MMM_YYYY).format(DateTime.now().toDate()),
                        appConfigService
                                .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_SUBJECT)
                                .getConfigValue(),
                        appConfigService
                                .getAppConfigForGranterOrg(finalClosure.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.CLOSURE_STATE_CHANGED_MAIL_MESSAGE)
                                .getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName,
                        previousState.getName(),
                        previousOwner.getFirstName().concat(STREMPTY).concat(previousOwner.getLastName()),
                        transition.getAction(), YES, PLEASE_REVIEW,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                                ? YES
                                : NO,
                        closureWithNote.getNote() != null && !closureWithNote.getNote().trim().equalsIgnoreCase(STRNOSPACE)
                                ? PLEASE_REVIEW
                                : STRNOSPACE,
                        null, null, null, null, null);

                notificationsService.saveNotification(nc, u.getId(), finalClosure.getId(), REPORT);
            });

        }

        closure = closureToReturn(closure, userId);
        saveSnapShot(closure, fromStateId, toStateId, currentOwner, previousOwner);

        if (toStatus.getInternalStatus().equalsIgnoreCase(CLOSED)) {

            Grant grant = grantService.getById(closure.getGrant().getId());
            Optional<WorkflowStatus> optionalClosedStatus =  workflowStatusService.findByWorkflow(grant.getGrantStatus().getWorkflow()).
                    stream().filter(w -> w.getInternalStatus().equalsIgnoreCase(CLOSED)).findFirst();
            if(optionalClosedStatus.isPresent()){
                GrantWithNote gn = new GrantWithNote();
                gn.setGrant(grant);
                gn.setNote(
                        "No note added.<br><i><b>System Note</b>: </i>"+closure.getReason());
                grantService.moveToNewState(gn,userId,grant.getId(),grant.getGrantStatus().getId(),optionalClosedStatus.get().getId(),tenantCode);
            }
        }
        return closure;
    }

    @GetMapping("{closureId}/changeHistory")
    public PlainClosure getReportHistory(@PathVariable("closureId") Long closureId,
                                        @PathVariable("userId") Long userId) throws IOException {

        GrantClosure closure = closureService.getClosureById(closureId);
        ClosureSnapshot snapshot = closureSnapshotService.getMostRecentSnapshotByClosureId(closureId);

        if(snapshot==null){
            return null;
        }

        closure.setReason(snapshot.getReason());
        closure.setDescription(snapshot.getDescription());
        closure.setStatus(workflowStatusService.findById(snapshot.getStatusId()));
        ClosureDetailVO details = new ObjectMapper().readValue(snapshot.getStringAttributes(),ClosureDetailVO.class);
        closure.setClosureDetails(details);

        return closureService.closureToPlain(closure);
    }

    @GetMapping(value = "/compare/{currentClosureId}/{origClosureId}")
    public List<PlainClosure> getClosuresToCompare(@RequestHeader("X-TENANT-CODE")String tenantCode,
                                                 @PathVariable("userId")Long userId,
                                                 @PathVariable("currentClosureId")Long currentClosureId,
                                                 @PathVariable("origClosureId")Long origClosureId){

        List<PlainClosure> closuresToReturn = new ArrayList<>();

        GrantClosure currentClosure = closureService.getClosureById(currentClosureId);
        currentClosure = closureToReturn(currentClosure,userId);

        GrantClosure origClosure = closureService.getClosureById(origClosureId);
        origClosure = closureToReturn(origClosure,userId);

        try {
            closuresToReturn.add(closureService.closureToPlain(currentClosure));
            closuresToReturn.add(closureService.closureToPlain(origClosure));
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return closuresToReturn;
    }

    @GetMapping(value = "/compare/{currentClosureId}")
    public PlainClosure getPlainClosureForCompare(@RequestHeader("X-TENANT-CODE")String tenantCode,
                                               @PathVariable("userId")Long userId,
                                               @PathVariable("currentClosureId")Long currentClosureId) throws IOException {
        GrantClosure currenClosure = closureService.getClosureById(currentClosureId);
        currenClosure = closureToReturn(currenClosure,userId);



        return closureService.closureToPlain(currenClosure);
    }

    private void checkAndReturnHistoricalCLosure(@PathVariable("userId") Long userId, GrantClosure closure) {
        if (userService.getUserById(userId).getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)
                && closure.getStatus().getInternalStatus().equalsIgnoreCase("REVIEW")) {
            try {
                GrantClosureHistory historicReport = closureService.getSingleClosureHistoryByStatusAndClosureId(ACTIVE,
                        closure.getId());
                if (historicReport != null && historicReport.getClosureDetail() != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    closure.setClosureDetails(mapper.readValue(historicReport.getClosureDetail(), ClosureDetailVO.class));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @GetMapping("/reasons")
    public List<ClosureReason> getClosureReasons(
            @PathVariable("userId") Long userId){

        return grantService.getClosureReasons(userService.getUserById(userId).getOrganization().getId());
    }

    private void saveSnapShot(GrantClosure closure, Long fromStatusId, Long toStatusId, User currentUser, User previousUser) {

        try {
            ClosureSnapshot snapshot = new ClosureSnapshot();
            snapshot.setAssignedToId(currentUser!=null?currentUser.getId():null);
            snapshot.setClosureId(closure.getId());
            snapshot.setReason(closure.getReason());
            snapshot.setDescription(closure.getDescription());
            snapshot.setStatusId(fromStatusId);
            String stringAttribs = new ObjectMapper().writeValueAsString(closure.getClosureDetails());
            snapshot.setStringAttributes(stringAttribs);
            snapshot.setFromStringAttributes(stringAttribs);
            snapshot.setAssignedToId(currentUser!=null?currentUser.getId():null);
            snapshot.setMovedBy(previousUser.getId());
            snapshot.setFromNote(closure.getNote());
            snapshot.setFromStateId(fromStatusId);
            snapshot.setToStateId(toStatusId);
            snapshot.setMovedOn(closure.getMovedOn());
            closureSnapshotService.saveClosureSnapshot(snapshot);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @GetMapping("/{closureId}/history/")
    public List<GrantClosureHistory> getReportHistory(@PathVariable("closureId") Long closureId,
                                                @PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode) {

        List<GrantClosureHistory> history = new ArrayList<>();
        List<ClosureSnapshot> closureSnapshotHistory = closureSnapshotService.getClosureSnapshotForClosure(closureId);
        if ((closureSnapshotHistory != null && closureSnapshotHistory.get(0).getFromStateId() == null)) {
            history = closureService.getClosureHistory(closureId);
            for (GrantClosureHistory historyEntry : history) {
                historyEntry.setNoteAddedByUser(userService.getUserById(historyEntry.getNoteAddedBy()));
            }
        } else {
            for (ClosureSnapshot snapShot : closureSnapshotHistory) {
                GrantClosureHistory hist = new GrantClosureHistory();
                hist.setReason(snapShot.getReason().getReason());
                hist.setDescription(snapShot.getDescription());
                hist.setId(snapShot.getClosureId());
                hist.setNote(snapShot.getFromNote());
                hist.setNoteAdded(snapShot.getMovedOn());
                User assignedBy = userService.getUserById(snapShot.getMovedBy());
                hist.setNoteAddedBy(assignedBy.getId());
                hist.setNoteAddedByUser(assignedBy);
                hist.setStatus(workflowStatusService.findById(snapShot.getFromStateId()));
                history.add(hist);
            }
        }

        return history;
    }

    @GetMapping("{grantId}/warnings")
    public ResponseEntity<ClosureWarnings> getClosureWarning(@PathVariable("userId") Long userId,
                                            @RequestHeader("X-TENANT-CODE") String tenantCode,
                                            @PathVariable("grantId")Long grantId){


        List<WorkflowStatus> reportWfStatuses = workflowStatusService.findByWorkflow(
                workflowService.findWorkflowByGrantTypeAndObject(grantService.getById(grantId).getGrantTypeId(),REPORT)
        );

        List<Report> reportsInProgress = new ArrayList<>();
        reportWfStatuses.removeIf(ws -> ws.getInternalStatus().equalsIgnoreCase(DRAFT) || ws.getInternalStatus().equalsIgnoreCase(CLOSED));
        for(WorkflowStatus status :reportWfStatuses){
            reportsInProgress.addAll(reportService.findReportsByStatusForGrant(status,grantService.getById(grantId)));
        }


        List<WorkflowStatus> disbursementWfStatuses = workflowStatusService.findByWorkflow(
                workflowService.findWorkflowByGrantTypeAndObject(grantService.getById(grantId).getGrantTypeId(), DISBURSEMENTCAPS)
        );

        disbursementWfStatuses.removeIf(ws -> ws.getInternalStatus().equalsIgnoreCase(DRAFT) || ws.getInternalStatus().equalsIgnoreCase(CLOSED) || ws.getInternalStatus().equalsIgnoreCase(ACTIVE));
        List<Disbursement> disbursementsInProgress = disbursementService.getDibursementsForGrantByStatuses(grantId,disbursementWfStatuses.stream().mapToLong(WorkflowStatus::getId).boxed().collect(Collectors.toList()));

        Grant grantInAmendment = null;
        if(grantService.getById(grantId).getAmendGrantId()!=null){
            grantInAmendment = grantService.getById(grantService.getById(grantId).getAmendGrantId());
        }

        return new ResponseEntity<>(new ClosureWarnings(disbursementsInProgress,reportsInProgress,grantInAmendment), HttpStatus.OK);
    }
}
