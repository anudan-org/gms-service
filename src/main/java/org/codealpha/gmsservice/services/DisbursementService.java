package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.models.PlainDisbursement;
import org.codealpha.gmsservice.repositories.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DisbursementService {

    public static final String TD = "</td>";
    public static final String HOME_ACTION_LOGIN_D = "/landing/?action=login&d=";
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

    public Double getPlannedFundFromOthersByGrant(Grant byId) {
        return disbursementRepository.getPlannedFundFromOthersByGrant(byId.getId());
        
    }

    public Double getActualFundFromOthersByGrant(Grant byId) {
        return disbursementRepository.getActualFundFromOthersByGrant(byId.getId());
        
    }

    public Disbursement createAssignmentPlaceholders(Disbursement disbursementToSave, Long userId) {
        Workflow currentWorkflow = disbursementToSave.getStatus().getWorkflow();
        final Disbursement finalDisbursement = disbursementToSave;
        List<WorkflowStatus> statuses = new ArrayList<>();
        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionRepository
                .findByWorkflow(currentWorkflow);
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

                if(Boolean.TRUE.equals(status.getTerminal())){
                    Optional<GrantAssignments> grantAssignment = grantService.getGrantWorkflowAssignments(disbursementToSave.getGrant()).stream().filter(ass -> ass.getStateId().longValue() == finalDisbursement.getGrant().getGrantStatus().getId().longValue()).findFirst();
                    if(grantAssignment.isPresent()) {
                        GrantAssignments activeStateOwner = grantAssignment.get();
                        assignment.setOwner(activeStateOwner.getAssignments());
                    }
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
                .getPermissionsForDisbursementFlow(disbursement.getStatus().getId(), disbursement.getId());

        disbursement.setFlowPermissions(permissions);
        List<DisbursementAssignment> disbursementAssignments = disbursementAssignmentRepository
                .findByDisbursementId(disbursement.getId());

        for (DisbursementAssignment ass : disbursementAssignments) {
            if(ass.getOwner()!=null && ass.getOwner()!=0) {
                ass.setAssignmentUser(userService.getUserById(ass.getOwner()));
            }
            if (!disbursementRepository.findDisbursementsThatMovedAtleastOnce(ass.getDisbursementId()).isEmpty()) {
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
        List<Long> statusIds = activeAndClosedStatuses.stream().mapToLong(WorkflowStatus::getId).boxed()
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

    private List<Disbursement> getDisbursementsForGrant(Grant grant, List<Long> statusIds) {
        List<Disbursement> disbursements = getDibursementsForGrantByStatuses(grant.getId(),statusIds);
        if(grant.getOrigGrantId()!=null){
                disbursements.addAll(getDisbursementsForGrant(grantService.getById(grant.getOrigGrantId()), statusIds));
        }
        return disbursements;
    }   

     private List<ActualDisbursement> getApprovedActualDisbursements(Disbursement disbursement, List<Long> statusIds,boolean includeCurrent) {
        //Fix: gets all disbursements upto original grant and gets approved actual disbursements for all of them
        List<Disbursement> approvedDisbursements = getDisbursementsForGrant(disbursement.getGrant(), statusIds);
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

        //Removing duplicate entries here
        approvedActualDisbursements = approvedActualDisbursements.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(ActualDisbursement::getId))),
                        ArrayList::new));
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
            disbursementToReturn(d, user.getId());
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
        Optional<DisbursementAssignment> disbursementAssignment = disbursementAssignmentRepository.findById(id);
        return disbursementAssignment.isPresent()?disbursementAssignment.get():null;
    }

    public DisbursementAssignment saveAssignmentForDisbursement(DisbursementAssignment assignment) {
        return disbursementAssignmentRepository.save(assignment);
    }

    public String[] buildEmailNotificationContent(Disbursement finalDisbursement, User user, String subConfigValue, String msgConfigValue, String currentState,
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
            url = url + HOME_ACTION_LOGIN_D + code + "&email=&type=disbursement";
        } catch (Exception e) {
            url = link;

            url = url + HOME_ACTION_LOGIN_D + code + "&email=&type=disbursement";
        }

        String grantName = "";
        if(finalDisbursement.getGrant().getReferenceNo()!=null){
            grantName = "[".concat(finalDisbursement.getGrant().getReferenceNo()).concat("] ").concat(finalDisbursement.getGrant().getName());
        }else{
            grantName = finalDisbursement.getGrant().getReferenceNo()!=null?"[".concat(finalDisbursement.getGrant().getReferenceNo()).concat("] ").concat(finalDisbursement.getGrant().getName()):finalDisbursement.getGrant().getName();
        }

        String message = msgConfigValue.replace("%GRANT_NAME%", grantName)
                .replace("%CURRENT_STATE%", currentState).replace("%CURRENT_OWNER%", currentOwner)
                .replace("%PREVIOUS_STATE%", previousState).replace("%PREVIOUS_OWNER%", previousOwner)
                .replace("%PREVIOUS_ACTION%", previousAction).replace("%HAS_CHANGES%", hasChanges)
                .replace("%HAS_CHANGES_COMMENT%", hasChangesComment).replace("%HAS_NOTES%", hasNotes)
                .replace("%HAS_NOTES_COMMENT%", hasNotesComment)
                .replace("%TENANT%", finalDisbursement.getGrant().getGrantorOrganization().getName())
                .replace("%DISBURSEMENT_LINK%", url)
                .replace("%OWNER_NAME%", owner == null ? "" : owner.getFirstName() + " " + owner.getLastName())
                .replace("%OWNER_EMAIL%", owner == null ? "" : owner.getEmailId())
                .replace("%NO_DAYS%", noOfDays == null ? "" : String.valueOf(noOfDays))
                .replace("%GRANTEE%", finalDisbursement.getGrant().getOrganization()!=null?finalDisbursement.getGrant().getOrganization().getName():finalDisbursement.getGrant().getGrantorOrganization().getName())
                .replace("%PREVIOUS_ASSIGNMENTS%", getAssignmentsTable(previousApprover, newApprover))
                .replace("%ENTITY_TYPE%", "Approval Request Note of ")
                .replace("%ENTITY_NAME%", grantName);
        String subject = subConfigValue.replace("%GRANT_NAME%", grantName);

        return new String[] { subject, message };
    }

    private String getAssignmentsTable(Map<Long, Long> assignments, List<DisbursementAssignment> newAssignments) {
        if (assignments == null) {
            return "";
        }

        newAssignments.sort(Comparator.comparing(DisbursementAssignment::getId, Comparator.naturalOrder()));

        String[] table = {
                "<table width='100%' border='1' cellpadding='2' cellspacing='0'><tr><td><b>Review State</b></td><td><b>Current State Owners</b></td><td><b>Previous State Owners</b></td></tr>" };
        newAssignments.forEach(a -> {
            Long prevAss = assignments.keySet().stream().filter(b -> b.longValue() == a.getStateId().longValue()).findFirst().get();

            table[0] = table[0].concat("<tr>").concat("<td width='30%'>")
                    .concat(workflowStatusRepository.findById(a.getStateId()).get().getName()).concat(TD)
                    .concat("<td>")
                    .concat(userService.getUserById(a.getOwner()).getFirstName().concat(" ")
                            .concat(userService.getUserById(a.getOwner()).getLastName()))
                    .concat(TD)

                    .concat("<td>")
                    .concat(userService.getUserById(assignments.get(prevAss)).getFirstName().concat(" ")
                            .concat(userService.getUserById(assignments.get(prevAss)).getLastName()).concat(TD)
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
        Optional<ActualDisbursement> actualDisbursement = actualDisbursementRepository.findById(actualDisbursementId);
        return actualDisbursement.isPresent()?actualDisbursement.get():null;
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
        return !disbursementRepository.findDisbursementsThatMovedAtleastOnce(id).isEmpty();
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
        Optional<DisbursementDocument> disbursementDocument = disbursementDocumentRepository.findById(attachmentId);
        return disbursementDocument.isPresent()?disbursementDocument.get():null;
    }

    public void deleteDisbursementDocument(DisbursementDocument doc) {
        disbursementDocumentRepository.delete(doc);
    }

    public Long getPendingActionDisbursements(Long userId) {
        return disbursementRepository.getPendingActionDisbursements(userId);
    }

    public List<Disbursement> getDetailedPendingActionDisbursements(Long userId) {
        //Fix: added two calls to avoid the unknown error in getPermissions that brings permissions from previous record.
        List<Disbursement> disbursementActive = disbursementRepository.getDetailedPendingActiveDisbursements(userId);
        List<Disbursement> disbursementReview = disbursementRepository.getDetailedPendingReviewDisbursements(userId);
        disbursementActive.addAll(disbursementReview);
       
        return disbursementActive;
    }

    public Long getUpComingDraftDisbursements(Long userId) {
        return disbursementRepository.getUpComingDraftDisbursements(userId);
    }

    public List<Disbursement> getDetailedUpComingDraftDisbursements(Long userId) {
        return disbursementRepository.getDetailedUpComingDraftDisbursements(userId);
    }

    public Double getAcutalDisbursementAmountByGrant(Long grantId) {
        return disbursementRepository.getAcutalDisbursementAmountByGrant(grantId);
    }

    public Long getDisbursementsInWorkflow(Long userId) {
        return disbursementRepository.getDisbursementsInWorkflow(userId);
    }

    public Long getUpcomingDisbursementsDisbursementAmount(Long userId) {
        return disbursementRepository.getUpcomingDisbursementsDisbursementAmount(userId);
    }

    public PlainDisbursement disbursementToPlain(Disbursement currentDisbursement) {
        PlainDisbursement plainDisbursement = new PlainDisbursement();
        plainDisbursement.setRequestedAmount(currentDisbursement.getRequestedAmount());
        plainDisbursement.setCommentary(currentDisbursement.getReason());
        plainDisbursement.setGrantName(currentDisbursement.getGrant().getName());

        plainDisbursement.setCurrentInternalStatus(currentDisbursement.getStatus().getInternalStatus());
        plainDisbursement.setCurrentStatus(currentDisbursement.getStatus().getName());
        Optional<DisbursementAssignment> assignment = getDisbursementAssignments(currentDisbursement).stream().filter(ass -> ass.getStateId().longValue()==currentDisbursement.getStatus().getId()).findFirst();
        if(assignment.isPresent()){
            User owner = userService.getUserById(assignment.get().getOwner());
            plainDisbursement.setCurrentOwner(owner.getFirstName()+" "+owner.getLastName());
        }

        if(currentDisbursement.getActualDisbursements()!=null){
            plainDisbursement.setActualDisbursement(currentDisbursement.getActualDisbursements());
        }

        return plainDisbursement;
    }
}
