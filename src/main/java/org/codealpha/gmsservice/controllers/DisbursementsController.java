package org.codealpha.gmsservice.controllers;

import org.codealpha.gmsservice.entities.Disbursement;
import org.codealpha.gmsservice.entities.DisbursementAssignment;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/active-grants")
    public List<Grant> getActiveGrantsOwnedByUser(@PathVariable("userId") Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode){
        List<Grant> ownerGrants = grantService.getGrantsOwnedByUserByStatus(userId,"ACTIVE");
        for (Grant ownerGrant : ownerGrants) {
            ownerGrant = grantService._grantToReturn(userId,ownerGrant);
        }

        return ownerGrants;
    }

    @PostMapping("/grant/{grantId}")
    public Disbursement createNewDisbursement(@PathVariable("userId")Long userId, @PathVariable("grantId")Long grantId,@RequestHeader("X-TENANT-CODE")String tenantCode,@RequestBody Disbursement disbursementToSave){

        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        disbursementToSave = new Disbursement();
        disbursementToSave.setGrant(grantService._grantToReturn(userId,grantService.getById(grantId)));
        disbursementToSave.setReason(null);
        disbursementToSave.setRequestedAmount(null);
        disbursementToSave.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("DISBURSEMENT",tenantOrg.getId()));

        disbursementToSave = disbursementService.saveDisbursement(disbursementToSave);

        disbursementToSave = disbursementService.createAssignmentPlaceholders(disbursementToSave, userId);
        //disbursementToSave = disbursementService.setWorkflowPermissions(disbursementToSave,userId);

        return disbursementToSave;
    }

    @PostMapping("/")
    public Disbursement saveDisbursement(@RequestHeader("X-TENANT-CODE")String tenantCode,@PathVariable("userId")Long userId, @RequestBody Disbursement disbursementToSave){

        Disbursement existingDisbursement = disbursementService.getDisbursementById(disbursementToSave.getId());
        existingDisbursement.setRequestedAmount(disbursementToSave.getRequestedAmount());
        existingDisbursement.setReason(disbursementToSave.getReason());
        existingDisbursement = disbursementService.saveDisbursement(existingDisbursement);
        return disbursementService.disbursementToReturn(existingDisbursement, userId);

    }

    @GetMapping("/")
    public List<Disbursement> getDisbursementsForUser(@PathVariable("userId")Long userId, @RequestHeader("X-TENANT-CODE")String tenantCode){
        User user = userService.getUserById(userId);
        Organization org = null;
        List<Disbursement> disbursements = new ArrayList<>();

        if(user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")){
            org = user.getOrganization();
            disbursements = disbursementService.getDisbursementsForUser(user,org);
        }else if(user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")){
            org = user.getOrganization();
            disbursements = disbursementService.getDisbursementsForUser(user,org);
        }
        return disbursements;
    }


    @DeleteMapping("/{disbursementId}")
    public List<Disbursement>  deleteDisbursement(@RequestHeader("X-TENANT-CODE")String tenantCode,@PathVariable("userId")Long userId,@PathVariable("disbursementId")Long disbursementId){
        Disbursement disbursement = disbursementService.getDisbursementById(disbursementId);
       
        disbursementService.deleteAllAssignmentsForDisbursement(disbursement);
        disbursementService.deleteDisbursement(disbursement);

        return getDisbursementsForUser(userId, tenantCode);

    }
}
