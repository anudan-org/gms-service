package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.repositories.DisbursementAssignmentRepository;
import org.codealpha.gmsservice.repositories.DisbursementRepository;
import org.codealpha.gmsservice.repositories.WorkflowPermissionRepository;
import org.codealpha.gmsservice.repositories.WorkflowStatusTransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public Disbursement saveDisbursement(Disbursement disbursement){
        return disbursementRepository.save(disbursement);
    }

    public Disbursement createAssignmentPlaceholders(Disbursement disbursementToSave, Long userId) {
        Workflow currentWorkflow = disbursementToSave.getStatus().getWorkflow();

        List<WorkflowStatus> statuses = new ArrayList<>();
        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionRepository.findByWorkflow(currentWorkflow);
        for (WorkflowStatusTransition supportedTransition : supportedTransitions) {
            if(!statuses.stream().filter(s -> Long.valueOf(s.getId())==Long.valueOf(supportedTransition.getFromState().getId())).findAny().isPresent()) {
                statuses.add(supportedTransition.getFromState());
            }
            if(!statuses.stream().filter(s -> Long.valueOf(s.getId())==Long.valueOf(supportedTransition.getToState().getId())).findAny().isPresent()) {
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
        disbursementToSave = disbursementToReturn(disbursementToSave,userId);
        return disbursementToSave;
    }

    public Disbursement disbursementToReturn(Disbursement disbursement,Long userId){
        List<WorkFlowPermission> permissions = workflowPermissionRepository.getPermissionsForDisbursementFlow(disbursement.getStatus().getId(),userId,disbursement.getId());
        
        disbursement.setFlowPermissions(permissions);
        disbursement.setAssignments(disbursementAssignmentRepository.findByDisbursementId(disbursement.getId()));

        GrantVO vo = new GrantVO().build(disbursement.getGrant(), grantService.getGrantSections(disbursement.getGrant()), workflowPermissionService, userService.getUserById(userId) , appConfigService.getAppConfigForGranterOrg(disbursement.getGrant().getGrantorOrganization().getId(),
        AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS), userService);

        disbursement.getGrant().setGrantDetails(vo.getGrantDetails());
        return disbursement;
    }

    public List<Disbursement> getDisbursementsForUser(User user,Organization org) {
       List<Disbursement> disbursements = new ArrayList<>();
       disbursements = disbursementRepository.getDisbursementsForUser(user.getId(),org.getId());
 
       for(Disbursement d : disbursements){
           d = disbursementToReturn(d, user.getId());
       }
        return disbursements;
    }

	public Disbursement getDisbursementById(Long id) {
		return disbursementRepository.findById(id).get();
    }

    public List<DisbursementAssignment> getDisbursementAssignments(Disbursement disbursement){
        return disbursementAssignmentRepository.findByDisbursementId(disbursement.getId());
    }
    
    public void deleteAllAssignmentsForDisbursement(Disbursement disbursement){
        
        disbursementAssignmentRepository.deleteAll(getDisbursementAssignments(disbursement));
    }

    public void deleteDisbursement(Disbursement disbursement){
        disbursementRepository.delete(disbursement);
    }
}
