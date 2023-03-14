package org.codealpha.gmsservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    public static final String DD_MMM_YYYY = "dd-MMM-yyyy";
    public static final String DISBURSEMENT = "disbursement";
    public static final String ACTIVE = "ACTIVE";
    public static final String GRANTEE = "GRANTEE";
    public static final String CLOSED = "CLOSED";
    public static final String CURRENCY = "currency";
    public static final String REPORT = "REPORT";
    public static final String HOME_ACTION_LOGIN_ORG = "/landing/?action=login&org=";
    public static final String EMAIL_TYPE_REPORT = "&email=&type=report";
    public static final String GRANT_NAME = "%GRANT_NAME%";
    public static final String REPORT_NAME = "%REPORT_NAME%";
    public static final String TD_CLOSE = "</td>";
    private static final String SECRET = "78yughvdbfv87ny4w87rbshfiv8aw4tr87awvyeruvbhdkjfhbity834t";
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportCardRepository reportCardRepository;

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;
    @Autowired
    private ReportAssignmentRepository reportAssignmentRepository;
    @Autowired
    private GrantAssignmentRepository grantAssignmentRepository;
    @Autowired
    private GranterReportTemplateRepository granterReportTemplateRepository;
    @Autowired
    private GranterReportSectionRepository granterReportSectionRepository;
    @Autowired
    private ReportSpecificSectionRepository reportSpecificSectionRepository;
    @Autowired
    private ReportSpecificSectionAttributeRepository reportSpecificSectionAttributeRepository;
    @Autowired
    private ReportStringAttributeRepository reportStringAttributeRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private WorkflowStatusTransitionRepository workflowStatusTransitionRepository;
    @Autowired
    private TemplateLibraryRepository templateLibraryRepository;
    @Autowired
    private GranterReportSectionAttributeRepository granterReportSectionAttributeRepository;
    @Autowired
    private ReportStringAttributeAttachmentsRepository reportStringAttributeAttachmentsRepository;
    @Autowired
    private ReportHistoryRepository reportHistoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private ReportAssignmentHistoryRepository assignmentHistoryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DisabledUsersEntityRepository disabledUsersEntityRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private GrantTypeWorkflowMappingService grantTypeWorkflowMappingService;
    @Value("${spring.timezone}")
    private String timezone;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private DisbursementService disbursementService;
    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private WorkflowPermissionRepository workflowPermissionRepository;

    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return (List<Report>) reportRepository.findAll();
    }

    public List<ReportAssignment> saveAssignments(Report report, String tenantCode) {
        ReportAssignment assignment = null;

        Organization granterOrg = organizationRepository.findByCode(tenantCode);
        List<WorkflowStatus> statuses = new ArrayList<>();
        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionRepository
                .findByWorkflow(workflowRepository.findByGranterAndObjectAndType(granterOrg.getId(), REPORT, report.getGrant().getGrantTypeId()).get(0));
        for (WorkflowStatusTransition supportedTransition : supportedTransitions) {
            if (statuses.stream()
                    .noneMatch(s -> s.getId().longValue() == supportedTransition.getFromState().getId().longValue())
                    ) {
                statuses.add(supportedTransition.getFromState());
            }
            if (statuses.stream()
                    .noneMatch(s -> s.getId().longValue() == supportedTransition.getToState().getId().longValue())
                    ) {
                statuses.add(supportedTransition.getToState());
            }
        }

        Workflow grantWorkflow = workflowRepository.findWorkflowByGrantTypeAndObject(report.getGrant().getGrantTypeId(), "GRANT");
        Optional<WorkflowStatus> grantActiveStatus = workflowStatusRepository
                .getAllTenantStatuses("GRANT", report.getGrant().getGrantorOrganization().getId()).stream()
                .filter(s -> s.getInternalStatus().equalsIgnoreCase(ACTIVE) && s.getWorkflow().getId().longValue() == grantWorkflow.getId().longValue()).findFirst();


        GrantAssignments anchorAssignment = null;
        if (grantActiveStatus.isPresent()) {
            anchorAssignment = grantAssignmentRepository
                    .findByGrantIdAndStateId(report.getGrant().getId(), grantActiveStatus.get().getId()).get(0);
        }
        List<ReportAssignment> assignments = new ArrayList<>();
        for (WorkflowStatus status : statuses) {

            assignment = new ReportAssignment();
            if (status.isInitial()) {
                assignment.setAnchor(true);
                assignment.setAssignment(anchorAssignment != null ? anchorAssignment.getAssignments() : null);
            } else {
                assignment.setAnchor(false);
            }
            assignment.setReportId(report.getId());
            assignment.setStateId(status.getId());
            if (Boolean.TRUE.equals(status.getTerminal())) {
                assignment.setAssignment(anchorAssignment != null ? anchorAssignment.getAssignments() : null);
            }
            assignment = saveReportAssignment(assignment);
            assignments.add(assignment);

        }

        return assignments;
    }

    public List<ReportAssignment> saveNewAssignmentForGrantee(Report report, Long granteeUserId) {
        ReportAssignment assignment = null;

        List<WorkflowStatus> statuses = workflowStatusRepository.getAllTenantStatuses(REPORT,
                report.getGrant().getGrantorOrganization().getId());

        List<ReportAssignment> assignments = new ArrayList<>();
        for (WorkflowStatus status : statuses) {
            if (Boolean.FALSE.equals(status.getTerminal())) {
                assignment = new ReportAssignment();
                if (status.getInternalStatus().equalsIgnoreCase(ACTIVE)) {
                    assignment.setAssignment(granteeUserId);
                    assignment.setAnchor(false);
                    assignment.setReportId(report.getId());
                    assignment.setStateId(status.getId());
                    assignment = saveReportAssignment(assignment);
                    assignments.add(assignment);
                }

            }
        }
        return assignments;
    }

    private ReportAssignment saveReportAssignment(ReportAssignment assignment) {
        return reportAssignmentRepository.save(assignment);
    }

    public List<ReportAssignment> getAssignmentsForReport(Report report) {
        return reportAssignmentRepository.findByReportId(report.getId());
    }

    public List<ReportAssignment> getAssignmentsForReportById(Long reportId) {
        return reportAssignmentRepository.findByReportId(reportId);
    }

    public List<Report> getAllAssignedReportsForGranteeUser(Long userId, Long granteeOrgId, String status) {

        return reportRepository.findAllAssignedReportsForGranteeUser(userId, granteeOrgId, status);
    }


    public List<ReportCard> getAllAssignedReportCardsForGranteeUser(Long userId, Long granteeOrgId, String status) {

        return reportCardRepository.findAllAssignedReportsForGranteeUser(userId, granteeOrgId, status);
    }

    public ReportHistory getSingleReportHistoryByStatusAndReportId(String status, Long reportId) {
        return reportHistoryRepository.getSingleReportHistoryByStatusAndReportId(status, reportId);
    }

    public List<Report> getAllAssignedReportsForGranterUser(Long userId, Long granterOrgId) {

        return reportRepository.findAllAssignedReportsForGranterUser(userId, granterOrgId);
    }

    public List<Report> getUpcomingReportsForGranterUserByDateRange(Long userId, Long granterOrgId, Date start,
                                                                    Date end) {

        return reportRepository.findUpcomingReportsForGranterUserByDateRange(userId, granterOrgId, start, end);
    }

    public List<ReportCard> getUpcomingReportCardsForGranterUserByDateRange(Long userId, Long granterOrgId, Date start,
                                                                            Date end) {

        return reportCardRepository.findUpcomingReportsForGranterUserByDateRange(userId, granterOrgId, start, end);
    }

    public List<Report> getUpcomingReportsForAdminGranterUserByDateRange(Long userId, Long granterOrgId, Date start,
                                                                         Date end) {

        return reportRepository.findUpcomingReportsForAdminGranterUserByDateRange(userId, granterOrgId, start, end);
    }

    public List<ReportCard> getUpcomingReportCardsForAdminGranterUserByDateRange(Long userId, Long granterOrgId, Date start,
                                                                                 Date end) {

        return reportCardRepository.findUpcomingReportsForAdminGranterUserByDateRange(userId, granterOrgId, start, end);
    }

    public List<Report> getFutureReportForGranterUserByDateRangeAndGrant(Long userId, Long granterOrgId, Date end,
                                                                         Long grantId) {

        return reportRepository.findFutureReportsToSubmitForGranterUserByDateRangeAndGrant(userId, granterOrgId, end,
                grantId);
    }

    public List<ReportCard> futureReportForGranterUserByDateRangeAndGrant(Long userId, Long granterOrgId, Date end,
                                                                          Long grantId) {

        return reportCardRepository.futureReportsToSubmitForGranterUserByDateRangeAndGrant(userId, granterOrgId, end,
                grantId);
    }

    public List<ReportCard> getFutureReportCardsForGranterUserByDateRangeAndGrant(Long userId, Long granterOrgId, Date end,
                                                                                  Long grantId) {

        return reportCardRepository.findFutureReportsToSubmitForGranterUserByDateRangeAndGrant(userId, granterOrgId, end,
                grantId);
    }

    public List<Report> getReadyToSubmitReportsForGranterUserByDateRange(Long userId, Long granterOrgId, Date start,
                                                                         Date end) {

        return reportRepository.findReadyToSubmitReportsForGranterUserByDateRange(userId, granterOrgId, start, end);
    }

    public List<ReportCard> getReadyToSubmitReportCardsForGranterUserByDateRange(Long userId, Long granterOrgId, Date start,
                                                                                 Date end) {

        return reportCardRepository.findReadyToSubmitReportsForGranterUserByDateRange(userId, granterOrgId, start, end);
    }

    public List<Report> getReadyToSubmitReportsForAdminGranterUserByDateRange(Long userId, Long granterOrgId, Date start,
                                                                              Date end) {

        return reportRepository.findReadyToSubmitReportsForAdminGranterUserByDateRange(userId, granterOrgId, start, end);
    }

    public List<ReportCard> getReadyToSubmitReportCardsForAdminGranterUserByDateRange(Long userId, Long granterOrgId, Date start,
                                                                                      Date end) {

        return reportCardRepository.findReadyToSubmitReportsForAdminGranterUserByDateRange(userId, granterOrgId, start, end);
    }

    public List<Report> getSubmittedReportsForGranterUserByDateRange(Long userId, Long granterOrgId) {

        return reportRepository.findSubmittedReportsForGranterUserByDateRange(userId, granterOrgId);
    }

    public List<ReportCard> getSubmittedReportCardsForGranterUserByDateRange(Long userId, Long granterOrgId) {

        return reportCardRepository.findSubmittedReportsForGranterUserByDateRange(userId, granterOrgId);
    }

    public List<Report> getSubmittedReportsForAdminGranterUserByDateRange(Long userId, Long granterOrgId) {

        return reportRepository.findSubmittedReportsForAdminGranterUserByDateRange(userId, granterOrgId);
    }

    public List<ReportCard> getSubmittedReportCardsForAdminGranterUserByDateRange(Long userId, Long granterOrgId) {

        return reportCardRepository.findSubmittedReportsForAdminGranterUserByDateRange(userId, granterOrgId);
    }

    public List<Report> getApprovedReportsForGranterUserByDateRange(Long userId, Long granterOrgId) {

        return reportRepository.findApprovedReportsForGranterUserByDateRange(userId, granterOrgId);
    }

    public List<ReportCard> getApprovedReportCardsForGranterUserByDateRange(Long userId, Long granterOrgId) {

        return reportCardRepository.findApprovedReportsForGranterUserByDateRange(userId, granterOrgId);
    }

    public List<Report> getApprovedReportsForAdminGranterUserByDateRange(Long userId, Long granterOrgId) {

        return reportRepository.findApprovedReportsForAdminGranterUserByDateRange(userId, granterOrgId);
    }

    public List<ReportCard> getApprovedReportCardsForAdminGranterUserByDateRange(Long userId, Long granterOrgId) {

        return reportCardRepository.findApprovedReportsForAdminGranterUserByDateRange(userId, granterOrgId);
    }

    public GranterReportTemplate getDefaultTemplate(Long granterId) {
        return granterReportTemplateRepository.findByGranterIdAndDefaultTemplate(granterId, true);
    }

    public List<GranterReportSection> findSectionsForReportTemplate(GranterReportTemplate template) {
        return granterReportSectionRepository.findByReportTemplate(template);
    }

    public ReportSpecificSection saveReportSpecificSection(ReportSpecificSection reportSpecificSection) {
        return reportSpecificSectionRepository.save(reportSpecificSection);
    }

    public ReportSpecificSectionAttribute saveReportSpecificSectionAttribute(
            ReportSpecificSectionAttribute sectionAttribute) {
        return reportSpecificSectionAttributeRepository.save(sectionAttribute);
    }

    public ReportStringAttribute saveReportStringAttribute(ReportStringAttribute stringAttribute) {
        return reportStringAttributeRepository.save(stringAttribute);
    }

    public List<ReportSpecificSection> getReportSections(Report report) {
        return reportSpecificSectionRepository.findByReportId(report.getId());
    }

    public String buildHashCode(Report report) {
        SecureReportEntity secureEntity = new SecureReportEntity();
        secureEntity.setReportId(report.getId());
        secureEntity.setTemplateId(report.getTemplate().getId());
        secureEntity.setSectionAndAtrribIds(new HashMap<>());
        if (report.getGrant() != null) {
            secureEntity.setGranterId(report.getGrant().getGrantorOrganization().getId());
        }
        Map<Long, List<Long>> map = new HashMap<>();
        report.getReportDetails().getSections().forEach(sec -> {
            List<Long> attribIds = new ArrayList<>();
            if (sec.getAttributes() != null) {
                sec.getAttributes().forEach(a -> attribIds.add(a.getId()));
            }

            map.put(sec.getId(), attribIds);
        });
        secureEntity.setSectionAndAtrribIds(map);
        List<Long> templateIds = new ArrayList<>();
        if (report.getGrant() != null) {
            granterReportTemplateRepository.findByGranterId(report.getGrant().getGrantorOrganization().getId())
                    .forEach(t -> templateIds.add(t.getId()));
        }
        secureEntity.setGrantTemplateIds(templateIds);

        List<Long> grantWorkflowIds = new ArrayList<>();
        Map<Long, List<Long>> grantWorkflowStatusIds = new HashMap<>();
        Map<Long, Long[][]> grantWorkflowTransitionIds = new HashMap<>();
        if (report.getGrant() != null) {
            workflowRepository.findByGranterAndObjectAndType(report.getGrant().getGrantorOrganization().getId(), REPORT, report.getGrant().getGrantTypeId())
                    .forEach(w -> {
                        grantWorkflowIds.add(w.getId());
                        List<Long> wfStatusIds = new ArrayList<>();
                        workflowStatusRepository.findByWorkflow(w).forEach(ws -> wfStatusIds.add(ws.getId()));
                        grantWorkflowStatusIds.put(w.getId(), wfStatusIds);

                        List<WorkflowStatusTransition> transitions = workflowStatusTransitionRepository
                                .findByWorkflow(w);
                        Long[][] stransitions = new Long[transitions.size()][2];
                        final int[] counter = {0};
                        workflowStatusTransitionRepository.findByWorkflow(w).forEach(st -> {
                            stransitions[counter[0]][0] = st.getFromState().getId();
                            stransitions[counter[0]][1] = st.getToState().getId();
                            counter[0]++;
                        });
                        grantWorkflowTransitionIds.put(w.getId(), stransitions);
                    });
        }

        secureEntity.setGrantWorkflowIds(grantWorkflowIds);
        secureEntity.setWorkflowStatusIds(grantWorkflowStatusIds);
        secureEntity.setWorkflowStatusTransitionIds(grantWorkflowTransitionIds);
        if (report.getGrant() != null) {
            secureEntity.setTenantCode(report.getGrant().getGrantorOrganization().getCode());
        }

        List<Long> tLibraryIds = new ArrayList<>();
        if (report.getGrant() != null) {
            templateLibraryRepository.findByGranterId(report.getGrant().getGrantorOrganization().getId())
                    .forEach(tl -> tLibraryIds.add(tl.getId()));
        }
        secureEntity.setTemplateLibraryIds(tLibraryIds);

        try {
            return Jwts.builder().setSubject(new ObjectMapper().writeValueAsString(secureEntity))
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(),e);
        }
        return "";
    }

    public SecureReportEntity unBuildGrantHashCode(Report grant) {
        String grantSecureCode = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(grant.getSecurityCode()).getBody()
                .getSubject();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(grantSecureCode, SecureReportEntity.class);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public List<ReportStringAttribute> getReportStringAttributesForReport(Report report) {
        return reportStringAttributeRepository.findByReport(report);
    }

    public Report getReportById(Long reportId) {
        Optional<Report> report = reportRepository.findById(reportId);
        if (report.isPresent()) {
            return report.get();
        }
        return null;
    }

    public ReportSpecificSection getReportSpecificSectionById(Long reportSpecificSectionId) {
        Optional<ReportSpecificSection> optional = reportSpecificSectionRepository.findById(reportSpecificSectionId);
        return optional.isPresent()?optional.get():null;
    }

    public ReportSpecificSectionAttribute getReportSpecificSectionAttributeById(Long reportSpecificSectionAttributeId) {
        Optional<ReportSpecificSectionAttribute> optional = reportSpecificSectionAttributeRepository.findById(reportSpecificSectionAttributeId);
        return optional.isPresent()?optional.get():null;
    }

    public ReportStringAttribute getReportStringAttributeBySectionAttributeAndSection(
            ReportSpecificSectionAttribute sectionAttribute, ReportSpecificSection section) {
        return reportStringAttributeRepository.findBySectionAttributeAndSection(sectionAttribute, section);
    }

    public ReportStringAttribute getReportStringByStringAttributeId(Long stringAttributeId) {
        Optional<ReportStringAttribute> optional = reportStringAttributeRepository.findById(stringAttributeId);
        return optional.isPresent()?optional.get():null;
    }

    public int getNextAttributeOrder(Long granterId, Long sectionId) {
        return reportSpecificSectionAttributeRepository.getNextAttributeOrder(granterId, sectionId);
    }

    public GranterReportTemplate findByTemplateId(Long id) {
        Optional<GranterReportTemplate> optional = granterReportTemplateRepository.findById(id);
        return optional.isPresent()?optional.get():null;
    }

    public void deleteReportTemplate(GranterReportTemplate currentReportTemplate) {
        granterReportTemplateRepository.delete(currentReportTemplate);
    }

    public GranterReportTemplate saveReportTemplate(GranterReportTemplate newTemplate) {
        return granterReportTemplateRepository.save(newTemplate);
    }

    public GranterReportSection saveReportTemplateSection(GranterReportSection newSection) {
        return granterReportSectionRepository.save(newSection);
    }

    public ReportSpecificSection saveSection(ReportSpecificSection currentSection) {
        return reportSpecificSectionRepository.save(currentSection);
    }

    public List<ReportSpecificSectionAttribute> getSpecificSectionAttributesBySection(
            ReportSpecificSection currentSection) {
        return reportSpecificSectionAttributeRepository.findBySection(currentSection);
    }

    public GranterReportSectionAttribute saveReportTemplateSectionAttribute(
            GranterReportSectionAttribute newAttribute) {
        return granterReportSectionAttributeRepository.save(newAttribute);
    }

    public ReportStringAttributeAttachments saveReportStringAttributeAttachment(
            ReportStringAttributeAttachments attachment) {
        return reportStringAttributeAttachmentsRepository.save(attachment);
    }

    public List<ReportStringAttributeAttachments> getStringAttributeAttachmentsByStringAttribute(
            ReportStringAttribute stringAttribute) {
        return reportStringAttributeAttachmentsRepository.findByReportStringAttribute(stringAttribute);
    }

    public Integer getNextSectionOrder(Long id, Long templateId) {
        return reportSpecificSectionRepository.getNextSectionOrder(id, templateId);
    }

    public List<ReportStringAttribute> getReportStringAttributesByAttribute(ReportSpecificSectionAttribute attrib) {
        return reportStringAttributeRepository.findBySectionAttribute(attrib);
    }

    public void deleteStringAttribute(ReportStringAttribute stringAttrib) {
        reportStringAttributeRepository.delete(stringAttrib);
    }

    public void deleteSectionAttributes(List<ReportSpecificSectionAttribute> specificSectionAttributesBySection) {
        reportSpecificSectionAttributeRepository.deleteAll(specificSectionAttributesBySection);
    }

    public void deleteSection(ReportSpecificSection section) {
        reportSpecificSectionRepository.delete(section);
    }

    public List<WorkFlowPermission> getFlowReportFlowAuthority(Report report){

        return workflowPermissionRepository.getPermissionsForReportFlow(report.getStatus().getId(),report.getId());
    }

    public List<WorkFlowPermission> getFlowAuthority(Report report, Long userId) {
        List<WorkFlowPermission> permissions = new ArrayList<>();
        Optional<User> optionalUser = userRepository.findById(userId);
        if ((reportAssignmentRepository.findByReportId(report.getId()).stream()
                .anyMatch(ass -> ass.getStateId().longValue() == report.getStatus().getId().longValue()
                        && (ass.getAssignment() == null ? 0 : ass.getAssignment().longValue()) == userId))
                || Boolean.TRUE.equals((optionalUser.isPresent()?optionalUser.get().getOrganization().getOrganizationType().equalsIgnoreCase(
                GRANTEE) && report.getStatus().getInternalStatus().equalsIgnoreCase(ACTIVE):null))) {

            List<WorkflowStatusTransition> allowedTransitions = workflowStatusTransitionRepository
                    .findByWorkflow(workflowStatusRepository.getById(report.getStatus().getId()).getWorkflow()).stream()
                    .filter(st -> st.getFromState().getId().longValue() == report.getStatus().getId().longValue())
                    .collect(Collectors.toList());
            if (allowedTransitions != null && !allowedTransitions.isEmpty()) {
                allowedTransitions.forEach(tr -> {
                    WorkFlowPermission workFlowPermission = new WorkFlowPermission();
                    workFlowPermission.setAction(tr.getAction());
                    workFlowPermission.setFromName(tr.getFromState().getName());
                    workFlowPermission.setFromStateId(tr.getFromState().getId());
                    workFlowPermission.setId(tr.getId());
                    workFlowPermission.setNoteRequired(tr.isNoteRequired());
                    workFlowPermission.setToName(tr.getToState().getName());
                    workFlowPermission.setToStateId(tr.getToState().getId());
                    workFlowPermission.setSeqOrder(tr.getSeqOrder());
                    permissions.add(workFlowPermission);
                });
            }
        }
        return permissions;
    }

    public ReportAssignment getReportAssignmentById(Long id) {
        Optional<ReportAssignment> optional = reportAssignmentRepository.findById(id);
        return optional.isPresent()?optional.get():null;
    }

    public ReportAssignment saveAssignmentForReport(ReportAssignment assignment) {
        return reportAssignmentRepository.save(assignment);
    }

    public String[] buildEmailNotificationContent(Report finalReport, User u, String subConfigValue, String msgConfigValue, String currentState, String currentOwner,
                                                  String previousState, String previousOwner, String previousAction, String hasChanges,
                                                  String hasChangesComment, String hasNotes, String hasNotesComment, String link, User owner,
                                                  Integer noOfDays, Map<Long, Long> previousApprover, List<ReportAssignment> newApprover) {

        String code = Base64.getEncoder().encodeToString(String.valueOf(finalReport.getId()).getBytes());

        String granteeHost = "";
        String granteeUrl = "";
        String granterUrl = "";
        UriComponents uriComponents = null;
        UriComponentsBuilder uriBuilder;
        String granterHost = "";
        try {
            uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
            if (u.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)) {
                granteeHost = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
                granterHost = uriComponents.getHost();

            } else {
                granterHost = uriComponents.getHost();
                granteeHost = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
            }
            uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme()).host(granteeHost)
                    .port(uriComponents.getPort());
            granteeUrl = uriBuilder.toUriString();
            granteeUrl = granteeUrl + HOME_ACTION_LOGIN_ORG
                    + URLEncoder.encode(finalReport.getGrant().getGrantorOrganization().getName(),
                    StandardCharsets.UTF_8.toString())
                    + "&r=" + code + EMAIL_TYPE_REPORT;

            uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme()).host(granterHost)
                    .port(uriComponents.getPort());
            granterUrl = uriBuilder.toUriString();
            granterUrl = granterUrl + HOME_ACTION_LOGIN_ORG
                    + URLEncoder.encode(finalReport.getGrant().getGrantorOrganization().getName(),
                    StandardCharsets.UTF_8.toString())
                    + "&r=" + code + EMAIL_TYPE_REPORT;
        } catch (Exception e) {
            granterUrl = link;
            try {
                granterUrl = granterUrl + HOME_ACTION_LOGIN_ORG
                        + URLEncoder.encode(finalReport.getGrant().getGrantorOrganization().getName(),
                        StandardCharsets.UTF_8.toString())
                        + "&r=" + code + EMAIL_TYPE_REPORT;
            } catch (UnsupportedEncodingException ex) {
                logger.error(e.getMessage(),e);
            }
        }

        String grantName = "";
        if (finalReport.getGrant().getReferenceNo() != null) {
            grantName = "[".concat(finalReport.getGrant().getReferenceNo()).concat("] ").concat(finalReport.getGrant().getName());
        } else {
            grantName =  finalReport.getGrant().getReferenceNo()!=null?"[".concat(finalReport.getGrant().getReferenceNo()).concat("] ").concat(finalReport.getGrant().getName()):finalReport.getGrant().getName();
        }
        currentOwner = currentOwner==null?"":currentOwner;
        currentState=currentState==null?"":currentState;
        String message = msgConfigValue.replace(GRANT_NAME, grantName)
                .replace(REPORT_NAME, finalReport.getName()).replace("%REPORT_LINK%", granteeUrl)
                .replace("%CURRENT_STATE%", currentState).replace("%CURRENT_OWNER%", currentOwner)
                .replace("%PREVIOUS_STATE%", previousState).replace("%PREVIOUS_OWNER%", previousOwner)
                .replace("%PREVIOUS_ACTION%", previousAction).replace("%HAS_CHANGES%", hasChanges)
                .replace("%HAS_CHANGES_COMMENT%", hasChangesComment).replace("%HAS_NOTES%", hasNotes)
                .replace("%HAS_NOTES_COMMENT%", hasNotesComment)
                .replace("%TENANT%", finalReport.getGrant().getGrantorOrganization().getName())
                .replace("%DUE_DATE%", new SimpleDateFormat(DD_MMM_YYYY).format(finalReport.getDueDate()))
                .replace("%OWNER_NAME%", owner == null ? "" : owner.getFirstName() + " " + owner.getLastName())
                .replace("%OWNER_EMAIL%", owner == null ? "" : owner.getEmailId())
                .replace("%NO_DAYS%", noOfDays == null ? "" : String.valueOf(noOfDays))
                .replace("%GRANTEE%", finalReport.getGrant().getOrganization() != null ? finalReport.getGrant().getOrganization().getName() : finalReport.getGrant().getGrantorOrganization().getName())
                .replace("%GRANTEE_REPORT_LINK%", granteeUrl).replace("%GRANTER_REPORT_LINK%", granterUrl)
                .replace("%GRANTER%", finalReport.getGrant().getGrantorOrganization().getName())
                .replace("%ENTITY_TYPE%", "report")
                .replace("%PREVIOUS_ASSIGNMENTS%", getAssignmentsTable(previousApprover, newApprover))
                .replace("%ENTITY_NAME%", finalReport.getName() + " of grant " + grantName);
        String subject = subConfigValue.replace(REPORT_NAME, finalReport.getName());

        return new String[]{subject, message};
    }

    private String getAssignmentsTable(Map<Long, Long> assignments, List<ReportAssignment> newAssignments) {
        if (assignments == null) {
            return "";
        }
        newAssignments.sort(Comparator.comparing(ReportAssignment::getId, Comparator.naturalOrder()));
        String[] table = {
                "<table width='100%' border='1' cellpadding='2' cellspacing='0'><tr><td><b>Review State</b></td><td><b>Current State Owners</b></td><td><b>Previous State Owners</b></td></tr>"};
        newAssignments.forEach(a -> {
            Long prevAss = assignments.keySet().stream().filter(b -> b.longValue() == a.getStateId().longValue()).findFirst().get();

            String str = a.getAssignment() != null
                    ? userService.getUserById(a.getAssignment()).getLastName()
                    : "";
            String str1 = assignments.get(prevAss) != null
                    ? userService.getUserById(assignments.get(prevAss)).getLastName()
                    : "";
            table[0] = table[0].concat("<tr>").concat("<td width='30%'>")
                    .concat(workflowStatusRepository.findById(a.getStateId()).get().getName()).concat(TD_CLOSE)
                    .concat("<td>")
                    .concat(a.getAssignment() != null ? userService.getUserById(a.getAssignment()).getFirstName()
                            : "".concat("-")
                            .concat(str))
                    .concat(TD_CLOSE)

                    .concat("<td>")
                    .concat(assignments.get(prevAss) != null
                            ? userService.getUserById(assignments.get(prevAss)).getFirstName()
                            : "".concat("-")
                            .concat(str1)
                            .concat(TD_CLOSE).concat("</tr>"));
        });

        table[0] = table[0].concat("</table>");
        return table[0];

    }

    public List<ReportHistory> getReportHistory(Long reportId) {
        return reportHistoryRepository.findByReportId(reportId);
    }

    public List<ReportHistory> getReportHistoryForGrantee(Long reportId, Long granteeUserId) {
        return reportHistoryRepository.findReportHistoryForGranteeByReportId(reportId, granteeUserId);
    }

    public void deleteStringAttributeAttachments(List<ReportStringAttributeAttachments> attachments) {
        reportStringAttributeAttachmentsRepository.deleteAll(attachments);
    }

    public void deleteSectionAttribute(ReportSpecificSectionAttribute attribute) {
        reportSpecificSectionAttributeRepository.delete(attribute);
    }

    public Long getApprovedReportsActualSumForGrant(Long grantId, String attributeName) {
        Long totals = reportRepository.getApprovedReportsActualSumForGrantAndAttribute(grantId, attributeName);
        totals = totals==null?0:totals;
        if(grantService.getById(grantId).getOrigGrantId()!=null){
            totals += getApprovedReportsActualSumForGrant(grantService.getById(grantId).getOrigGrantId(),attributeName);
        }
        return totals;
    }

    public List<GranterReportTemplate> findByGranterIdAndPublishedStatus(Long id, boolean publishedStatus) {
        return granterReportTemplateRepository.findByGranterIdAndPublished(id, publishedStatus);
    }

    public List<GranterReportTemplate> findByGranterIdAndPublishedStatusAndPrivateStatus(Long id,
                                                                                         boolean publishedStatus, boolean isPrivate) {
        return granterReportTemplateRepository.findByGranterIdAndPublishedAndPrivateToReport(id, publishedStatus,
                isPrivate);
    }

    public String[] buildReportInvitationContent(Report report, String sub, String msg, String url) {
        String finalGrantName = report.getGrant().getReferenceNo()!=null?"[".concat(report.getGrant().getReferenceNo()).concat("] ").concat(report.getGrant().getName()):report.getGrant().getName();
        sub = sub.replace(GRANT_NAME,finalGrantName );
        sub = sub.replace(REPORT_NAME, report.getName());
        msg = msg.replace(GRANT_NAME, finalGrantName)
                .replace("%TENANT_NAME%", report.getGrant().getGrantorOrganization().getName()).replace("%LINK%", url);
        msg = msg.replace(REPORT_NAME, report.getName());
        return new String[]{sub, msg};
    }

    public ReportStringAttributeAttachments getStringAttributeAttachmentsByAttachmentId(Long attachmentId) {
        Optional<ReportStringAttributeAttachments> optional = reportStringAttributeAttachmentsRepository.findById(attachmentId);
        return optional.isPresent()?optional.get():null;
    }

    public ReportStringAttribute findReportStringAttributeById(Long attributeId) {

        Optional<ReportStringAttribute> optional = reportStringAttributeRepository.findById(attributeId);
        return optional.isPresent()?optional.get():null;
    }

    public GranterReportTemplate createNewReportTemplateFromExisiting(Report report) {
        GranterReportTemplate currentReportTemplate = findByTemplateId(report.getTemplate().getId());
        GranterReportTemplate newTemplate = null;
        if (!currentReportTemplate.isPublished()) {
            deleteReportTemplate(currentReportTemplate);
        }
        newTemplate = new GranterReportTemplate();
        newTemplate.setName("Custom Template");
        newTemplate.setGranterId(report.getGrant().getGrantorOrganization().getId());
        newTemplate.setPublished(false);
        newTemplate = saveReportTemplate(newTemplate);

        List<GranterReportSection> newSections = new ArrayList<>();
        for (ReportSpecificSection currentSection : getReportSections(report)) {
            GranterReportSection newSection = new GranterReportSection();
            newSection.setSectionOrder(currentSection.getSectionOrder());
            newSection.setSectionName(currentSection.getSectionName());
            newSection.setReportTemplate(newTemplate);
            newSection.setGranter((Granter) report.getGrant().getGrantorOrganization());
            newSection.setDeletable(currentSection.getDeletable());

            newSection = saveReportTemplateSection(newSection);
            newSections.add(newSection);

            currentSection.setReportTemplateId(newTemplate.getId());
            currentSection = saveSection(currentSection);

            for (ReportSpecificSectionAttribute currentAttribute : getSpecificSectionAttributesBySection(
                    currentSection)) {
                GranterReportSectionAttribute newAttribute = new GranterReportSectionAttribute();
                newAttribute.setDeletable(currentAttribute.getDeletable());
                newAttribute.setFieldName(currentAttribute.getFieldName());
                newAttribute.setFieldType(currentAttribute.getFieldType());
                newAttribute.setGranter((Granter) currentAttribute.getGranter());
                newAttribute.setRequired(currentAttribute.getRequired());
                newAttribute.setAttributeOrder(currentAttribute.getAttributeOrder());
                newAttribute.setSection(newSection);
                if (currentAttribute.getFieldType().equalsIgnoreCase("table")) {
                    ReportStringAttribute stringAttribute = getReportStringAttributeBySectionAttributeAndSection(
                            currentAttribute, currentSection);

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
                        logger.error(e.getMessage(),e);
                    }
                }

                saveReportTemplateSectionAttribute(newAttribute);

            }
        }

        newTemplate.setSections(newSections);
        newTemplate = saveReportTemplate(newTemplate);

        report.setTemplate(newTemplate);
        saveReport(report);
        return newTemplate;
    }

    public List<Report> getDueReportsForPlatform(Date dueDate, List<Long> granterIds) {
        return reportRepository.getDueReportsForPlatform(dueDate, granterIds);
    }

    public List<Report> getDueReportsForGranter(Date dueDate, Long granterId) {
        return reportRepository.getDueReportsForGranter(dueDate, granterId);
    }

    public List<ReportAssignment> getActionDueReportsForPlatform(List<Long> granterIds) {
        return reportAssignmentRepository.getActionDueReportsForPlatform(granterIds);
    }

    public List<ReportAssignment> getActionDueReportsForGranterOrg(Long granterId) {
        return reportAssignmentRepository.getActionDueReportsForGranterOrg(granterId);
    }

    public Boolean checkIfReportTemplateChanged(Report report, ReportSpecificSection newSection,
                                                ReportSpecificSectionAttribute newAttribute) {
        GranterReportTemplate currentReportTemplate = findByTemplateId(report.getTemplate().getId());
        for (GranterReportSection reportSection : currentReportTemplate.getSections()) {
            if (!reportSection.getSectionName().equalsIgnoreCase(newSection.getSectionName())) {
                return true;
            }
            if (newAttribute != null) {
                for (GranterReportSectionAttribute sectionAttribute : reportSection.getAttributes()) {
                    if (!sectionAttribute.getFieldName().equalsIgnoreCase(newAttribute.getFieldName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Report> findByGrantAndStatus(Grant grant, WorkflowStatus status, Long reportId) {
        return reportRepository.findByGrantAndStatus(grant.getId(), status.getInternalStatus(), reportId);
    }

    public List<Report> getReportsByIds(String linkedApprovedReports) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Long> reportIds = mapper.readValue(linkedApprovedReports, new TypeReference<List<Long>>() {
            });
            return reportRepository.findReportsByIds(reportIds);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public List<Report> findReportsByStatusForGrant(WorkflowStatus status, Grant grant) {
        return reportRepository.findByStatusAndGrant(status.getId(), grant.getId());
    }

    public List<ReportCard> findReportCardsByStatusForGrant(WorkflowStatus status, Grant grant) {
        return reportCardRepository.findByStatusAndGrant(status.getId(), grant.getId());
    }

    public List<Report> getReportsForGrant(Grant grant) {
        return reportRepository.getReportsByGrant(grant.getId());
    }

    public List<ReportCard> getReportCardsForGrant(Grant grant) {
        return reportCardRepository.getReportsByGrant(grant.getId());
    }

    public void setAssignmentHistory(ReportAssignmentsVO assignmentsVO) {

        if (!reportRepository.findReportsThatMovedAtleastOnce(assignmentsVO.getReportId()).isEmpty()) {
            List<ReportAssignmentHistory> assignmentHistories = assignmentHistoryRepository
                    .findByReportIdAndStateIdOrderByUpdatedOnDesc(assignmentsVO.getReportId(),
                            assignmentsVO.getStateId());
            for (ReportAssignmentHistory reportAss : assignmentHistories) {
                if (reportAss.getAssignment() != null && reportAss.getAssignment() != 0) {
                    Optional<User> optionalUser = userRepository.findById(reportAss.getAssignment());
                    reportAss.setAssignmentUser(optionalUser.isPresent()?optionalUser.get():null);
                }
                if (reportAss.getUpdatedBy() != null && reportAss.getUpdatedBy() != 0) {
                    Optional<User> optional = userRepository.findById(reportAss.getUpdatedBy());
                    reportAss.setUpdatedByUser(optional.isPresent()?optional.get():null);
                }

            }
            assignmentsVO.setHistory(assignmentHistories);
        }
    }

    public boolean checkIfReportMovedThroughWFAtleastOnce(Long reportId) {
        return !reportRepository.findReportsThatMovedAtleastOnce(reportId).isEmpty();
    }

    public void deleteStringAttributes(List<ReportStringAttribute> strAttribs) {
        reportStringAttributeRepository.deleteAll(strAttribs);
    }

    public void deleteReport(Report report) {

        if (checkIfReportMovedThroughWFAtleastOnce(report.getId())) {
            report.setDeleted(true);
            saveReport(report);
        } else {
            for (ReportSpecificSection section : getReportSections(report)) {
                List<ReportSpecificSectionAttribute> attribs = getSpecificSectionAttributesBySection(section);
                for (ReportSpecificSectionAttribute attribute : attribs) {
                    List<ReportStringAttribute> strAttribs = getReportStringAttributesByAttribute(attribute);
                    deleteStringAttributes(strAttribs);
                }
                deleteSectionAttributes(attribs);
                deleteSection(section);
            }

            reportRepository.delete(report);

            Optional<GranterReportTemplate> optionalGranterReportTemplate = granterReportTemplateRepository.findById(report.getTemplate().getId());
            GranterReportTemplate template = optionalGranterReportTemplate.isPresent()?optionalGranterReportTemplate.get():null;
            if (template!=null && !template.isPublished()) {
                deleteReportTemplate(template);
            }
        }
    }

    public List<Report> getUpcomingFutureReportsForGranterUserByDate(Long userId, Long id) {
        return reportRepository.findUpcomingFutureReports(userId, id);
    }

    public List<ReportCard> getUpcomingFutureReportCardsForGranterUserByDate(Long userId, Long id) {
        return reportCardRepository.findUpcomingFutureReports(userId, id);
    }

    public List<Report> getUpcomingFutureReportsForAdminGranterUserByDate(Long userId, Long id) {
        return reportRepository.findUpcomingFutureAdminReports(userId, id);
    }

    public List<ReportCard> getUpcomingFutureReportCardsForAdminGranterUserByDate(Long userId, Long id) {
        return reportCardRepository.findUpcomingFutureAdminReports(userId, id);
    }

    public List<DisabledUsersEntity> getReportsWithDisabledUsers() {
        return disabledUsersEntityRepository.getReports();
    }

    public Long approvedReportsNotInTimeForUser(Long userId) {
        return reportRepository.approvedReportsNotInTimeForUser(userId);
    }

    public Long approvedReportsInTimeForUser(Long userId) {
        return reportRepository.approvedReportsInTimeForUser(userId);
    }

    public Long getUpComingDraftReports(Long userId) {
        return reportRepository.getUpComingDraftReports(userId);
    }

    public List<ReportCard> getDetailedUpComingDraftReports(Long userId) {
        return reportCardRepository.getDetailedUpComingDraftReports(userId);
    }

    public Long getReportsInWorkflow(Long userId) {
        return reportRepository.getReportsInWorkflow(userId);
    }

    public Report reportToReturn(Report report, Long userId) {

        report.setStringAttributes(getReportStringAttributesForReport(report));

        List<ReportAssignmentsVO> workflowAssignments = new ArrayList<>();
        for (ReportAssignment assignment : getAssignmentsForReport(report)) {
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

            setAssignmentHistory(assignmentsVO);

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

        ReportVO reportVO = new ReportVO().build(report, getReportSections(report), userService,
                this);
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
        report.setSecurityCode(buildHashCode(report));
        report.setFlowAuthorities(getFlowAuthority(report, userId));

        List<GrantTag> grantTags = grantService.getTagsForGrant(report.getGrant().getId());

        report.getGrant().setGrantTags(grantTags);
        return report;
    }

    private List<ReportAssignment> determineCanManage(Report report, Long userId) {
        List<ReportAssignment> reportAssignments = getAssignmentsForReport(report);
        report.setCanManage((reportAssignments.stream()
                .anyMatch(ass -> (ass.getAssignment() == null ? 0L : ass.getAssignment().longValue()) == userId
                        .longValue() && ass.getStateId().longValue() == report.getStatus().getId().longValue()))
                || (report.getStatus().getInternalStatus().equalsIgnoreCase(ACTIVE) && userService.getUserById(userId)
                .getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)));

        return reportAssignments;
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
                        List<Disbursement> closedDisbursements = getDisbursementsByStatusIds(report.getGrant(), closedStatusIds); //disbursementService
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
                                    if (currentUser.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE) && td.isEnteredByGrantee() && report.getId().longValue() != repId.longValue() && !disbursementService.getDisbursementById(ad.getDisbursementId()).getStatus().getInternalStatus().equalsIgnoreCase(CLOSED)) {
                                        td.setShowForGrantee(false);
                                    }


                                    ColumnData cdDate = new ColumnData();
                                    cdDate.setDataType("date");
                                    cdDate.setName("Disbursement Date");
                                    cdDate.setValue(ad.getDisbursementDate() != null
                                            ? new SimpleDateFormat(DD_MMM_YYYY).format(ad.getDisbursementDate())
                                            : null);

                                    ColumnData cdDA = new ColumnData();
                                    cdDA.setDataType(CURRENCY);
                                    cdDA.setName("Actual Disbursement");
                                    cdDA.setValue(
                                            ad.getActualAmount() != null ? String.valueOf(ad.getActualAmount()) : null);

                                    ColumnData cdFOS = new ColumnData();
                                    cdFOS.setDataType(CURRENCY);
                                    cdFOS.setName("Funds from Other Sources");
                                    cdFOS.setValue(
                                            ad.getOtherSources() != null ? String.valueOf(ad.getOtherSources()) : null);

                                    ColumnData cdN = new ColumnData();
                                    cdN.setName("Notes");
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
                                    cdDate.setDataType("date");
                                    cdDate.setName("Disbursement Date");
                                    cdDate.setValue(ad.getDisbursementDate() != null
                                            ? new SimpleDateFormat(DD_MMM_YYYY).format(ad.getDisbursementDate())
                                            : null);

                                    ColumnData cdDA = new ColumnData();
                                    cdDA.setDataType(CURRENCY);
                                    cdDA.setName("Actual Disbursement");
                                    cdDA.setValue(String.valueOf(ad.getActualAmount()));

                                    ColumnData cdFOS = new ColumnData();
                                    cdFOS.setDataType(CURRENCY);
                                    cdFOS.setName("Funds from Other Sources");
                                    cdFOS.setValue(String.valueOf(ad.getOtherSources()));

                                    ColumnData cdN = new ColumnData();
                                    cdN.setName("Notes");
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

    public PlainReport reportToPlain(Report report) throws IOException {
        SimpleDateFormat sd = new SimpleDateFormat(DD_MMM_YYYY);
        PlainReport plainReport = new PlainReport();
        plainReport.setName(report.getName());
        Date end = report.getEndDate();
        plainReport.setEndDate(end != null ? sd.format(end) : "");
        Date start = report.getStartDate();
        plainReport.setStartDate(start != null ? sd.format(start) : "");
        Date due = report.getDueDate();
        plainReport.setDueDate(due != null ? sd.format(due) : "");

        plainReport.setCurrentInternalStatus(report.getStatus().getInternalStatus());
        plainReport.setCurrentStatus(report.getStatus().getName());

        Optional<ReportAssignment> assignment = getAssignmentsForReport(report).stream().filter(ass -> ass.getStateId().longValue() == report.getStatus().getId()).findFirst();
        if (assignment.isPresent()) {
            User owner = userService.getUserById(assignment.get().getAssignment());
            plainReport.setCurrentOwner(owner.getFirstName() + " " + owner.getLastName());
        }

        if (report.getReportDetails().getSections() != null && !report.getReportDetails().getSections().isEmpty()) {
            List<PlainSection> plainSections = new ArrayList<>();

            for (SectionVO section : report.getReportDetails().getSections()) {
                List<PlainAttribute> plainAttributes = new ArrayList<>();
                if (section.getAttributes() != null && !section.getAttributes().isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    for (SectionAttributesVO attribute : section.getAttributes()) {
                        PlainAttribute plainAttribute = new PlainAttribute();
                        plainAttribute.setId(attribute.getId());
                        plainAttribute.setName(attribute.getFieldName());
                        plainAttribute.setType(attribute.getFieldType());
                        plainAttribute.setValue(attribute.getFieldValue());
                        plainAttribute.setOrder(attribute.getAttributeOrder());
                        switch (attribute.getFieldType()) {

                            case "kpi":
                                plainAttribute.setFrequency(attribute.getFrequency());
                                if (attribute.getTarget() != null) {
                                    plainAttribute.setTarget(Long.valueOf(attribute.getTarget()));
                                }
                                if (attribute.getActualTarget() != null) {
                                    plainAttribute.setActualTarget(attribute.getActualTarget());
                                }
                                break;
                            case DISBURSEMENT:
                            case "table":
                                if (attribute.getFieldValue() != null && !"".equalsIgnoreCase(attribute.getFieldValue())) {
                                    plainAttribute.setTableValue(mapper.readValue(attribute.getFieldValue(), new TypeReference<List<TableData>>() {
                                    }));
                                }
                                break;
                            case "document":
                                if (attribute.getFieldValue() != null && !"".equalsIgnoreCase(attribute.getFieldValue().trim())) {
                                    plainAttribute.setAttachments(mapper.readValue(attribute.getFieldValue(), new TypeReference<List<GrantStringAttributeAttachments>>() {
                                    }));
                                }
                                break;
                            default:
                                //Do nothing
                        }

                        plainAttributes.add(plainAttribute);
                    }

                }
                plainSections.add(new PlainSection(section.getId(), section.getName(), section.getOrder(), plainAttributes));
            }
            plainReport.setSections(plainSections);
        }

        return plainReport;
    }
}
