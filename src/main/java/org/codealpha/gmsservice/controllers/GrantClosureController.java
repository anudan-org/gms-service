package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.ColumnData;
import org.codealpha.gmsservice.models.TableData;
import org.codealpha.gmsservice.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/{userId}/closure")
public class GrantClosureController {

    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private GrantClosureService closureService;
    @Autowired
    private WorkflowStatusTransitionService workflowStatusTransitionService;
    @Autowired
    private WorkflowService workflowService;

    @GetMapping("/templates")
    @ApiOperation("Get all published closure templates for tenant")
    public List<GranterClosureTemplate> getTenantPublishedReportTemplates(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {
        return closureService.findTemplatesAndPublishedStatusAndPrivateStatus(
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), true, false);
    }

    public Report createReport(
            @ApiParam(name = "grantId", value = "Unique identifier for the selected grant") @PathVariable("grantId") Long grantId,
            @ApiParam(name = "templateId", value = "Unique identifier for the selected template") @PathVariable("templateId") Long templateId,
            @PathVariable("userId") Long userId,
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {

        Organization granterOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        GranterClosureTemplate closureTemplate = closureService.findByTemplateId(templateId);

        Grant grant = grantService.getById(grantId);
        GrantClosure closure = new GrantClosure();
        closure.setCreateBy(userId);
        closure.setCreatedAt(new Date());
        closure.setGrant(grant);
        closure.setTemplateId(templateId);
        closure.setStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("GRANTCLOSURE",
                organizationService.findOrganizationByTenantCode(tenantCode).getId(),grant.getGrantTypeId()));

        List<WorkflowStatusTransition> supportedTransitions = workflowStatusTransitionService
                .getStatusTransitionsForWorkflow(
                        workflowService.findDefaultByGranterAndObjectAndType(granterOrg, "GRANTCLOSURE",grant.getGrantTypeId()));

        List<WorkflowStatus> statuses = new ArrayList<>();
        ClosureAssignments assignment = null;
        for (WorkflowStatusTransition supportedTransition : supportedTransitions) {
            if (!statuses.stream()
                    .filter(s -> s.getId().longValue() == supportedTransition.getFromState().getId().longValue())
                    .findFirst().isPresent()) {
                statuses.add(supportedTransition.getFromState());
            }
            if (!statuses.stream()
                    .filter(s -> s.getId().longValue() == supportedTransition.getToState().getId().longValue())
                    .findFirst().isPresent()) {
                statuses.add(supportedTransition.getToState());
            }
        }
        for (WorkflowStatus status : statuses) {

            assignment = new ClosureAssignments();
            if (status.isInitial()) {
                assignment.setAnchor(true);
                assignment.setAssignment(userId);
            } else {
                assignment.setAnchor(false);
            }
            assignment.setGrant(grant);
            assignment.setStateId(status.getId());

            /*if(status.getTerminal()){
                final Grant finalGrant = grant;
                GrantAssignments activeStateOwner =  grantService.getGrantWorkflowAssignments(report.getGrant()).stream().filter(ass -> ass.getStateId().longValue()==finalReport.getGrant().getGrantStatus().getId().longValue()).findFirst().get();
                assignment.setAssignment(activeStateOwner.getAssignments());
            }*/
            closureService.saveAssignmentForClosure(assignment);

        }

        List<GranterClosureSection> granterClosureSections = closureTemplate.getSections();
        closure.setStringAttributes(new ArrayList<>());
        AtomicBoolean closureTemplateHasDisbursement = new AtomicBoolean(false);
        AtomicReference<ClosureStringAttribute> disbursementAttributeValue = new AtomicReference<>(new ClosureStringAttribute());


        /*if(!granterClosureSections.stream().filter(rs -> rs.getSectionName().equalsIgnoreCase("Project Indicators")).findFirst().isPresent()){
            GranterClosureSection indicatorSection = new GranterClosureSection();
            indicatorSection.setClosureTemplate(closureTemplate);
            indicatorSection.setDeletable(true);
            indicatorSection.setGranter((Granter)granterOrg);
            indicatorSection.setSectionName("Project Indicators");
            indicatorSection.setSectionOrder(granterClosureSections.size());
            granterClosureSections.add(indicatorSection);
        }*/
        for (GranterClosureSection closureSection : granterClosureSections) {
            ClosureSpecificSection specificSection = new ClosureSpecificSection();
            specificSection.setDeletable(true);
            specificSection.setGranter((Granter) organizationService.findOrganizationByTenantCode(tenantCode));
            specificSection.setClosureId(closure.getId());
            specificSection.setClosureTemplateId(closureTemplate.getId());
            specificSection.setSectionName(closureSection.getSectionName());
            specificSection.setSectionOrder(closureSection.getSectionOrder());

            specificSection = closureService.saveClosureSpecificSection(specificSection);
            ClosureSpecificSection finalSpecificSection = specificSection;
            GrantClosure finalClosure = closure;
            final AtomicInteger[] attribVOOrder = { new AtomicInteger(1) };
            GrantClosure finalClosure1 = closure;

            /*if (specificSection.getSectionName().equalsIgnoreCase("Project Indicators")) {
                for(Map<DatePeriod, PeriodAttribWithLabel> hold: getPeriodsWithAttributes(report.getGrant(),userId)){
                    hold.forEach((entry, val) -> {
                        val.getAttributes().forEach(attribVo -> {
                            ReportSpecificSectionAttribute sectionAttribute = new ReportSpecificSectionAttribute();
                            sectionAttribute.setAttributeOrder(attribVOOrder[0].getAndIncrement());
                            sectionAttribute.setDeletable(attribVo.isDeletable());
                            sectionAttribute.setFieldName(attribVo.getFieldName());
                            sectionAttribute.setFieldType(attribVo.getFieldType());
                            sectionAttribute.setGranter(finalSpecificSection.getGranter());
                            sectionAttribute.setRequired(attribVo.isRequired());
                            sectionAttribute.setSection(finalSpecificSection);
                            sectionAttribute.setCanEdit(false);
                            sectionAttribute = reportService.saveReportSpecificSectionAttribute(sectionAttribute);

                            ReportStringAttribute stringAttribute = new ReportStringAttribute();

                            stringAttribute.setSection(finalSpecificSection);
                            stringAttribute.setReport(finalReport);
                            stringAttribute.setSectionAttribute(sectionAttribute);
                            stringAttribute.setGrantLevelTarget(attribVo.getTarget());
                            stringAttribute.setFrequency(attribVo.getFrequency());

                            stringAttribute = reportService.saveReportStringAttribute(stringAttribute);
                        });
                    });
                }
            }*/

            closureSection.getAttributes().forEach(a -> {
                ClosureSpecificSectionAttribute sectionAttribute = new ClosureSpecificSectionAttribute();
                sectionAttribute.setAttributeOrder(attribVOOrder[0].getAndIncrement());
                sectionAttribute.setDeletable(a.getDeletable());
                sectionAttribute.setFieldName(a.getFieldName());
                sectionAttribute.setFieldType(a.getFieldType());
                sectionAttribute.setGranter(finalSpecificSection.getGranter());
                sectionAttribute.setRequired(a.getRequired());
                sectionAttribute.setSection(finalSpecificSection);
                sectionAttribute.setCanEdit(true);
                sectionAttribute.setExtras(a.getExtras());
                sectionAttribute = closureService.saveClosureSpecificSectionAttribute(sectionAttribute);

                ClosureStringAttribute stringAttribute = new ClosureStringAttribute();

                stringAttribute.setSection(finalSpecificSection);
                stringAttribute.setClosure(finalClosure);
                stringAttribute.setSectionAttribute(sectionAttribute);
                if (sectionAttribute.getFieldType().equalsIgnoreCase("kpi")) {
                    stringAttribute.setGrantLevelTarget(null);
                    //stringAttribute.setFrequency(finalClosure1.getType().toLowerCase());
                } else if (sectionAttribute.getFieldType().equalsIgnoreCase("table")) {
                    stringAttribute.setValue(a.getExtras());
                }
                stringAttribute = closureService.saveClosureStringAttribute(stringAttribute);
                if (sectionAttribute.getFieldType().equalsIgnoreCase("disbursement")) {
                    closureTemplateHasDisbursement.set(true);
                    disbursementAttributeValue.set(stringAttribute);
                }
            });
        }

        // Handle logic for setting dibursement type in reports
        for (ClosureSpecificSection closureSection : closureService.getClosureSections(closure.getGrant())) {
            for (ClosureSpecificSectionAttribute specificSectionAttribute : closureService
                    .getAttributesBySection(closureSection)) {
                if (specificSectionAttribute.getFieldType().equalsIgnoreCase("disbursement")) {
                    if (closureTemplateHasDisbursement.get()) {
                        ObjectMapper mapper = new ObjectMapper();
                        String[] colHeaders = new String[] { "Disbursement Date", "Actual Disbursement",
                                "Funds from other Sources", "Notes" };
                        List<TableData> tableDataList = new ArrayList<>();
                        TableData tableData = new TableData();
                        tableData.setName("1");
                        tableData.setHeader("Planned Installment #");
                        tableData.setEnteredByGrantee(false);
                        tableData.setColumns(new ColumnData[4]);
                        for (int i = 0; i < tableData.getColumns().length; i++) {

                            tableData.getColumns()[i] = new ColumnData(colHeaders[i], "",
                                    (i == 1 || i == 2) ? "currency" : (i == 0) ? "date" : null);
                        }
                        tableDataList.add(tableData);

                        try {
                            disbursementAttributeValue.get().setValue(mapper.writeValueAsString(tableDataList));
                            closureService.saveClosureStringAttribute(disbursementAttributeValue.get());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ReportSpecificSection specificSection = new ReportSpecificSection();
                        specificSection.setDeletable(true);
                        specificSection.setGranter((Granter) report.getGrant().getGrantorOrganization());
                        specificSection.setReportId(report.getId());
                        specificSection.setReportTemplateId(closureTemplate.getId());
                        specificSection.setSectionName("Project Funds");
                        List<ReportSpecificSection> reportSections = reportService.getReportSections(report);
                        specificSection.setSectionOrder(Collections.max(reportSections.stream()
                                .map(rs -> new Integer(rs.getSectionOrder())).collect(Collectors.toList())) + 1);
                        specificSection = reportService.saveReportSpecificSection(specificSection);

                        ReportSpecificSectionAttribute sectionAttribute = new ReportSpecificSectionAttribute();
                        sectionAttribute.setAttributeOrder(1);
                        sectionAttribute.setDeletable(true);
                        sectionAttribute.setFieldName("Disbursement Details");
                        sectionAttribute.setFieldType("disbursement");
                        sectionAttribute.setGranter((Granter) report.getGrant().getGrantorOrganization());
                        sectionAttribute.setRequired(false);
                        sectionAttribute.setSection(specificSection);
                        sectionAttribute.setCanEdit(true);
                        sectionAttribute.setExtras(null);
                        sectionAttribute = reportService.saveReportSpecificSectionAttribute(sectionAttribute);

                        ReportStringAttribute stringAttribute = new ReportStringAttribute();

                        stringAttribute.setSection(specificSection);
                        stringAttribute.setReport(report);
                        stringAttribute.setSectionAttribute(sectionAttribute);

                        stringAttribute = reportService.saveReportStringAttribute(stringAttribute);

                    }
                }
            }
        }

        report = _ReportToReturn(report, userId);
        return report;
        return null;
    }
}
