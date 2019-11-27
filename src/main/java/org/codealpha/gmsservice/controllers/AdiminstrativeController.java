package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Workflow;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowTransitionModel;
import org.codealpha.gmsservice.models.WorkFlowDataModel;
import org.codealpha.gmsservice.models.WorkflowTransitionDataModel;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.WorkflowService;
import org.codealpha.gmsservice.services.WorkflowTransitionModelService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@ApiIgnore
public class AdiminstrativeController {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private WorkflowTransitionModelService workflowTransitionModelService;
    @Autowired
    private WorkflowService workflowService;

    @GetMapping("/workflow/grant/{grantId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for grant")
    public List<WorkflowTransitionModel> getGrantWorkflows(@ApiParam(name = "X-TENANT-CODE",value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,@ApiParam(name = "grantId",value = "Unique identifier of grant") @PathVariable("grantId") Long grantId,@ApiParam(name = "userId",value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);

        return workflowTransitionModelService.getWorkflowsByGranterAndType(tenantOrg.getId(),"GRANT");
    }

    @GetMapping("/workflow/report/{grantId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for report")
    public List<WorkflowTransitionModel> getReportWorkflows(@ApiParam(name = "X-TENANT-CODE",value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,@ApiParam(name = "grantId",value = "Unique identifier of grant") @PathVariable("grantId") Long grantId,@ApiParam(name = "userId",value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);

        return workflowTransitionModelService.getWorkflowsByGranterAndType(tenantOrg.getId(),"REPORT");
    }

    @PostMapping("/workflow")
    public void createBasicWorkflow(@RequestBody WorkFlowDataModel workFlowDataModel, @RequestHeader("X-TENANT-CODE") String tenantCode){
        Workflow workflow = new Workflow();
        workflow.setObject(WorkflowObject.valueOf(workFlowDataModel.getType()));
        workflow.setName(workFlowDataModel.getName());
        workflow.setGranter(organizationService.findOrganizationByTenantCode(tenantCode));
        workflow.setCreatedBy("System");
        workflow.setCreatedAt(DateTime.now().toDate());

        workflow = workflowService.saveWorkflow(workflow);

        Map<String, WorkflowStatus> statuses = new HashMap<>();
        int index =0;
        for(WorkflowTransitionDataModel transition : workFlowDataModel.getTransitions()){
            WorkflowStatus status = new WorkflowStatus();
            status.setWorkflow(workflow);
            status.setVerb(transition.getAction());
            if(_checkIfTerminal(transition,workFlowDataModel.getTransitions())) {
                status.setTerminal(true);
            }
        }


    }

    private boolean _checkIfTerminal(WorkflowTransitionDataModel transition, List<WorkflowTransitionDataModel> transitions) {
        transitions.stream().filter(t -> t.getFrom().equalsIgnoreCase(transition.getFrom()));
        return true;
    }
}
