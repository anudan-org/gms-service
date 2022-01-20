package org.codealpha.gmsservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.Frequency;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.exceptions.ApplicationException;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class GrantService {

    private static final Logger logger = LoggerFactory.getLogger(GrantService.class);
    private static final String SECRET = "bhjgsdf788778hsdfhgsdf777werghsbdfjhdsf88yw3r7t7yt^%^%%@#Ghj";
    public static final String GRANT_NAME = "%GRANT_NAME%";
    public static final String ACTIVE = "ACTIVE";
    public static final String CLOSED = "CLOSED";
    public static final String DISBURSEMENT = "disbursement";
    public static final String TABLE = "table";
    public static final String FILE_SEPARATOR = "/";
    public static final String PLEASE_REVIEW = "Please review.";
    public static final String DD_MMM_YYYY = "dd-MMM-yyyy";
    public static final String RELEASE_VERSION = "%RELEASE_VERSION%";
    public static final String GRANT = "GRANT";
    public static final String REPORT = "REPORT";
    public static final String TDCLOSE = "</td>";
    public static final String TDOPEN = "<td>";
    public static final String TROPEN = "<tr>";


    @Autowired
    private GrantRepository grantRepository;
    @Autowired
    private GrantCardRepository grantCardRepository;
    @Autowired
    private GranterGrantSectionRepository granterGrantSectionRepository;
    @Autowired
    private GranterReportSectionRepository granterReportSectionRepository;
    @Autowired
    private GranterGrantSectionAttributeRepository granterGrantSectionAttributeRepository;
    @Autowired
    private GranterReportSectionAttributeRepository granterReportSectionAttributeRepository;
    @Autowired
    private GrantStringAttributeRepository grantStringAttributeRepository;
    @Autowired
    private GrantDocumentAttributesRepository grantDocumentAttributesRepository;
    @Autowired
    private GrantQuantitativeDataRepository grantQuantitativeDataRepository;
    @Autowired
    private GrantKpiRepository grantKpiRepository;
    @Autowired
    private GrantQualitativeDataRepository grantQualitativeDataRepository;
    @Autowired
    private GrantDocumentDataRepository grantDocumentDataRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DocumentKpiNotesRepository documentKpiNotesRepository;
    @Autowired
    private DocKpiDataDocumentRepository docKpiDataDocumentRepository;
    @Autowired
    private QualKpiDocumentRepository qualKpiDocumentRepository;
    @Autowired
    private QualitativeKpiNotesRepository qualitativeKpiNotesRepository;
    @Autowired
    private QuantitativeKpiNotesRepository quantitativeKpiNotesRepository;
    @Autowired
    private QuantKpiDocumentRepository quantKpiDocumentRepository;
    @Autowired
    private GrantSpecificSectionAttributeRepository grantSpecificSectionAttributeRepository;
    @Autowired
    private GrantSpecificSectionRepository grantSpecificSectionRepository;
    @Autowired
    private GranterGrantTemplateRepository granterGrantTemplateRepository;
    @Autowired
    private GrantAssignmentRepository grantAssignmentRepository;
    @Autowired
    private GrantStringAttributeAttachmentRepository grantStringAttributeAttachmentRepository;
    @Autowired
    private GrantHistoryRepository grantHistoryRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private WorkflowStatusTransitionRepository workflowStatusTransitionRepository;
    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;
    @Autowired
    private TemplateLibraryRepository templateLibraryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private GranterGrantTemplateService granterGrantTemplateService;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private GrantAssignmentHistoryRepository assignmentHistoryRepository;
    @Autowired
    private GrantDocumentRepository grantDocumentRepository;
    @Autowired
    private DisbursementRepository disbursementRepository;
    @Autowired
    private ActualDisbursementRepository actualDisbursementRepository;
    @Autowired
    private ReportService reportService;
    @Autowired
    private DisbursementService disbursementService;
    @Autowired
    private DisabledUsersEntityRepository disabledUsersEntityRepository;
    @Autowired
    private GrantTypeRepository grantTypeRepository;
    @Autowired
    private GrantTagRepository grantTagRepository;
    @Autowired
    private OrgTagService orgTagService;
    @Autowired
    private OrgTagRepository orgTagRepository;
    @Autowired
    private DataExportSummaryRepository dataExportSummaryRepository;
    @Value("${spring.timezone}")
    private String timezone;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ReportCardRepository reportCardRepository;
    @Autowired
    private ClosureReasonsRepository closureReasonsRepository;
    @Autowired
    private GranteeService granteeService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private WorkflowStatusTransitionService workflowStatusTransitionService;
    @Autowired
    private CommonEmailSevice commonEmailSevice;
    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private GrantSnapshotService grantSnapshotService;
    @Autowired
    private GrantClosureService closureService;

    public GrantService() {
        //Adding for sonar
    }


    public Grant saveGrant(Grant grant) {
        return grantRepository.save(grant);
    }

    public Grant getById(Long id) {
        return grantRepository.getById(id);
    }

    public GrantCard getGrantCardById(Long id) {
        return grantCardRepository.getById(id);
    }

    public GrantSpecificSection getGrantSectionBySectionId(Long sectionId) {

        Optional<GrantSpecificSection> granterGrantSection = grantSpecificSectionRepository.findById(sectionId);
        if (granterGrantSection.isPresent()) {
            return granterGrantSection.get();
        }
        return null;
    }

    public GrantSpecificSectionAttribute getAttributeById(Long attributeId) {
        Optional<GrantSpecificSectionAttribute> attr = grantSpecificSectionAttributeRepository.findById(attributeId);
        return attr.isPresent() ? attr.get() : null;
    }

    public GrantSpecificSectionAttribute getSectionAttributeByAttributeIdAndType(Long attributeId, String type) {
        if (type.equalsIgnoreCase("text")) {
            Optional<GrantStringAttribute> grantStringAttribute = grantStringAttributeRepository.findById(attributeId);
            if (grantStringAttribute.isPresent()) {
                return grantStringAttribute.get().getSectionAttribute();
            }
        } else if (type.equalsIgnoreCase("multiline")) {
            return getGrantSpecificSectionAttribute(attributeId);

        } else if (type.equalsIgnoreCase("document")) {
            return getGrantSpecificSectionAttribute(attributeId);

        } else if (type.equalsIgnoreCase("kpi")) {
            return getGrantSpecificSectionAttribute(attributeId);

        } else if (type.equalsIgnoreCase(TABLE) || type.equalsIgnoreCase(DISBURSEMENT)) {
            return getGrantSpecificSectionAttribute(attributeId);
        }

        return null;
    }

    private GrantSpecificSectionAttribute getGrantSpecificSectionAttribute(Long attributeId) {
        GrantStringAttribute grantStringAttribute = grantStringAttributeRepository.getGrantStringAttributeById(attributeId);
        return grantStringAttribute.getSectionAttribute();
    }

    public List<GrantStringAttribute> getStringAttributesByAttribute(
            GrantSpecificSectionAttribute grantSectionAttribute) {
        return grantStringAttributeRepository.findBySectionAttribute(grantSectionAttribute);
    }

    public GrantDocumentAttributes getDocumentAttributeById(Long docAttribId) {
        Optional<GrantDocumentAttributes> attribs = grantDocumentAttributesRepository.findById(docAttribId);
        return attribs.isPresent() ? attribs.get() : null;
    }

    public GrantStringAttribute saveStringAttribute(GrantStringAttribute grantStringAttribute) {
        return grantStringAttributeRepository.save(grantStringAttribute);
    }

    public GrantSpecificSectionAttribute saveSectionAttribute(GrantSpecificSectionAttribute sectionAttribute) {
        return grantSpecificSectionAttributeRepository.save(sectionAttribute);
    }

    public GrantSpecificSection saveSection(GrantSpecificSection newSection) {
        return grantSpecificSectionRepository.save(newSection);
    }


    public GrantDocumentAttributes saveGrantDocumentAttribute(GrantDocumentAttributes grantDocumentAttributes) {
        return grantDocumentAttributesRepository.save(grantDocumentAttributes);
    }

    public GrantStringAttribute findGrantStringBySectionAttribueAndGrant(GrantSpecificSection granterGrantSection,
                                                                         GrantSpecificSectionAttribute granterGrantSectionAttribute, Grant grant) {
        return grantStringAttributeRepository.findBySectionAndSectionAttributeAndGrant(granterGrantSection,
                granterGrantSectionAttribute, grant);
    }

    public GrantStringAttribute findGrantStringBySectionIdAttribueIdAndGrantId(Long granterGrantSectionId,
                                                                               Long granterGrantSectionAttributeId, Long grantId) {
        return grantStringAttributeRepository.findBySectionAndSectionIdAttributeIdAndGrantId(granterGrantSectionId,
                granterGrantSectionAttributeId, grantId);
    }

    public GrantStringAttribute findGrantStringAttributeById(Long grantStringAttributeId) {
        Optional<GrantStringAttribute> attr = grantStringAttributeRepository.findById(grantStringAttributeId);
        return attr.isPresent() ? attr.get() : null;
    }

    public GrantDocumentAttributes findGrantDocumentBySectionAttribueAndGrant(GrantSpecificSection granterGrantSection,
                                                                              GrantSpecificSectionAttribute granterGrantSectionAttribute, Grant grant) {
        return grantDocumentAttributesRepository.findBySectionAndSectionAttributeAndGrant(granterGrantSection,
                granterGrantSectionAttribute, grant);
    }

    public GrantStringAttribute saveGrantStringAttribute(GrantStringAttribute stringAttribute) {
        return grantStringAttributeRepository.save(stringAttribute);
    }

    public Template saveKpiTemplate(Template storedTemplate) {
        return templateRepository.save(storedTemplate);
    }

    public GrantSpecificSectionAttribute findBySectionAndFieldName(GrantSpecificSection section, String fieldName) {
        return grantSpecificSectionAttributeRepository.findBySectionAndFieldName(section, fieldName);
    }

    public GrantSpecificSection findByGranterAndSectionName(Granter granter, String sectionName) {
        return grantSpecificSectionRepository.findByGranterAndSectionName(granter, sectionName);
    }

    public Grant findGrantByNameAndGranter(String name, Granter granter) {
        return grantRepository.findByNameAndGrantorOrganization(name, granter.getId());
    }

    public String buildNotificationContent(Grant grant, WorkflowStatus status, String configValue) {
        return configValue.replaceAll(GRANT_NAME, grant.getName()).replaceAll("%GRANT_STATUS%", status.getVerb());
    }

    public List<GrantSpecificSection> getGrantSections(Grant grant) {
        return grantSpecificSectionRepository.findByGranterAndGrantId((Granter) grant.getGrantorOrganization(),
                grant.getId());
    }

    public List<GrantSpecificSectionAttribute> getAttributesBySection(GrantSpecificSection section) {
        return grantSpecificSectionAttributeRepository.findBySection(section);
    }

    public void deleteSections(List<GrantSpecificSection> sections) {
        grantSpecificSectionRepository.deleteAll(sections);
    }

    public void deleteSection(GrantSpecificSection section) {
        grantSpecificSectionRepository.delete(section);
    }

    public void deleteAtttribute(GrantSpecificSectionAttribute attrib) {
        grantSpecificSectionAttributeRepository.delete(attrib);
    }

    public void deleteSectionAttributes(List<GrantSpecificSectionAttribute> attributes) {
        grantSpecificSectionAttributeRepository.deleteAll(attributes);
    }

    public void deleteStringAttributes(List<GrantStringAttribute> stringAttributes) {
        grantStringAttributeRepository.deleteAll(stringAttributes);
    }

    public void deleteStringAttribute(GrantStringAttribute stringAttribute) {
        grantStringAttributeRepository.delete(stringAttribute);
    }

    public GranterGrantTemplate saveGrantTemplate(GranterGrantTemplate newTemplate) {
        return granterGrantTemplateRepository.save(newTemplate);
    }

    public GranterGrantSection saveGrantTemaplteSection(GranterGrantSection section) {
        return granterGrantSectionRepository.save(section);
    }

    public GranterReportSection saveReportTemplateSection(GranterReportSection section) {
        return granterReportSectionRepository.save(section);
    }

    public GranterGrantSectionAttribute saveGrantTemaplteSectionAttribute(GranterGrantSectionAttribute attribute) {
        return granterGrantSectionAttributeRepository.save(attribute);
    }

    public GranterReportSectionAttribute saveReportTemplateSectionAttribute(GranterReportSectionAttribute attribute) {
        return granterReportSectionAttributeRepository.save(attribute);
    }

    public void deleteGrantTemplateSections(List<GranterGrantSection> sections) {
        granterGrantSectionRepository.deleteAll(sections);
    }

    public void deleteGrantTemplate(GranterGrantTemplate template) {
        granterGrantTemplateRepository.delete(template);
    }

    public int getNextAttributeOrder(Long granterId, Long sectionId) {
        return grantSpecificSectionAttributeRepository.getNextAttributeOrder(granterId, sectionId);
    }

    public int getNextSectionOrder(Long granterId, Long templateId) {
        return grantSpecificSectionRepository.getNextSectionOrder(granterId, templateId);
    }

    public void deleteGrant(Grant grant) {
        grantRepository.delete(grant);
    }

    public GrantAssignments saveAssignmentForGrant(GrantAssignments assignment) {
        return grantAssignmentRepository.save(assignment);
    }

    public List<GrantAssignments> getGrantCurrentAssignments(Grant grant) {
        return grantAssignmentRepository.findByGrantIdAndStateId(grant.getId(), grant.getGrantStatus().getId());
    }

    public GrantAssignments getGrantAssignmentForGrantStateAndUser(Grant grant, WorkflowStatus status, User user) {
        return grantAssignmentRepository.findByGrantIdAndStateIdAndAssignments(grant.getId(), status.getId(),
                user.getId());
    }

    public List<GrantAssignments> getGrantWorkflowAssignments(Grant grant) {
        return grantAssignmentRepository.findByGrantId(grant.getId());
    }

    public GrantAssignments getGrantAssignmentById(Long assignmentId) {
        Optional<GrantAssignments> assignments = grantAssignmentRepository.findById(assignmentId);
        if (assignments.isPresent()) {
            return assignments.get();
        }
        return null;
    }

    public GrantStringAttributeAttachments saveGrantStringAttributeAttachment(
            GrantStringAttributeAttachments attachment) {
        return grantStringAttributeAttachmentRepository.save(attachment);
    }

    public List<GrantStringAttributeAttachments> getStringAttributeAttachmentsByStringAttribute(
            GrantStringAttribute grantStringAttribute) {
        return grantStringAttributeAttachmentRepository.findByGrantStringAttribute(grantStringAttribute);
    }

    public GrantStringAttributeAttachments getStringAttributeAttachmentsByAttachmentId(Long attachmentId) {
        Optional<GrantStringAttributeAttachments> attachments = grantStringAttributeAttachmentRepository.findById(attachmentId);
        return attachments.isPresent() ? attachments.get() : null;
    }

    public void deleteStringAttributeAttachmentsByAttachmentId(Long attachmentId) {
        grantStringAttributeAttachmentRepository.deleteById(attachmentId);
    }

    public void deleteStringAttributeAttachments(List<GrantStringAttributeAttachments> attachments) {
        grantStringAttributeAttachmentRepository.deleteAll(attachments);
    }

    public List<GrantHistory> getGrantHistory(Long grantId) {
        return grantHistoryRepository.findByGrantId(grantId);
    }

    public String[] buildEmailNotificationContent(Grant finalGrant, User user, String subConfigValue, String msgConfigValue, String currentState, String currentOwner,
                                                  String previousState, String previousOwner, String previousAction, String hasChanges,
                                                  String hasChangesComment, String hasNotes, String hasNotesComment, String link, User owner,
                                                  Integer noOfDays, Map<Long, Long> previousApprover, List<GrantAssignments> newApprover) {

        String code = Base64.getEncoder().encodeToString(String.valueOf(finalGrant.getId()).getBytes());

        String host = "";
        String url = "";
        UriComponents uriComponents = null;
        try {
            uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
            if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")) {
                host = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);

            } else {
                host = uriComponents.getHost();
            }
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme())
                    .host(host).port(uriComponents.getPort());
            url = uriBuilder.toUriString();
            url = url + "/home/?action=login&g=" + code + "&email=&type=grant";
        } catch (Exception e) {
            url = link;

            url = url + "/home/?action=login&g=" + code + "&email=&type=grant";
        }

        String grantName = "";
        if ((finalGrant.getGrantStatus().getInternalStatus().equalsIgnoreCase("DRAFT") || finalGrant.getGrantStatus().getInternalStatus().equalsIgnoreCase("REVIEW")) && finalGrant.getOrigGrantId() != null) {
            grantName = "Amendment in-progress [" + getById(finalGrant.getOrigGrantId()).getReferenceNo() + "] " + finalGrant.getName();
        } else {
            grantName = finalGrant.getReferenceNo() != null ? "[".concat(finalGrant.getReferenceNo()).concat("] ").concat(finalGrant.getName()) : finalGrant.getName();
        }
        String message = msgConfigValue.replaceAll(GRANT_NAME, grantName)
                .replaceAll("%CURRENT_STATE%", currentState).replaceAll("%CURRENT_OWNER%", currentOwner)
                .replaceAll("%PREVIOUS_STATE%", previousState).replaceAll("%PREVIOUS_OWNER%", previousOwner)
                .replaceAll("%PREVIOUS_ACTION%", previousAction).replaceAll("%HAS_CHANGES%", hasChanges)
                .replaceAll("%HAS_CHANGES_COMMENT%", hasChangesComment).replaceAll("%HAS_NOTES%", hasNotes)
                .replaceAll("%HAS_NOTES_COMMENT%", hasNotesComment)
                .replaceAll("%TENANT%", finalGrant.getGrantorOrganization().getName()).replaceAll("%GRANT_LINK%", url)
                .replaceAll("%OWNER_NAME%", owner == null ? "" : owner.getFirstName() + " " + owner.getLastName())
                .replaceAll("%OWNER_EMAIL%", owner == null ? "" : owner.getEmailId())
                .replaceAll("%NO_DAYS%", noOfDays == null ? "" : String.valueOf(noOfDays))
                .replaceAll("%GRANTEE%",
                        finalGrant.getOrganization() != null ? finalGrant.getOrganization().getName() : "")
                .replaceAll("%APPROVER_TYPE%", "Approver").replaceAll("%ENTITY_TYPE%", "grant")
                .replaceAll("%PREVIOUS_ASSIGNMENTS%", getAssignmentsTable(previousApprover, newApprover))
                .replaceAll("%ENTITY_NAME%", finalGrant.getName());
        String subject = subConfigValue.replaceAll(GRANT_NAME, grantName);

        return new String[]{subject, message};
    }

    private String getAssignmentsTable(Map<Long, Long> assignments, List<GrantAssignments> newAssignments) {
        if (assignments == null) {
            return "";
        }

        newAssignments.sort(Comparator.comparing(GrantAssignments::getId, (a, b) ->
                a.compareTo(b)));

        String[] table = {
                "<table width='100%' border='1' cellpadding='2' cellspacing='0'><tr><td><b>Review State</b></td><td><b>Current State Owners</b></td><td><b>Previous State Owners</b></td></tr>"};
        newAssignments.forEach(a -> {
            Long prevAss = assignments.keySet().stream().filter(b -> b.longValue() == a.getStateId().longValue()).findFirst().get();

            table[0] = table[0].concat(TROPEN).concat("<td width='30%'>")
                    .concat(workflowStatusRepository.findById(a.getStateId()).get().getName()).concat(TDCLOSE)
                    .concat(TDOPEN)
                    .concat(userService.getUserById(a.getAssignments()).getFirstName().concat(" ")
                            .concat(userService.getUserById(a.getAssignments()).getLastName()))
                    .concat(TDCLOSE)

                    .concat(TDOPEN)
                    .concat(userService.getUserById(assignments.get(prevAss)).getFirstName().concat(" ")
                            .concat(userService.getUserById(assignments.get(prevAss)).getLastName()).concat(TDCLOSE)
                            .concat("</tr>"));
        });

        table[0] = table[0].concat("</table>");
        return table[0];

    }

    public String[] buildGrantInvitationContent(Grant grant, String sub, String msg, String url) {
        sub = sub.replaceAll(GRANT_NAME, grant.getName());
        msg = msg.replaceAll(GRANT_NAME, grant.getName())
                .replaceAll("%TENANT_NAME%", grant.getGrantorOrganization().getName()).replaceAll("%LINK%", url);
        return new String[]{sub, msg};
    }

    public String buildHashCode(Grant grant) {
        SecureEntity secureEntity = new SecureEntity();
        secureEntity.setGrantId(grant.getId());
        secureEntity.setTemplateId(grant.getTemplateId());
        secureEntity.setSectionAndAtrribIds(new HashMap<>());
        secureEntity.setGranterId(grant.getGrantorOrganization().getId());
        Map<Long, List<Long>> map = new HashMap<>();
        grant.getGrantDetails().getSections().forEach(sec -> {
            List<Long> attribIds = new ArrayList<>();
            if (sec.getAttributes() != null) {
                sec.getAttributes().forEach(a ->
                        attribIds.add(a.getId())
                );
            }

            map.put(sec.getId(), attribIds);
        });
        secureEntity.setSectionAndAtrribIds(map);
        List<Long> templateIds = new ArrayList<>();
        granterGrantTemplateRepository.findByGranterId(grant.getGrantorOrganization().getId()).forEach(t ->
            templateIds.add(t.getId())
        );
        secureEntity.setGrantTemplateIds(templateIds);

        List<Long> grantWorkflowIds = new ArrayList<>();
        Map<Long, List<Long>> grantWorkflowStatusIds = new HashMap<>();
        Map<Long, Long[][]> grantWorkflowTransitionIds = new HashMap<>();
        workflowRepository.findByGranterAndObjectAndType(grant.getGrantorOrganization().getId(), GRANT, grant.getGrantTypeId()).forEach(w -> {
            grantWorkflowIds.add(w.getId());
            List<Long> wfStatusIds = new ArrayList<>();
            workflowStatusRepository.findByWorkflow(w).forEach(ws ->
                wfStatusIds.add(ws.getId())
            );
            grantWorkflowStatusIds.put(w.getId(), wfStatusIds);

            List<WorkflowStatusTransition> transitions = workflowStatusTransitionRepository.findByWorkflow(w);
            Long[][] stransitions = new Long[transitions.size()][2];
            final int[] counter = {0};
            workflowStatusTransitionRepository.findByWorkflow(w).forEach(st -> {
                stransitions[counter[0]][0] = st.getFromState().getId();
                stransitions[counter[0]][1] = st.getToState().getId();
                counter[0]++;
            });
            grantWorkflowTransitionIds.put(w.getId(), stransitions);
        });

        secureEntity.setGrantWorkflowIds(grantWorkflowIds);
        secureEntity.setWorkflowStatusIds(grantWorkflowStatusIds);
        secureEntity.setWorkflowStatusTransitionIds(grantWorkflowTransitionIds);
        secureEntity.setTenantCode(grant.getGrantorOrganization().getCode());

        List<Long> tLibraryIds = new ArrayList<>();
        templateLibraryRepository.findByGranterId(grant.getGrantorOrganization().getId()).forEach(tl ->
            tLibraryIds.add(tl.getId()));
        secureEntity.setTemplateLibraryIds(tLibraryIds);

        try {
            return Jwts.builder().setSubject(new ObjectMapper().writeValueAsString(secureEntity))
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public SecureEntity unBuildGrantHashCode(Grant grant) {
        String grantSecureCode = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(grant.getSecurityCode()).getBody()
                .getSubject();
        SecureEntity secureHash = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            secureHash = mapper.readValue(grantSecureCode, SecureEntity.class);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return secureHash;
    }

    public List<Grant> getActiveGrantsForTenant(Organization organization) {
        return grantRepository.findActiveGrants(organization.getId());
    }

    public List<GrantAssignments> getActionDueGrantsForPlatform(List<Long> granterIds) {
        return grantAssignmentRepository.getActionDueGrantsForPlatform(granterIds);
    }

    public List<GrantAssignments> getActionDueGrantsForGranterOrg(Long granterId) {
        return grantAssignmentRepository.getActionDueGrantsForGranterOrg(granterId);
    }

    public Long getCountOfOtherGrantsWithStartDateAndStatus(Date startDate, Long granterId, Long statusId) {
        return grantRepository.getCountOfOtherGrantsWithStartDateAndStatus(startDate, granterId, statusId);
    }

    public List<Grant> getGrantsOwnedByUserByStatus(Long userId, String status) {
        return grantRepository.findGrantsOwnedByUserByStatus(userId, status);
    }

    public Grant grantToReturn(@PathVariable("userId") Long userId, Grant grant) {
        User user = userService.getUserById(userId);

        grant.setActionAuthorities(
                workflowPermissionService.getGrantActionPermissions(grant.getGrantorOrganization().getId(),
                        user.getUserRoles(), grant.getGrantStatus().getId(), userId, grant.getId()));

        grant.setFlowAuthorities(workflowPermissionService.getGrantFlowPermissions(grant.getGrantStatus().getId(),
                userId, grant.getId()));

        grant.setGrantTemplate(granterGrantTemplateService.findByTemplateId(grant.getTemplateId()));

        GrantVO grantVO = new GrantVO();

        grantVO = grantVO.build(grant, getGrantSections(grant), workflowPermissionService, user,
                userService, this);
        grant.setGrantDetails(grantVO.getGrantDetails());
        grant.setNoteAddedBy(grantVO.getNoteAddedBy());
        grant.setNoteAddedByUser(grantVO.getNoteAddedByUser());

        List<GrantAssignmentsVO> workflowAssignments = new ArrayList<>();
        grant.setWorkflowAssignment(getGrantWorkflowAssignments(grant));
        for (GrantAssignments assignment : grant.getWorkflowAssignment()) {
            GrantAssignmentsVO assignmentsVO = new GrantAssignmentsVO();
            assignmentsVO.setId(assignment.getId());
            assignmentsVO.setAnchor(assignment.isAnchor());
            assignmentsVO.setAssignments(assignment.getAssignments());
            if (assignment.getAssignments() != null && assignment.getAssignments() > 0) {
                assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignments()));
            }
            assignmentsVO.setGrantId(assignment.getGrant().getId());
            assignmentsVO.setStateId(assignment.getStateId());
            assignmentsVO.setStateName(workflowStatusService.findById(assignment.getStateId()));

            setAssignmentHistory(grant, assignmentsVO);
            workflowAssignments.add(assignmentsVO);
        }
        grant.setWorkflowAssignments(workflowAssignments);
        List<GrantAssignments> grantAssignments = getGrantCurrentAssignments(grant);
        if (grantAssignments != null) {
            for (GrantAssignments assignment : grantAssignments) {
                grant.setCurrentAssignment(assignment.getAssignments());
            }
        }
        grant = saveGrant(grant);

        grant.getWorkflowAssignment().sort((a, b) -> a.getId().compareTo(b.getId()));
        grant.getGrantDetails().getSections()
                .sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
        for (SectionVO section : grant.getGrantDetails().getSections()) {
            if (section.getAttributes() != null) {
                section.getAttributes().sort(
                        (a, b) -> Long.valueOf(a.getAttributeOrder()).compareTo(Long.valueOf(b.getAttributeOrder())));
            }
        }

        grant.setSecurityCode(buildHashCode(grant));
        List<WorkflowStatus> workflowStatuses = workflowStatusRepository.getAllTenantStatuses("DISBURSEMENT",
                grant.getGrantorOrganization().getId());

        List<WorkflowStatus> activeAndClosedStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase(ACTIVE)
                        || ws.getInternalStatus().equalsIgnoreCase(CLOSED))
                .collect(Collectors.toList());
        List<Long> statusIds = activeAndClosedStatuses.stream().mapToLong(WorkflowStatus::getId).boxed()
                .collect(Collectors.toList());
        List<Disbursement> approvedDisbursements = disbursementRepository
                .getDisbursementByGrantAndStatuses(grant.getId(), statusIds);
        List<ActualDisbursement> approvedActualDisbursements = new ArrayList<>();
        if (approvedDisbursements != null) {

            for (Disbursement approved : approvedDisbursements) {
                List<ActualDisbursement> approvedActuals = actualDisbursementRepository
                        .findByDisbursementId(approved.getId());
                approvedActualDisbursements.addAll(approvedActuals);
            }
        }

        if (grant.getOrigGrantId() != null) {
            approvedActualDisbursements.addAll(getPastApprovedActualDisbursementsForGrant(getById(grant.getOrigGrantId()), true));
        }
        HashSet<Object> seen = new HashSet<>();
        approvedActualDisbursements.removeIf(e -> !seen.add(e.getId()));

        grant.setProjectDocumentsCount(getGrantsDocuments(grant.getId()).size());
        approvedActualDisbursements.removeIf(ad -> ad.getActualAmount() == null);
        grant.setApprovedDisbursementsTotal(
                approvedActualDisbursements.stream().mapToDouble(ActualDisbursement::getActualAmount).sum());

        List<WorkflowStatus> reportApprovedStatus = workflowStatusService
                .getTenantWorkflowStatuses(REPORT, grant.getGrantorOrganization().getId()).stream()
                .filter(s -> s.getInternalStatus().equalsIgnoreCase(CLOSED)).collect(Collectors.toList());
        Workflow currentReportWorkflow = workflowRepository.findWorkflowByGrantTypeAndObject(grant.getGrantTypeId(), REPORT);
        reportApprovedStatus.removeIf(wf -> wf.getWorkflow().getId().longValue() != currentReportWorkflow.getId().longValue());
        int noOfReports = 0;
        List<ReportCard> reports = reportService.findReportCardsByStatusForGrant(reportApprovedStatus.get(0), grant);
        noOfReports = reports.size();
        if (grant.getOrigGrantId() != null) {
            reports = reportService.findReportCardsByStatusForGrant(reportApprovedStatus.get(0),
                    getById(grant.getOrigGrantId()));
            noOfReports += reports.size();
        }
        grant.setApprovedReportsForGrant(noOfReports);


        // Set old grant ref no if current amendment grant is still in porogress
        if (grant.getOrigGrantId() != null && !grant.getGrantStatus().getInternalStatus().equalsIgnoreCase(ACTIVE)
                && !grant.getGrantStatus().getInternalStatus().equalsIgnoreCase(CLOSED)) {
            grant.setOrigGrantRefNo(getById(grant.getOrigGrantId()).getReferenceNo());
        }

        // Set Minimum End Date for Amendment grant
        if (grant.getOrigGrantId() != null) {
            List<Report> existingReports = reportService.getReportsForGrant(getById(grant.getOrigGrantId()));
            if (existingReports != null && !existingReports.isEmpty()) {
                existingReports.removeIf(r -> r.getEndDate() == null);
                if (!existingReports.isEmpty()) {

                    Comparator<Report> endDateComparator = Comparator.comparing(Report::getEndDate);
                    existingReports.sort(endDateComparator);
                    Report lastReport = existingReports.get(existingReports.size() - 1);
                    grant.setMinEndEndate(lastReport.getEndDate());
                } else {
                    grant.setMinEndEndate(grant.getStartDate());
                }
            }

            List<Disbursement> existingDisbursements = disbursementService
                    .getAllDisbursementsForGrant(grant.getOrigGrantId());
            if (existingDisbursements != null && !existingDisbursements.isEmpty()) {
                existingDisbursements.removeIf(d -> !d.getStatus().getInternalStatus().equalsIgnoreCase("DRAFT"));
                if (!existingDisbursements.isEmpty()) {

                    Comparator<Disbursement> endDateComparator = Comparator.comparing(Disbursement::getMovedOn);
                    existingDisbursements.sort(endDateComparator);
                    Disbursement lastDisbursement = existingDisbursements.get(existingDisbursements.size() - 1);
                    if (grant.getMinEndEndate() != null && new DateTime(lastDisbursement.getMovedOn())
                            .isAfter(new DateTime(grant.getMinEndEndate()))) {
                        grant.setMinEndEndate(lastDisbursement.getMovedOn());
                    } else {
                        grant.setMinEndEndate(grant.getStartDate());
                    }

                }
            }
        }

        List<GrantTag> grantTags = getTagsForGrant(grant.getId());
        List<GrantTagVO> grantTagsVoList = new ArrayList<>();
        for (GrantTag tag : grantTags) {
            GrantTagVO vo = new GrantTagVO();
            vo.setGrantId(grant.getId());
            vo.setId(tag.getId());
            vo.setOrgTagId(tag.getOrgTagId());
            vo.setTagName(orgTagService.getOrgTagById(tag.getOrgTagId()).getName());
            grantTagsVoList.add(vo);
        }
        grant.setTags(grantTagsVoList);

        List<GrantClosure> closuresForGrant = closureService.getClosuresForGrant(grant.getId());
        GrantClosure currentClosure = null;
        if(closuresForGrant!=null && !closuresForGrant.isEmpty()){
            currentClosure=closuresForGrant.get(0);
            grant.setHashClosure(true);
            grant.setClosureId(currentClosure.getId());
        }



        return grant;
    }

    private List<ActualDisbursement> getPastApprovedActualDisbursementsForGrant(Grant grant, boolean includeCurrent) {
        List<Disbursement> disbs = disbursementService.getAllDisbursementsForGrant(grant.getId());
        List<ActualDisbursement> approvedActualDisbursements = new ArrayList<>();
        List<WorkflowStatus> workflowStatuses = workflowStatusRepository.getAllTenantStatuses("DISBURSEMENT",
                grant.getGrantorOrganization().getId());

        List<WorkflowStatus> activeAndClosedStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase(ACTIVE)
                        || ws.getInternalStatus().equalsIgnoreCase(CLOSED))
                .collect(Collectors.toList());
        List<Long> statusIds = activeAndClosedStatuses.stream().mapToLong(WorkflowStatus::getId).boxed()
                .collect(Collectors.toList());

        for (Disbursement disbursement : disbs) {
            List<Disbursement> approvedDisbursements = disbursementService.getDibursementsForGrantByStatuses(disbursement.getGrant().getId(),
                    statusIds);

            if (approvedDisbursements != null) {
                if (!includeCurrent) {
                    approvedDisbursements.removeIf(d -> d.getId().longValue() == disbursement.getId().longValue());
                }
                approvedDisbursements.removeIf(d -> new DateTime(d.getMovedOn(), DateTimeZone.forID(timezone))
                        .isAfter(new DateTime(disbursement.getMovedOn(), DateTimeZone.forID(timezone))));
                for (Disbursement approved : approvedDisbursements) {
                    List<ActualDisbursement> approvedActuals = disbursementService.getActualDisbursementsForDisbursement(approved);
                    approvedActualDisbursements.addAll(approvedActuals);
                }
            }
            //Get previous actual disbursements if grant is amended
            if (grant.getOrigGrantId() != null) {
                List<Disbursement> disburs = disbursementService.getAllDisbursementsForGrant(disbursement.getGrant().getOrigGrantId());
                for (Disbursement d : disburs) {
                    approvedActualDisbursements.addAll(getPastApprovedActualDisbursementsForGrant(d.getGrant(), true));
                }
            }
        }

        approvedActualDisbursements.sort(Comparator.comparing(ActualDisbursement::getOrderPosition));
        return approvedActualDisbursements;
    }

    public void setAssignmentHistory(Grant grant, GrantAssignmentsVO assignmentsVO) {
        if (!grantRepository.findGrantsThatMovedAtleastOnce(grant.getId()).isEmpty()) {
            List<GrantAssignmentHistory> assignmentHistories = assignmentHistoryRepository
                    .findByGrantIdAndStateIdOrderByUpdatedOnDesc(grant.getId(), assignmentsVO.getStateId());
            for (GrantAssignmentHistory grantAss : assignmentHistories) {
                if (grantAss.getAssignments() != null && grantAss.getAssignments() != 0) {
                    grantAss.setAssignmentUser(userService.getUserById(grantAss.getAssignments()));
                }
                if (grantAss.getUpdatedBy() != null && grantAss.getUpdatedBy() != 0) {
                    grantAss.setUpdatedByUser(userService.getUserById(grantAss.getUpdatedBy()));
                }
            }
            assignmentsVO.setHistory(assignmentHistories);
        }
    }

    public boolean checkIfGrantMovedThroughWFAtleastOnce(Long grantId) {
        return !grantRepository.findGrantsThatMovedAtleastOnce(grantId).isEmpty();
    }

    public List<GrantDocument> getGrantsDocuments(Long grantId) {
        return grantDocumentRepository.findByGrantId(grantId);
    }

    public GrantDocument saveGrantDocument(GrantDocument attachment) {
        return grantDocumentRepository.save(attachment);
    }

    public GrantDocument getGrantDocumentById(Long attachmentId) {
        Optional<GrantDocument> doc = grantDocumentRepository.findById(attachmentId);
        return doc.isPresent() ? doc.get() : null;
    }

    public void deleteGrantDocument(GrantDocument doc) {
        grantDocumentRepository.delete(doc);
    }

    public List<DisabledUsersEntity> getGrantsWithDisabledUsers() {
        return disabledUsersEntityRepository.getGrants();
    }

    public List<GrantType> getGrantTypesForTenantOrg(Long orgId) {
        return grantTypeRepository.findGrantTypesForTenant(orgId);
    }

    public GrantType getGrantypeById(Long grantTypeId) {
        Optional<GrantType> type = grantTypeRepository.findById(grantTypeId);
        return type.isPresent() ? type.get() : null;
    }

    public List<Grant> getAllGrantsForGranter(Long granterId) {
        return grantRepository.getAllGrantsForGranter(granterId);
    }

    public List<GrantTag> getTagsForGrant(Long grantId) {
        return grantTagRepository.getTagsForGrant(grantId);
    }

    public GrantTag attachTagToGrant(GrantTag tag) {
        return grantTagRepository.save(tag);
    }

    public void detachTagToGrant(GrantTag tag) {
        grantTagRepository.delete(tag);
    }

    public GrantTag getGrantTagById(Long id) {
        return grantTagRepository.getTagById(id);
    }

    public boolean isTagInUse(Long orgTagId) {
        return grantTagRepository.isTagInUse(orgTagId);
    }

    public DataExportSummary saveExportSummary(DataExportSummary summary) {
        return dataExportSummaryRepository.save(summary);
    }

    public List<Long> getAllGrantIdsForProject(Long grantId) {
        List<Long> dGrantIds = getDownstreamGrantIds(grantId);
        List<Long> uGrantIds = getUpstreamGrantIds(grantId);

        Set<Long> finalGrantIds = new HashSet<>();
        finalGrantIds.addAll(dGrantIds);
        finalGrantIds.addAll(uGrantIds);
        finalGrantIds.removeIf(g -> g.longValue() == grantId.longValue());
        return finalGrantIds.stream().collect(Collectors.toList());
    }

    private List<Long> getDownstreamGrantIds(Long grantId) {
        List<Long> ids = new ArrayList<>();
        ids.add(grantId);
        Grant grant = getById(grantId);
        if (grant.getOrigGrantId() != null) {
            ids.addAll(getDownstreamGrantIds(grant.getOrigGrantId()));
        }
        return ids;
    }

    private List<Long> getUpstreamGrantIds(Long grantId) {
        List<Long> ids = new ArrayList<>();

        Grant grant = getByOrigGrantId(grantId);

        if (grant != null) {
            ids.add(grant.getId());
            ids.addAll(getUpstreamGrantIds(grant.getId()));
        }
        return ids;
    }

    public Grant getByOrigGrantId(Long grantId) {
        return grantRepository.getByOrigGrantId(grantId);
    }


    public Long getActionDueGrantsForUser(Long userId) {
        return grantRepository.getActionDueGrantsForUser(userId);
    }

    public List<GrantCard> getDetailedActionDueGrantsForUser(Long userId) {
        return grantCardRepository.getDetailedActionDueGrantsForUser(userId);
    }

    public Long getActionDueReportsForUser(Long userId) {
        return reportRepository.getActionDueReportsForUser(userId);
    }

    public List<ReportCard> getDetailedActionDueReportsForUser(Long userId) {
        return reportCardRepository.getDetailedActionDueReportsForUser(userId);
    }

    public Long getUpComingDraftGrants(Long userId) {
        return grantRepository.getUpComingDraftGrants(userId);
    }

    public List<GrantCard> getDetailedUpComingDraftGrants(Long userId) {
        return grantCardRepository.getDetailedUpComingDraftGrants(userId);
    }

    public Long getGrantsInWorkflow(Long userId) {
        return grantRepository.getGrantsInWorkflow(userId);
    }

    public Long getUpcomingGrantsDisbursementAmount(Long userId) {
        return grantRepository.getUpcomingGrantsDisbursementAmount(userId);
    }

    public Long getGrantsTotalForUserByStatus(Long userId, String status) {
        return grantRepository.getGrantsTotalForUserByStatus(userId, status);
    }

    public Long getCommittedAmountByUserAndStatus(Long userId, String status) {
        return grantRepository.getCommittedAmountByUserAndStatus(userId, status);
    }

    public Long getDisbursedAmountByUserAndStatus(Long userId, String status) {
        return grantRepository.getDisbursedAmountByUserAndStatus(userId, status);
    }

    public Long getGranteeOrgsCountByUserAndStatus(Long userId, String status) {
        return grantRepository.getGranteeOrgsCountByUserAndStatus(userId, status);
    }

    public Long getGrantsWithNoApprovedReportsByUserAndStatus(Long userId, String status) {
        return grantRepository.getGrantsWithNoApprovedReportsByUserAndStatus(userId, status);
    }

    public Long getGrantsWithNoAKPIsByUserAndStatus(Long userId, String status) {
        return grantRepository.getGrantsWithNoAKPIsByUserAndStatus(userId, status);
    }

    public boolean isUserPartOfActiveWorkflow(Long userId) {
        return grantRepository.isUserPartOfActiveWorkflow(userId);
    }

    public Long getUpcomingReportsDisbursementAmount(Long userId) {
        return reportRepository.getUpcomingReportsDisbursementAmount(userId);
    }

    public List<Grant> getgrantsByStatusForUser(Long userId, String status) {
        return grantRepository.getgrantsByStatusForUser(userId, status);
    }

    public PlainGrant grantToPlain(Grant grant) throws IOException {
        SimpleDateFormat sd = new SimpleDateFormat(DD_MMM_YYYY);
        PlainGrant plainGrant = new PlainGrant();
        plainGrant.setName(grant.getName());
        plainGrant.setAmount(grant.getAmount());
        Date end = grant.getEndDate();
        plainGrant.setEndDate(end != null ? sd.format(end) : "");
        Date start = grant.getStartDate();
        plainGrant.setStartDate(start != null ? sd.format(start) : "");
        plainGrant.setImplementingOrganizationName(grant.getOrganization() != null ? grant.getOrganization().getName() : "");
        plainGrant.setImplementingOrganizationRepresentative(grant.getRepresentative() != null ? grant.getRepresentative() : "");
        plainGrant.setReferenceNo(grant.getReferenceNo());
        plainGrant.setCurrentInternalStatus(grant.getGrantStatus().getInternalStatus());
        plainGrant.setCurrentStatus(grant.getGrantStatus().getName());
        Optional<GrantAssignments> assignment = grant.getWorkflowAssignment().stream().filter(ass -> ass.getStateId().longValue() == grant.getGrantStatus().getId()).findFirst();
        if (assignment.isPresent()) {
            User owner = userService.getUserById(assignment.get().getAssignments());
            plainGrant.setCurrentOwner(owner.getFirstName() + " " + owner.getLastName());
        }
        if (grant.getGrantDetails().getSections() != null && !grant.getGrantDetails().getSections().isEmpty()) {
            List<PlainSection> plainSections = new ArrayList<>();
            grant.getGrantDetails().getSections().sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
            for (SectionVO section : grant.getGrantDetails().getSections()) {
                List<PlainAttribute> plainAttributes = new ArrayList<>();
                if (section.getAttributes() != null && !section.getAttributes().isEmpty()) {
                    section.getAttributes().sort((a, b) -> Long.valueOf(a.getAttributeOrder()).compareTo(Long.valueOf(b.getAttributeOrder())));
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
                                plainAttribute.setTarget(Long.valueOf(attribute.getTarget()));
                                break;
                            case DISBURSEMENT:
                            case TABLE:

                                plainAttribute.setTableValue(mapper.readValue(attribute.getFieldValue(), new TypeReference<List<TableData>>() {
                                }));
                                break;
                            case "document":
                                if (attribute.getFieldValue() != null && !attribute.getFieldValue().equalsIgnoreCase("")) {
                                    plainAttribute.setAttachments(mapper.readValue(attribute.getFieldValue(), new TypeReference<List<GrantStringAttributeAttachments>>() {
                                    }));
                                }
                                break;
                            default:
                                //do nothing
                        }

                        plainAttributes.add(plainAttribute);
                    }

                }
                plainSections.add(new PlainSection(section.getId(), section.getName(), section.getOrder(), plainAttributes));
            }
            plainGrant.setSections(plainSections);
        }

        return plainGrant;
    }

    public List<ClosureReason> getClosureReasons(Long orgId) {
        return closureReasonsRepository.getClosureReasonsForOrg(orgId);
    }

    public Grant moveToNewState(GrantWithNote grantwithNote, Long userId, Long grantId, Long fromStateId,
                                Long toStateId, String tenantCode) {

        String fromStringAttributes = null;
        try {
            fromStringAttributes = getCurrentGrantDetails(grantId, userService.getUserById(userId));
        } catch (JsonProcessingException e) {

            logger.error(e.getMessage(), e);
        }
        for (SectionVO section : grantwithNote.getGrant().getGrantDetails().getSections()) {
            if (section.getAttributes() != null) {
                for (SectionAttributesVO attribute : section.getAttributes()) {
                    if (attribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                        List<String> rowNames = new ArrayList<>();
                        if (attribute.getFieldTableValue().size() > 1) {
                            for (int i = 0; i < attribute.getFieldTableValue().size(); i++) {
                                if (attribute.getFieldTableValue().get(i).getColumns()[0].getValue().trim().equalsIgnoreCase("")
                                        && attribute.getFieldTableValue().get(i).getColumns()[1].getValue().trim().equalsIgnoreCase("")
                                        && attribute.getFieldTableValue().get(i).getColumns()[2].getValue().trim().equalsIgnoreCase("")
                                        && attribute.getFieldTableValue().get(i).getColumns()[3].getValue()
                                        .trim().equalsIgnoreCase("")) {
                                    rowNames.add(attribute.getFieldTableValue().get(i).getName());
                                }
                            }
                        }

                        if (attribute.getFieldTableValue().size() == rowNames.size()) {
                            rowNames.remove(rowNames.size() - 1);
                        }
                        for (String rowName : rowNames) {
                            attribute.getFieldTableValue().removeIf(e -> e.getName().equalsIgnoreCase(rowName));
                        }

                        for (int i = 0; i < attribute.getFieldTableValue().size(); i++) {
                            attribute.getFieldTableValue().get(i).setName(String.valueOf(i + 1));
                            try {
                                attribute.setFieldValue(
                                        new ObjectMapper().writeValueAsString(attribute.getFieldTableValue()));
                            } catch (JsonProcessingException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }

                    }
                }
            }
        }

        saveGrant(grantId, grantwithNote.getGrant(), userId, tenantCode);
        Grant grant = getById(grantId);
        Grant finalGrant = grant;
        WorkflowStatus toStatus = workflowStatusService.findById(toStateId);
        User user = userService.getUserById(userId);

        if (grant.getOrigGrantId() != null) {
            grant.setAmendmentNo(getById(grant.getOrigGrantId()).getAmendmentNo() + 1);
        }

        if (toStatus.getInternalStatus().equalsIgnoreCase(ACTIVE)) {

            if (Boolean.TRUE.equals(Boolean.valueOf(appConfigService
                    .getAppConfigForGranterOrg(organizationService.findOrganizationByTenantCode(tenantCode).getId(),
                            AppConfiguration.GENERATE_GRANT_REFERENCE)
                    .getConfigValue()))) {

                grant = generateGrantReferenceNo(grant);

            }

            createReportingPeriods(grant, user, tenantCode);
        }

        WorkflowStatus previousState = grant.getGrantStatus();
        List<GrantAssignments> previousAssignments = getGrantWorkflowAssignments(grant).stream()
                .filter(ass -> ass.getGrant().getId().longValue() == grantId.longValue()
                        && ass.getStateId().longValue() == finalGrant.getGrantStatus().getId().longValue())
                .collect(Collectors.toList());
        User previousOwner = null;
        if (previousAssignments != null && !previousAssignments.isEmpty()) {
            previousOwner = userService.getUserById(previousAssignments.get(0).getAssignments());
        }
        grant.setGrantStatus(workflowStatusService.findById(toStateId));
        grant.setNote((grantwithNote.getNote() != null && !grantwithNote.getNote().trim().equalsIgnoreCase(""))
                ? grantwithNote.getNote()
                : "No note added");
        grant.setNoteAdded(new Date());
        grant.setNoteAddedBy(userService.getUserById(userId).getEmailId());

        Date currentDateTime = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate();
        grant.setUpdatedAt(currentDateTime);
        grant.setUpdatedBy(userService.getUserById(userId).getEmailId());
        grant.setMovedOn(currentDateTime);


        grant = saveGrant(grant);
        List<User> usersToNotify = new ArrayList<>();

        final List<GrantAssignments> assigments = getGrantWorkflowAssignments(grant);
        assigments.forEach(ass -> {
            if (usersToNotify.stream().noneMatch(u -> u.getId().longValue() == ass.getAssignments().longValue())) {
                usersToNotify.add(userService.getUserById(ass.getAssignments()));
            }
        });

        Optional<GrantAssignments> assignmentForCurrentState = getGrantWorkflowAssignments(grant).stream()
                .filter(ass -> ass.getGrant().getId().longValue() == grantId.longValue()
                        && ass.getStateId().longValue() == toStateId.longValue())
                .findFirst();
        User currentOwner = null;
        if (assignmentForCurrentState.isPresent()) {
            currentOwner = userService.getUserById(assignmentForCurrentState.get().getAssignments());
        } else {
            currentOwner = new User();
            currentOwner.setFirstName("-");
            currentOwner.setLastName("-");
        }

        WorkflowStatusTransition transition = workflowStatusTransitionService.findByFromAndToStates(previousState,
                toStatus);

        String[] emailNotificationContent = buildEmailNotificationContent(finalGrant, user,
                appConfigService.getAppConfigForGranterOrg(finalGrant.getGrantorOrganization().getId(),
                        AppConfiguration.GRANT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                appConfigService.getAppConfigForGranterOrg(finalGrant.getGrantorOrganization().getId(),
                        AppConfiguration.GRANT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                workflowStatusService.findById(toStateId).getName(),
                currentOwner.getFirstName().concat(" ").concat(currentOwner.getLastName()), previousState.getName(),
                previousOwner == null ? " -"
                        : previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                transition==null?"Request Modifications":transition.getAction(), "Yes", PLEASE_REVIEW,
                grantwithNote.getNote() != null && !grantwithNote.getNote().trim().equalsIgnoreCase("") ? "Yes" : "No",
                grantwithNote.getNote() != null && !grantwithNote.getNote().trim().equalsIgnoreCase("")
                        ? PLEASE_REVIEW
                        : "",
                "", null, null, null, null);
        String[] notificationContent = buildEmailNotificationContent(finalGrant, user,
                appConfigService.getAppConfigForGranterOrg(finalGrant.getGrantorOrganization().getId(),
                        AppConfiguration.GRANT_STATE_CHANGED_MAIL_SUBJECT).getConfigValue(),
                appConfigService.getAppConfigForGranterOrg(finalGrant.getGrantorOrganization().getId(),
                        AppConfiguration.GRANT_STATE_CHANGED_MAIL_MESSAGE).getConfigValue(),
                workflowStatusService.findById(toStateId).getName(),
                currentOwner.getFirstName().concat(" ").concat(currentOwner.getLastName()), previousState.getName(),
                previousOwner == null ? " -"
                        : previousOwner.getFirstName().concat(" ").concat(previousOwner.getLastName()),
                transition==null?"Request Modifications":transition.getAction(), "Yes", PLEASE_REVIEW,
                grantwithNote.getNote() != null && !grantwithNote.getNote().trim().equalsIgnoreCase("") ? "Yes" : "No",
                grantwithNote.getNote() != null && !grantwithNote.getNote().trim().equalsIgnoreCase("")
                        ? PLEASE_REVIEW
                        : "",
                "", null, null, null, null);

        if (!toStatus.getInternalStatus().equalsIgnoreCase(CLOSED)) {
            final User currentOwnerFinal = currentOwner;
            usersToNotify.removeIf(u -> u.getId().longValue() == currentOwnerFinal.getId() || u.isDeleted());
            commonEmailSevice
                    .sendMail(!currentOwner.isDeleted() ? new String[]{currentOwner.getEmailId()} : null,
                            usersToNotify.stream().map(User::getEmailId).collect(Collectors.toList())
                                    .toArray(new String[usersToNotify.size()]),
                            emailNotificationContent[0], emailNotificationContent[1],
                            new String[]{appConfigService
                                    .getAppConfigForGranterOrg(finalGrant.getGrantorOrganization().getId(),
                                            AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                    .getConfigValue().replaceAll(RELEASE_VERSION,
                                    releaseService.getCurrentRelease().getVersion())});

            usersToNotify.stream().forEach(u -> notificationsService.saveNotification(notificationContent, u.getId(),
                    finalGrant.getId(), GRANT));
            notificationsService.saveNotification(notificationContent, currentOwner.getId(), finalGrant.getId(),
                    GRANT);
        } else {

            Optional<WorkflowStatus> first = workflowStatusService
                    .getTenantWorkflowStatuses(GRANT, grant.getGrantorOrganization().getId()).stream()
                    .filter(st -> st.getInternalStatus().equalsIgnoreCase(ACTIVE) && st.getWorkflow().getId().longValue() == finalGrant.getGrantStatus().getWorkflow().getId().longValue()).findFirst();
            WorkflowStatus activeStatus = first.isPresent() ? first.get() : null;
            Optional<GrantAssignments> optionalGrantAssignments = getGrantWorkflowAssignments(grant).stream()
                    .filter(ass -> ass.getStateId().longValue() == activeStatus.getId().longValue()).findFirst();
            GrantAssignments activeStateAssignment = optionalGrantAssignments.isPresent() ? optionalGrantAssignments.get() : null;

            User activeStateOwner = userService.getUserById(activeStateAssignment.getAssignments());
            usersToNotify.removeIf(u -> u.getId().longValue() == activeStateOwner.getId().longValue() || u.isDeleted());

            try {
                commonEmailSevice
                        .sendMail(!activeStateOwner.isDeleted() ? new String[]{activeStateOwner.getEmailId()} : null,
                                usersToNotify.stream().map(User::getEmailId).collect(Collectors.toList())
                                        .toArray(new String[usersToNotify.size()]),
                                emailNotificationContent[0], emailNotificationContent[1],
                                new String[]{appConfigService
                                        .getAppConfigForGranterOrg(finalGrant.getGrantorOrganization().getId(),
                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                        .getConfigValue().replaceAll(RELEASE_VERSION,
                                        releaseService.getCurrentRelease().getVersion())});

                usersToNotify.stream().forEach(u -> notificationsService.saveNotification(notificationContent, u.getId(),
                        finalGrant.getId(), GRANT));
                notificationsService.saveNotification(notificationContent, activeStateOwner.getId(), finalGrant.getId(),
                        GRANT);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }

        grant.setActionAuthorities(
                workflowPermissionService.getGrantActionPermissions(grant.getGrantorOrganization().getId(),
                        user.getUserRoles(), grant.getGrantStatus().getId(), userId, grantId));

        grant.setFlowAuthorities(workflowPermissionService.getGrantFlowPermissions(grant.getGrantStatus().getId(),
                userId, grant.getId()));
        GrantVO grantVO = new GrantVO().build(grant, getGrantSections(grant), workflowPermissionService,
                user,
                userService, this);

        grant.setGrantDetails(grantVO.getGrantDetails());
        grant.setNoteAddedByUser(
                userService.getUserByEmailAndOrg(grant.getNoteAddedBy(), grant.getGrantorOrganization()));
        List<GrantAssignments> grantAssignments = getGrantCurrentAssignments(grant);
        if (grantAssignments != null) {
            for (GrantAssignments assignment : grantAssignments) {
                grant.setCurrentAssignment(assignment.getAssignments());
            }
        }
        grant.setGrantTemplate(granterGrantTemplateService.findByTemplateId(grant.getTemplateId()));

        grant.getWorkflowAssignment().sort((a, b) -> a.getId().compareTo(b.getId()));
        grant.getSubmissions().sort((a, b) -> a.getSubmitBy().compareTo(b.getSubmitBy()));
        grant.getGrantDetails().getSections()
                .sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
        for (SectionVO sec : grant.getGrantDetails().getSections()) {
            if (sec.getAttributes() != null) {
                sec.getAttributes().sort(
                        (a, b) -> Long.valueOf(a.getAttributeOrder()).compareTo(Long.valueOf(b.getAttributeOrder())));
            }
        }

        // Save Snapshot

        saveSnapShot(grant, fromStateId, fromStringAttributes, toStateId, grant.getNote(), currentDateTime,
                assignmentForCurrentState.isPresent()
                        ? userService.getUserById(assignmentForCurrentState.get().getAssignments())
                        : null,
                previousOwner);

        return grant;
    }

    private void saveSnapShot(Grant grant, Long fromStateId, String fromStringAttributes, Long toStatusId,
                              String fromNote, Date movedOn, User currentUser, User previousUser) {

        try {
            GrantSnapshot snapshot = new GrantSnapshot();
            snapshot.setAmount(grant.getAmount());
            if (currentUser != null) {
                snapshot.setAssignedToId(currentUser.getId());
            }
            if (previousUser != null) {
                snapshot.setAssignedBy(previousUser.getId());
            }
            snapshot.setDescription(grant.getDescription());
            snapshot.setMovedOn(movedOn);
            snapshot.setEndDate(grant.getEndDate());
            snapshot.setGrantee(grant.getOrganization() != null ? grant.getOrganization().getName() : "");
            snapshot.setGrantId(grant.getId());
            snapshot.setName(grant.getName());
            snapshot.setRepresentative(grant.getRepresentative());
            snapshot.setStartDate(grant.getStartDate());
            snapshot.setGrantStatusId(fromStateId); // Legacy for backward compatibility
            snapshot.setToStateId(toStatusId);
            snapshot.setFromStateId(fromStateId);
            snapshot.setFromStringAttributes(fromStringAttributes);
            snapshot.setFromNote(fromNote);
            snapshot.setStringAttributes(new ObjectMapper().writeValueAsString(grant.getGrantDetails()));
            grantSnapshotService.saveGrantSnapshot(snapshot);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }

    }

    private String getCurrentGrantDetails(Long grantId, User user) throws JsonProcessingException {
        Grant g = getById(grantId);
        GrantVO vo = new GrantVO().build(g, getGrantSections(g), workflowPermissionService, user,
                userService, this);

        return new ObjectMapper().writeValueAsString(vo.getGrantDetails());

    }

    public Grant saveGrant(
            Long grantId,
            Grant grantToSave,
            Long userId,
            String tenantCode) {

        Organization tenantOrg = null;
        User user = userService.getUserById(userId);
        if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")) {
            tenantOrg = getById(grantId).getGrantorOrganization();
        } else {
            tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        }
        GrantType grantType = getGrantypeById(grantToSave.getGrantTypeId());
        if (grantType.isInternal()) {
            grantToSave.setOrganization(tenantOrg);
        } else {
            grantToSave.setOrganization(processNewGranteeOrgIfPresent(grantToSave));
        }
        Grant grant = processGrant(grantToSave, tenantOrg, user);
        grant.setActionAuthorities(
                workflowPermissionService.getGrantActionPermissions(grant.getGrantorOrganization().getId(),
                        user.getUserRoles(), grant.getGrantStatus().getId(), userId, grant.getId()));

        grant.setFlowAuthorities(workflowPermissionService.getGrantFlowPermissions(grant.getGrantStatus().getId(),
                userId, grant.getId()));

        for (Submission submission : grant.getSubmissions()) {
            submission.setActionAuthorities(workflowPermissionService
                    .getSubmissionActionPermission(grant.getGrantorOrganization().getId(), user.getUserRoles()));

            AppConfig submissionWindow = appConfigService.getAppConfigForGranterOrg(
                    submission.getGrant().getGrantorOrganization().getId(),
                    AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS);
            Date submissionWindowStart = new DateTime(submission.getSubmitBy())
                    .minusDays(Integer.valueOf(submissionWindow.getConfigValue()) + 1).toDate();

            List<WorkFlowPermission> flowPermissions = workflowPermissionService.getSubmissionFlowPermissions(
                    grant.getGrantorOrganization().getId(), user.getUserRoles(),
                    submission.getSubmissionStatus().getId());

            if (!flowPermissions.isEmpty() && DateTime.now().toDate().after(submissionWindowStart)) {
                submission.setFlowAuthorities(flowPermissions);
            }
            submission.setOpenForReporting(DateTime.now().toDate().after(submissionWindowStart));
        }

        GrantVO grantVO = new GrantVO();

        grantVO = grantVO.build(grant, getGrantSections(grant), workflowPermissionService, user,
                userService, this);
        grant.setGrantDetails(grantVO.getGrantDetails());
        grant.setNoteAddedBy(grantVO.getNoteAddedBy());
        grant.setNoteAddedByUser(grantVO.getNoteAddedByUser());
        List<GrantAssignmentsVO> workflowAssignments = new ArrayList<>();
        for (GrantAssignments assignment : getGrantWorkflowAssignments(grant)) {
            GrantAssignmentsVO assignmentsVO = new GrantAssignmentsVO();
            assignmentsVO.setId(assignment.getId());
            assignmentsVO.setAnchor(assignment.isAnchor());
            assignmentsVO.setAssignments(assignment.getAssignments());
            if (assignment.getAssignments() != null && assignment.getAssignments() > 0) {
                assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignments()));
            }
            assignmentsVO.setGrantId(assignment.getGrant().getId());
            assignmentsVO.setStateId(assignment.getStateId());
            assignmentsVO.setStateName(workflowStatusService.findById(assignment.getStateId()));
            setAssignmentHistory(grant, assignmentsVO);
            workflowAssignments.add(assignmentsVO);

        }
        grant.setWorkflowAssignments(workflowAssignments);
        List<GrantAssignments> grantAssignments = getGrantCurrentAssignments(grant);
        if (grantAssignments != null) {
            for (GrantAssignments assignment : grantAssignments) {
                grant.setCurrentAssignment(assignment.getAssignments());
            }
        }
        GranterGrantTemplate templ = granterGrantTemplateService.findByTemplateId(grant.getTemplateId());
        if (templ != null) {
            grant.setGrantTemplate(templ);
        }


        grant.getSubmissions().sort((a, b) -> a.getSubmitBy().compareTo(b.getSubmitBy()));
        grant.getGrantDetails().getSections()
                .sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
        for (SectionVO sec : grant.getGrantDetails().getSections()) {
            if (sec.getAttributes() != null) {
                sec.getAttributes().sort(
                        (a, b) -> Long.valueOf(a.getAttributeOrder()).compareTo(Long.valueOf(b.getAttributeOrder())));
            }
        }

        grant.setSecurityCode(buildHashCode(grant));

        if (grant.getOrigGrantId() != null && !grant.getGrantStatus().getInternalStatus().equalsIgnoreCase(ACTIVE)
                && !grant.getGrantStatus().getInternalStatus().equalsIgnoreCase(CLOSED)) {
            grant.setOrigGrantRefNo(getById(grant.getOrigGrantId()).getReferenceNo());
        }

        if (grant.getOrigGrantId() != null) {
            List<Report> existingReports = reportService
                    .getReportsForGrant(getById(grant.getOrigGrantId()));
            if (existingReports != null && !existingReports.isEmpty()) {
                existingReports.removeIf(r -> r.getEndDate() == null);
                if (!existingReports.isEmpty()) {
                    Comparator<Report> endDateComparator = Comparator.comparing(Report::getEndDate);
                    existingReports.sort(endDateComparator);
                    Report lastReport = existingReports.get(existingReports.size() - 1);
                    grant.setMinEndEndate(lastReport.getEndDate());
                } else {
                    grant.setMinEndEndate(grant.getStartDate());
                }
            }
        }

        List<GrantTag> grantTags = getTagsForGrant(grant.getId());
        List<GrantTagVO> grantTagsVoList = new ArrayList<>();
        for (GrantTag tag : grantTags) {
            GrantTagVO vo = new GrantTagVO();
            vo.setGrantId(grant.getId());
            vo.setId(tag.getId());
            vo.setOrgTagId(tag.getOrgTagId());
            vo.setTagName(orgTagService.getOrgTagById(tag.getOrgTagId()).getName());
            grantTagsVoList.add(vo);
        }
        grant.setTags(grantTagsVoList);
        return grant;
    }

    private Grantee processNewGranteeOrgIfPresent(Grant grantToSave) {
        Grantee newGrantee = null;
        if (grantToSave.getOrganization() != null) {
            if (grantToSave.getOrganization().getId() < 0) {
                newGrantee = (Grantee) grantToSave.getOrganization();
                newGrantee = granteeService.saveGrantee(newGrantee);
                Role role = new Role();
                role.setCreatedBy("System");
                role.setCreatedAt(DateTime.now().toDate());
                role.setDescription("Admin role");
                role.setName("Admin");
                role.setOrganization(newGrantee);
                role.setHasUsers(false);
                role.setLinkedUsers(0);
                role.setInternal(false);
                roleService.saveRole(role);
            } else {
                newGrantee = (Grantee) grantToSave.getOrganization();
            }
        }
        return newGrantee;
    }

    private Grant processGrant(Grant grantToSave, Organization tenant, User user) {
        Grant grant = getById(grantToSave.getId());

        grant.setAmount(grantToSave.getAmount());
        grant.setDescription(grantToSave.getDescription());
        grant.setRepresentative(grantToSave.getRepresentative());

        if (grantToSave.getEndDate() != null) {
            grant.setEnDate(grantToSave.getEnDate());
            grant.setEndDate(grantToSave.getEndDate());
        }
        if (grantToSave.getStartDate() != null) {
            grant.setStDate(grantToSave.getStDate());
            grant.setStartDate(grantToSave.getStartDate());
        }
        grant.setGrantorOrganization((Granter) tenant);
        grant.setGrantStatus(grantToSave.getGrantStatus());
        grant.setName(grantToSave.getName());

        grantToSave.setOrganization(grantToSave.getOrganization());

        grant.setOrganization(grantToSave.getOrganization());

        grant.setStatusName(grantToSave.getStatusName());
        if (grantToSave.getEndDate() != null) {
            grant.setStartDate(grantToSave.getStartDate());
            grant.setStDate(grantToSave.getStDate());
        }
        grant.setSubstatus(grantToSave.getSubstatus());
        grant.setUpdatedAt(DateTime.now().toDate());
        grant.setUpdatedBy(user.getEmailId());
        grant.setSubstatus(grantToSave.getSubstatus());
        grant = saveGrant(grant);

        processStringAttributes(grant, grantToSave, tenant);

        grant = saveGrant(grant);

        return grant;
    }

    private void processStringAttributes(Grant grant, Grant grantToSave, Organization tenant) {
        GrantSpecificSection grantSpecificSection = null;

        for (SectionVO sectionVO : grantToSave.getGrantDetails().getSections()) {
            if (sectionVO.getId() < 0 && grantSpecificSection == null) {
                grantSpecificSection = new GrantSpecificSection();
            } else if (sectionVO.getId() > 0) {
                grantSpecificSection = getGrantSectionBySectionId(sectionVO.getId());
            }
            grantSpecificSection.setSectionName(sectionVO.getName());
            grantSpecificSection.setSectionOrder(sectionVO.getOrder());
            grantSpecificSection.setGranter((Granter) tenant);
            grantSpecificSection.setDeletable(true);

            grantSpecificSection = saveSection(grantSpecificSection);

            GrantSpecificSectionAttribute sectionAttribute = null;

            if (sectionVO.getAttributes() != null) {
                List<GrantSpecificSectionAttribute> sectionAttributesToDelete = new ArrayList<>();
                for (SectionAttributesVO sectionAttributesVO : sectionVO.getAttributes()) {

                    sectionAttribute = getSectionAttributeByAttributeIdAndType(sectionAttributesVO.getId(),
                            sectionAttributesVO.getFieldType());

                    if (sectionAttribute == null) {
                        sectionAttributesToDelete.add(sectionAttribute);
                        continue;
                    }
                    sectionAttribute.setFieldName(sectionAttributesVO.getFieldName());
                    sectionAttribute.setFieldType(sectionAttributesVO.getFieldType());
                    sectionAttribute.setGranter((Granter) tenant);
                    sectionAttribute.setAttributeOrder(sectionAttributesVO.getAttributeOrder());
                    sectionAttribute.setRequired(true);
                    sectionAttribute.setSection(grantSpecificSection);
                    sectionAttribute = saveSectionAttribute(sectionAttribute);

                    GrantStringAttribute grantStringAttribute = findGrantStringBySectionAttribueAndGrant(grantSpecificSection, sectionAttribute, grant);
                    if (grantStringAttribute == null) {
                        grantStringAttribute = new GrantStringAttribute();
                        grantStringAttribute.setSectionAttribute(sectionAttribute);
                        grantStringAttribute.setSection(grantSpecificSection);
                        grantStringAttribute.setGrant(grant);
                    }
                    grantStringAttribute.setTarget(sectionAttributesVO.getTarget());
                    grantStringAttribute.setFrequency(sectionAttributesVO.getFrequency());
                    if (sectionAttribute.getFieldType().equalsIgnoreCase(TABLE)
                            || sectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                        List<TableData> tableData = sectionAttributesVO.getFieldTableValue();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            grantStringAttribute.setValue(mapper.writeValueAsString(tableData));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                    } else {
                        grantStringAttribute.setValue(sectionAttributesVO.getFieldValue());
                    }
                    saveGrantStringAttribute(grantStringAttribute);
                    grant.getStringAttributes().add(grantStringAttribute);
                }
            }

        }
        saveGrant(grant);
    }

    private Grant generateGrantReferenceNo(Grant grant) {
        logger.info("GENERATING SNO");
        if (grant.getStartDate() == null && grant.getEndDate() == null && grant.getOrganization() == null) {
            throw new ApplicationException("Cannot generate reference code");
        }

        SimpleDateFormat stFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat enFormat = new SimpleDateFormat("yyMM");
        Integer sNo = getActiveGrantsForTenant(grant.getGrantorOrganization()).size();

        String referenceCode = "";
        if (grant.getOrigGrantId() != null) {

            String prevRefNo = getById(grant.getOrigGrantId()).getReferenceNo();
            if (prevRefNo.startsWith("A" + (grant.getAmendmentNo() - 1) + "-")) {
                prevRefNo = prevRefNo.substring(prevRefNo.indexOf("-") + 1);
            }
            referenceCode = "A" + grant.getAmendmentNo() + "-" + prevRefNo;
        } else {
            String code = grant.getOrganization() == null ? grant.getGrantorOrganization().getName() : grant.getOrganization().getName();
            referenceCode = code.replaceAll(" ", "").substring(0, 4).toUpperCase() + "-"
                    + stFormat.format(grant.getStartDate()) + "-" + enFormat.format(grant.getEndDate()) + ("-" + (sNo + 1));
        }
        grant.setReferenceNo(referenceCode);
        return saveGrant(grant);

    }

    private void createReportingPeriods(Grant grant, User user, String tenantCode) {
        Map<DatePeriod, PeriodAttribWithLabel> quarterlyPeriods = new HashMap<>();
        Map<DatePeriod, PeriodAttribWithLabel> halfyearlyPeriods = new HashMap<>();
        Map<DatePeriod, PeriodAttribWithLabel> monthlyPeriods = new HashMap<>();
        Map<DatePeriod, PeriodAttribWithLabel> yearlyPeriods = new HashMap<>();
        if (grant.getStartDate() != null && grant.getEndDate() != null) {
            grant.getGrantDetails().getSections().forEach(sec -> {
                if (sec.getAttributes() != null && !sec.getAttributes().isEmpty()) {
                    List<String> order = ImmutableList.of("YEARLY", "HALF-YEARLY", "QUARTERLY", "MONTHLY");
                    Ordering.explicit(order);
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

        Date now = DateTime.now().toDate();

        final int[] i = {1};
        GranterReportTemplate reportTemplate = reportService.getDefaultTemplate(grant.getGrantorOrganization().getId());

        yearlyPeriods.forEach((entry, val) -> {
            Report report = new Report();
            report.setCreatedAt(now);
            report.setCreatedBy(user.getId());
            report.setDueDate(
                    new DateTime(entry.getEnd(),
                            DateTimeZone.forID(timezone))
                            .plusDays(
                                    Integer.valueOf(appConfigService
                                            .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                                    AppConfiguration.REPORT_DUE_DATE_INTERVAL)
                                            .getConfigValue()))
                            .toDate());
            report.setEndDate(entry.getEnd());
            report.setGrant(grant);
            report.setName(val.getPeriodLabel());
            report.setTemplate(reportTemplate);
            report.setStartDate(entry.getStart());
            report.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId(REPORT,
                    grant.getGrantorOrganization().getId(), grant.getGrantTypeId()));
            report.setType("Yearly");

            report = reportService.saveReport(report);

            createSectionsForReports(reportTemplate, val.getAttributes(), report);

            reportService.saveAssignments(report, tenantCode);
            i[0]++;
        });

        i[0] = 1;

        halfyearlyPeriods.forEach((entry, val) -> {
            Report report = new Report();
            report.setCreatedAt(now);
            report.setCreatedBy(user.getId());
            report.setDueDate(
                    new DateTime(entry.getEnd(),
                            DateTimeZone.forID(timezone))
                            .plusDays(
                                    Integer.valueOf(appConfigService
                                            .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                                    AppConfiguration.REPORT_DUE_DATE_INTERVAL)
                                            .getConfigValue()))
                            .toDate());
            report.setEndDate(entry.getEnd());
            report.setGrant(grant);
            report.setName(val.getPeriodLabel());
            report.setTemplate(reportTemplate);
            report.setStartDate(entry.getStart());
            report.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId(REPORT,
                    grant.getGrantorOrganization().getId(), grant.getGrantTypeId()));
            report.setType("Half-Yearly");
            report = reportService.saveReport(report);
            createSectionsForReports(reportTemplate, val.getAttributes(), report);
            reportService.saveAssignments(report, tenantCode);
            i[0]++;
        });

        i[0] = 1;

        quarterlyPeriods.forEach((entry, val) -> {
            Report report = new Report();
            report.setCreatedAt(now);
            report.setCreatedBy(user.getId());
            report.setDueDate(
                    new DateTime(entry.getEnd(),
                            DateTimeZone.forID(timezone))
                            .plusDays(
                                    Integer.valueOf(appConfigService
                                            .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                                    AppConfiguration.REPORT_DUE_DATE_INTERVAL)
                                            .getConfigValue()))
                            .toDate());
            report.setEndDate(entry.getEnd());
            report.setGrant(grant);
            report.setName(val.getPeriodLabel());
            report.setTemplate(reportTemplate);
            report.setStartDate(entry.getStart());
            report.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId(REPORT,
                    grant.getGrantorOrganization().getId(), grant.getGrantTypeId()));
            report.setType("Quarterly");
            report = reportService.saveReport(report);
            createSectionsForReports(reportTemplate, val.getAttributes(), report);
            reportService.saveAssignments(report, tenantCode);
            i[0]++;
        });

        i[0] = 1;

        monthlyPeriods.forEach((entry, val) -> {
            Report report = new Report();
            report.setCreatedAt(now);
            report.setCreatedBy(user.getId());
            report.setDueDate(
                    new DateTime(entry.getEnd(),
                            DateTimeZone.forID(timezone))
                            .plusDays(
                                    Integer.valueOf(appConfigService
                                            .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                                    AppConfiguration.REPORT_DUE_DATE_INTERVAL)
                                            .getConfigValue()))
                            .toDate());
            report.setEndDate(entry.getEnd());
            report.setGrant(grant);
            report.setName(val.getPeriodLabel());
            report.setTemplate(reportTemplate);
            report.setStartDate(entry.getStart());
            report.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId(REPORT,
                    grant.getGrantorOrganization().getId(), grant.getGrantTypeId()));
            report.setType("Monthly");
            report = reportService.saveReport(report);
            createSectionsForReports(reportTemplate, val.getAttributes(), report);
            reportService.saveAssignments(report, tenantCode);
            i[0]++;
        });
    }

    private List<DatePeriod> getReportingFrequencies(DateTime st, DateTime en, Frequency frequency) {

        List<DatePeriod> periods = new ArrayList<>();
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
                assert qrtrEnd != null;
                DateTime tempEn = qrtrEnd.getDateTime().dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
                if (tempEn.isAfter(en)) {
                    DatePeriod dp = new DatePeriod(st.toDate(), en.toDate());
                    DatePeriodLabel lbl = endOfQuarter(st);
                    dp.setLabel(lbl != null ? lbl.getPeriodLabel() : "");
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
                assert halfYrEnd != null;
                DateTime tempEn = halfYrEnd.getDateTime().dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
                if (tempEn.isAfter(en)) {
                    DatePeriod dp = new DatePeriod(st.toDate(), en.toDate());
                    dp.setLabel(halfYrEnd.getPeriodLabel());
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
                DatePeriodLabel endOfYear = endOfYear(st);
                assert endOfYear != null;
                DateTime tempEn = endOfYear.getDateTime().dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
                if (tempEn.isAfter(en)) {
                    DatePeriod dp = new DatePeriod(st.toDate(), en.toDate());
                    String periodLabel = endOfYear.getPeriodLabel();
                    assert periodLabel != null;
                    dp.setLabel(periodLabel);
                    periods.add(dp);
                    break;
                }
                DatePeriod p = new DatePeriod(st.toDate(), tempEn.toDate());
                p.setLabel(endOfYear.getPeriodLabel());
                periods.add(p);
                st = tempEn.plusDays(1).withTimeAtStartOfDay();
            }
        }

        return periods;
    }

    private DatePeriodLabel endOfQuarter(DateTime st) {
        if (st.getMonthOfYear() >= Month.JANUARY.getValue() && st.getMonthOfYear() <= Month.MARCH.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.MARCH.getValue()),
                    "Quarterly Report - Q4 " + (st.getYear() - 1) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear()).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.APRIL.getValue() && st.getMonthOfYear() <= Month.JUNE.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.JUNE.getValue()),
                    "Quarterly Report - Q1 " + (st.getYear()) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.JULY.getValue() && st.getMonthOfYear() <= Month.SEPTEMBER.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.SEPTEMBER.getValue()),
                    "Quarterly Report - Q2 " + (st.getYear()) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.OCTOBER.getValue()
                && st.getMonthOfYear() <= Month.DECEMBER.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.DECEMBER.getValue()),
                    "Quarterly Report - Q3 " + (st.getYear()) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        }
        return null;
    }

    private DatePeriodLabel endOfHalfYear(DateTime st) {
        if (st.getMonthOfYear() >= Month.APRIL.getValue() && st.getMonthOfYear() <= Month.SEPTEMBER.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.SEPTEMBER.getValue()),
                    "Half-Yearly Report - H1 " + (st.getYear()) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.OCTOBER.getValue()
                && st.getMonthOfYear() <= Month.DECEMBER.getValue()) {
            return new DatePeriodLabel(st.plusYears(1).withMonthOfYear(Month.MARCH.getValue()),
                    "Half-Yearly Report - H2 " + (st.getYear()) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.JANUARY.getValue() && st.getMonthOfYear() <= Month.MARCH.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.MARCH.getValue()),
                    "Half-Yearly Report - H2 " + (st.getYear() - 1) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear()).substring(2, 4)));
        }
        return null;
    }

    private DatePeriodLabel endOfYear(DateTime st) {
        if (st.getMonthOfYear() >= Month.APRIL.getValue() && st.getMonthOfYear() <= Month.DECEMBER.getValue()) {
            return new DatePeriodLabel(st.plusYears(1).withMonthOfYear(Month.MARCH.getValue()),
                    "Yearly Report " + (st.getYear()) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear() + 1).substring(2, 4)));
        } else if (st.getMonthOfYear() >= Month.JANUARY.getValue() && st.getMonthOfYear() <= Month.MARCH.getValue()) {
            return new DatePeriodLabel(st.withMonthOfYear(Month.MARCH.getValue()),
                    "Yearly Report " + (st.getYear() - 1) + FILE_SEPARATOR
                            + (String.valueOf(st.getYear()).substring(2, 4)));
        }
        return null;
    }

    private void createSectionsForReports(GranterReportTemplate reportTemplate, List<SectionAttributesVO> val,
                                          Report report) {
        List<GranterReportSection> granterReportSections = reportTemplate.getSections();
        report.setStringAttributes(new ArrayList<>());
        AtomicBoolean reportTemplateHasDisbursement = new AtomicBoolean(false);
        AtomicReference<ReportStringAttribute> disbursementAttributeValue = null;
        AtomicInteger sectionOrder = new AtomicInteger(0);
        for (GranterReportSection reportSection : granterReportSections) {
            ReportSpecificSection specificSection = new ReportSpecificSection();
            specificSection.setDeletable(true);
            specificSection.setGranter((Granter) report.getGrant().getGrantorOrganization());
            specificSection.setReportId(report.getId());
            specificSection.setReportTemplateId(reportTemplate.getId());
            specificSection.setSectionName(reportSection.getSectionName());
            specificSection.setSectionOrder(reportSection.getSectionOrder());
            sectionOrder.set(specificSection.getSectionOrder());
            specificSection = reportService.saveReportSpecificSection(specificSection);
            ReportSpecificSection finalSpecificSection = specificSection;
            Report finalReport = report;
            final AtomicInteger[] attribVOOrder = {new AtomicInteger(1)};
            if (specificSection.getSectionName().equalsIgnoreCase("Project Indicators")) {
                val.forEach(attribVo -> {
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
                });
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
                    stringAttribute.setFrequency(report.getType().toLowerCase());
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
        for (GrantSpecificSection grantSection : getGrantSections(report.getGrant())) {
            for (GrantSpecificSectionAttribute specificSectionAttribute : getAttributesBySection(grantSection)) {
                if (specificSectionAttribute.getFieldType().equalsIgnoreCase(DISBURSEMENT)) {
                    if (reportTemplateHasDisbursement.get()) {
                        ObjectMapper mapper = new ObjectMapper();
                        String[] colHeaders = new String[]{"Disbursement Date", "Actual Disbursement",
                                "Funds from other Sources", "Notes"};
                        List<TableData> tableDataList = new ArrayList<>();
                        TableData tableData = new TableData();
                        tableData.setName("1");
                        tableData.setHeader("Planned Installment #");
                        tableData.setEnteredByGrantee(false);
                        tableData.setColumns(new ColumnData[4]);
                        for (int i = 0; i < tableData.getColumns().length; i++) {

                            String check = (i == 0) ? "date" : null;
                            tableData.getColumns()[i] = new ColumnData(colHeaders[i], "",
                                    (i == 1 || i == 2) ? "currency" : check);
                        }
                        tableDataList.add(tableData);

                        try {
                            assert disbursementAttributeValue != null;
                            disbursementAttributeValue.get().setValue(mapper.writeValueAsString(tableDataList));
                            reportService.saveReportStringAttribute(disbursementAttributeValue.get());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ReportSpecificSection specificSection = new ReportSpecificSection();
                        specificSection.setDeletable(true);
                        specificSection.setGranter((Granter) report.getGrant().getGrantorOrganization());
                        specificSection.setReportId(report.getId());
                        specificSection.setReportTemplateId(reportTemplate.getId());
                        specificSection.setSectionName("Project Funds");
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
    }
}
