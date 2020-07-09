package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.repositories.ActualDisbursementRepository;
import org.codealpha.gmsservice.repositories.DisbursementAssignmentHistoryRepository;
import org.codealpha.gmsservice.repositories.DisbursementAssignmentRepository;
import org.codealpha.gmsservice.repositories.DisbursementHistoryRepository;
import org.codealpha.gmsservice.repositories.DisbursementRepository;
import org.codealpha.gmsservice.repositories.WorkflowPermissionRepository;
import org.codealpha.gmsservice.repositories.WorkflowStatusRepository;
import org.codealpha.gmsservice.repositories.WorkflowStatusTransitionRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public Disbursement saveDisbursement(Disbursement disbursement) {
        return disbursementRepository.save(disbursement);
    }

    public Disbursement createAssignmentPlaceholders(Disbursement disbursementToSave, Long userId) {
        Workflow currentWorkflow = disbursementToSave.getStatus().getWorkflow();

        List<WorkflowStatus> statuses = new ArrayList<>();
        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionRepository
                .findByWorkflow(currentWorkflow);
        for (WorkflowStatusTransition supportedTransition : supportedTransitions) {
            if (!statuses.stream()
                    .filter(s -> Long.valueOf(s.getId()) == Long.valueOf(supportedTransition.getFromState().getId()))
                    .findAny().isPresent()) {
                statuses.add(supportedTransition.getFromState());
            }
            if (!statuses.stream()
                    .filter(s -> Long.valueOf(s.getId()) == Long.valueOf(supportedTransition.getToState().getId()))
                    .findAny().isPresent()) {
                statuses.add(supportedTransition.getToState());
            }
        }

        List<DisbursementAssignment> assignmentList = new ArrayList<>();
        DisbursementAssignment assignment = null;
        for (WorkflowStatus status : statuses) {
            if (!status.getTerminal()) {
                assignment = new DisbursementAssignment();
                if (status.isInitial()) {
                    assignment.setAnchor(true);
                    assignment.setOwner(userId);
                } else {
                    assignment.setAnchor(false);
                }
                assignment.setDisbursementId(disbursementToSave.getId());
                assignment.setStateId(status.getId());
                assignment = disbursementAssignmentRepository.save(assignment);
                assignmentList.add(assignment);
            }
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
                userService);

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

        List<Disbursement> approvedDisbursements = getDibursementsForGrantByStatuses(disbursement.getGrant().getId(),
                statusIds);
        List<ActualDisbursement> approvedActualDisbursements = new ArrayList<>();
        if (approvedDisbursements != null) {
            approvedDisbursements.removeIf(d -> d.getId().longValue() == disbursement.getId().longValue());
            approvedDisbursements
                    .removeIf(d -> new DateTime(d.getMovedOn()).isAfter(new DateTime(disbursement.getMovedOn())));
            for (Disbursement approved : approvedDisbursements) {
                List<ActualDisbursement> approvedActuals = getActualDisbursementsForDisbursement(approved);
                approvedActualDisbursements.addAll(approvedActuals);
            }
        }
        approvedActualDisbursements.sort(Comparator.comparing(ActualDisbursement::getOrderPosition));
        disbursement.setApprovedActualsDibursements(approvedActualDisbursements);

        return disbursement;
    }

    public List<Disbursement> getDibursementsForGrantByStatuses(Long grantId, List<Long> statuses) {
        return disbursementRepository.getDisbursementByGrantAndStatuses(grantId, statuses);
    }

    public List<Disbursement> getDisbursementsForUserByStatus(User user, Organization org, String status) {
        List<Disbursement> disbursements = new ArrayList<>();
        if (status.equalsIgnoreCase("DRAFT")) {
            disbursements = disbursementRepository.getInprogressDisbursementsForUser(user.getId(), org.getId());
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
        return disbursementRepository.findById(id).get();
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

        String message = msgConfigValue.replaceAll("%GRANT_NAME%", finalDisbursement.getGrant().getName())
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
                .replaceAll("%GRANTEE%", finalDisbursement.getGrant().getOrganization().getName())
                .replaceAll("%PREVIOUS_ASSIGNMENTS%", getAssignmentsTable(previousApprover))
                .replaceAll("%ENTITY_TYPE%", "Approval Request Note of ")
                .replaceAll("%CURRENT_ASSIGNMENTS%", getAssignmentsTable(newApprover))
                .replaceAll("%ENTITY_NAME%", finalDisbursement.getGrant().getName());
        String subject = subConfigValue.replaceAll("%GRANT_NAME%", finalDisbursement.getGrant().getName());

        return new String[] { subject, message };
    }

    private String getAssignmentsTable(Map<Long, Long> assignments) {
        if (assignments == null) {
            return "";
        }
        String[] table = {
                "<table width='100%' border='1' cellpadding='2' cellspacing='0'><tr><td><b>Review State</b></td><td><b>State Owner</b></td></tr>" };
        assignments.keySet().forEach(a -> {
            table[0] = table[0].concat("<tr>").concat("<td width='30%'>")
                    .concat(workflowStatusRepository.findById(a).get().getName()).concat("</td>").concat("<td>")
                    .concat(userService.getUserById(assignments.get(a)).getFirstName().concat(" ")
                            .concat(userService.getUserById(assignments.get(a)).getLastName()))
                    .concat("</td>").concat("</tr>");
        });

        table[0] = table[0].concat("</table>");
        return table[0];

    }

    private String getAssignmentsTable(List<DisbursementAssignment> assignments) {
        if (assignments == null) {
            return "";
        }
        String table = "<Table width='100%' border='1' cellpadding='2' cellspacing='0'><tr><td><b>Review State</b></td><td><b>State Owner</b></td></tr>";
        for (DisbursementAssignment ass : assignments) {
            table = table.concat("<tr>").concat("<td>")
                    .concat(workflowStatusRepository.findById(ass.getStateId()).get().getName()).concat("</td>")
                    .concat("<td>")
                    .concat(userService.getUserById(ass.getOwner()).getFirstName().concat(" ")
                            .concat(userService.getUserById(ass.getOwner()).getLastName()))
                    .concat("</td>").concat("</tr>");
        }
        table = table.concat("</table>");
        return table;
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
}
