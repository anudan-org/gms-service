package org.codealpha.gmsservice.controllers;

import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.ActualDisbursement;
import org.codealpha.gmsservice.entities.Disbursement;
import org.codealpha.gmsservice.entities.DisbursementAssignment;
import org.codealpha.gmsservice.entities.DisbursementHistory;
import org.codealpha.gmsservice.entities.DisbursementSnapshot;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Report;
import org.codealpha.gmsservice.entities.ReportStringAttribute;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowStatusTransition;
import org.codealpha.gmsservice.models.AssignedTo;
import org.codealpha.gmsservice.models.ColumnData;
import org.codealpha.gmsservice.models.DisbursementAssignmentModel;
import org.codealpha.gmsservice.models.DisbursementAssignmentsVO;
import org.codealpha.gmsservice.models.DisbursementWithNote;
import org.codealpha.gmsservice.models.TableData;
import org.codealpha.gmsservice.repositories.WorkflowStatusRepository;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/{userId}/disbursements")
public class DisbursementsController {

        @Autowired
        private GrantService grantService;
        @Autowired
        private WorkflowStatusService workflowStatusService;
        @Autowired
        private OrganizationService organizationService;
        @Autowired
        private DisbursementService disbursementService;
        @Autowired
        private UserService userService;
        @Autowired
        DisbursementSnapshotService disbursementSnapshotService;
        @Autowired
        private WorkflowStatusTransitionService workflowStatusTransitionService;
        @Autowired
        AppConfigService appConfigService;
        @Autowired
        CommonEmailSevice commonEmailSevice;
        @Autowired
        NotificationsService notificationsService;
        @Autowired
        private ReportService reportService;
        @Autowired
        private WorkflowStatusRepository workflowStatusRepository;

        @GetMapping("/active-grants")
        public List<Grant> getActiveGrantsOwnedByUser(@PathVariable("userId") Long userId,
                        @RequestHeader("X-TENANT-CODE") String tenantCode) {
                List<Grant> ownerGrants = grantService.getGrantsOwnedByUserByStatus(userId, "ACTIVE");
                List<Grant> grantsToReturn = new ArrayList<>();
                if (ownerGrants != null && ownerGrants.size() > 0) {
                        List<WorkflowStatus> workflowStatuses = workflowStatusRepository.getAllTenantStatuses(
                                        "DISBURSEMENT", ownerGrants.get(0).getGrantorOrganization().getId());

                        List<WorkflowStatus> activeAndClosedStatuses = workflowStatuses.stream()
                                        .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("CLOSED"))
                                        .collect(Collectors.toList());
                        List<Long> statusIds = activeAndClosedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                                        .collect(Collectors.toList());

                        for (Grant g : ownerGrants) {
                                Double total = 0d;
                                List<Disbursement> closedDisbursements = disbursementService
                                                .getDibursementsForGrantByStatuses(g.getId(), statusIds);
                                if (closedDisbursements != null && closedDisbursements.size() > 0) {
                                        for (Disbursement d : closedDisbursements) {
                                                List<ActualDisbursement> actualDisbursements = disbursementService
                                                                .getActualDisbursementsForDisbursement(d);
                                                if (actualDisbursements != null && actualDisbursements.size() > 0) {
                                                        for (ActualDisbursement ad : actualDisbursements) {
                                                                total += ad.getActualAmount() == null ? 0d
                                                                                : ad.getActualAmount();
                                                        }
                                                }
                                        }
                                }
                                if (total < g.getAmount()) {
                                        grantsToReturn.add(g);
                                }
                        }

                        for (Grant ownerGrant : grantsToReturn) {
                                ownerGrant = grantService._grantToReturn(userId, ownerGrant);
                        }
                }

                return grantsToReturn;
        }

        @PostMapping("/grant/{grantId}")
        public Disbursement createNewDisbursement(@PathVariable("userId") Long userId,
                        @PathVariable("grantId") Long grantId, @RequestHeader("X-TENANT-CODE") String tenantCode,
                        @RequestBody Disbursement disbursementToSave) {

                Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
                disbursementToSave = new Disbursement();
                disbursementToSave.setGrant(grantService._grantToReturn(userId, grantService.getById(grantId)));
                disbursementToSave.setReason(null);
                disbursementToSave.setRequestedAmount(null);
                disbursementToSave.setGranteeEntry(false);
                disbursementToSave.setStatus(workflowStatusService
                                .findInitialStatusByObjectAndGranterOrgId("DISBURSEMENT", tenantOrg.getId()));
                disbursementToSave.setCreatedAt(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());
                disbursementToSave.setCreatedBy(userService.getUserById(userId).getEmailId());

                disbursementToSave = disbursementService.saveDisbursement(disbursementToSave);

                disbursementToSave = disbursementService.createAssignmentPlaceholders(disbursementToSave, userId);
                // disbursementToSave =
                // disbursementService.setWorkflowPermissions(disbursementToSave,userId);

                return disbursementToSave;
        }

        @PostMapping("/")
        public Disbursement saveDisbursement(@RequestHeader("X-TENANT-CODE") String tenantCode,
                        @PathVariable("userId") Long userId, @RequestBody Disbursement disbursementToSave) {

                Disbursement existingDisbursement = disbursementService.getDisbursementById(disbursementToSave.getId());
                existingDisbursement.setRequestedAmount(disbursementToSave.getRequestedAmount());
                existingDisbursement.setReason(disbursementToSave.getReason());
                existingDisbursement.setUpdatedAt(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());
                existingDisbursement.setUpdatedBy(userService.getUserById(userId).getEmailId());
                existingDisbursement = disbursementService.saveDisbursement(existingDisbursement);

                if (disbursementToSave.getActualDisbursements() != null
                                && disbursementToSave.getActualDisbursements().size() > 0) {
                        for (ActualDisbursement ad : disbursementToSave.getActualDisbursements()) {
                                ActualDisbursement existingActualDisbursement = disbursementService
                                                .getActualDisbursementById(ad.getId());
                                existingActualDisbursement.setActualAmount(ad.getActualAmount());
                                existingActualDisbursement.setCreatedAt(DateTime.now().toDate());
                                existingActualDisbursement.setCreatedBy(userId);
                                existingActualDisbursement.setDisbursementDate(ad.getDisbursementDate());
                                existingActualDisbursement.setNote(ad.getNote());
                                existingActualDisbursement.setUpdatedAt(
                                                DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());
                                existingActualDisbursement.setUpdatedBy(userService.getUserById(userId).getId());
                                if (existingActualDisbursement.getOrderPosition() == null) {
                                        existingActualDisbursement.setOrderPosition(disbursementService
                                                        .getNewOrderPositionForActualDisbursementOfGrant(
                                                                        disbursementToSave.getGrant().getId()));
                                }
                                existingActualDisbursement.setStatus(false);
                                existingActualDisbursement.setSaved(true);
                                existingActualDisbursement = disbursementService
                                                .saveActualDisbursement(existingActualDisbursement);
                        }
                }

                return disbursementService.disbursementToReturn(existingDisbursement, userId);

        }

        @GetMapping("/status/{status}")
        public List<Disbursement> getDisbursementsForUser(@PathVariable("userId") Long userId,
                        @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("status") String status) {
                User user = userService.getUserById(userId);
                Organization org = null;
                List<Disbursement> disbursements = new ArrayList<>();

                if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")) {
                        org = user.getOrganization();
                        disbursements = disbursementService.getDisbursementsForUserByStatus(user, org, status);
                } else if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")) {
                        org = user.getOrganization();
                        disbursements = disbursementService.getDisbursementsForUserByStatus(user, org, status);
                }

                if (disbursements != null) {
                        for (Disbursement d : disbursements) {
                                d = disbursementService.disbursementToReturn(d, userId);
                        }
                }
                return disbursements;
        }

        @DeleteMapping("/{disbursementId}/{status}")
        public List<Disbursement> deleteDisbursement(@RequestHeader("X-TENANT-CODE") String tenantCode,
                        @PathVariable("userId") Long userId, @PathVariable("disbursementId") Long disbursementId,
                        @PathVariable("status") String status) {
                Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);

                disbursementService.deleteAllAssignmentsForDisbursement(disbursement);
                disbursementService.deleteDisbursement(disbursement);

                return getDisbursementsForUser(userId, tenantCode, status);

        }

        @GetMapping("/{disbursementId}")
        public Disbursement getDisbursement(@RequestHeader("X-TENANT-CODE") String tenantCode,
                        @PathVariable("userId") Long userId, @PathVariable("disbursementId") Long disbursementId) {
                Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);

                return disbursementService.disbursementToReturn(disbursement, userId);

        }

        @PostMapping("/{disbursementId}/assignment")
        @ApiOperation("Set owners for disbursement workflow states")
        public Disbursement saveGrantAssignments(
                        @ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
                        @ApiParam(name = "disbursementId", value = "Unique identifier of the disbursement") @PathVariable("disbursementId") Long disbursementId,
                        @ApiParam(name = "assignmentModel", value = "Set assignment for disbursement per workflow state") @RequestBody DisbursementAssignmentModel assignmentModel,
                        @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
                Disbursement disbursement = saveDisbursement(tenantCode, userId, assignmentModel.getDisbursement());
                for (DisbursementAssignmentsVO assignmentsVO : assignmentModel.getAssignments()) {
                        DisbursementAssignment assignment = null;
                        if (assignmentsVO.getId() == null) {
                                assignment = new DisbursementAssignment();
                                assignment.setStateId(assignmentsVO.getStateId());
                                assignment.setDisbursementId(assignmentsVO.getDisbursementId());
                        } else {
                                assignment = disbursementService.getDisbursementAssignmentById(assignmentsVO.getId());
                        }

                        assignment.setOwner(assignmentsVO.getAssignmentId());

                        disbursementService.saveAssignmentForDisbursement(assignment);
                }

                disbursement = disbursementService.getDisbursementById(disbursementId);
                disbursement = disbursementService.disbursementToReturn(disbursement, userId);
                return disbursement;
        }

        @GetMapping("/{disbursementId}/changeHistory")
        public DisbursementSnapshot getReportHistory(@PathVariable("disbursementId") Long disbursementId,
                        @PathVariable("userId") Long userId) {
                Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);

                return disbursementSnapshotService.getSnapshotByDisbursementIdAndStatusId(disbursementId,
                                disbursement.getStatus().getId());
        }

        @PostMapping("/{disbursementId}/flow/{fromState}/{toState}")
        @ApiOperation("Move disbursement through workflow")
        public Disbursement MoveDisbursementState(@RequestBody DisbursementWithNote disbursementWithNote,
                        @ApiParam(name = "userId", value = "Unique identified of logged in user") @PathVariable("userId") Long userId,
                        @ApiParam(name = "disbursementId", value = "Unique identifier of the disbursement") @PathVariable("disbursementId") Long disbursementId,
                        @ApiParam(name = "fromStateId", value = "Unique identifier of the starting state of the disbursement in the workflow") @PathVariable("fromState") Long fromStateId,
                        @ApiParam(name = "toStateId", value = "Unique identifier of the ending state of the disbursement in the workflow") @PathVariable("toState") Long toStateId,
                        @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

                saveDisbursement(tenantCode, userId, disbursementWithNote.getDisbursement());

                Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);
                Disbursement finalDisbursement = disbursement;
                WorkflowStatus previousState = disbursement.getStatus();

                DisbursementAssignment currentAssignment = disbursementService.getDisbursementAssignments(disbursement)
                                .stream()
                                .filter(ass -> ass.getDisbursementId().longValue() == disbursementId.longValue()
                                                && ass.getStateId().longValue() == finalDisbursement.getStatus().getId()
                                                                .longValue())
                                .collect(Collectors.toList()).get(0);
                User previousOwner = userService.getUserById(currentAssignment.getOwner());

                disbursement.setStatus(workflowStatusService.findById(toStateId));
                if (disbursementWithNote.getNote() != null
                                && !disbursementWithNote.getNote().trim().equalsIgnoreCase("")) {
                        disbursement.setNote(disbursementWithNote.getNote());
                        disbursement.setNoteAdded(new Date());
                        disbursement.setNoteAddedBy(userService.getUserById(userId).getId());
                }
                Date currentDateTime = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate();
                disbursement.setUpdatedAt(currentDateTime);
                disbursement.setUpdatedBy(userService.getUserById(userId).getEmailId());
                disbursement.setMovedOn(currentDateTime);
                disbursement = disbursementService.saveDisbursement(disbursement);

                User user = userService.getUserById(userId);
                WorkflowStatus toStatus = workflowStatusService.findById(toStateId);

                List<User> usersToNotify = new ArrayList<>();

                List<DisbursementAssignment> assigments = disbursementService.getDisbursementAssignments(disbursement);
                assigments.forEach(ass -> {
                        if (!usersToNotify.stream().filter(u -> u.getId() == ass.getOwner()).findFirst().isPresent()) {
                                usersToNotify.add(userService.getUserById(ass.getOwner()));
                        }
                });

                Optional<DisbursementAssignment> disbAss = disbursementService.getDisbursementAssignments(disbursement)
                                .stream()
                                .filter(ass -> ass.getDisbursementId().longValue() == disbursementId.longValue()
                                                && ass.getStateId().longValue() == toStateId.longValue())
                                .findAny();
                User currentOwner = null;
                String currentOwnerName = "";
                if (disbAss.isPresent()) {
                        currentOwner = userService.getUserById(disbAss.get().getOwner());
                        currentOwnerName = currentOwner.getFirstName().concat(" ").concat(currentOwner.getLastName());
                }

                WorkflowStatusTransition transition = workflowStatusTransitionService
                                .findByFromAndToStates(previousState, toStatus);

                String emailNotificationContent[] = disbursementService.buildEmailNotificationContent(finalDisbursement,
                                user, user.getFirstName().concat(" ").concat(user.getLastName()), toStatus.getVerb(),
                                new SimpleDateFormat("dd-MMM-yyyy").format(DateTime.now().toDate()),
                                appConfigService.getAppConfigForGranterOrg(
                                                finalDisbursement.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.DISBURSEMENT_STATE_CHANGED_MAIL_SUBJECT)
                                                .getConfigValue(),
                                appConfigService.getAppConfigForGranterOrg(
                                                finalDisbursement.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.DISBURSEMENT_STATE_CHANGED_MAIL_MESSAGE)
                                                .getConfigValue(),
                                workflowStatusService.findById(toStateId).getName(),
                                currentOwner == null ? "-"
                                                : (currentOwner.getFirstName().concat(" ")
                                                                .concat(currentOwner.getLastName())),
                                previousState.getName(),
                                previousOwner == null ? " -"
                                                : previousOwner.getFirstName().concat(" ")
                                                                .concat(previousOwner.getLastName()),
                                transition.getAction(), "Yes", "Please review.",
                                disbursementWithNote.getNote() != null
                                                && !disbursementWithNote.getNote().trim().equalsIgnoreCase("") ? "Yes"
                                                                : "No",
                                disbursementWithNote.getNote() != null
                                                && !disbursementWithNote.getNote().trim().equalsIgnoreCase("")
                                                                ? "Please review."
                                                                : "",
                                "", null, null);
                String notificationContent[] = disbursementService.buildEmailNotificationContent(finalDisbursement,
                                user, user.getFirstName().concat(" ").concat(user.getLastName()), toStatus.getVerb(),
                                new SimpleDateFormat("dd-MMM-yyyy").format(DateTime.now().toDate()),
                                appConfigService.getAppConfigForGranterOrg(
                                                finalDisbursement.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.DISBURSEMENT_STATE_CHANGED_NOTIFICATION_SUBJECT)
                                                .getConfigValue(),
                                appConfigService.getAppConfigForGranterOrg(
                                                finalDisbursement.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.DISBURSEMENT_STATE_CHANGED_NOTIFICATION_MESSAGE)
                                                .getConfigValue(),
                                workflowStatusService.findById(toStateId).getName(),
                                currentOwner == null ? "-"
                                                : (currentOwner.getFirstName().concat(" ")
                                                                .concat(currentOwner.getLastName())),
                                previousState.getName(),
                                previousOwner == null ? " -"
                                                : previousOwner.getFirstName().concat(" ")
                                                                .concat(previousOwner.getLastName()),
                                transition.getAction(), "Yes", "Please review.",
                                disbursementWithNote.getNote() != null
                                                && !disbursementWithNote.getNote().trim().equalsIgnoreCase("") ? "Yes"
                                                                : "No",
                                disbursementWithNote.getNote() != null
                                                && !disbursementWithNote.getNote().trim().equalsIgnoreCase("")
                                                                ? "Please review."
                                                                : "",
                                "", null, null);
                final User finalCurrentOwner = currentOwner;
                if (!toStatus.getInternalStatus().equalsIgnoreCase("CLOSED")) {
                        usersToNotify.removeIf(u -> u.getId().longValue() == finalCurrentOwner.getId().longValue());

                        commonEmailSevice.sendMail(finalCurrentOwner.getEmailId(),
                                        usersToNotify.stream().map(mapper -> mapper.getEmailId())
                                                        .collect(Collectors.toList())
                                                        .toArray(new String[usersToNotify.size()]),
                                        emailNotificationContent[0], emailNotificationContent[1],
                                        new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(finalDisbursement.getGrant()
                                                                        .getGrantorOrganization().getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue() });
                        usersToNotify.stream().forEach(u -> notificationsService.saveNotification(notificationContent,
                                        u.getId(), finalDisbursement.getId(), "DISBURSEMENT"));
                        notificationsService.saveNotification(notificationContent, finalCurrentOwner.getId(),
                                        finalDisbursement.getId(), "DISBURSEMENT");

                } else {

                        WorkflowStatus activeStatus = workflowStatusService
                                        .getTenantWorkflowStatuses("DISBURSEMENT",
                                                        disbursement.getGrant().getGrantorOrganization().getId())
                                        .stream().filter(st -> st.getInternalStatus().equalsIgnoreCase("ACTIVE"))
                                        .findFirst().get();
                        User activeStatusOwner = userService.getUserById(disbursementService
                                        .getDisbursementAssignments(disbursement).stream()
                                        .filter(ass -> ass.getStateId().longValue() == activeStatus.getId().longValue())
                                        .findFirst().get().getOwner());
                        usersToNotify.removeIf(u -> u.getId().longValue() == activeStatusOwner.getId().longValue());

                        commonEmailSevice.sendMail(activeStatusOwner.getEmailId(),
                                        usersToNotify.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                                                        .toArray(new String[usersToNotify.size()]),
                                        emailNotificationContent[0], emailNotificationContent[1],
                                        new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(finalDisbursement.getGrant()
                                                                        .getGrantorOrganization().getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue() });
                        usersToNotify.stream().forEach(u -> notificationsService.saveNotification(notificationContent,
                                        u.getId(), finalDisbursement.getId(), "DISBURSEMENT"));
                        notificationsService.saveNotification(notificationContent, activeStatusOwner.getId(),
                                        finalDisbursement.getId(), "DISBURSEMENT");

                }

                if (toStatus.getInternalStatus().equalsIgnoreCase("ACTIVE")) {
                        disbursementService.createEmtptyActualDisbursement(disbursement);
                }

                disbursement = disbursementService.disbursementToReturn(disbursement, userId);
                _saveSnapShot(disbursement);

                return disbursement;

        }

        private void _saveSnapShot(Disbursement disbursement) {

                DisbursementSnapshot snapshot = new DisbursementSnapshot();
                snapshot.setStatusId(disbursement.getStatus().getId());
                snapshot.setDisbursementId(disbursement.getId());
                snapshot.setReason(disbursement.getReason());
                snapshot.setRequestedAmount(disbursement.getRequestedAmount());

                disbursementSnapshotService.saveSnapShot(snapshot);

        }

        @GetMapping("/{disbursementId}/history/")
        public List<DisbursementHistory> getDisbursementHistory(@PathVariable("disbursementId") Long disbursementId,
                        @PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode) {

                List<DisbursementHistory> history = null;
                User user = userService.getUserById(userId);
                if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")) {
                        history = disbursementService.getDisbursementHistory(disbursementId);
                }

                for (DisbursementHistory dh : history) {
                        dh.setNoteAddedByUser(userService.getUserById(dh.getNoteAddedBy()));
                }

                return history;
        }

        @GetMapping("/{disbursementId}/actual")
        public ActualDisbursement addNewActualDisbursement(@PathVariable("disbursementId") Long disbursementId,
                        @PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode) {
                Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);
                List<ActualDisbursement> actualDisbursements = disbursementService
                                .getActualDisbursementsForDisbursement(disbursement);
                ActualDisbursement actualDisbursement = null;
                if (actualDisbursements != null) {
                        actualDisbursement = new ActualDisbursement();
                        actualDisbursement.setDisbursementId(disbursement.getId());
                        actualDisbursement.setOrderPosition(
                                        disbursementService.getNewOrderPositionForActualDisbursementOfGrant(
                                                        disbursement.getGrant().getId()));
                        actualDisbursement.setStatus(false);
                        actualDisbursement.setSaved(true);
                        actualDisbursement = disbursementService.saveActualDisbursement(actualDisbursement);
                        actualDisbursements.add(actualDisbursement);
                }
                return actualDisbursement;
        }

        @DeleteMapping("/{disbursementId}/actual/{actualDisbursementId}")
        public void deleteActualDisbursement(@PathVariable("disbursementId") Long disbursementId,
                        @PathVariable("actualDisbursementId") Long actualDisbursementId,
                        @PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode) {

                ActualDisbursement actualDisbursement = disbursementService
                                .getActualDisbursementById(actualDisbursementId);
                disbursementService.deleteActualDisbursement(actualDisbursement);
        }

        @GetMapping("/resolve")
        public Disbursement resolveDisbursement(@PathVariable("userId") Long userId,
                        @RequestHeader("X-TENANT-CODE") String tenantCode, @RequestParam("d") String disbursementCode) {
                Long disbursementId = Long.valueOf(
                                new String(Base64.getDecoder().decode(disbursementCode), StandardCharsets.UTF_8));
                Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);

                disbursement = disbursementService.disbursementToReturn(disbursement, userId);
                return disbursement;
        }
}
