package org.codealpha.gmsservice.controllers;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.WorkflowTransitionModel;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.WorkflowTransitionModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/workflow/grant")
    public List<WorkflowTransitionModel> getGrantWorkflows(@RequestHeader("X-TENANT-CODE") String tenantCode) {
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);

        return workflowTransitionModelService.getWorkflowsByGranterAndType(tenantOrg.getId(),"GRANT");
    }
}
