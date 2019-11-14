package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.WorkflowTransitionModel;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.WorkflowTransitionModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/admin")
@ApiIgnore
public class AdiminstrativeController {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private WorkflowTransitionModelService workflowTransitionModelService;

    @GetMapping("/workflow/grant/{grantId}/user/{userId}")
    @ApiOperation(value = "Get workflow assignments for grant")
    public List<WorkflowTransitionModel> getGrantWorkflows(@ApiParam(name = "X-TENANT-CODE",value = "Tenant code header") @RequestHeader("X-TENANT-CODE") String tenantCode,@ApiParam(name = "grantId",value = "Unique identifier of grant") @PathVariable("grantId") Long grantId,@ApiParam(name = "userId",value = "Unique identifier of user") @PathVariable("userId") Long userId) {
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);

        return workflowTransitionModelService.getWorkflowsByGranterAndType(tenantOrg.getId(),"GRANT");
    }
}
