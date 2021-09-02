package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.WorkflowStatusRepository;
import org.codealpha.gmsservice.services.*;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/user/{userId}/disbursements")
public class DisbursementsController {

        private static Logger logger = LoggerFactory.getLogger(DisbursementsController.class);
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
        @Autowired
        private ReleaseService releaseService;
        @Autowired
        private WorkflowService workflowService;
        @Value("${spring.upload-file-location}")
        private String uploadLocation;
        @Autowired
        private ResourceLoader resourceLoader;

        @GetMapping("/active-grants")
        public List<Grant> getActiveGrantsOwnedByUser(@PathVariable("userId") Long userId,
                        @RequestHeader("X-TENANT-CODE") String tenantCode) {
                List<Grant> ownerGrants = grantService.getGrantsOwnedByUserByStatus(userId, "ACTIVE");
                List<Grant> grantsToReturn = new ArrayList<>();
                if (ownerGrants != null && ownerGrants.size() > 0) {
                        /*List<WorkflowStatus> workflowStatuses = workflowStatusRepository.getAllTenantStatuses(
                                        "DISBURSEMENT", ownerGrants.get(0).getGrantorOrganization().getId());

                        workflowStatusService.findInitialStatusByObjectAndGranterOrgId()
                        List<WorkflowStatus> activeAndClosedStatuses = workflowStatuses.stream()
                                        .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("CLOSED"))
                                        .collect(Collectors.toList());
                        List<Long> statusIds = activeAndClosedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                                        .collect(Collectors.toList());

                        List<WorkflowStatus> draftAndReviewStatuses = workflowStatuses.stream()
                                        .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("DRAFT")
                                                        || ws.getInternalStatus().equalsIgnoreCase("REVIEW"))
                                        .collect(Collectors.toList());
                        List<Long> draftAndReviewStatusIds = draftAndReviewStatuses.stream().mapToLong(s -> s.getId())
                                        .boxed().collect(Collectors.toList());*/

                        for (Grant g : ownerGrants) {
                                Double total = 0d;

                                List<Long> statusIds=workflowStatusService.findByWorkflow(workflowService.findWorkflowByGrantTypeAndObject(g.getGrantTypeId(),"DISBURSEMENT")).stream()
                                        .filter(st ->st.getInternalStatus().equalsIgnoreCase("CLOSED"))
                                        .mapToLong(s -> s.getId()).boxed()
                                        .collect(Collectors.toList());
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

                        for (Grant ownerGrant : grantsToReturn) {
                                List<Long> draftAndReviewStatusIds = workflowStatusRepository.findByWorkflow(workflowService.findWorkflowByGrantTypeAndObject(ownerGrant.getGrantTypeId(),"DISBURSEMENT"))
                                        .stream()
                                        .filter(st -> (st.getInternalStatus().equalsIgnoreCase("DRAFT") || st.getInternalStatus().equalsIgnoreCase("REVIEW")))
                                        .mapToLong(s->s.getId()).boxed()
                                        .collect(Collectors.toList());
                                List<Disbursement> draftAndReviewDisbursements = disbursementService
                                                .getDibursementsForGrantByStatuses(ownerGrant.getId(),
                                                                draftAndReviewStatusIds);
                                draftAndReviewDisbursements.removeIf(d -> d.isGranteeEntry());
                                if (draftAndReviewDisbursements != null && draftAndReviewDisbursements.size() > 0) {
                                        ownerGrant.setHasOngoingDisbursement(true);

                                }
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
                Grant grant = grantService._grantToReturn(userId, grantService.getById(grantId));
                disbursementToSave.setGrant(grant);
                disbursementToSave.setReason(null);
                disbursementToSave.setRequestedAmount(null);
                disbursementToSave.setGranteeEntry(false);
                disbursementToSave.setStatus(workflowStatusService
                                .findInitialStatusByObjectAndGranterOrgId("DISBURSEMENT", tenantOrg.getId(),grant.getGrantTypeId()));
                disbursementToSave.setCreatedAt(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());
                disbursementToSave.setCreatedBy(userService.getUserById(userId).getEmailId());

                disbursementToSave = disbursementService.saveDisbursement(disbursementToSave);

                disbursementToSave = disbursementService.createAssignmentPlaceholders(disbursementToSave, userId);


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

                List<ActualDisbursement> existingActualDisbursements = new ArrayList<>();
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
                                existingActualDisbursements.add(existingActualDisbursement);
                        }
                }

                existingDisbursement.setActualDisbursements(existingActualDisbursements);

                return disbursementService.disbursementToReturn(existingDisbursement, userId);

        }

        @GetMapping("/status/{status}")
        public List<Disbursement> getDisbursementsForUser(@PathVariable("userId") Long userId,
                        @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("status") String status) {
                User user = userService.getUserById(userId);
                Organization org = org = user.getOrganization();;
                List<Disbursement> disbursements = new ArrayList<>();

                if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")) {
                        disbursements = disbursementService.getDisbursementsForUserByStatus(user, org, status);
                } else if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")) {
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

                Map<Long, Long> currentAssignments = new LinkedHashMap();

                if (disbursementService.checkIfDisbursementMovedThroughWFAtleastOnce(
                                assignmentModel.getDisbursement().getId())) {
                        disbursementService
                                        .getDisbursementAssignments(disbursementService
                                                        .getDisbursementById(assignmentModel.getDisbursement().getId()))
                                        .stream().forEach(a -> {
                                                currentAssignments.put(a.getStateId(), a.getOwner());
                                        });
                }

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
                        assignment.setUpdatedBy(userId);
                        assignment.setAssignedOn(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());

                        disbursementService.saveAssignmentForDisbursement(assignment);
                }

                if (currentAssignments.size() > 0) {

                        List<DisbursementAssignment> newAssignments = disbursementService
                                        .getDisbursementAssignments(disbursement);
                        String[] notifications = disbursementService.buildEmailNotificationContent(disbursement,
                                        userService.getUserById(userId), null, null, null,
                                        appConfigService.getAppConfigForGranterOrg(
                                                        disbursement.getGrant().getGrantorOrganization().getId(),
                                                        AppConfiguration.OWNERSHIP_CHANGED_EMAIL_SUBJECT)
                                                        .getConfigValue(),
                                        appConfigService.getAppConfigForGranterOrg(
                                                        disbursement.getGrant().getGrantorOrganization().getId(),
                                                        AppConfiguration.OWNERSHIP_CHANGED_EMAIL_MESSAGE)
                                                        .getConfigValue(),
                                        null, null, null, null, null, null, null, null, null, null, null, null,
                                        currentAssignments, newAssignments);
                        List<User> toUsers = newAssignments.stream().map(a -> a.getOwner())
                                        .map(uid -> userService.getUserById(uid)).collect(Collectors.toList());
                        toUsers.removeIf(u -> u.isDeleted());

                        List<User> ccUsers = currentAssignments.values().stream()
                                        .map(uid -> userService.getUserById(uid)).collect(Collectors.toList());
                        ccUsers.removeIf(u -> u.isDeleted());
                        commonEmailSevice.sendMail(
                                        toUsers.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                                                        .toArray(new String[toUsers.size()]),
                                        ccUsers.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                                                        .toArray(new String[ccUsers.size()]),
                                        notifications[0], notifications[1],
                                        new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                        disbursement.getGrant().getGrantorOrganization()
                                                                                        .getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replaceAll("%RELEASE_VERSION%", releaseService
                                                                        .getCurrentRelease().getVersion()) });

                        Map<Long, Long> cleanAsigneesList = new HashMap();
                        for (Long ass : currentAssignments.values()) {
                                cleanAsigneesList.put(ass, ass);
                        }
                        for (DisbursementAssignment ass : newAssignments) {
                                cleanAsigneesList.put(ass.getOwner(), ass.getOwner());
                        }
                        final String[] finaNotifications = disbursementService.buildEmailNotificationContent(
                                        disbursement, userService.getUserById(userId), null, null, null,
                                        appConfigService.getAppConfigForGranterOrg(
                                                        disbursement.getGrant().getGrantorOrganization().getId(),
                                                        AppConfiguration.OWNERSHIP_CHANGED_EMAIL_SUBJECT)
                                                        .getConfigValue(),
                                        appConfigService.getAppConfigForGranterOrg(
                                                        disbursement.getGrant().getGrantorOrganization().getId(),
                                                        AppConfiguration.OWNERSHIP_CHANGED_EMAIL_MESSAGE)
                                                        .getConfigValue(),
                                        null, null, null, null, null, null, null, null, null, null, null, null,
                                        currentAssignments, newAssignments);

                        final Disbursement finalDisbursement = disbursement;

                        cleanAsigneesList.keySet().stream()
                                        .forEach(u -> notificationsService.saveNotification(finaNotifications, u,
                                                        finalDisbursement.getId(), "DISBURSEMENT"));

                }

                disbursement = disbursementService.getDisbursementById(disbursementId);
                disbursement = disbursementService.disbursementToReturn(disbursement, userId);
                return disbursement;
        }

        @GetMapping("/{disbursementId}/changeHistory")
        public PlainDisbursement getReportHistory(@PathVariable("disbursementId") Long disbursementId,
                        @PathVariable("userId") Long userId) {

                Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);
                DisbursementSnapshot snapshot = disbursementSnapshotService.getMostRecentSnapshotByDisbursementId(disbursementId);

                if(snapshot==null){
                        return null;
                }

                disbursement.setRequestedAmount(snapshot.getRequestedAmount());
                disbursement.setReason(snapshot.getReason());

                PlainDisbursement disbursementToReturn = disbursementService.disbursementToPlain(disbursement);
                return disbursementToReturn;
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

                disbursement.setNote((disbursementWithNote.getNote() != null
                                && !disbursementWithNote.getNote().trim().equalsIgnoreCase(""))
                                                ? disbursementWithNote.getNote()
                                                : "No note added");
                disbursement.setNoteAdded(new Date());
                disbursement.setNoteAddedBy(userService.getUserById(userId).getId());

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
                                "", null, null, null, null);
                String notificationContent[] = disbursementService.buildEmailNotificationContent(finalDisbursement,
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
                                "", null, null, null, null);
                final User finalCurrentOwner = currentOwner;
                if (!toStatus.getInternalStatus().equalsIgnoreCase("CLOSED")) {
                        usersToNotify.removeIf(u -> u.getId().longValue() == finalCurrentOwner.getId().longValue()
                                        || u.isDeleted());

                        commonEmailSevice.sendMail(
                                        new String[] { !finalCurrentOwner.isDeleted() ? finalCurrentOwner.getEmailId()
                                                        : null },
                                        usersToNotify.stream().map(mapper -> mapper.getEmailId())
                                                        .collect(Collectors.toList())
                                                        .toArray(new String[usersToNotify.size()]),
                                        emailNotificationContent[0], emailNotificationContent[1],
                                        new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(finalDisbursement.getGrant()
                                                                        .getGrantorOrganization().getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replaceAll("%RELEASE_VERSION%", releaseService
                                                                        .getCurrentRelease().getVersion()) });
                        usersToNotify.stream().forEach(u -> notificationsService.saveNotification(notificationContent,
                                        u.getId(), finalDisbursement.getId(), "DISBURSEMENT"));
                        notificationsService.saveNotification(notificationContent, finalCurrentOwner.getId(),
                                        finalDisbursement.getId(), "DISBURSEMENT");

                } else {
                        WorkflowStatus activeStatus = workflowStatusService.findById(fromStateId);
                        User activeStatusOwner = userService.getUserById(disbursementService
                                        .getDisbursementAssignments(disbursement).stream()
                                        .filter(ass -> ass.getStateId().longValue() == activeStatus.getId().longValue())
                                        .findFirst().get().getOwner());
                        usersToNotify.removeIf(u -> u.getId().longValue() == activeStatusOwner.getId().longValue()
                                        || u.isDeleted());

                        commonEmailSevice.sendMail(
                                        new String[] { !activeStatusOwner.isDeleted() ? activeStatusOwner.getEmailId()
                                                        : null },
                                        usersToNotify.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                                                        .toArray(new String[usersToNotify.size()]),
                                        emailNotificationContent[0], emailNotificationContent[1],
                                        new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(finalDisbursement.getGrant()
                                                                        .getGrantorOrganization().getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replaceAll("%RELEASE_VERSION%", releaseService
                                                                        .getCurrentRelease().getVersion()) });
                        usersToNotify.stream().forEach(u -> notificationsService.saveNotification(notificationContent,
                                        u.getId(), finalDisbursement.getId(), "DISBURSEMENT"));
                        notificationsService.saveNotification(notificationContent, activeStatusOwner.getId(),
                                        finalDisbursement.getId(), "DISBURSEMENT");

                }

                if (toStatus.getInternalStatus().equalsIgnoreCase("ACTIVE")) {
                        disbursementService.createEmtptyActualDisbursement(disbursement);
                }

                disbursement = disbursementService.disbursementToReturn(disbursement, userId);
                _saveSnapShot(disbursement, fromStateId, toStateId, currentOwner, previousOwner);

                return disbursement;

        }

        private void _saveSnapShot(Disbursement disbursement, Long fromStateId, Long toStateId, User currentUser,
                        User previousUser) {

                DisbursementSnapshot snapshot = new DisbursementSnapshot();
                snapshot.setStatusId(fromStateId);
                snapshot.setDisbursementId(disbursement.getId());
                snapshot.setReason(disbursement.getReason());
                snapshot.setRequestedAmount(disbursement.getRequestedAmount());
                snapshot.setFromNote(disbursement.getNote());
                snapshot.setFromStateId(fromStateId);
                snapshot.setToStateId(toStateId);
                snapshot.setAssignedToId(currentUser==null?null:currentUser.getId());
                snapshot.setMovedBy(previousUser.getId());
                snapshot.setMovedOn(disbursement.getMovedOn());

                disbursementSnapshotService.saveSnapShot(snapshot);

        }

        @GetMapping("/{disbursementId}/history/")
        public List<DisbursementHistory> getDisbursementHistory(@PathVariable("disbursementId") Long disbursementId,
                        @PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode) {

                /*List<DisbursementHistory> history = null;
                User user = userService.getUserById(userId);
                if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")) {
                        history = disbursementService.getDisbursementHistory(disbursementId);
                }

                for (DisbursementHistory dh : history) {
                        dh.setNoteAddedByUser(userService.getUserById(dh.getNoteAddedBy()));
                        dh.setStatus(workflowStatusService.findById(dh.getStatusId()));
                }

                return history;*/

                List<DisbursementHistory> history = new ArrayList();
                List<DisbursementSnapshot> disbursementSnapshotHistory = disbursementSnapshotService.getDisbursementSnapshotForDisbursement(disbursementId);
                if (disbursementSnapshotHistory == null
                        || (disbursementSnapshotHistory != null && disbursementSnapshotHistory.get(0).getFromStateId() == null)) {
                        history = disbursementService.getDisbursementHistory(disbursementId);
                        for (DisbursementHistory historyEntry : history) {
                                historyEntry.setNoteAddedByUser(userService.getUserById(historyEntry.getNoteAddedBy()));
                        }
                } else {
                        for (DisbursementSnapshot snapShot : disbursementSnapshotHistory) {
                                DisbursementHistory hist = new DisbursementHistory();
                                //hist.set(snapShot.);
                                hist.setId(snapShot.getDisbursementId());
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

        @PostMapping("/grant/{grantId}/report/{reportId}/record")
        public TableData recordNewActualDisbursement(@PathVariable("grantId") Long grantId,
                        @PathVariable("reportId") Long reportId, @PathVariable("userId") Long userId,
                        @RequestHeader("X-TENANT-CODE") String tenantCode) {

                Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
                Disbursement disbursementToSave = new Disbursement();
                Grant grant = grantService._grantToReturn(userId, grantService.getById(grantId));
                disbursementToSave.setGrant(grant);
                disbursementToSave.setReason(null);
                disbursementToSave.setRequestedAmount(null);
                disbursementToSave.setGranteeEntry(true);
                disbursementToSave.setReportId(reportId);
                disbursementToSave.setMovedOn(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());
                disbursementToSave.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId(
                                "DISBURSEMENT", grant.getGrantorOrganization().getId(),grant.getGrantTypeId()));
                disbursementToSave.setCreatedAt(DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate());
                disbursementToSave.setCreatedBy(userService.getUserById(userId).getEmailId());

                disbursementToSave = disbursementService.saveDisbursement(disbursementToSave);
                ActualDisbursement ad = addNewActualDisbursement(disbursementToSave.getId(), userId, tenantCode);
                ad.setStatus(true);
                ad.setSaved(false);

                List<ActualDisbursement> existingActualDisbursements = disbursementService
                                .getActualDisbursementsForDisbursement(
                                                disbursementService.getDisbursementById(disbursementToSave.getId()));
                int index = (existingActualDisbursements != null && existingActualDisbursements.size() > 0)
                                ? existingActualDisbursements.size()
                                : 0;
                TableData td = new TableData();
                ColumnData[] colDataList = new ColumnData[4];
                td.setName(String.valueOf(index));
                td.setHeader("#");
                td.setStatus(ad.getStatus());
                td.setSaved(ad.getSaved());
                td.setActualDisbursementId(ad.getId());
                td.setDisbursementId(ad.getDisbursementId());
                td.setReportId(reportId);
                if (disbursementToSave.isGranteeEntry()) {
                        td.setEnteredByGrantee(true);
                }
                ColumnData cdDate = new ColumnData();
                cdDate.setDataType("date");
                cdDate.setName("Disbursement Date");
                cdDate.setValue(null);

                ColumnData cdDA = new ColumnData();
                cdDA.setDataType("currency");
                cdDA.setName("Actual Disbursement");
                cdDA.setValue(null);

                ColumnData cdFOS = new ColumnData();
                cdFOS.setDataType("currency");
                cdFOS.setName("Funds from Other Sources");
                cdFOS.setValue(null);

                ColumnData cdN = new ColumnData();
                cdN.setName("Notes");
                cdN.setValue(ad.getNote());

                colDataList[0] = cdDate;
                colDataList[1] = cdDA;
                colDataList[2] = cdFOS;
                colDataList[3] = cdN;
                td.setColumns(colDataList);
                return td;
        }

        @DeleteMapping("/remove/{actualDisbursementId}")
        public void recordNewActualDisbursement(@PathVariable("userId") Long userId,
                        @RequestHeader("X-TENANT-CODE") String tenantCode,
                        @PathVariable("actualDisbursementId") Long actualDisbursementId) {

                ActualDisbursement actualDisbursement = disbursementService
                                .getActualDisbursementById(actualDisbursementId);
                disbursementService.deleteActualDisbursement(actualDisbursement);
                disbursementService.deleteDisbursement(
                                disbursementService.getDisbursementById(actualDisbursement.getDisbursementId()));
        }

        @PostMapping(value = "/{disbursementId}/documents/upload", consumes = { "multipart/form-data" })
        public List<DisbursementDocument> saveUploadedFiles(

                @PathVariable("userId") Long userId,
                @ApiParam(name = "grantId", value = "Unique identifier of the grant") @PathVariable("disbursementId") Long disbursementId,
                @RequestParam("file") MultipartFile[] files,
                @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

                Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);
                String filePath = uploadLocation + tenantCode + "/disbursement-documents/" + disbursement.getGrant().getId() + "/" + disbursementId + "/";
                File dir = new File(filePath);
                dir.mkdirs();
                List<DisbursementDocument> attachments = new ArrayList();
                for (MultipartFile file : files) {
                        try {
                                String fileName = file.getOriginalFilename();

                                File fileToCreate = new File(dir, fileName);
                                FileOutputStream fos = new FileOutputStream(fileToCreate);
                                fos.write(file.getBytes());
                                fos.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        DisbursementDocument attachment = new DisbursementDocument();
                        attachment.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
                        attachment.setName(file.getOriginalFilename()
                                .replace("." + FilenameUtils.getExtension(file.getOriginalFilename()), ""));
                        attachment.setLocation(filePath + file.getOriginalFilename());
                        attachment.setUploadedOn(new Date());
                        attachment.setUploadedBy(userId);
                        attachment.setDisbursementId(disbursementId);
                        attachment = disbursementService.saveDisbursementDocument(attachment);
                        attachments.add(attachment);
                }

                //updateDisbursementDocuments(disbursementId,tenantCode,userId);

                return attachments;
        }

        @PostMapping(value = "/{disbursementId}/documents/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
        public byte[] downloadProjectDocuments(@PathVariable("userId") Long userId, @PathVariable("disbursementId") Long disbursementId,
                                               @RequestHeader("X-TENANT-CODE") String tenantCode, @RequestBody AttachmentDownloadRequest downloadRequest,
                                               HttpServletResponse response) throws IOException {

                ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
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

                ArrayList<File> files = new ArrayList<>();
                //files.add(new File("README.md"));

                // packing files
                for (Long attachmentId : downloadRequest.getAttachmentIds()) {
                        DisbursementDocument attachment = disbursementService.getDisbursementDocumentById(attachmentId);

                        File file = resourceLoader.getResource("file:" + attachment.getLocation()).getFile();
                        // new zip entry and copying inputstream with file to zipOutputStream, after all
                        // closing streams
                        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                        FileInputStream fileInputStream = new FileInputStream(file);

                        IOUtils.copy(fileInputStream, zipOutputStream);

                        fileInputStream.close();
                        zipOutputStream.closeEntry();
                }

                if (zipOutputStream != null) {
                        zipOutputStream.finish();
                        zipOutputStream.flush();
                        IOUtils.closeQuietly(zipOutputStream);
                }
                IOUtils.closeQuietly(bufferedOutputStream);
                IOUtils.closeQuietly(byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
        }

        @DeleteMapping(value = "/{disbursementId}/document/{documentId}")
        public void deleteDisbursementDocuments(@PathVariable("userId") Long userId, @PathVariable("disbursementId") Long disbursementId,
                                             @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("documentId") Long attachmentId) {

                DisbursementDocument doc = disbursementService.getDisbursementDocumentById(attachmentId);
                File file = new File(doc.getLocation());
                disbursementService.deleteDisbursementDocument(doc);
                file.delete();

                //updateProjectDocuments(grantId,tenantCode,userId);

        }

        @GetMapping("/{disbursementId}/file/{fileId}")
        @ApiOperation(value = "Get file for download")
        public ResponseEntity<Resource> getFileForDownload(HttpServletResponse servletResponse,
                                                           @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("disbursementId") Long disbursementId,
                                                           @PathVariable("fileId") Long fileId) {

                DisbursementDocument attachment = disbursementService.getDisbursementDocumentById(fileId);
                String filePath = attachment.getLocation();

                try {
                        File file = resourceLoader.getResource("file:" + filePath).getFile();
                        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

                        HttpHeaders headers = new HttpHeaders();
                        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=" + attachment.getName());
                        servletResponse.setHeader("filename", attachment.getName() );
                        return ResponseEntity.ok().headers(headers).contentLength(file.length())
                                .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
                        // StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());
                } catch (IOException ex) {
                        logger.error(ex.getMessage(), ex);
                }
                return null;
        }

        @GetMapping(value = "/compare/{currentDisbursementId}/{origDisbursementId}")
        public List<PlainDisbursement> getReportsToCompare(@RequestHeader("X-TENANT-CODE")String tenantCode,
                                                     @PathVariable("userId")Long userId,
                                                     @PathVariable("currentDisbursementId")Long currentDisbursementId,
                                                     @PathVariable("origDisbursementId")Long origDisbursementId){

                List<PlainDisbursement> disbursementsToReturn = new ArrayList<>();

                Disbursement currentDisbursement = disbursementService.getDisbursementById(currentDisbursementId);
                currentDisbursement = disbursementService.disbursementToReturn(currentDisbursement,userId);

                Disbursement origDisbursement = disbursementService.getDisbursementById(origDisbursementId);
                origDisbursement = disbursementService.disbursementToReturn(origDisbursement,userId);

                try {
                        disbursementsToReturn.add(disbursementService.disbursementToPlain(currentDisbursement));
                        disbursementsToReturn.add(disbursementService.disbursementToPlain(origDisbursement));
                }catch (Exception e){
                        logger.error(e.getMessage(),e);
                }
                return disbursementsToReturn;
        }

        @GetMapping(value = "/compare/{currentDisbursementId}")
        public PlainDisbursement getPlainGrantForCompare(@RequestHeader("X-TENANT-CODE")String tenantCode,
                                                   @PathVariable("userId")Long userId,
                                                   @PathVariable("currentDisbursementId")Long currentDisbursementId) throws IOException {
                Disbursement currentDisbursement = disbursementService.getDisbursementById(currentDisbursementId);
                currentDisbursement = disbursementService.disbursementToReturn(currentDisbursement,userId);



                return disbursementService.disbursementToPlain(currentDisbursement);
        }
}
