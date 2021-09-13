package org.codealpha.gmsservice.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class DisbursementService {

    @Autowired
    private DisbursementRepository disbursementRepository;
    @Autowired
    private WorkflowStatusTransitionRepository workflowStatusTransitionRepository;
    @Autowired
    private DisbursementAssignmentRepository disbursementAssignmentRepository;
    @Autowired
    private WorkflowPermissionRepository workflowPermissionRepository;
    @Autowired
    private GrantService grantService;
    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private UserService userService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private DisbursementHistoryRepository disbursementHistoryRepository;
    @Autowired
    private ActualDisbursementRepository actualDisbursementRepository;
    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;
    @Autowired
    private DisbursementAssignmentHistoryRepository assignmentHistoryRepository;
    @Autowired
    private DisabledUsersEntityRepository disabledUsersEntityRepository;
    @Value("${spring.timezone}")
    private String timezone;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private OrgTagService orgTagService;
    @Autowired
    private DisbursementDocumentRepository disbursementDocumentRepository;
    @Autowired
    private WorkflowStatusService workflowStatusService;

    public Disbursement saveDisbursement(Disbursement disbursement) {
        return disbursementRepository.save(disbursement);
    }

    public Disbursement createAssignmentPlaceholders(Disbursement disbursementToSave, Long userId) {
        Workflow currentWorkflow = disbursementToSave.getStatus().getWorkflow();
        final Disbursement finalDisbursement = disbursementToSave;
        List<WorkflowStatus> statuses = new ArrayList<>();
        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionRepository
                .findByWorkflow(currentWorkflow);
        for (WorkflowStatusTransition supportedTransition : supportedTransitions) {
            if (!statuses.stream()
                    .filter(s -> s.getId().longValue() == supportedTransition.getFromState().getId().longValue())
                    .findFirst().isPresent()) {
                statuses.add(supportedTransition.getFromState());
            }
            if (!statuses.stream()
                    .filter(s -> s.getId().longValue() == supportedTransition.getToState().getId().longValue())
                    .findFirst().isPresent()) {
                statuses.add(supportedTransition.getToState());
            }
        }

        List<DisbursementAssignment> assignmentList = new ArrayList<>();
        DisbursementAssignment assignment = null;
        for (WorkflowStatus status : statuses) {

                assignment = new DisbursementAssignment();
                if (status.isInitial()) {
                    assignment.setAnchor(true);
                    assignment.setOwner(userId);
                } else {
                    assignment.setAnchor(false);
                }
                assignment.setDisbursementId(disbursementToSave.getId());
                assignment.setStateId(status.getId());

                if(status.getTerminal()){
                    GrantAssignments activeStateOwner =  grantService.getGrantWorkflowAssignments(disbursementToSave.getGrant()).stream().filter(ass -> ass.getStateId().longValue()==finalDisbursement.getGrant().getGrantStatus().getId().longValue()).findFirst().get();
                    assignment.setOwner(activeStateOwner.getAssignments());
                }
            assignment = disbursementAssignmentRepository.save(assignment);
            assignmentList.add(assignment);
        }

        disbursementToSave.setAssignments(assignmentList);
        disbursementToSave = disbursementToReturn(disbursementToSave, userId);
        return disbursementToSave;
    }

    public Disbursement disbursementToReturn(Disbursement disbursement, Long userId) {
        List<WorkFlowPermission> permissions = workflowPermissionRepository
                .getPermissionsForDisbursementFlow(disbursement.getStatus().getId(), userId, disbursement.getId());

        disbursement.setFlowPermissions(permissions);
        List<DisbursementAssignment> disbursementAssignments = disbursementAssignmentRepository
                .findByDisbursementId(disbursement.getId());

        for (DisbursementAssignment ass : disbursementAssignments) {
            if(ass.getOwner()!=null && ass.getOwner()!=0) {
                ass.setAssignmentUser(userService.getUserById(ass.getOwner()));
            }
            if (disbursementRepository.findDisbursementsThatMovedAtleastOnce(ass.getDisbursementId()).size() > 0) {
                List<DisbursementAssignmentHistory> assignmentHistories = assignmentHistoryRepository
                        .findByDisbursementIdAndStateIdOrderByUpdatedOnDesc(ass.getDisbursementId(), ass.getStateId());
                for (DisbursementAssignmentHistory assHist : assignmentHistories) {
                    if (assHist.getOwner() != null && assHist.getOwner() != 0) {
                        assHist.setAssignmentUser(userService.getUserById(assHist.getOwner()));
                    }

                    if (assHist.getUpdatedBy() != null && assHist.getUpdatedBy() != 0) {
                        assHist.setUpdatedByUser(userService.getUserById(assHist.getUpdatedBy()));
                    }

                }
                ass.setHistory(assignmentHistories);
            }
        }
        disbursement.setAssignments(disbursementAssignments);

        GrantVO vo = new GrantVO().build(disbursement.getGrant(),
                grantService.getGrantSections(disbursement.getGrant()), workflowPermissionService,
                userService.getUserById(userId),
                appConfigService.getAppConfigForGranterOrg(disbursement.getGrant().getGrantorOrganization().getId(),
                        AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS),
                userService,grantService);

        disbursement.getGrant().setGrantDetails(vo.getGrantDetails());
        if (disbursement.getNoteAddedBy() != null) {
            disbursement.setNoteAddedByUser(userService.getUserById(disbursement.getNoteAddedBy()));
        }

        List<ActualDisbursement> actualDisbursements = getActualDisbursementsForDisbursement(disbursement);
        if (actualDisbursements != null) {
            disbursement.setActualDisbursements(actualDisbursements);
        }

        List<WorkflowStatus> workflowStatuses = workflowStatusRepository.getAllTenantStatuses("DISBURSEMENT",
                disbursement.getGrant().getGrantorOrganization().getId());

        List<WorkflowStatus> activeAndClosedStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("ACTIVE")
                        || ws.getInternalStatus().equalsIgnoreCase("CLOSED"))
                .collect(Collectors.toList());
        List<Long> statusIds = activeAndClosedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                .collect(Collectors.toList());

        List<ActualDisbursement> approvedActualDisbursements = getApprovedActualDisbursements(disbursement, statusIds,false);
        disbursement.setApprovedActualsDibursements(approvedActualDisbursements);

        List<GrantTag> grantTags = grantService.getTagsForGrant(disbursement.getGrant().getId());

        disbursement.getGrant().setGrantTags(grantTags);

        disbursement.setDisbursementDocuments(getDisbursementDocsByDisbursementId(disbursement.getId()));

        return disbursement;
    }

    List<DisbursementDocument> getDisbursementDocsByDisbursementId(Long disbursementId){
        return disbursementDocumentRepository.findByDisbursementId(disbursementId);
    }

    private List<ActualDisbursement> getApprovedActualDisbursements(Disbursement disbursement, List<Long> statusIds,boolean includeCurrent) {
        List<Disbursement> approvedDisbursements = getDibursementsForGrantByStatuses(disbursement.getGrant().getId(),
                statusIds);
        List<ActualDisbursement> approvedActualDisbursements = new ArrayList<>();
        if (approvedDisbursements != null) {
            if(!includeCurrent){
                approvedDisbursements.removeIf(d -> d.getId().longValue() == disbursement.getId().longValue());
            }
            approvedDisbursements.removeIf(d -> new DateTime(d.getMovedOn(), DateTimeZone.forID(timezone))
                    .isAfter(new DateTime(disbursement.getMovedOn(), DateTimeZone.forID(timezone))));
            for (Disbursement approved : approvedDisbursements) {
                List<ActualDisbursement> approvedActuals = getActualDisbursementsForDisbursement(approved);
                approvedActualDisbursements.addAll(approvedActuals);
            }
        }
        //Get previous actual disbursements if grant is amended
        if(disbursement.getGrant().getOrigGrantId()!=null){
            List<Disbursement> disbs = getAllDisbursementsForGrant(disbursement.getGrant().getOrigGrantId());
            for(Disbursement d : disbs){
                approvedActualDisbursements.addAll(getApprovedActualDisbursements(d,statusIds,true));
            }
        }
        approvedActualDisbursements.sort(Comparator.comparing(ActualDisbursement::getOrderPosition));
        return approvedActualDisbursements;
    }

    public List<Disbursement> getDibursementsForGrantByStatuses(Long grantId, List<Long> statuses) {
        return disbursementRepository.getDisbursementByGrantAndStatuses(grantId, statuses);
    }

    public List<Disbursement> getDisbursementsForUserByStatus(User user, Organization org, String status) {
        List<Disbursement> disbursements = new ArrayList<>();

        boolean isAdmin = false;

        for (Role role : userRoleService.findRolesForUser(userService.getUserById(user.getId()))) {
            if (role.getName().equalsIgnoreCase("ADMIN")) {
                isAdmin = true;
                break;
            }
        }

        if (status.equalsIgnoreCase("DRAFT")) {
            if(!isAdmin) {
                disbursements = disbursementRepository.getInprogressDisbursementsForUser(user.getId(), org.getId());
            }else{
                disbursements = disbursementRepository.getInprogressDisbursementsForAdminUser(user.getId(), org.getId());
            }
        } else if (status.equalsIgnoreCase("ACTIVE")) {
            disbursements = disbursementRepository.getActiveDisbursementsForUser(org.getId());
        } else if (status.equalsIgnoreCase("CLOSED")) {
            disbursements = disbursementRepository.getClosedDisbursementsForUser(org.getId());
        }
        for (Disbursement d : disbursements) {
            d = disbursementToReturn(d, user.getId());
        }
        return disbursements;
    }

    public Disbursement getDisbursementById(Long id) {
        return disbursementRepository.findByDisbursementId(id);
    }

    public List<DisbursementAssignment> getDisbursementAssignments(Disbursement disbursement) {
        return disbursementAssignmentRepository.findByDisbursementId(disbursement.getId());
    }

    public void deleteAllAssignmentsForDisbursement(Disbursement disbursement) {

        disbursementAssignmentRepository.deleteAll(getDisbursementAssignments(disbursement));
    }

    public void deleteDisbursement(Disbursement disbursement) {
        disbursementRepository.delete(disbursement);
    }

    public DisbursementAssignment getDisbursementAssignmentById(Long id) {
        return disbursementAssignmentRepository.findById(id).get();
    }

    public DisbursementAssignment saveAssignmentForDisbursement(DisbursementAssignment assignment) {
        return disbursementAssignmentRepository.save(assignment);
    }

    public String[] buildEmailNotificationContent(Disbursement finalDisbursement, User user, String userName,
            String action, String date, String subConfigValue, String msgConfigValue, String currentState,
            String currentOwner, String previousState, String previousOwner, String previousAction, String hasChanges,
            String hasChangesComment, String hasNotes, String hasNotesComment, String link, User owner,
            Integer noOfDays, Map<Long, Long> previousApprover, List<DisbursementAssignment> newApprover) {

        String code = Base64.getEncoder().encodeToString(String.valueOf(finalDisbursement.getId()).getBytes());

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
            url = url + "/home/?action=login&d=" + code + "&email=&type=disbursement";
        } catch (Exception e) {
            url = link;

            url = url + "/home/?action=login&d=" + code + "&email=&type=disbursement";
        }

        String grantName = "";
        if(finalDisbursement.getGrant().getReferenceNo()!=null){
            grantName = "[".concat(finalDisbursement.getGrant().getReferenceNo()).concat("] ").concat(finalDisbursement.getGrant().getName());
        }else{
            grantName = finalDisbursement.getGrant().getName();
        }

        String message = msgConfigValue.replaceAll("%GRANT_NAME%", grantName)
                .replaceAll("%CURRENT_STATE%", currentState).replaceAll("%CURRENT_OWNER%", currentOwner)
                .replaceAll("%PREVIOUS_STATE%", previousState).replaceAll("%PREVIOUS_OWNER%", previousOwner)
                .replaceAll("%PREVIOUS_ACTION%", previousAction).replaceAll("%HAS_CHANGES%", hasChanges)
                .replaceAll("%HAS_CHANGES_COMMENT%", hasChangesComment).replaceAll("%HAS_NOTES%", hasNotes)
                .replaceAll("%HAS_NOTES_COMMENT%", hasNotesComment)
                .replaceAll("%TENANT%", finalDisbursement.getGrant().getGrantorOrganization().getName())
                .replaceAll("%DISBURSEMENT_LINK%", url)
                .replaceAll("%OWNER_NAME%", owner == null ? "" : owner.getFirstName() + " " + owner.getLastName())
                .replaceAll("%OWNER_EMAIL%", owner == null ? "" : owner.getEmailId())
                .replaceAll("%NO_DAYS%", noOfDays == null ? "" : String.valueOf(noOfDays))
                .replaceAll("%GRANTEE%", finalDisbursement.getGrant().getOrganization()!=null?finalDisbursement.getGrant().getOrganization().getName():finalDisbursement.getGrant().getGrantorOrganization().getName())
                .replaceAll("%PREVIOUS_ASSIGNMENTS%", getAssignmentsTable(previousApprover, newApprover))
                .replaceAll("%ENTITY_TYPE%", "Approval Request Note of ")
                .replaceAll("%ENTITY_NAME%", grantName);
        String subject = subConfigValue.replaceAll("%GRANT_NAME%", grantName);

        return new String[] { subject, message };
    }

    private String getAssignmentsTable(Map<Long, Long> assignments, List<DisbursementAssignment> newAssignments) {
        if (assignments == null) {
            return "";
        }

        newAssignments.sort(Comparator.comparing(DisbursementAssignment::getId, (a, b) -> {
            return a.compareTo(b);
        }));

        String[] table = {
                "<table width='100%' border='1' cellpadding='2' cellspacing='0'><tr><td><b>Review State</b></td><td><b>Current State Owners</b></td><td><b>Previous State Owners</b></td></tr>" };
        newAssignments.forEach(a -> {
            Long prevAss = assignments.keySet().stream().filter(b -> b == a.getStateId()).findFirst().get();

            table[0] = table[0].concat("<tr>").concat("<td width='30%'>")
                    .concat(workflowStatusRepository.findById(a.getStateId()).get().getName()).concat("</td>")
                    .concat("<td>")
                    .concat(userService.getUserById(a.getOwner()).getFirstName().concat(" ")
                            .concat(userService.getUserById(a.getOwner()).getLastName()))
                    .concat("</td>")

                    .concat("<td>")
                    .concat(userService.getUserById(assignments.get(prevAss)).getFirstName().concat(" ")
                            .concat(userService.getUserById(assignments.get(prevAss)).getLastName()).concat("</td>")
                            .concat("</tr>"));
        });

        table[0] = table[0].concat("</table>");
        return table[0];

    }

    public List<DisbursementHistory> getDisbursementHistory(Long disbursementId) {
        return disbursementHistoryRepository.findByDisbursementId(disbursementId);
    }

    public ActualDisbursement createEmtptyActualDisbursement(Disbursement disbursement) {
        ActualDisbursement actualDisbursement = new ActualDisbursement();
        actualDisbursement.setDisbursementId(disbursement.getId());
        actualDisbursement
                .setOrderPosition(getNewOrderPositionForActualDisbursementOfGrant(disbursement.getGrant().getId()));
        return actualDisbursementRepository.save(actualDisbursement);
    }

    public List<ActualDisbursement> getActualDisbursementsForDisbursement(Disbursement disbursement) {
        return actualDisbursementRepository.findByDisbursementId(disbursement.getId());
    }

    public ActualDisbursement saveActualDisbursement(ActualDisbursement actualDisbursement) {
        return actualDisbursementRepository.save(actualDisbursement);
    }

    public ActualDisbursement getActualDisbursementById(Long actualDisbursementId) {
        return actualDisbursementRepository.findById(actualDisbursementId).get();
    }

    public void deleteActualDisbursement(ActualDisbursement actualDisbursement) {
        actualDisbursementRepository.delete(actualDisbursement);
    }

    public Integer getNewOrderPositionForActualDisbursementOfGrant(Long grantId) {
        Integer returnValue = actualDisbursementRepository.getMaxOrderPositionForClosedDisbursementOfGrant(grantId);
        if (returnValue == null) {
            return 1;
        } else {
            return returnValue + 1;
        }
    }

    public List<DisbursementAssignment> getActionDueDisbursementsForPlatform(List<Long> granterIds) {
        return disbursementAssignmentRepository.getActionDueDisbursementsForPlatform(granterIds);
    }

    public List<DisbursementAssignment> getActionDueDisbursementsForGranterOrg(Long granterId) {
        return disbursementAssignmentRepository.getActionDueDisbursementsForOrg(granterId);
    }

    public boolean checkIfDisbursementMovedThroughWFAtleastOnce(Long id) {
        return disbursementRepository.findDisbursementsThatMovedAtleastOnce(id).size() > 0;
    }

    public List<Disbursement> getAllDisbursementsForGrant(Long grantId) {
        return disbursementRepository.getAllDisbursementsForGrant(grantId);
    }

    public List<DisabledUsersEntity> getDisbursementsWithDisabledUsers(){
        return disabledUsersEntityRepository.getDisbursements();
    }

    public DisbursementDocument saveDisbursementDocument(DisbursementDocument attachment) {
        return disbursementDocumentRepository.save(attachment);
    }

    public DisbursementDocument getDisbursementDocumentById(Long attachmentId) {
        return disbursementDocumentRepository.findById(attachmentId).get();
    }

    public void deleteDisbursementDocument(DisbursementDocument doc) {
        disbursementDocumentRepository.delete(doc);
    }

    public Long getPendingActionDisbursements(Long userId) {
        return disbursementRepository.getPendingActionDisbursements(userId);
    }

    public List<Disbursement> getDetailedPendingActionDisbursements(Long userId) {
        return disbursementRepository.getDetailedPendingActionDisbursements(userId);
    }

    public Long getUpComingDraftDisbursements(Long userId) {
        return disbursementRepository.getUpComingDraftDisbursements(userId);
    }

    public List<Disbursement> getDetailedUpComingDraftDisbursements(Long userId) {
        return disbursementRepository.getDetailedUpComingDraftDisbursements(userId);
    }

    public Long getDisbursementsInWorkflow(Long userId) {
        return disbursementRepository.getDisbursementsInWorkflow(userId);
    }

    public Long getUpcomingDisbursementsDisbursementAmount(Long userId) {
        return disbursementRepository.getUpcomingDisbursementsDisbursementAmount(userId);
    }

    public PlainDisbursement disbursementToPlain(Disbursement currentDisbursement) {
        SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yyyy");
        PlainDisbursement plainDisbursement = new PlainDisbursement();
        plainDisbursement.setRequestedAmount(currentDisbursement.getRequestedAmount());
        plainDisbursement.setCommentary(currentDisbursement.getReason());
        plainDisbursement.setGrantName(currentDisbursement.getGrant().getName());

        if(currentDisbursement.getActualDisbursements()!=null){
            plainDisbursement.setActualDisbursement(currentDisbursement.getActualDisbursements());
        }

        return plainDisbursement;
    }
}
