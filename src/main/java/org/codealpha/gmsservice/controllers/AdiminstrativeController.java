package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.WorkFlowDataModel;
import org.codealpha.gmsservice.models.WorkflowTransitionDataModel;
import org.codealpha.gmsservice.models.templateVO;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
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
    @Autowired
    private GranterReportTemplateService reportTemplateService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;

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

    @PostMapping("/template")
    public void createTemplate(@RequestBody templateVO template,@RequestHeader("X-TENANT-CODE") String tenantCode){

        Organization org = organizationService.findOrganizationByTenantCode(tenantCode);
        switch (template.getType()){
            case "report":
                GranterReportTemplate reportTemplate = _extractReportTemplate(org,template);
                reportTemplateService.saveReportTemplate(reportTemplate);
        }


    }

    private GranterReportTemplate _extractReportTemplate(Organization granter ,templateVO template) {

        reportTemplateService.markAllAsNotDefault();
        GranterReportTemplate reportTemplate = new GranterReportTemplate();
        reportTemplate.setDefaultTemplate(template.is_default());
        reportTemplate.setDescription(template.getDescription());
        reportTemplate.setGranterId(granter.getId());
        reportTemplate.setName(template.getName());
        reportTemplate.setPrivateToReport(false);
        reportTemplate.setPublished(true);
        if(template.getSections()!=null){
            List<GranterReportSection> sectionsList = new ArrayList<>();
            template.getSections().forEach(s ->{
                GranterReportSection section = new GranterReportSection();
                section.setReportTemplate(reportTemplate);
                section.setDeletable(true);
                section.setGranter((Granter)granter);
                section.setSectionName(s.getName());
                section.setSectionOrder(s.getOrder());
                sectionsList.add(section);
                if(s.getAttributes()!=null){
                    List<GranterReportSectionAttribute> attributesList = new ArrayList<>();
                    s.getAttributes().forEach(a -> {
                        GranterReportSectionAttribute attribute = new GranterReportSectionAttribute();
                        attribute.setAttributeOrder(a.getOrder());
                        attribute.setDeletable(true);
                        if(a.getType().equalsIgnoreCase("table")) {
                            try {
                                attribute.setExtras(new ObjectMapper().writeValueAsString(a.getTableValue()));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                        attribute.setFieldName(a.getName());
                        attribute.setFieldType(a.getType());
                        attribute.setGranter((Granter)granter);
                        attribute.setRequired(false);
                        attribute.setSection(section);
                        attributesList.add(attribute);
                    });
                    section.setAttributes(attributesList);
                }

            });

            reportTemplate.setSections(sectionsList);
        }
        return reportTemplate;
    }

    @GetMapping("/user/{userId}/role")
    public List<Role> getRolesForOrg(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId){

        User user = userService.getUserById(userId);

        List<Role> roles = roleService.getByOrganization(user.getOrganization());
        for (Role role : roles) {
            List<UserRole> userRoles = userRoleService.findUsersForRole(role);
            if(userRoles!=null && userRoles.size()>0){
                role.setHasUsers(true);
                role.setLinkedUsers(userRoles.size());
            }
        }
        return roles;
    }
}
