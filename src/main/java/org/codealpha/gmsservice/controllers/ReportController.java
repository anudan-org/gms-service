package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.Frequency;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.modelmapper.ModelMapper;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/user/{userId}/report")
public class ReportController {

    public static final String GRANTEE = "GRANTEE";
    public static final String REPORT_DOCUMENTS = "/report-documents/";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String REVIEW = "REVIEW";
    public static final String APPROVED = "APPROVED";
    public static final String CLOSED = "CLOSED";
    public static final String ACTIVE = "ACTIVE";
    public static final String REPORT = "REPORT";
    public static final String EMAIL = "&email=";
    public static final String TYPE_REPORT = "&type=report";
    public static final String ACTUAL_DISBURSEMENT = "Actual Disbursement";
    public static final String DISBURSEMENT = "disbursement";
    public static final String FILE = "file:";
    public static final String NOTES = "Notes";
    public static final String RELEASE_VERSION = "%RELEASE_VERSION%";
    public static final String DISBURSEMENT_DATE = "Disbursement Date";
    public static final String PROJECT_INDICATORS = "Project Indicators";
    public static final String CURRENCY = "currency";
    public static final String DD_MMM_YYYY = "dd-MMM-yyyy";
    public static final String PLEASE_REVIEW = "Please review.";
    public static final String TENANT = "%TENANT%";
    public static final String PATH_SEPARATOR = "/";
    public static final String UTF_8 = "UTF-8";
    public static final String TABLE = "table";
    private static Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private TemplateLibraryService templateLibraryService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    ReportSnapshotService reportSnapshotService;
    @Value("${spring.upload-file-location}")
    private String uploadLocation;
    @Value("${spring.supported-file-types}")
    private String[] supportedFileTypes;
    @Autowired
    private WorkflowStatusTransitionService workflowStatusTransitionService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private CommonEmailSevice commonEmailSevice;
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private GranterReportTemplateService granterReportTemplateService;
    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private DisbursementService disbursementService;
    @Autowired
    private ReleaseService releaseService;
    @Value("${spring.timezone}")
    private String timezone;
    @Autowired
    private OrgTagService orgTagService;
    @Autowired
    private GrantTypeService grantTypeService;
    @Autowired
    private WorkflowValidationService workflowValidationService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(PATH_SEPARATOR)
    public List<ReportCard> getAllReports(@PathVariable("userId") Long userId,
                                          @RequestHeader("X-TENANT-CODE") String tenantCode,
                                          @RequestParam(value = "q", required = false) String filterClause) {
        Organization org = null;
        User user = userService.getUserById(userId);

        List<ReportCard> reports = new ArrayList<>();
        if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
            org = user.getOrganization();
            if (filterClause != null && filterClause.equalsIgnoreCase("UPCOMING-DUE")) {
                reports = reportService.getAllAssignedReportCardsForGranteeUser(userId, org.getId(), ReportService.ACTIVE);
            } else if (filterClause != null && filterClause.equalsIgnoreCase(SUBMITTED)) {
                reports = reportService.getAllAssignedReportCardsForGranteeUser(userId, org.getId(), REVIEW);
            } else if (filterClause != null && filterClause.equalsIgnoreCase(APPROVED)) {
                reports = reportService.getAllAssignedReportCardsForGranteeUser(userId, org.getId(), CLOSED);
            }
        } else {
            org = organizationService.findOrganizationByTenantCode(tenantCode);
            Date start = DateTime.now().withTimeAtStartOfDay().toDate();
            Date end = new DateTime(start, DateTimeZone.forID(timezone)).plusDays(15).withTime(23, 59, 59, 999)
                    .toDate();
            boolean isAdmin = false;

            for (Role role : userRoleService.findRolesForUser(userService.getUserById(userId))) {
                if (role.getName().equalsIgnoreCase("ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
            if (filterClause != null && filterClause.equalsIgnoreCase("UPCOMING")) {
                if (!isAdmin) {
                    reports = reportService.getUpcomingReportCardsForGranterUserByDateRange(userId, org.getId(), start, end);
                } else {
                    reports = reportService.getUpcomingReportCardsForAdminGranterUserByDateRange(userId, org.getId(), start, end);

                }
                for (ReportCard report : reports) {
                    int futureReportsCount = reportService.getFutureReportCardsForGranterUserByDateRangeAndGrant(userId,
                            org.getId(), end, report.getGrant().getId()).size();
                    report.setFutureReportsCount(futureReportsCount);
                }
            } else if (filterClause != null && filterClause.equalsIgnoreCase("UPCOMING-FUTURE")) {
                if (!isAdmin) {
                    reports = reportService.getUpcomingFutureReportCardsForGranterUserByDate(userId, org.getId());
                } else {
                    reports = reportService.getUpcomingFutureReportCardsForAdminGranterUserByDate(userId, org.getId());
                }
                Map<Long, ReportCard> reportsHolder = new LinkedHashMap<>();
                for (ReportCard report : reports) {
                    if (!reportsHolder.keySet().contains(report.getGrant().getId())) {
                        reportsHolder.put(report.getGrant().getId(), report);
                    }
                }

                reports = new ArrayList<>();

                for (Map.Entry<Long, ReportCard> entry : reportsHolder.entrySet()) {
                    ReportCard r = entry.getValue();
                    List<ReportCard> otherReports = reportService.getReportCardsForGrant(r.getGrant());
                    otherReports.removeIf(a -> a.getId().longValue() == r.getId().longValue());
                    r.setFutureReportsCount(otherReports.size());
                    reports.add(r);
                }

                List<ReportCard> reportWithNullEndDate = reports.stream().filter(r -> r.getEndDate() == null)
                        .collect(Collectors.toList());
                reports.removeAll(reportWithNullEndDate);
                reports.sort(Comparator.comparing(ReportCard::getEndDate));
                reports.addAll(reportWithNullEndDate);
            } else if (filterClause != null && filterClause.equalsIgnoreCase("UPCOMING-DUE")) {
                if (!isAdmin) {
                    reports = reportService.getReadyToSubmitReportCardsForGranterUserByDateRange(userId, org.getId(), start,
                            end);
                } else {
                    reports = reportService.getReadyToSubmitReportCardsForAdminGranterUserByDateRange(userId, org.getId(), start,
                            end);
                }
            } else if (filterClause != null && filterClause.equalsIgnoreCase(SUBMITTED)) {
                if (!isAdmin) {
                    reports = reportService.getSubmittedReportCardsForGranterUserByDateRange(userId, org.getId());
                } else {
                    reports = reportService.getSubmittedReportCardsForAdminGranterUserByDateRange(userId, org.getId());
                }
            } else if (filterClause != null && filterClause.equalsIgnoreCase(APPROVED)) {
                if (!isAdmin) {
                    reports = reportService.getApprovedReportCardsForGranterUserByDateRange(userId, org.getId());
                } else {
                    reports = reportService.getApprovedReportCardsForAdminGranterUserByDateRange(userId, org.getId());
                }
            }
        }


        if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE) && filterClause != null
                && filterClause.equalsIgnoreCase(SUBMITTED)) {
            for (ReportCard report : reports) {
                try {
                    ReportHistory historicReport = reportService.getSingleReportHistoryByStatusAndReportId(ReportService.ACTIVE,
                            report.getId());
                    if (historicReport != null && historicReport.getReportDetail() != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        report.setReportDetails(
                                mapper.readValue(historicReport.getReportDetail(), ReportDetailVO.class));
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        for (ReportCard report : reports) {
            List<ReportAssignment> assignments = reportService.getAssignmentsForReportById(report.getId());
            List<ReportAssignmentsVO> assignmentsVOs = new ArrayList<>();
            for (ReportAssignment assignment : assignments) {
                ReportAssignmentsVO assignmentsVO = new ReportAssignmentsVO();
                assignmentsVO.setAssignmentId(assignment.getAssignment());
                assignmentsVO.setAnchor(assignment.isAnchor());
                if (assignment.getAssignment() != null) {
                    assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignment()));
                }
                assignmentsVO.setReportId(assignment.getReportId());
                assignmentsVO.setStateId(assignment.getStateId());
                assignmentsVOs.add(assignmentsVO);
            }
            report.setWorkflowAssignments(assignmentsVOs);
        }
        return reports;
    }

    @GetMapping("/{reportId}")
    public Report getAllReports(@PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                                @PathVariable("reportId") Long reportId) {
        Report report = reportService.getReportById(reportId);

        report = reportToReturn(report, userId);
        checkAndReturnHistoricalReport(userId, report);
        return report;
    }

    @GetMapping("/{reportId}/{grantId}")
    public List<ReportCard> getFutureReports(@PathVariable("userId") Long userId,
                                             @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("reportId") Long reportId,
                                             @PathVariable("grantId") Long grantId, @RequestParam(value = "type", required = false) String forType) {
        User user = userService.getUserById(userId);

        List<ReportCard> reports = null;
        if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")) {
            Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
            Date start = DateTime.now().withTimeAtStartOfDay().toDate();
            Date end = new DateTime(start, DateTimeZone.forID(timezone)).plusDays(30).withTime(23, 59, 59, 999)
                    .toDate();
            if (forType.equalsIgnoreCase("upcoming")) {
                reports = reportService.futureReportForGranterUserByDateRangeAndGrant(userId, org.getId(), end, grantId);
            } else if (forType.equalsIgnoreCase("all")) {
                reports = reportService.getReportCardsForGrant(grantService.getById(grantId));
            }
        }

        if (reports != null) {
            reports.removeIf(r -> r.getId().longValue() == reportId.longValue());
        }


        return reports;
    }

    @GetMapping("/{grantId}/approved")
    public List<Report> getApprovedReportsForGrant(@PathVariable("userId") Long userId,
                                                   @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("grantId") Long grantId) {
        List<WorkflowStatus> reportApprovedStatus = workflowStatusService
                .getTenantWorkflowStatuses(REPORT,
                        organizationService.findOrganizationByTenantCode(tenantCode).getId())
                .stream().filter(s -> s.getInternalStatus().equalsIgnoreCase(CLOSED)).collect(Collectors.toList());
        Workflow currentReportWorkflow = workflowService.findWorkflowByGrantTypeAndObject(grantService.getById(grantId).getGrantTypeId(), REPORT);
        reportApprovedStatus.removeIf(r -> r.getWorkflow().getId().longValue() != currentReportWorkflow.getId().longValue());

        Grant grant = grantService.getById(grantId);
        List<Report> reports = reportService.findReportsByStatusForGrant(reportApprovedStatus.get(0), grant);
        if (grant.getOrigGrantId() != null) {
            reports.addAll(reportService.findReportsByStatusForGrant(reportApprovedStatus.get(0),
                    grantService.getById(grant.getOrigGrantId())));
        }


        for (Report report : reports) {
            reportToReturn(report, userId);
        }

        return reports;
    }

    private Report reportToReturn(Report report, Long userId) {

        report.setStringAttributes(reportService.getReportStringAttributesForReport(report));

        List<ReportAssignmentsVO> workflowAssignments = new ArrayList<>();
        for (ReportAssignment assignment : reportService.getAssignmentsForReport(report)) {
            ReportAssignmentsVO assignmentsVO = new ReportAssignmentsVO();
            assignmentsVO.setId(assignment.getId());
            assignmentsVO.setAnchor(assignment.isAnchor());
            assignmentsVO.setAssignmentId(assignment.getAssignment());
            if (assignment.getAssignment() != null && assignment.getAssignment() > 0) {
                assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignment()));
            }
            assignmentsVO.setReportId(assignment.getReportId());
            assignmentsVO.setStateId(assignment.getStateId());
            assignmentsVO.setStateName(workflowStatusService.findById(assignment.getStateId()));

            reportService.setAssignmentHistory(assignmentsVO);

            workflowAssignments.add(assignmentsVO);
        }
        report.setWorkflowAssignments(workflowAssignments);
        List<ReportAssignment> reportAssignments = determineCanManage(report, userId);

        report.setForGranteeUse(userService.getUserById(userId).getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE));

        if (reportAssignments != null) {
            for (ReportAssignment assignment : reportAssignments) {
                if (report.getCurrentAssignment() == null) {
                    List<AssignedTo> assignedToList = new ArrayList<>();
                    report.setCurrentAssignment(assignedToList);
                }
                AssignedTo newAssignedTo = new AssignedTo();
                if (assignment.getAssignment() != null && assignment.getAssignment() > 0) {
                    newAssignedTo.setUser(userService.getUserById(assignment.getAssignment()));
                }
                report.getCurrentAssignment().add(newAssignedTo);
            }
        }

        ReportVO reportVO = new ReportVO().build(report, reportService.getReportSections(report), userService,
                reportService);
        report.setReportDetails(reportVO.getReportDetails());

        showDisbursementsForReport(report, userService.getUserById(userId));

        report.setNoteAddedBy(reportVO.getNoteAddedBy());
        report.setNoteAddedByUser(reportVO.getNoteAddedByUser());

        report.getWorkflowAssignments().sort((a, b) -> a.getId().compareTo(b.getId()));
        report.getReportDetails().getSections()
                .sort((a, b) -> Long.compare(a.getOrder(),b.getOrder()));
        for (SectionVO section : report.getReportDetails().getSections()) {
            if (section.getAttributes() != null) {
                section.getAttributes().sort(
                        (a, b) -> Long.compare(a.getAttributeOrder(),b.getAttributeOrder()));
            }
        }

        report.setGranteeUsers(userService.getAllGranteeUsers(report.getGrant().getOrganization()));

        GrantVO grantVO = new GrantVO().build(report.getGrant(), grantService.getGrantSections(report.getGrant()),
                workflowPermissionService, userService.getUserById(userId),
                userService, grantService);

        ObjectMapper mapper = new ObjectMapper();
        report.getGrant().setGrantDetails(grantVO.getGrantDetails());

        List<TableData> approvedDisbursements = new ArrayList<>();
        AtomicInteger installmentNumber = new AtomicInteger();

        report.getGrant().setApprovedReportsDisbursements(approvedDisbursements);

        report.getReportDetails().getSections().forEach(sec -> {
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
        report.setSecurityCode(reportService.buildHashCode(report));
        report.setFlowAuthorities(reportService.getFlowReportFlowAuthority(report));

        List<GrantTag> grantTags = grantService.getTagsForGrant(report.getGrant().getId());

        report.getGrant().setGrantTags(grantTags);

        return report;
    }

    private void showDisbursementsForReport(Report report, User currentUser) {
        List<WorkflowStatus> workflowStatuses = workflowStatusService.getTenantWorkflowStatuses("DISBURSEMENT",
                report.getGrant().getGrantorOrganization().getId());

        List<WorkflowStatus> closedStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase(CLOSED)).collect(Collectors.toList());
        List<Long> closedStatusIds = closedStatuses.stream().mapToLong(WorkflowStatus::getId).boxed()
                .collect(Collectors.toList());

        List<WorkflowStatus> draftStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("DRAFT")).collect(Collectors.toList());
        List<Long> draftStatusIds = draftStatuses.stream().mapToLong(WorkflowStatus::getId).boxed()
                .collect(Collectors.toList());

        List<ActualDisbursement> finalActualDisbursements = new ArrayList<>();
        report.getReportDetails().getSections().forEach(s -> {
            if (s.getAttributes() != null && !s.getAttributes().isEmpty()) {
                s.getAttributes().forEach(a -> {
                    if (a.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                        List<Disbursement> closedDisbursements = getDisbursementsByStatusIds(report.getGrant(), closedStatusIds);
                        List<Disbursement> draftDisbursements = getDisbursementsByStatusIds(report.getGrant(), draftStatusIds);
                        if (!report.getStatus().getInternalStatus().equalsIgnoreCase(CLOSED)) {
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
                                            && dd.getReportId().longValue() != report.getId().longValue() && dd.isGranteeEntry()) || (dd.getReportId() != null
                                            && dd.getReportId().longValue() == report.getId().longValue() && dd.isGranteeEntry() && report.getStatus().getInternalStatus().equalsIgnoreCase(ACTIVE))));
                                }
                                draftDisbursements.sort(Comparator.comparing(Disbursement::getCreatedAt));
                                draftDisbursements.forEach(cd -> {
                                    List<ActualDisbursement> ads = disbursementService
                                            .getActualDisbursementsForDisbursement(cd);
                                    if (ads != null && !ads.isEmpty()) {
                                        finalActualDisbursements.addAll(ads);
                                    }

                                });
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


                                    if (currentUser.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE) && td.isEnteredByGrantee() && report.getId().longValue() != repId.longValue() && !disbursementService.getDisbursementById(ad.getDisbursementId()).getStatus().getInternalStatus().equalsIgnoreCase(CLOSED)) {
                                        td.setShowForGrantee(false);
                                    }


                                    ColumnData cdDate = new ColumnData();
                                    cdDate.setDataType("date");
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
                                                new DateTime(report.getMovedOn(), DateTimeZone.forID(timezone))));
                                closedDisbursements.forEach(cd -> {

                                    List<ActualDisbursement> ads = disbursementService
                                            .getActualDisbursementsForDisbursement(cd);
                                    if (ads != null && !ads.isEmpty()) {
                                        finalActualDisbursements.addAll(ads);
                                    }
                                });
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
                                    cdDate.setDataType("date");
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
        if (grant.getOrigGrantId() != null) {
            closedDisbursements.addAll(getDisbursementsByStatusIds(grantService.getById(grant.getOrigGrantId()), statusIds));
        }
        return closedDisbursements;
    }

    private List<ReportAssignment> determineCanManage(Report report, Long userId) {
        List<ReportAssignment> reportAssignments = reportService.getAssignmentsForReport(report);
        boolean b = false;
        for (ReportAssignment ass : reportAssignments) {
            if ((ass.getAssignment() == null ? 0L : ass.getAssignment()) == userId && ass.getStateId().longValue() == report.getStatus().getId().longValue()) {
                b = true;
                break;
            }
        }
        report.setCanManage((b)
                || (report.getStatus().getInternalStatus().equalsIgnoreCase(ACTIVE) && userService.getUserById(userId)
                .getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)));

        return reportAssignments;
    }

    @PutMapping("/{reportId}")
    @ApiOperation("Save report")
    public Report saveReport(
            @ApiParam(name = "grantId", value = "Unique identifier of report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "reportToSave", value = "Report to save in edit mode, passed in Body of request") @RequestBody ReportDTO reportToSave,
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        User user = userService.getUserById(userId);
        Report report = null;
        Report savedReports = reportService.getReportById(reportId);
        determineCanManage(savedReports, userId);
        if (savedReports.getCanManage())
            processReport(modelMapper.map(reportToSave,Report.class), tenantOrg, user);

        report = reportToReturn(modelMapper.map(reportToSave,Report.class), userId);
        return report;
    }

    private void processReport(Report reportToSave, Organization tenantOrg, User user) {
        Report report = reportService.getReportById(reportToSave.getId());

        report.setStartDate(reportToSave.getStartDate());
        report.setName(reportToSave.getName());
        report.setEndDate(reportToSave.getEndDate());
        report.setDueDate(reportToSave.getDueDate());
        report.setUpdatedAt(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());
        report.setUpdatedBy(user.getId());
        try {
            report.setReportDetail(new ObjectMapper().writeValueAsString(reportToSave.getReportDetails()));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }

        List<Report> approvedReports = null;
        if (report.getLinkedApprovedReports() == null || report.getLinkedApprovedReports().isEmpty()) {
            Optional<WorkflowStatus> optionalWorkflowStatus = workflowStatusService
                    .getTenantWorkflowStatuses(REPORT, report.getGrant().getGrantorOrganization().getId())
                    .stream().filter(s -> s.getInternalStatus().equalsIgnoreCase(CLOSED)).findFirst();
            approvedReports = reportService.findByGrantAndStatus(report.getGrant(),
                    optionalWorkflowStatus.isPresent() ? optionalWorkflowStatus.get() : null,
                    report.getId());
            if (approvedReports == null || approvedReports.isEmpty()) {
                try {
                    report.setLinkedApprovedReports(
                            new ObjectMapper().writeValueAsString(Arrays.asList(0l)));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                try {
                    report.setLinkedApprovedReports(new ObjectMapper().writeValueAsString(
                            approvedReports.stream().map(Report::getId).collect(Collectors.toList())));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            report = reportService.saveReport(report);
        }

        processStringAttributes(user, report, reportToSave, tenantOrg);

        reportService.saveReport(report);

    }

    private void processStringAttributes(User user, Report report, Report reportToSave, Organization tenant) {
        ReportSpecificSection reportSpecificSection = null;

        for (SectionVO sectionVO : reportToSave.getReportDetails().getSections()) {
            reportSpecificSection = reportService.getReportSpecificSectionById(sectionVO.getId());

            reportSpecificSection.setSectionName(sectionVO.getName());
            reportSpecificSection.setSectionOrder(sectionVO.getOrder());
            if ("ANUDAN".equalsIgnoreCase(tenant.getCode())) {
                reportSpecificSection.setGranter((Granter) report.getGrant().getGrantorOrganization());
            } else {
                reportSpecificSection.setGranter((Granter) tenant);
            }

            reportSpecificSection.setDeletable(true);

            reportSpecificSection = reportService.saveReportSpecificSection(reportSpecificSection);

            ReportSpecificSectionAttribute sectionAttribute = null;

            if (sectionVO.getAttributes() != null) {
                for (SectionAttributesVO sectionAttributesVO : sectionVO.getAttributes()) {

                    sectionAttribute = reportService.getReportStringByStringAttributeId(sectionAttributesVO.getId())
                            .getSectionAttribute();

                    sectionAttribute.setFieldName(sectionAttributesVO.getFieldName());
                    sectionAttribute.setFieldType(sectionAttributesVO.getFieldType());
                    if ("ANUDAN".equalsIgnoreCase(tenant.getCode())) {
                        sectionAttribute.setGranter((Granter) report.getGrant().getGrantorOrganization());
                    } else {
                        sectionAttribute.setGranter((Granter) tenant);
                    }

                    sectionAttribute.setAttributeOrder(sectionAttributesVO.getAttributeOrder());
                    sectionAttribute.setRequired(true);
                    sectionAttribute.setSection(reportSpecificSection);

                    sectionAttribute = reportService.saveReportSpecificSectionAttribute(sectionAttribute);

                    ReportStringAttribute reportStringAttribute = reportService
                            .getReportStringAttributeBySectionAttributeAndSection(sectionAttribute,
                                    reportSpecificSection);

                    reportStringAttribute.setTarget(sectionAttributesVO.getTarget());
                    reportStringAttribute.setFrequency(sectionAttributesVO.getFrequency());
                    if ((user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE) && !grantTypeService.findById(report.getGrant().getGrantTypeId()).isInternal()) || (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER") && grantTypeService.findById(report.getGrant().getGrantTypeId()).isInternal())) {
                        reportStringAttribute.setActualTarget(sectionAttributesVO.getActualTarget());
                    }
                    if (sectionAttribute.getFieldType().equalsIgnoreCase(TABLE)
                            || sectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                        List<TableData> tableData = sectionAttributesVO.getFieldTableValue();
                        // Do the below only if field type is Disbursement
                        // The idea is to create a real disbursement if a new row is added
                        if (sectionAttribute.getFieldType().equalsIgnoreCase(TABLE)){
                            try {
                                reportStringAttribute.setValue(new ObjectMapper().writeValueAsString(sectionAttributesVO.getFieldTableValue()));
                                reportService.saveReportStringAttribute(reportStringAttribute);
                            } catch (JsonProcessingException e) {
                                logger.error(e.getMessage(),e);
                            }
                        }else if (sectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
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
                                                            report.getGrant().getId()));
                                            disbursementService.saveActualDisbursement(actualDisbursement);
                                        }
                                    }

                                }
                            } catch (ParseException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }

                    } else {
                        reportStringAttribute.setValue(sectionAttributesVO.getFieldValue());
                    }
                    reportService.saveReportStringAttribute(reportStringAttribute);
                }
            }
        }
    }

    @PostMapping("/{reportId}/section/{sectionId}/field")
    @ApiOperation("Added new field to section")
    public ReportFieldInfo createFieldInSection(
            @ApiParam(name = "reportToSave", value = "Report to save if in edit mode passed in Body of request") @RequestBody ReportDTO reportToSave,
            @ApiParam(name = "reportId", value = "Unique identifier of the grant") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "sectionId", value = "Unique identifier of the section to which the field is being added") @PathVariable("sectionId") Long sectionId,
            @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        saveReport(reportId, reportToSave, userId, tenantCode);
        Report report = reportService.getReportById(reportId);
        ReportSpecificSection reportSection = reportService.getReportSpecificSectionById(sectionId);

        ReportSpecificSectionAttribute newSectionAttribute = new ReportSpecificSectionAttribute();
        newSectionAttribute.setSection(reportSection);
        newSectionAttribute.setRequired(false);
        newSectionAttribute.setFieldType("multiline");
        newSectionAttribute.setFieldName("");
        newSectionAttribute.setDeletable(true);
        newSectionAttribute.setCanEdit(true);
        newSectionAttribute.setAttributeOrder(reportService.getNextAttributeOrder(
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), sectionId));
        newSectionAttribute.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
        newSectionAttribute = reportService.saveReportSpecificSectionAttribute(newSectionAttribute);
        ReportStringAttribute stringAttribute = new ReportStringAttribute();
        stringAttribute.setValue("");
        stringAttribute.setSectionAttribute(newSectionAttribute);
        stringAttribute.setSection(reportSection);
        stringAttribute.setReport(report);

        stringAttribute = reportService.saveReportStringAttribute(stringAttribute);

        if (Boolean.TRUE.equals(reportService.checkIfReportTemplateChanged(report, reportSection, newSectionAttribute))) {
            reportService.createNewReportTemplateFromExisiting(report);
        }

        report = reportToReturn(report, userId);
        return new ReportFieldInfo(newSectionAttribute.getId(), stringAttribute.getId(), report);
    }

    @PutMapping("/{reportId}/section/{sectionId}/field/{fieldId}")
    @ApiOperation("Update field information")
    public ReportFieldInfo updateField(
            @ApiParam(name = "sectionId", value = "Unique identifier of section") @PathVariable("sectionId") Long sectionId,
            @ApiParam(name = "attributeToSave", value = "Updated attribute to be saved") @RequestBody ReportAttributeToSaveVO attributeToSave,
            @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "fieldId", value = "Unique identifier of the field being updated") @PathVariable("fieldId") Long fieldId,
            @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        saveReport(reportId, attributeToSave.getReport(), userId, tenantCode);
        ReportSpecificSectionAttribute currentAttribute = reportService.getReportStringByStringAttributeId(fieldId)
                .getSectionAttribute();
        currentAttribute.setFieldName(attributeToSave.getAttr().getFieldName());
        currentAttribute.setFieldType(attributeToSave.getAttr().getFieldType());
        currentAttribute = reportService.saveReportSpecificSectionAttribute(currentAttribute);
        ReportStringAttribute stringAttribute = reportService
                .getReportStringAttributeBySectionAttributeAndSection(currentAttribute, currentAttribute.getSection());
        if (currentAttribute.getFieldType().equalsIgnoreCase("kpi")) {
            stringAttribute.setFrequency("adhoc");
        }
        stringAttribute = reportService.saveReportStringAttribute(stringAttribute);

        Report report = reportService.getReportById(reportId);
        if (Boolean.TRUE.equals(reportService.checkIfReportTemplateChanged(report, currentAttribute.getSection(), currentAttribute))) {
            reportService.createNewReportTemplateFromExisiting(report);
        }

        report = reportToReturn(report, userId);
        return new ReportFieldInfo(currentAttribute.getId(), stringAttribute.getId(), report);
    }

    @PostMapping("/{reportId}/field/{fieldId}/template/{templateId}")
    @ApiOperation(value = "Attach document to field", notes = "Valid for Document field types only")
    public ReportDocInfo createDocumentForReportSectionField(
            @ApiParam(name = "reportToSave", value = "Report to save in edit mode, passed in Body of request") @RequestBody ReportDTO reportToSave,
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "fieldId", value = "Unique identifier of the field to which document is being attached") @PathVariable("fieldId") Long fieldId,
            @ApiParam(name = "temaplteId", value = "Unique identified of the document template being attached") @PathVariable("templateId") Long templateId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        saveReport(reportId, reportToSave, userId, tenantCode);
        TemplateLibrary libraryDoc = templateLibraryService.getTemplateLibraryDocumentById(templateId);
        Report report = reportService.getReportById(reportId); 
        ReportStringAttribute stringAttribute = reportService.getReportStringByStringAttributeId(fieldId);
        File file = null;
        String filePath = null;
        try {
            file = resourceLoader
                    .getResource(FILE + URLDecoder.decode(libraryDoc.getLocation(), UTF_8))
                    .getFile();

            User user = userService.getUserById(userId);
            
            if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
                
                filePath = uploadLocation + report.getGrant().getGrantorOrganization().getCode() + REPORT_DOCUMENTS
                        + reportId + PATH_SEPARATOR + stringAttribute.getSection().getId() + PATH_SEPARATOR
                        + stringAttribute.getSectionAttribute().getId() + PATH_SEPARATOR;
                } else {
                filePath = uploadLocation +  userService.getUserById(userId).getOrganization().getCode() + REPORT_DOCUMENTS + reportId + PATH_SEPARATOR
                        + stringAttribute.getSection().getId() + PATH_SEPARATOR + stringAttribute.getSectionAttribute().getId()
                        + PATH_SEPARATOR;
            }
            
            File dir = new File(filePath);
            dir.mkdirs();
            File fileToCreate = new File(dir, libraryDoc.getName() + "." + libraryDoc.getType());
            FileCopyUtils.copy(file, fileToCreate);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        ReportStringAttributeAttachments attachment = new ReportStringAttributeAttachments();
        attachment.setCreatedBy(userService.getUserById(userId).getEmailId());
        attachment.setCreatedOn(new Date());
        attachment.setDescription(libraryDoc.getDescription());
        attachment.setReportStringAttribute(stringAttribute);
        attachment.setLocation(filePath);
        attachment.setName(libraryDoc.getName());
        attachment.setTitle("");
        attachment.setType(libraryDoc.getType());
        attachment.setVersion(1);
        attachment = reportService.saveReportStringAttributeAttachment(attachment);

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ReportStringAttributeAttachments> stringAttributeAttachments = reportService
                    .getStringAttributeAttachmentsByStringAttribute(stringAttribute);
            stringAttribute.setValue(mapper.writeValueAsString(stringAttributeAttachments));
            reportService.saveReportStringAttribute(stringAttribute);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        report = reportService.getReportById(reportId);
        report = reportToReturn(report, userId);
        return new ReportDocInfo(attachment.getId(), report);
    }

    @PostMapping(value = "/{reportId}/section/{sectionId}/attribute/{attributeId}/upload", consumes = {
            "multipart/form-data"})
    @ApiOperation("Upload and attach files to Document field from disk")
    public ReportDocInfo saveUploadedFiles(
            @ApiParam(name = "sectionId", value = "Unique identifier of section") @PathVariable("sectionId") Long sectionId,
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "attributeId", value = "Unique identifier of the document field") @PathVariable("attributeId") Long attributeId,
            @ApiParam(name = "reportData", value = "Report data") @RequestParam("reportToSave") String reportToSaveStr,
            @RequestParam("file") MultipartFile[] files,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Report report = reportService.getReportById(reportId);

        ReportStringAttribute attr = reportService.getReportStringByStringAttributeId(attributeId);
        User user = userService.getUserById(userId);

        String filePath = "";
        if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
            filePath = uploadLocation + report.getGrant().getGrantorOrganization().getCode() + REPORT_DOCUMENTS + reportId
                    + PATH_SEPARATOR + attr.getSection().getId() + PATH_SEPARATOR + attr.getSectionAttribute().getId() + PATH_SEPARATOR;
        } else {
            filePath = uploadLocation +  userService.getUserById(userId).getOrganization().getCode() + REPORT_DOCUMENTS + reportId + PATH_SEPARATOR + attr.getSection().getId()
                    + PATH_SEPARATOR + attr.getSectionAttribute().getId() + PATH_SEPARATOR;
        }
        File dir = new File(filePath);
        dir.mkdirs();

        List<ReportStringAttributeAttachments> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                File fileToCreate = new File(dir, fileName);
                try (FileOutputStream fos = new FileOutputStream(fileToCreate)) {
                    fos.write(file.getBytes());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                ReportStringAttributeAttachments attachment = new ReportStringAttributeAttachments();
                attachment.setVersion(1);
                attachment.setType(FilenameUtils.getExtension(fileName));
                attachment.setTitle(fileName
                        .replace("." + FilenameUtils.getExtension(fileName), ""));
                attachment.setLocation(filePath);
                attachment.setName(fileName
                        .replace("." + FilenameUtils.getExtension(fileName), ""));
                attachment.setReportStringAttribute(attr);
                attachment.setDescription(fileName
                        .replace("." + FilenameUtils.getExtension(fileName), ""));
                attachment.setCreatedOn(new Date());
                attachment.setCreatedBy(userService.getUserById(userId).getEmailId());
                attachment = reportService.saveReportStringAttributeAttachment(attachment);
                attachments.add(attachment);
            }
        }

        mapper = new ObjectMapper();
        try {
            if (attr.getValue() == null || attr.getValue().equalsIgnoreCase("")) {
                attr.setValue("[]");
            }
            List<ReportStringAttributeAttachments> currentAttachments = mapper.readValue(attr.getValue(),
                    new TypeReference<List<ReportStringAttributeAttachments>>() {
                    });
            if (currentAttachments == null) {
                currentAttachments = new ArrayList<>();
            }
            currentAttachments.addAll(attachments);

            attr.setValue(mapper.writeValueAsString(currentAttachments));
            attr = reportService.saveReportStringAttribute(attr);
            ReportStringAttribute finalAttr = attr;
            ReportStringAttribute finalAttr1 = finalAttr;
            Optional<ReportStringAttribute> optionalAttr = report.getStringAttributes().stream().filter(g -> g.getId().longValue() == finalAttr1.getId().longValue()).findFirst();
            finalAttr = optionalAttr.isPresent() ? optionalAttr
                    .get() : null;
            if (finalAttr != null) {
                finalAttr.setValue(mapper.writeValueAsString(currentAttachments));
            }
            reportService.saveReport(report);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        report = reportService.getReportById(reportId);
        report = reportToReturn(report, userId);

        return new ReportDocInfo(attachments.get(attachments.size() - 1).getId(), report);
    }

    @PostMapping("/{reportId}/template/{templateId}/section/{sectionName}")
    @ApiOperation("Create new section in grant")
    public ReportSectionInfo createSection(@RequestBody ReportDTO reportToSave,
                                           @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
                                           @ApiParam(name = "templateId", value = "Unique identifier of the report template") @PathVariable("templateId") Long templateId,
                                           @ApiParam(name = "sectionName", value = "Name of the new section") @PathVariable("sectionName") String sectionName,
                                           @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
                                           @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        Report report = saveReport(reportId, reportToSave, userId, tenantCode);

        ReportSpecificSection specificSection = new ReportSpecificSection();
        specificSection.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
        specificSection.setSectionName(sectionName);

        specificSection.setReportTemplateId(templateId);
        specificSection.setDeletable(true);
        specificSection.setReportId(reportId);
        specificSection.setSectionOrder(reportService
                .getNextSectionOrder(organizationService.findOrganizationByTenantCode(tenantCode).getId(), templateId));
        specificSection = reportService.saveSection(specificSection);

        if (Boolean.TRUE.equals(reportService.checkIfReportTemplateChanged(report, specificSection, null))) {
            reportService.createNewReportTemplateFromExisiting(report);
        }

        report = reportToReturn(report, userId);
        return new ReportSectionInfo(specificSection.getId(), specificSection.getSectionName(), report);

    }

    @PutMapping("/{reportId}/template/{templateId}/section/{sectionId}")
    @ApiOperation("Delete existing section in report")
    public Report deleteSection(@RequestBody ReportDTO reportToSave,
                                @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
                                @ApiParam(name = "templateId", value = "Unique identifier of the grant template") @PathVariable("templateId") Long templateId,
                                @ApiParam(name = "sectionId", value = "Unique identifier of the section being deleted") @PathVariable("sectionId") Long sectionId,
                                @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
                                @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        ReportSpecificSection section = reportService.getReportSpecificSectionById(sectionId);
        Report report = reportService.getReportById(reportId);

        for (ReportSpecificSectionAttribute attrib : reportService.getSpecificSectionAttributesBySection(section)) {
            for (ReportStringAttribute stringAttrib : reportService.getReportStringAttributesByAttribute(attrib)) {
                if (stringAttrib != null) {
                    reportService.deleteStringAttribute(stringAttrib);

                    report.getStringAttributes().removeIf(e -> e.getId().longValue() == stringAttrib.getId().longValue());
                }
            }
        }

        reportService.deleteSectionAttributes(reportService.getSpecificSectionAttributesBySection(section));
        reportService.deleteSection(section);

        report = reportService.getReportById(reportId);
        if (Boolean.TRUE.equals(reportService.checkIfReportTemplateChanged(report, section, null))) {
            reportService.createNewReportTemplateFromExisiting(report);
        }
        report = reportToReturn(report, userId);
        return report;
    }

    @PostMapping("/{reportId}/assignment")
    @ApiOperation("Set owners for report workflow states")
    public Report saveReportAssignments(
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "assignmentModel", value = "Set assignment for report per workflow state") @RequestBody ReportAssignmentModel assignmentModel,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        Report report = saveReport(reportId, assignmentModel.getReport(), userId, tenantCode);

        Map<Long, Long> currentAssignments = new LinkedHashMap<>();
        if (reportService.checkIfReportMovedThroughWFAtleastOnce(report.getId())) {

            reportService.getAssignmentsForReport(report).stream().forEach(a -> currentAssignments.put(a.getStateId(), a.getAssignment()));
        }
        String customAss = null;
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
        String host = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme())
                .host(host).port(uriComponents.getPort());
        String url = uriBuilder.toUriString();
        for (ReportAssignmentsVO assignmentsVO : assignmentModel.getAssignments()) {
            if (customAss == null && assignmentsVO.getCustomAssignments() != null) {
                customAss = assignmentsVO.getCustomAssignments();
            }
            ReportAssignment assignment = null;
            if (assignmentsVO.getId() == null) {
                assignment = new ReportAssignment();
                assignment.setStateId(assignmentsVO.getStateId());
                assignment.setReportId(assignmentsVO.getReportId());
            } else {
                assignment = reportService.getReportAssignmentById(assignmentsVO.getId());
            }

            assignment.setAssignment(assignmentsVO.getAssignmentId());
            assignment.setUpdatedBy(userId);
            assignment.setAssignedOn(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());

            if ((customAss != null && !"".equalsIgnoreCase(customAss.trim())) && workflowStatusService
                    .getById(assignmentsVO.getStateId()).getInternalStatus().equalsIgnoreCase(ACTIVE)) {
                User granteeUser = null;
                User existingUser = userService.getUserByEmailAndOrg(customAss, report.getGrant().getOrganization());

                String code = Base64.getEncoder().encodeToString(String.valueOf(report.getId()).getBytes());
                try {
                    if (existingUser != null && existingUser.isActive()) {
                        granteeUser = existingUser;
                        url = new StringBuilder(url).append("/home/?action=login&org=").append(URLEncoder.encode(report.getGrant().getOrganization().getName(), UTF_8)).append("&r=").append(code).append(EMAIL).append(granteeUser.getEmailId()).append(TYPE_REPORT).toString();
                    } else if (existingUser != null && !existingUser.isActive()) {
                        granteeUser = existingUser;
                        url = new StringBuilder(url).append("/home/?action=registration&org=")
                                .append(URLEncoder.encode(report.getGrant().getOrganization().getName(), UTF_8))
                                .append("&r=").append(code)
                                .append(EMAIL).append(granteeUser.getEmailId()).append(TYPE_REPORT).toString();

                    } else {
                        granteeUser = new User();
                        Role newRole = roleService.findByOrganizationAndName(report.getGrant().getOrganization(), "Admin");

                        UserRole userRole = new UserRole();
                        userRole.setRole(newRole);
                        userRole.setUser(granteeUser);

                        List<UserRole> userRoles = new ArrayList<>();
                        userRoles.add(userRole);
                        granteeUser.setUserRoles(userRoles);
                        granteeUser.setFirstName("");
                        granteeUser.setLastName("");
                        granteeUser.setEmailId(customAss);
                        granteeUser.setOrganization(report.getGrant().getOrganization());
                        granteeUser.setActive(false);
                        granteeUser = userService.save(granteeUser);
                        userRoleService.saveUserRole(userRole);
                        url = new StringBuilder(url).append("/home/?action=registration&org=")
                                .append(URLEncoder.encode(report.getGrant().getOrganization().getName(), UTF_8))
                                .append("&r=").append(code)
                                .append(EMAIL).append(granteeUser.getEmailId()).append(TYPE_REPORT).toString();
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                String[] notifications = reportService.buildReportInvitationContent(report,
                        appConfigService.getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.REPORT_INVITE_SUBJECT).getConfigValue(),
                        appConfigService.getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.REPORT_INVITE_MESSAGE).getConfigValue(),
                        url);
                commonEmailSevice.sendMail(new String[]{!granteeUser.isDeleted() ? granteeUser.getEmailId() : null},
                        null, notifications[0], notifications[1],
                        new String[]{appConfigService
                                .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                .getConfigValue()
                                .replace(RELEASE_VERSION, releaseService.getCurrentRelease().getVersion()).replace(TENANT, report.getGrant()
                                .getGrantorOrganization().getName())});

                assignment.setAssignment(granteeUser.getId());
            }

            reportService.saveAssignmentForReport(assignment);
        }

        if (currentAssignments.size() > 0) {

            List<ReportAssignment> newAssignments = reportService.getAssignmentsForReport(report);

            String[] notifications = reportService.buildEmailNotificationContent(report,
                    userService.getUserById(userId),
                    appConfigService.getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_MESSAGE).getConfigValue(),
                    "", "", "", "", "", "", "", "", "", null, null, null, currentAssignments,
                    newAssignments);
            List<User> toUsers = newAssignments.stream().map(ReportAssignment::getAssignment)
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
                            new String[]{appConfigService
                                    .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                            AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                    .getConfigValue().replace(RELEASE_VERSION,
                                            releaseService.getCurrentRelease().getVersion()).replace(TENANT, report.getGrant()
                                    .getGrantorOrganization().getName())});

            Map<Long, Long> cleanAsigneesList = new HashMap<>();
            for (Long ass : currentAssignments.values()) {
                cleanAsigneesList.put(ass, ass);
            }
            for (ReportAssignment ass : newAssignments) {
                cleanAsigneesList.put(ass.getAssignment(), ass.getAssignment());
            }
            notifications = reportService.buildEmailNotificationContent(report, userService.getUserById(userId),
                    appConfigService.getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.OWNERSHIP_CHANGED_EMAIL_MESSAGE).getConfigValue(),
                    "", "", "", "", "", "", "", "", "", "", null, null, currentAssignments,
                    newAssignments);

            final String[] finaNotifications = notifications;
            final Report finalGrant = report;

            cleanAsigneesList.keySet().stream().forEach(u -> notificationsService.saveNotification(finaNotifications, u, finalGrant.getId(), REPORT));

        }

        report = reportToReturn(report, userId);
        return report;
    }

    @GetMapping("{reportId}/changeHistory")
    public PlainReport getReportHistory(@PathVariable("reportId") Long reportId,
                                        @PathVariable("userId") Long userId) throws IOException {

        Report report = reportService.getReportById(reportId);
        ReportSnapshot snapshot = reportSnapshotService.getMostRecentSnapshotByReportId(reportId);

        if (snapshot == null) {
            return null;
        }

        report.setName(snapshot.getName());
        report.setStartDate(snapshot.getStartDate());
        report.setEndDate(snapshot.getEndDate());
        report.setStatus(workflowStatusService.findById(snapshot.getStatusId()));
        report.setDueDate(snapshot.getDueDate());
        ReportDetailVO details = new ObjectMapper().readValue(snapshot.getStringAttributes(), ReportDetailVO.class);
        report.setReportDetails(details);

        return reportService.reportToPlain(report);
    }

    @PostMapping("/{reportId}/flow/{fromState}/{toState}")
    @ApiOperation("Move report through workflow")
    public Report moveReportState(@RequestBody ReportWithNote reportWithNote,
                                  @ApiParam(name = "userId", value = "Unique identified of logged in user") @PathVariable("userId") Long userId,
                                  @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
                                  @ApiParam(name = "fromStateId", value = "Unique identifier of the starting state of the report in the workflow") @PathVariable("fromState") Long fromStateId,
                                  @ApiParam(name = "toStateId", value = "Unique identifier of the ending state of the report in the workflow") @PathVariable("toState") Long toStateId,
                                  @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        saveReport(reportId, reportWithNote.getReport(), userId, tenantCode);

        Report report = reportService.getReportById(reportId);
        Report finalReport = report;
        WorkflowStatus previousState = report.getStatus();

        User updatingUser = userService.getUserById(userId);
        if (updatingUser.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)
                && previousState.getInternalStatus().equalsIgnoreCase(ACTIVE)) {
            ReportAssignment changedAssignment = reportService.getAssignmentsForReport(report).stream()
                    .filter(ass -> ass.getReportId().longValue() == reportId.longValue()
                            && ass.getStateId().longValue() == finalReport.getStatus().getId().longValue())
                    .collect(Collectors.toList()).get(0);
            changedAssignment.setAssignment(userId);
            reportService.saveAssignmentForReport(changedAssignment);
        }
        ReportAssignment currentAssignment = reportService.getAssignmentsForReport(report).stream()
                .filter(ass -> ass.getReportId().longValue() == reportId.longValue()
                        && ass.getStateId().longValue() == finalReport.getStatus().getId().longValue())
                .collect(Collectors.toList()).get(0);
        User previousOwner = userService.getUserById(currentAssignment.getAssignment());

        report.setStatus(workflowStatusService.findById(toStateId));

        report.setNote((reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase(""))
                ? reportWithNote.getNote()
                : "No note added");
        report.setNoteAdded(new Date());
        report.setNoteAddedBy(userId);

        Date currentDateTime = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate();
        report.setUpdatedAt(currentDateTime);
        report.setUpdatedBy(userId);
        report.setMovedOn(currentDateTime);
        report = reportService.saveReport(report);

        WorkflowStatus toStatus = workflowStatusService.findById(toStateId);

        List<User> usersToNotify = new ArrayList<>();

        List<ReportAssignment> assigments = reportService.getAssignmentsForReport(report);
        assigments.forEach(ass -> {
            if (usersToNotify.stream().noneMatch(u -> u.getId().longValue() == ass.getAssignment().longValue())) {
                usersToNotify.add(userService.getUserById(ass.getAssignment()));
            }
        });

        Optional<ReportAssignment> repAss = reportService.getAssignmentsForReport(report).stream()
                .filter(ass -> ass.getReportId().longValue() == reportId.longValue()
                        && ass.getStateId().longValue() == toStateId.longValue())
                .findAny();
        User currentOwner = null;
        String currentOwnerName = "";
        if (repAss.isPresent()) {
            currentOwner = userService.getUserById(repAss.get().getAssignment());
            currentOwnerName = currentOwner.getFirstName().concat(" ").concat(currentOwner.getLastName());
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
            String[] emailNotificationContent = reportService.buildEmailNotificationContent(finalReport,
                    finalCurrentOwner,
                    appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                    workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                    previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                    transition != null ? transition.getAction() : "", "Yes", PLEASE_REVIEW,
                    reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("") ? "Yes"
                            : "No",
                    reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                            ? PLEASE_REVIEW
                            : "",
                    null, null, null, null, null);
            commonEmailSevice
                    .sendMail(new String[]{!currentOwner.isDeleted() ? currentOwner.getEmailId() : null},
                            usersToNotify.stream().map(User::getEmailId).collect(Collectors.toList())
                                    .toArray(new String[usersToNotify.size()]),
                            emailNotificationContent[0], emailNotificationContent[1],
                            new String[]{appConfigService
                                    .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                            AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                    .getConfigValue().replace(RELEASE_VERSION,
                                            releaseService.getCurrentRelease().getVersion()).replace(TENANT, finalReport.getGrant()
                                    .getGrantorOrganization().getName())});

            String[] notificationContent = reportService.buildEmailNotificationContent(finalReport, currentOwner,
                    appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                    workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                    previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                    transition != null ? transition.getAction() : "", "Yes", PLEASE_REVIEW,
                    reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("") ? "Yes"
                            : "No",
                    reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                            ? PLEASE_REVIEW
                            : "",
                    null, null, null, null, null);

            notificationsService.saveNotification(notificationContent, currentOwner.getId(), finalReport.getId(),
                    REPORT);

            usersToNotify.stream().forEach(u -> {
                final String[] nc = reportService.buildEmailNotificationContent(finalReport, u,
                        appConfigService
                                .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT)
                                .getConfigValue(),
                        appConfigService
                                .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE)
                                .getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName,
                        previousState.getName(),
                        previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                        transition != null ? transition.getAction() : "", "Yes", PLEASE_REVIEW,
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                                ? "Yes"
                                : "No",
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                                ? PLEASE_REVIEW
                                : "",
                        null, null, null, null, null);

                notificationsService.saveNotification(nc, u.getId(), finalReport.getId(), REPORT);
            });
        } else if (!toStatus.getInternalStatus().equalsIgnoreCase(CLOSED)) {
            usersToNotify
                    .removeIf(u -> u.getId().longValue() == finalCurrentOwner.getId().longValue() || u.isDeleted());
            if (!workflowStatusService.findById(fromStateId).getInternalStatus().equalsIgnoreCase(ACTIVE)) {
                usersToNotify.removeIf(u -> u.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE));
            }

            String[] emailNotificationContent = reportService.buildEmailNotificationContent(finalReport,
                    finalCurrentOwner,
                    appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                    workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                    previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                    transition != null ? transition.getAction() : "", "Yes", PLEASE_REVIEW,
                    reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("") ? "Yes"
                            : "No",
                    reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                            ? PLEASE_REVIEW
                            : "",
                    null, null, null, null, null);
            commonEmailSevice
                    .sendMail(new String[]{!currentOwner.isDeleted() ? currentOwner.getEmailId() : null},
                            usersToNotify.stream().map(User::getEmailId).collect(Collectors.toList())
                                    .toArray(new String[usersToNotify.size()]),
                            emailNotificationContent[0], emailNotificationContent[1],
                            new String[]{appConfigService
                                    .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                            AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                    .getConfigValue().replace(RELEASE_VERSION,
                                            releaseService.getCurrentRelease().getVersion()).replace(TENANT, finalReport.getGrant()
                                    .getGrantorOrganization().getName())});

            String[] notificationContent = reportService.buildEmailNotificationContent(finalReport, currentOwner,
                    appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                    appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                    workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                    previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                    transition != null ? transition.getAction() : "", "Yes", PLEASE_REVIEW,
                    reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("") ? "Yes"
                            : "No",
                    reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                            ? PLEASE_REVIEW
                            : "",
                    null, null, null, null, null);

            notificationsService.saveNotification(notificationContent, currentOwner.getId(), finalReport.getId(),
                    REPORT);

            usersToNotify.stream().forEach(u -> {
                final String[] nc = reportService.buildEmailNotificationContent(finalReport, u,
                        appConfigService
                                .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT)
                                .getConfigValue(),
                        appConfigService
                                .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE)
                                .getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName,
                        previousState.getName(),
                        previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                        transition != null ? transition.getAction() : "", "Yes", PLEASE_REVIEW,
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                                ? "Yes"
                                : "No",
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                                ? PLEASE_REVIEW
                                : "",
                        null, null, null, null, null);

                notificationsService.saveNotification(nc, u.getId(), finalReport.getId(), REPORT);
            });
        } else {

            Optional<User> granteeUsr = usersToNotify.stream()
                    .filter(u -> u.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)).findFirst();
            if (granteeUsr.isPresent()) {
                User granteeUser = granteeUsr.get();
                usersToNotify.removeIf(u -> u.getId().longValue() == granteeUser.getId().longValue() || u.isDeleted());

                String[] emailNotificationContent = reportService.buildEmailNotificationContent(finalReport, granteeUser,
                        appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                        appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                        previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                        transition != null ? transition.getAction() : "", "Yes", PLEASE_REVIEW,
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("") ? "Yes"
                                : "No",
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                                ? PLEASE_REVIEW
                                : "",
                        null, null, null, null, null);
                commonEmailSevice
                        .sendMail(new String[]{!granteeUser.isDeleted() ? granteeUser.getEmailId() : null},
                                usersToNotify.stream().map(User::getEmailId).collect(Collectors.toList())
                                        .toArray(new String[usersToNotify.size()]),
                                emailNotificationContent[0], emailNotificationContent[1],
                                new String[]{appConfigService
                                        .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                        .getConfigValue().replace(RELEASE_VERSION,
                                                releaseService.getCurrentRelease().getVersion()).replace(TENANT, finalReport.getGrant()
                                        .getGrantorOrganization().getName())});

                String[] notificationContent = reportService.buildEmailNotificationContent(finalReport, granteeUser,
                        appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                        appConfigService.getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName, previousState.getName(),
                        previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                        transition != null ? transition.getAction() : "Request Modifications", "Yes", PLEASE_REVIEW,
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("") ? "Yes"
                                : "No",
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                                ? PLEASE_REVIEW
                                : "",
                        null, null, null, null, null);

                notificationsService.saveNotification(notificationContent, granteeUser.getId(), finalReport.getId(),
                        REPORT);
            }


            usersToNotify.stream().forEach(u -> {
                final String[] nc = reportService.buildEmailNotificationContent(finalReport, u,
                        appConfigService
                                .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.REPORT_STATE_CHANGED_MAIL_SUBJECT)
                                .getConfigValue(),
                        appConfigService
                                .getAppConfigForGranterOrg(finalReport.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.REPORT_STATE_CHANGED_MAIL_MESSAGE)
                                .getConfigValue(),
                        workflowStatusService.findById(toStateId).getName(), finalCurrentOwnerName,
                        previousState.getName(),
                        previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                        transition != null ? transition.getAction() : "Request Modifications", "Yes", PLEASE_REVIEW,
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                                ? "Yes"
                                : "No",
                        reportWithNote.getNote() != null && !reportWithNote.getNote().trim().equalsIgnoreCase("")
                                ? PLEASE_REVIEW
                                : "",
                        null, null, null, null, null);

                notificationsService.saveNotification(nc, u.getId(), finalReport.getId(), REPORT);
            });

        }

        report = reportToReturn(report, userId);
        saveSnapShot(report, fromStateId, toStateId, currentOwner, previousOwner);

        if (toStatus.getInternalStatus().equalsIgnoreCase(CLOSED)) {
            List<WorkflowStatus> workflowStatuses = workflowStatusService.getTenantWorkflowStatuses("DISBURSEMENT",
                    report.getGrant().getGrantorOrganization().getId());
            final Report fReport = report;
            List<WorkflowStatus> draftStatuses = workflowStatuses.stream()
                    .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("DRAFT")).collect(Collectors.toList());
            List<Long> draftStatusIds = draftStatuses.stream().mapToLong(WorkflowStatus::getId).boxed()
                    .collect(Collectors.toList());
            List<Disbursement> draftDisbursements = disbursementService
                    .getDibursementsForGrantByStatuses(report.getGrant().getId(), draftStatusIds);


            WorkflowStatus closedtatus = workflowStatuses.stream()
                    .filter(ws -> ws.getInternalStatus().equalsIgnoreCase(CLOSED)).collect(Collectors.toList())
                    .get(0);

            if (draftDisbursements != null && !draftDisbursements.isEmpty()) {
                draftDisbursements
                        .removeIf(dd -> (dd.getReportId() == null || dd.getReportId().longValue() != fReport.getId().longValue() && dd.isGranteeEntry()));
                if (!draftDisbursements.isEmpty()) {
                    for (Disbursement d : draftDisbursements) {
                        d.setStatus(closedtatus);
                        d.setMovedOn(fReport.getMovedOn());
                        List<ActualDisbursement> ads = disbursementService.getActualDisbursementsForDisbursement(d);
                        if (ads != null && !ads.isEmpty()) {
                            for (ActualDisbursement ad : ads) {
                                ad.setStatus(false);
                                ad.setSaved(true);
                            }
                        }
                        disbursementService.saveDisbursement(d);
                    }
                }
            }

        }


        return report;

    }

    private void saveSnapShot(Report report, Long fromStatusId, Long toStatusId, User currentUser, User previousUser) {

        try {
            ReportSnapshot snapshot = new ReportSnapshot();
            snapshot.setAssignedToId(currentUser != null ? currentUser.getId() : null);
            snapshot.setEndDate(report.getEndDate());
            snapshot.setDueDate(report.getDueDate());
            snapshot.setReportId(report.getId());
            snapshot.setName(report.getName());
            snapshot.setStartDate(report.getStartDate());
            snapshot.setStatusId(fromStatusId);
            String stringAttribs = new ObjectMapper().writeValueAsString(report.getReportDetails());
            snapshot.setStringAttributes(stringAttribs);
            snapshot.setFromStringAttributes(stringAttribs);
            snapshot.setAssignedToId(currentUser != null ? currentUser.getId() : null);
            snapshot.setMovedBy(previousUser.getId());
            snapshot.setFromNote(report.getNote());
            snapshot.setFromStateId(fromStatusId);
            snapshot.setToStateId(toStatusId);
            snapshot.setMovedOn(report.getMovedOn());
            reportSnapshotService.saveReportSnapshot(snapshot);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @GetMapping("/{reportId}/history/")
    public List<ReportHistory> getReportHistory(@PathVariable("reportId") Long reportId,
                                                @PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode) {


        List<ReportHistory> history = new ArrayList<>();
        List<ReportSnapshot> reportSnapshotHistory = reportSnapshotService.getReportSnapshotForReport(reportId);
        if (reportSnapshotHistory != null && reportSnapshotHistory.get(0).getFromStateId() == null) {
            history = reportService.getReportHistory(reportId);
            for (ReportHistory historyEntry : history) {
                historyEntry.setNoteAddedByUser(userService.getUserById(historyEntry.getNoteAddedBy()));
            }
        } else {
            for (ReportSnapshot snapShot : reportSnapshotHistory) {
                ReportHistory hist = new ReportHistory();
                hist.setName(snapShot.getName());
                hist.setId(snapShot.getReportId());
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

    @PostMapping("/{reportId}/section/{sectionId}/field/{fieldId}")
    @ApiOperation("Delete field in a section")
    public Report deleteField(
            @ApiParam(name = "reportToSave", value = "Report to save if in edit mode, passed in Body of request") @RequestBody ReportDTO reportToSave,
            @ApiParam(name = "userId", value = "Unique identifier of the logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "sectionId", value = "Unique identifier of the section being modified") @PathVariable("sectionId") Long sectionId,
            @ApiParam(name = "fieldId", value = "Unique identifier of the field being deleted") @PathVariable("fieldId") Long fieldId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        Report report = saveReport(reportId, reportToSave, userId, tenantCode);

        ReportStringAttribute stringAttrib = reportService.getReportStringByStringAttributeId(fieldId);
        ReportSpecificSectionAttribute attribute = stringAttrib.getSectionAttribute();

        if (stringAttrib.getSectionAttribute().getFieldType().equalsIgnoreCase("document")) {
            List<ReportStringAttributeAttachments> attachments = reportService
                    .getStringAttributeAttachmentsByStringAttribute(stringAttrib);
            reportService.deleteStringAttributeAttachments(attachments);
        }
        reportService.deleteStringAttribute(stringAttrib);
        reportService.deleteSectionAttribute(attribute);
        Optional<ReportStringAttribute> optionalReportStringAttribute = report.getStringAttributes().stream()
                .filter(g -> g.getId().longValue() == stringAttrib.getId().longValue()).findFirst();
        ReportStringAttribute rsa2Delete = optionalReportStringAttribute.isPresent() ? optionalReportStringAttribute.get() : null;
        report.getStringAttributes().remove(rsa2Delete);
        report = reportService.saveReport(report);

        if (Boolean.TRUE.equals(reportService.checkIfReportTemplateChanged(report, attribute.getSection(), null))) {
            reportService.createNewReportTemplateFromExisiting(report);
        }
        report = reportToReturn(report, userId);
        return report;
    }

    @GetMapping("/templates")
    @ApiOperation("Get all published grant templates for tenant")
    public List<GranterReportTemplate> getTenantPublishedReportTemplates(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {
        return reportService.findByGranterIdAndPublishedStatusAndPrivateStatus(
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), true, false);
    }

    @PutMapping("/{reportId}/template/{templateId}/{templateName}")
    @ApiOperation("Save custom grant template with name and description")
    public Report updateTemplateName(
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "templateId", value = "Unique identfier of the grant template") @PathVariable("templateId") Long templateId,
            @ApiParam(name = "templateName", value = "NName of the template to be saved") @PathVariable("templateName") String templateName,
            @ApiParam(name = "templateDate", value = "Additional information about the template such as descriptio, publish or save as private") @RequestBody TemplateMetaData templateData) {

        GranterReportTemplate template = granterReportTemplateService.findByTemplateId(templateId);
        if(template!=null) {
            template.setName(templateName);
            template.setDescription(templateData.getDescription());
            template.setPublished(templateData.isPublish());
            template.setPrivateToReport(templateData.isPrivateToGrant());
            template.setPublished(true);
            reportService.saveReportTemplate(template);
        }

        Report report = reportService.getReportById(reportId);
        report = reportToReturn(report, userId);
        return report;

    }

    @GetMapping("/create/grant/{grantId}/template/{templateId}")
    @ApiOperation("Create new report with a template")
    public Report createReport(
            @ApiParam(name = "grantId", value = "Unique identifier for the selected grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "templateId", value = "Unique identifier for the selected template") @PathVariable("templateId") Long templateId,
            @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        Report report = new Report();
        Grant reportForGrant = grantService.getById(grantId);
        GranterReportTemplate reportTemplate = reportService.findByTemplateId(templateId);

        report.setName("");
        report.setStartDate(null);
        report.setStDate("");
        report.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId(REPORT,
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), reportForGrant.getGrantTypeId()));
        report.setEndDate(null);
        report.setEnDate("");
        report.setGrant(reportForGrant);
        report.setType("adhoc");
        report.setCreatedAt(new Date());
        report.setTemplate(reportTemplate);
        report.setCreatedBy(userService.getUserById(userId).getId());

        report = reportService.saveReport(report);

        ReportAssignment assignment = null;

        Organization granterOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        List<WorkflowStatus> statuses = new ArrayList<>();
        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionService
                .getStatusTransitionsForWorkflow(
                        workflowService.findDefaultByGranterAndObjectAndType(granterOrg, REPORT, reportForGrant.getGrantTypeId()));
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

            assignment = new ReportAssignment();
            if (status.isInitial()) {
                assignment.setAnchor(true);
                assignment.setAssignment(userId);
            } else {
                assignment.setAnchor(false);
            }
            assignment.setReportId(report.getId());
            assignment.setStateId(status.getId());

            if (Boolean.TRUE.equals(status.getTerminal())) {
                final Report finalReport = report;
                Optional<GrantAssignments> optional = grantService.getGrantWorkflowAssignments(report.getGrant()).stream().filter(ass -> ass.getStateId().longValue() == finalReport.getGrant().getGrantStatus().getId().longValue()).findFirst();
                GrantAssignments activeStateOwner = optional.isPresent() ? optional.get() : null;
                if (activeStateOwner != null) {
                    assignment.setAssignment(activeStateOwner.getAssignments());
                }
            }
            reportService.saveAssignmentForReport(assignment);

        }

        List<GranterReportSection> granterReportSections = reportTemplate.getSections();
        report.setStringAttributes(new ArrayList<>());
        AtomicBoolean reportTemplateHasDisbursement = new AtomicBoolean(false);
        AtomicReference<ReportStringAttribute> disbursementAttributeValue = new AtomicReference<>(new ReportStringAttribute());


        if (granterReportSections.stream().noneMatch(rs -> rs.getSectionName().equalsIgnoreCase(PROJECT_INDICATORS))) {
            GranterReportSection indicatorSection = new GranterReportSection();
            indicatorSection.setReportTemplate(reportTemplate);
            indicatorSection.setDeletable(true);
            indicatorSection.setGranter((Granter) granterOrg);
            indicatorSection.setSectionName(PROJECT_INDICATORS);
            indicatorSection.setSectionOrder(granterReportSections.size());
            granterReportSections.add(indicatorSection);
        }
        for (GranterReportSection reportSection : granterReportSections) {
            ReportSpecificSection specificSection = new ReportSpecificSection();
            specificSection.setDeletable(true);
            specificSection.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
            specificSection.setReportId(report.getId());
            specificSection.setReportTemplateId(reportTemplate.getId());
            specificSection.setSectionName(reportSection.getSectionName());
            specificSection.setSectionOrder(reportSection.getSectionOrder());

            if (specificSection.getSectionName().equalsIgnoreCase(PROJECT_INDICATORS)) {
                specificSection.setSystemGenerated(true);
            }
            specificSection = reportService.saveReportSpecificSection(specificSection);
            ReportSpecificSection finalSpecificSection = specificSection;
            Report finalReport = report;
            final AtomicInteger[] attribVOOrder = {new AtomicInteger(1)};
            Report finalReport1 = report;

            if (specificSection.getSectionName().equalsIgnoreCase(PROJECT_INDICATORS)) {
                for (Map<DatePeriod, PeriodAttribWithLabel> hold : getPeriodsWithAttributes(report.getGrant(), userId)) {
                    hold.forEach((entry, val) -> val.getAttributes().forEach(attribVo -> {
                        ReportSpecificSectionAttribute sectionAttribute = new ReportSpecificSectionAttribute();
                        sectionAttribute.setAttributeOrder(attribVOOrder[0].getAndIncrement());
                        sectionAttribute.setDeletable(attribVo.isDeletable());
                        sectionAttribute.setFieldName(attribVo.getFieldName());
                        sectionAttribute.setFieldType(attribVo.getFieldType());
                        sectionAttribute.setGranter(finalSpecificSection.getGranter());
                        sectionAttribute.setRequired(attribVo.isRequired());
                        sectionAttribute.setSection(finalSpecificSection);
                        sectionAttribute.setCanEdit(false);
                        sectionAttribute = reportService.saveReportSpecificSectionAttribute(sectionAttribute);

                        ReportStringAttribute stringAttribute = new ReportStringAttribute();

                        stringAttribute.setSection(finalSpecificSection);
                        stringAttribute.setReport(finalReport);
                        stringAttribute.setSectionAttribute(sectionAttribute);
                        stringAttribute.setGrantLevelTarget(attribVo.getTarget());
                        stringAttribute.setFrequency(attribVo.getFrequency());

                        stringAttribute = reportService.saveReportStringAttribute(stringAttribute);
                    }));
                }

            }

            reportSection.getAttributes().forEach(a -> {
                ReportSpecificSectionAttribute sectionAttribute = new ReportSpecificSectionAttribute();
                sectionAttribute.setAttributeOrder(attribVOOrder[0].getAndIncrement());
                sectionAttribute.setDeletable(a.getDeletable());
                sectionAttribute.setFieldName(a.getFieldName());
                sectionAttribute.setFieldType(a.getFieldType());
                sectionAttribute.setGranter(finalSpecificSection.getGranter());
                sectionAttribute.setRequired(a.getRequired());
                sectionAttribute.setSection(finalSpecificSection);
                sectionAttribute.setCanEdit(true);
                sectionAttribute.setExtras(a.getExtras());
                sectionAttribute = reportService.saveReportSpecificSectionAttribute(sectionAttribute);

                ReportStringAttribute stringAttribute = new ReportStringAttribute();

                stringAttribute.setSection(finalSpecificSection);
                stringAttribute.setReport(finalReport);
                stringAttribute.setSectionAttribute(sectionAttribute);
                if (sectionAttribute.getFieldType().equalsIgnoreCase("kpi")) {
                    stringAttribute.setGrantLevelTarget(null);
                    stringAttribute.setFrequency(finalReport1.getType().toLowerCase());
                } else if (sectionAttribute.getFieldType().equalsIgnoreCase(TABLE)) {
                    stringAttribute.setValue(a.getExtras());
                }
                stringAttribute = reportService.saveReportStringAttribute(stringAttribute);
                if (sectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                    reportTemplateHasDisbursement.set(true);
                    disbursementAttributeValue.set(stringAttribute);
                }
            });
        }

        // Handle logic for setting dibursement type in reports
        for (GrantSpecificSection grantSection : grantService.getGrantSections(report.getGrant())) {
            for (GrantSpecificSectionAttribute specificSectionAttribute : grantService
                    .getAttributesBySection(grantSection)) {
                if (specificSectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                    if (reportTemplateHasDisbursement.get()) {
                        ObjectMapper mapper = new ObjectMapper();
                        String[] colHeaders = new String[]{DISBURSEMENT_DATE, ACTUAL_DISBURSEMENT,
                                "Funds from other Sources", NOTES};
                        List<TableData> tableDataList = new ArrayList<>();
                        TableData tableData = new TableData();
                        tableData.setName("1");
                        tableData.setHeader("Planned Installment #");
                        tableData.setEnteredByGrantee(false);
                        tableData.setColumns(new ColumnData[4]);
                        for (int i = 0; i < tableData.getColumns().length; i++) {

                            String check = (i == 0) ? "date" : "";
                            tableData.getColumns()[i] = new ColumnData(colHeaders[i], "",
                                    (i == 1 || i == 2) ? CURRENCY : check);
                        }
                        tableDataList.add(tableData);

                        try {
                            disbursementAttributeValue.get().setValue(mapper.writeValueAsString(tableDataList));
                            reportService.saveReportStringAttribute(disbursementAttributeValue.get());
                        } catch (JsonProcessingException e) {
                            logger.error(e.getMessage(), e);
                        }
                    } else {
                        ReportSpecificSection specificSection = new ReportSpecificSection();
                        specificSection.setDeletable(true);
                        specificSection.setGranter((Granter) report.getGrant().getGrantorOrganization());
                        specificSection.setReportId(report.getId());
                        specificSection.setReportTemplateId(reportTemplate.getId());
                        specificSection.setSectionName("Project Funds");
                        specificSection.setSystemGenerated(true);
                        List<ReportSpecificSection> reportSections = reportService.getReportSections(report);
                        specificSection.setSectionOrder(Collections.max(reportSections.stream()
                                .map(ReportSpecificSection::getSectionOrder).collect(Collectors.toList())) + 1);
                        specificSection = reportService.saveReportSpecificSection(specificSection);

                        ReportSpecificSectionAttribute sectionAttribute = new ReportSpecificSectionAttribute();
                        sectionAttribute.setAttributeOrder(1);
                        sectionAttribute.setDeletable(true);
                        sectionAttribute.setFieldName("Disbursement Details");
                        sectionAttribute.setFieldType(DISBURSEMENT);
                        sectionAttribute.setGranter((Granter) report.getGrant().getGrantorOrganization());
                        sectionAttribute.setRequired(false);
                        sectionAttribute.setSection(specificSection);
                        sectionAttribute.setCanEdit(true);
                        sectionAttribute.setExtras(null);
                        sectionAttribute = reportService.saveReportSpecificSectionAttribute(sectionAttribute);

                        ReportStringAttribute stringAttribute = new ReportStringAttribute();

                        stringAttribute.setSection(specificSection);
                        stringAttribute.setReport(report);
                        stringAttribute.setSectionAttribute(sectionAttribute);

                        reportService.saveReportStringAttribute(stringAttribute);

                    }
                }
            }
        }

        report = reportToReturn(report, userId);
        return report;
    }

    private List<Map<DatePeriod, PeriodAttribWithLabel>> getPeriodsWithAttributes(Grant grant, Long userId) {

        GrantVO grantVO = new GrantVO().build(grant, grantService.getGrantSections(grant),
                workflowPermissionService, userService.getUserById(userId),
                userService, grantService);
        grant.setGrantDetails(grantVO.getGrantDetails());

        List<Map<DatePeriod, PeriodAttribWithLabel>> periodsWithAttributes = new ArrayList<>();
        Map<DatePeriod, PeriodAttribWithLabel> quarterlyPeriods = new HashMap<>();
        Map<DatePeriod, PeriodAttribWithLabel> halfyearlyPeriods = new HashMap<>();
        Map<DatePeriod, PeriodAttribWithLabel> monthlyPeriods = new HashMap<>();
        Map<DatePeriod, PeriodAttribWithLabel> yearlyPeriods = new HashMap<>();
        if (grant.getStartDate() != null && grant.getEndDate() != null) {
            grant.getGrantDetails().getSections().forEach(sec -> {
                if (sec.getAttributes() != null && !sec.getAttributes().isEmpty()) {
                    List<String> order = ImmutableList.of("YEARLY", "HALF-YEARLY", "QUARTERLY", "MONTHLY");
                    Comparator<SectionAttributesVO> attrComparator = Comparator
                            .comparing(c -> order.indexOf(c.getFrequency().toUpperCase()));
                    sec.getAttributes().removeIf(attr -> attr.getFrequency() == null);
                    sec.getAttributes().sort(attrComparator);

                    sec.getAttributes().forEach(attr -> {
                        if (attr.getFieldType().equalsIgnoreCase("KPI")) {

                            if (attr.getFrequency().equalsIgnoreCase("YEARLY")) {
                                DateTime st = new DateTime(grant.getMinEndEndate() != null
                                        ? new DateTime(grant.getMinEndEndate()).plusDays(1).toDate()
                                        : grant.getStartDate(), DateTimeZone.forID(timezone)).withTimeAtStartOfDay();
                                DateTime en = new DateTime(grant.getEnDate(), DateTimeZone.forID(timezone)).withTime(23,
                                        59, 59, 999);
                                List<DatePeriod> reportingFrequencies = getReportingFrequencies(st, en,
                                        Frequency.YEARLY);

                                reportingFrequencies.forEach(rf -> {

                                    List<SectionAttributesVO> attrList = null;

                                    if (yearlyPeriods.containsKey(rf)) {
                                        attrList = yearlyPeriods.get(rf).getAttributes();
                                    } else {
                                        attrList = new ArrayList<>();
                                    }
                                    attrList.add(attr);
                                    yearlyPeriods.put(rf, new PeriodAttribWithLabel(rf.getLabel(), attrList));

                                });
                            }

                            if (attr.getFrequency().equalsIgnoreCase("HALF-YEARLY")) {
                                DateTime st = new DateTime(grant.getMinEndEndate() != null
                                        ? new DateTime(grant.getMinEndEndate()).plusDays(1).toDate()
                                        : grant.getStartDate(), DateTimeZone.forID(timezone)).withTimeAtStartOfDay();
                                DateTime en = new DateTime(grant.getEnDate(), DateTimeZone.forID(timezone)).withTime(23,
                                        59, 59, 999);
                                List<DatePeriod> reportingFrequencies = getReportingFrequencies(st, en,
                                        Frequency.HALF_YEARLY);

                                reportingFrequencies.forEach(rf -> {

                                    List<SectionAttributesVO> attrList = null;
                                    if (yearlyPeriods.containsKey(rf)) {
                                        yearlyPeriods.get(rf).getAttributes().add(attr);
                                    } else {

                                        if (halfyearlyPeriods.containsKey(rf)) {
                                            attrList = halfyearlyPeriods.get(rf).getAttributes();
                                        } else {
                                            attrList = new ArrayList<>();
                                        }
                                        attrList.add(attr);
                                        halfyearlyPeriods.put(rf, new PeriodAttribWithLabel(rf.getLabel(), attrList));
                                    }
                                });
                            }

                            if (attr.getFrequency().equalsIgnoreCase("QUARTERLY")) {

                                DateTime st = new DateTime(grant.getMinEndEndate() != null
                                        ? new DateTime(grant.getMinEndEndate()).plusDays(1).toDate()
                                        : grant.getStartDate(), DateTimeZone.forID(timezone)).withTimeAtStartOfDay();
                                DateTime en = new DateTime(grant.getEnDate(), DateTimeZone.forID(timezone)).withTime(23,
                                        59, 59, 999);
                                List<DatePeriod> reportingFrequencies = getReportingFrequencies(st, en,
                                        Frequency.QUARTERLY);
                                reportingFrequencies.forEach(rf -> {

                                    List<SectionAttributesVO> attrList = null;

                                    if (yearlyPeriods.containsKey(rf)) {
                                        yearlyPeriods.get(rf).getAttributes().add(attr);
                                    } else if (halfyearlyPeriods.containsKey(rf)) {
                                        halfyearlyPeriods.get(rf).getAttributes().add(attr);
                                    } else {
                                        if (quarterlyPeriods.containsKey(rf)) {
                                            attrList = quarterlyPeriods.get(rf).getAttributes();
                                        } else {
                                            attrList = new ArrayList<>();
                                        }
                                        attrList.add(attr);
                                        quarterlyPeriods.put(rf, new PeriodAttribWithLabel(rf.getLabel(), attrList));
                                    }
                                });

                            }
                        }

                        if (attr.getFrequency().equalsIgnoreCase("MONTHLY")) {
                            DateTime st = new DateTime(grant.getMinEndEndate() != null
                                    ? new DateTime(grant.getMinEndEndate()).plusDays(1).toDate()
                                    : grant.getStartDate(), DateTimeZone.forID(timezone)).withTimeAtStartOfDay();
                            DateTime en = new DateTime(grant.getEnDate(), DateTimeZone.forID(timezone)).withTime(23, 59,
                                    59, 999);
                            List<DatePeriod> reportingFrequencies = getReportingFrequencies(st, en, Frequency.MONTHLY);

                            reportingFrequencies.forEach(rf -> {

                                List<SectionAttributesVO> attrList = null;
                                if (yearlyPeriods.containsKey(rf)) {
                                    yearlyPeriods.get(rf).getAttributes().add(attr);
                                } else if (halfyearlyPeriods.containsKey(rf)) {
                                    halfyearlyPeriods.get(rf).getAttributes().add(attr);
                                } else if (quarterlyPeriods.containsKey(rf)) {
                                    quarterlyPeriods.get(rf).getAttributes().add(attr);
                                } else {

                                    if (monthlyPeriods.containsKey(rf)) {
                                        attrList = monthlyPeriods.get(rf).getAttributes();
                                    } else {
                                        attrList = new ArrayList<>();
                                    }
                                    attrList.add(attr);
                                    monthlyPeriods.put(rf, new PeriodAttribWithLabel(rf.getLabel(), attrList));
                                }
                            });

                        }

                    });
                }
            });
        }


        periodsWithAttributes.add(monthlyPeriods);
        periodsWithAttributes.add(quarterlyPeriods);
        periodsWithAttributes.add(halfyearlyPeriods);
        periodsWithAttributes.add(yearlyPeriods);
        return periodsWithAttributes;
    }

    private List<DatePeriod> getReportingFrequencies(DateTime st, DateTime en, Frequency frequency) {

        List<DatePeriod> periods = new ArrayList<>();
        List<DatePeriod> periodsToReturn = new ArrayList<>();
        if (frequency == Frequency.MONTHLY) {

            while (st.isBefore(en) && !st.withTime(23, 59, 59, 999).isEqual(en)) {
                DateTime tempEn = st.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
                if (tempEn.isAfter(en)) {
                    DatePeriod dp = new DatePeriod(st.toDate(), en.toDate());
                    dp.setLabel("Monthly Report");
                    periods.add(dp);
                    break;
                }
                DatePeriod p = new DatePeriod(st.toDate(), tempEn.toDate());
                p.setLabel("Monthly Report");
                periods.add(p);
                st = tempEn.plusDays(1).withTimeAtStartOfDay();
            }

        } else if (frequency == Frequency.QUARTERLY) {

            while (st.isBefore(en) && !st.withTime(23, 59, 59, 999).isEqual(en)) {
                DatePeriodLabel qrtrEnd = endOfQuarter(st);
                DateTime tempEn = qrtrEnd.getDateTime().dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
                if (tempEn.isAfter(en)) {
                    DatePeriod dp = new DatePeriod(st.toDate(), en.toDate());
                    dp.setLabel(endOfQuarter(st).getPeriodLabel());
                    periods.add(dp);
                    break;
                }
                DatePeriod p = new DatePeriod(st.toDate(), tempEn.toDate());
                p.setLabel(qrtrEnd.getPeriodLabel());
                periods.add(p);
                st = tempEn.plusDays(1).withTimeAtStartOfDay();
            }
        } else if (frequency == Frequency.HALF_YEARLY) {

            while (st.isBefore(en) && !st.withTime(23, 59, 59, 999).isEqual(en)) {
                DatePeriodLabel halfYrEnd = endOfHalfYear(st);
                DateTime tempEn = halfYrEnd.getDateTime().dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
                if (tempEn.isAfter(en)) {
                    DatePeriod dp = new DatePeriod(st.toDate(), en.toDate());
                    dp.setLabel(endOfHalfYear(st).getPeriodLabel());
                    periods.add(dp);
                    break;
                }
                DatePeriod p = new DatePeriod(st.toDate(), tempEn.toDate());
                p.setLabel(halfYrEnd.getPeriodLabel());
                periods.add(p);
                st = tempEn.plusDays(1).withTimeAtStartOfDay();
            }
        } else if (frequency == Frequency.YEARLY) {

            while (st.isBefore(en) && !st.withTime(23, 59, 59, 999).isEqual(en)) {
                DatePeriodLabel yrEnd = endOfYear(st);
                DateTime tempEn = yrEnd.getDateTime().dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
                if (tempEn.isAfter(en)) {
                    DatePeriod dp = new DatePeriod(st.toDate(), en.toDate());
                    dp.setLabel(endOfYear(st).getPeriodLabel());
                    periods.add(dp);
                    break;
                }
                DatePeriod p = new DatePeriod(st.toDate(), tempEn.toDate());
                p.setLabel(yrEnd.getPeriodLabel());
                periods.add(p);
                st = tempEn.plusDays(1).withTimeAtStartOfDay();
            }
        }
        periodsToReturn.add(periods.get(0));
        return periodsToReturn;
    }

    private DatePeriodLabel endOfQuarter(DateTime st) {
        if (st.getMonthOfYear() >= Month.JANUARY.getValue() && st.getMonthOfYear() <= Month.MARCH.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.MARCH.getValue()),
                    "Quarterly Report - Q4 " + (st.getYear() - 1) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear()).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.APRIL.getValue() && st.getMonthOfYear() <= Month.JUNE.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.JUNE.getValue()),
                    "Quarterly Report - Q1 " + (st.getYear()) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.JULY.getValue() && st.getMonthOfYear() <= Month.SEPTEMBER.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.SEPTEMBER.getValue()),
                    "Quarterly Report - Q2 " + (st.getYear()) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else {
            return new DatePeriodLabel(st.withMonthOfYear(Month.DECEMBER.getValue()),
                    "Quarterly Report - Q3 " + (st.getYear()) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        }
    }

    private DatePeriodLabel endOfHalfYear(DateTime st) {
        if (st.getMonthOfYear() >= Month.APRIL.getValue() && st.getMonthOfYear() <= Month.SEPTEMBER.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.SEPTEMBER.getValue()),
                    "Half-Yearly Report - H1 " + (st.getYear()) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.OCTOBER.getValue()
                && st.getMonthOfYear() <= Month.DECEMBER.getValue()) {
            return new DatePeriodLabel(st.plusYears(1).withMonthOfYear(Month.MARCH.getValue()),
                    "Half-Yearly Report - H2 " + (st.getYear()) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else {
            return new DatePeriodLabel(st.withMonthOfYear(Month.MARCH.getValue()),
                    "Half-Yearly Report - H2 " + (st.getYear() - 1) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear()).substring(2, 4)));
        }
    }

    private DatePeriodLabel endOfYear(DateTime st) {
        if (st.getMonthOfYear() >= Month.APRIL.getValue() && st.getMonthOfYear() <= Month.DECEMBER.getValue()) {
            return new DatePeriodLabel(st.plusYears(1).withMonthOfYear(Month.MARCH.getValue()),
                    "Yearly Report " + (st.getYear()) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else {
            return new DatePeriodLabel(st.withMonthOfYear(Month.MARCH.getValue()),
                    "Yearly Report " + (st.getYear() - 1) + PATH_SEPARATOR
                            + (String.valueOf(st.getYear()).substring(2, 4)));
        }
    }

    @PostMapping(value = "/{reportId}/attachments", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadSelectedAttachments(@PathVariable("userId") Long userId,
                                              @PathVariable("reportId") Long reportId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                                              @RequestBody AttachmentDownloadRequest downloadRequest, HttpServletResponse response) throws IOException {

        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"test.zip\"");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);) {


            ArrayList<File> files = new ArrayList<>(2);
            files.add(new File("README.md"));

            User user = userService.getUserById(userId);

            for (Long attachmentId : downloadRequest.getAttachmentIds()) {
                ReportStringAttributeAttachments attachment = reportService
                        .getStringAttributeAttachmentsByAttachmentId(attachmentId);
                Long sectionId = attachment.getReportStringAttribute().getSectionAttribute().getSection().getId();
                Long attributeId = attachment.getReportStringAttribute().getSectionAttribute().getId();
                File file = null;
                if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
                    file = resourceLoader.getResource(FILE + uploadLocation
                            + user.getOrganization().getName().toUpperCase() + REPORT_DOCUMENTS + reportId + PATH_SEPARATOR
                            + sectionId + PATH_SEPARATOR + attributeId + PATH_SEPARATOR + attachment.getName() + "." + attachment.getType())
                            .getFile();
                    if (!file.exists()) {
                        file = resourceLoader
                                .getResource(FILE + uploadLocation
                                        + reportService.getReportById(reportId).getGrant().getGrantorOrganization()
                                        .getCode().toUpperCase()
                                        + REPORT_DOCUMENTS + reportId + PATH_SEPARATOR + sectionId + PATH_SEPARATOR + attributeId + PATH_SEPARATOR
                                        + attachment.getName() + "." + attachment.getType())
                                .getFile();
                    }
                } else {
                    file = resourceLoader.getResource(
                            FILE + uploadLocation + tenantCode + REPORT_DOCUMENTS + reportId + PATH_SEPARATOR + sectionId + PATH_SEPARATOR
                                    + attributeId + PATH_SEPARATOR + attachment.getName() + "." + attachment.getType())
                            .getFile();
                    if (!file.exists()) {

                        file = resourceLoader
                                .getResource(FILE + uploadLocation
                                        + reportService.getReportById(reportId).getGrant().getOrganization().getName()
                                        .toUpperCase()
                                        + REPORT_DOCUMENTS + reportId + PATH_SEPARATOR + sectionId + PATH_SEPARATOR + attributeId + PATH_SEPARATOR
                                        + attachment.getName() + "." + attachment.getType())
                                .getFile();
                    }
                }
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    IOUtils.copy(fileInputStream, zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            }

            zipOutputStream.finish();
            zipOutputStream.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @GetMapping("/{reportId}/file/{fileId}")
    @ApiOperation(value = "Get file for download")
    public ResponseEntity<Resource> getFileForDownload(HttpServletResponse servletResponse,
                                                       @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("reportId") Long reportId,
                                                       @PathVariable("fileId") Long fileId) {

        ReportStringAttributeAttachments attachment = reportService.getStringAttributeAttachmentsByAttachmentId(fileId);
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

    @PostMapping("{reportId}/attribute/{attributeId}/attachment/{attachmentId}")
    @ApiOperation("Delete attachment from document field")
    public Report deleteReportStringAttributeAttachment(
            @ApiParam(name = "reportToSave", value = "Report to save in edit mode, pass in Body of request") @RequestBody ReportDTO reportToSave,
            @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "userId", value = "Unique identifier og logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "attachmentId", value = "Unique identifier of the document attachment being deleted") @PathVariable("attachmentId") Long attachmentId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @ApiParam(name = "attributeId", value = "Unique identifier of the document field") @PathVariable("attributeId") Long attributeId) {
        saveReport(reportId, reportToSave, userId, tenantCode);
        ReportStringAttributeAttachments attch = reportService
                .getStringAttributeAttachmentsByAttachmentId(attachmentId);
        reportService.deleteStringAttributeAttachments(Arrays.asList(attch));

        String fileName = attch.getName();
        if (!fileName.contains(".".concat(attch.getType()))) {
            fileName = fileName.concat(".".concat(attch.getType()));
        }
        File file = new File(attch.getLocation() + fileName);
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        ReportStringAttribute stringAttribute = reportService.findReportStringAttributeById(attributeId);
        List<ReportStringAttributeAttachments> stringAttributeAttachments = reportService
                .getStringAttributeAttachmentsByStringAttribute(stringAttribute);
        ObjectMapper mapper = new ObjectMapper();
        try {
            stringAttribute.setValue(mapper.writeValueAsString(stringAttributeAttachments));
            reportService.saveReportStringAttribute(stringAttribute);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }

        Report report = reportService.getReportById(reportId);

        report = reportToReturn(report, userId);
        return report;
    }

    @GetMapping("/resolve")
    public Report resolveReport(@PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                                @RequestParam("r") String reportCode) {
        Long reportId = Long.valueOf(new String(Base64.getDecoder().decode(reportCode), StandardCharsets.UTF_8));
        Report report = reportService.getReportById(reportId);

        report = reportToReturn(report, userId);
        checkAndReturnHistoricalReport(userId, report);
        return report;
    }

    private void checkAndReturnHistoricalReport(@PathVariable("userId") Long userId, Report report) {
        if (userService.getUserById(userId).getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)
                && report.getStatus().getInternalStatus().equalsIgnoreCase(REVIEW)) {
            try {
                ReportHistory historicReport = reportService.getSingleReportHistoryByStatusAndReportId(ACTIVE,
                        report.getId());
                if (historicReport != null && historicReport.getReportDetail() != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    report.setReportDetails(mapper.readValue(historicReport.getReportDetail(), ReportDetailVO.class));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @DeleteMapping("/{reportId}")
    @ApiOperation("Delete report")
    public void deleteReport(
            @ApiParam(name = "reportId", value = "Unique identifier of the report") @PathVariable("reportId") Long reportId,
            @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code ") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        Report report = reportService.getReportById(reportId);

        reportService.deleteReport(report);
    }

    @GetMapping(value = "/compare/{currentReportId}/{origReportId}")
    public List<PlainReport> getReportsToCompare(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                 @PathVariable("userId") Long userId,
                                                 @PathVariable("currentGrantId") Long currentReportId,
                                                 @PathVariable("origGrantId") Long origReportId) {

        List<PlainReport> reportsToReturn = new ArrayList<>();

        Report currentReport = reportService.getReportById(currentReportId);
        currentReport = reportToReturn(currentReport, userId);

        Report origReport = reportService.getReportById(origReportId);
        origReport = reportToReturn(origReport, userId);

        try {
            reportsToReturn.add(reportService.reportToPlain(currentReport));
            reportsToReturn.add(reportService.reportToPlain(origReport));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return reportsToReturn;
    }

    @GetMapping(value = "/compare/{currentReportId}")
    public PlainReport getPlainGrantForCompare(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                               @PathVariable("userId") Long userId,
                                               @PathVariable("currentReportId") Long currentReportId) throws IOException {
        Report currenReport = reportService.getReportById(currentReportId);
        currenReport = reportToReturn(currenReport, userId);


        return reportService.reportToPlain(currenReport);
    }
}
