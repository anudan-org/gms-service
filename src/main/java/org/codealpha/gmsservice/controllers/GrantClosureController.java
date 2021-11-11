package org.codealpha.gmsservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
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
    @Autowired
    private UserService userService;

    @GetMapping("/templates")
    @ApiOperation("Get all published closure templates for tenant")
    public List<GranterClosureTemplate> getTenantPublishedReportTemplates(
            @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode,
            @PathVariable("userId") Long userId) {
        return closureService.findTemplatesAndPublishedStatusAndPrivateStatus(
                organizationService.findOrganizationByTenantCode(tenantCode).getId(), true, false);
    }

    public GrantClosure createClosure(
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
                        ClosureSpecificSection specificSection = new ClosureSpecificSection();
                        specificSection.setDeletable(true);
                        specificSection.setGranter((Granter) closure.getGrant().getGrantorOrganization());
                        specificSection.setClosureId(closure.getId());
                        specificSection.setClosureTemplateId(closureTemplate.getId());
                        specificSection.setSectionName("Project Funds");
                        List<ClosureSpecificSection> reportSections = closureService.getClosureSections(grant);
                        specificSection.setSectionOrder(Collections.max(reportSections.stream()
                                .map(rs -> new Integer(rs.getSectionOrder())).collect(Collectors.toList())) + 1);
                        specificSection = closureService.saveClosureSpecificSection(specificSection);

                        ClosureSpecificSectionAttribute sectionAttribute = new ClosureSpecificSectionAttribute();
                        sectionAttribute.setAttributeOrder(1);
                        sectionAttribute.setDeletable(true);
                        sectionAttribute.setFieldName("Disbursement Details");
                        sectionAttribute.setFieldType("disbursement");
                        sectionAttribute.setGranter((Granter) closure.getGrant().getGrantorOrganization());
                        sectionAttribute.setRequired(false);
                        sectionAttribute.setSection(specificSection);
                        sectionAttribute.setCanEdit(true);
                        sectionAttribute.setExtras(null);
                        sectionAttribute = closureService.saveClosureSpecificSectionAttribute(sectionAttribute);

                        ClosureStringAttribute stringAttribute = new ClosureStringAttribute();

                        stringAttribute.setSection(specificSection);
                        stringAttribute.setClosure(closure);
                        stringAttribute.setSectionAttribute(sectionAttribute);

                        stringAttribute = closureService.saveClosureStringAttribute(stringAttribute);

                    }
                }
            }
        }

        closure = _ClosureToReturn(closure, userId);
        return closure;
    }


    private GrantClosure _ClosureToReturn(GrantClosure closure, Long userId) {

        closure.setStringAttributes(closureService.getStringAttributesForClosure(closure));

        List<ClosureAssignmentsVO> workflowAssignments = new ArrayList<>();
        for (ClosureAssignments assignment : closureService.getAssignmentsForClosure(closure)) {
            ClosureAssignmentsVO assignmentsVO = new ClosureAssignmentsVO();
            assignmentsVO.setId(assignment.getId());
            assignmentsVO.setAnchor(assignment.isAnchor());
            assignmentsVO.setAssignmentId(assignment.getAssignment());
            if (assignment.getAssignment() != null && assignment.getAssignment() > 0) {
                assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignment()));
            }
            assignmentsVO.setGrantId(assignment.getGrant().getId());
            assignmentsVO.setStateId(assignment.getStateId());
            assignmentsVO.setStateName(workflowStatusService.findById(assignment.getStateId()));

            closureService.setAssignmentHistory(assignmentsVO);

            workflowAssignments.add(assignmentsVO);
        }
        report.setWorkflowAssignments(workflowAssignments);
        List<ReportAssignment> reportAssignments = determineCanManage(report, userId);

        if (userService.getUserById(userId).getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")) {
            report.setForGranteeUse(true);
        } else {
            report.setForGranteeUse(false);
        }
        if (reportAssignments != null) {
            for (ReportAssignment assignment : reportAssignments) {
                if (report.getCurrentAssignment() == null) {
                    List<AssignedTo> assignedToList = new ArrayList<>();
                    report.setCurrentAssignment(assignedToList);
                }
                AssignedTo newAssignedTo = new AssignedTo();
                if (assignment.getAssignment() != null && assignment.getAssignment() > 0) {
                    newAssignedTo.setUser(userService.getUserById(assignment.getAssignment()));
                }
                report.getCurrentAssignment().add(newAssignedTo);
            }
        }

        ReportVO reportVO = new ReportVO().build(report, reportService.getReportSections(report), userService,
                reportService);
        report.setReportDetails(reportVO.getReportDetails());

        showDisbursementsForReport(report,userService.getUserById(userId));

        report.setNoteAddedBy(reportVO.getNoteAddedBy());
        report.setNoteAddedByUser(reportVO.getNoteAddedByUser());

        report.getWorkflowAssignments().sort((a, b) -> a.getId().compareTo(b.getId()));
        report.getReportDetails().getSections()
                .sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
        for (SectionVO section : report.getReportDetails().getSections()) {
            if (section.getAttributes() != null) {
                section.getAttributes().sort(
                        (a, b) -> Long.valueOf(a.getAttributeOrder()).compareTo(Long.valueOf(b.getAttributeOrder())));
            }
        }

        report.setGranteeUsers(userService.getAllGranteeUsers(report.getGrant().getOrganization()));

        GrantVO grantVO = new GrantVO().build(report.getGrant(), grantService.getGrantSections(report.getGrant()),
                workflowPermissionService, userService.getUserById(userId),
                appConfigService.getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                        AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS),
                userService,grantService);

        ObjectMapper mapper = new ObjectMapper();
        report.getGrant().setGrantDetails(grantVO.getGrantDetails());

        List<Report> approvedReports = null;
        List<TableData> approvedDisbursements = new ArrayList<>();
        AtomicInteger installmentNumber = new AtomicInteger();

        /*
         * if (report.getLinkedApprovedReports() != null) { approvedReports =
         * reportService.getReportsByIds(report.getLinkedApprovedReports()); for (Report
         * approvedReport : approvedReports) {
         * reportService.getReportSections(approvedReport).forEach(sec -> { if
         * (sec.getAttributes() != null) { sec.getAttributes().forEach(attr -> { if
         * (attr.getFieldType().equalsIgnoreCase("disbursement")) {
         *
         * try { List<TableData> tableDataList = mapper.readValue(
         * reportService.getReportStringByStringAttributeId(attr.getId()).getValue(),
         * new TypeReference<List<TableData>>() { }); tableDataList.forEach(td -> {
         * approvedDisbursements.add(td); installmentNumber.getAndIncrement(); }); }
         * catch (Exception e) { logger.error("Failed for report "+report.getId(),e); }
         *
         * } }); } }); } }
         */

        report.getGrant().setApprovedReportsDisbursements(approvedDisbursements);

        report.getReportDetails().getSections().forEach(sec -> {
            if (sec.getAttributes() != null) {
                sec.getAttributes().forEach(attr -> {
                    if (attr.getFieldType().equalsIgnoreCase("disbursement") && attr.getFieldTableValue() != null) {
                        for (TableData data : attr.getFieldTableValue()) {
                            installmentNumber.getAndIncrement();
                            data.setName(String.valueOf(installmentNumber.get()));
                        }

                        try {
                            attr.setFieldValue(mapper.writeValueAsString(attr.getFieldTableValue()));
                        } catch (JsonProcessingException e) {
                            logger.error(e.getMessage(), e);
                        }

                    }
                });
            }
        });
        report.setSecurityCode(reportService.buildHashCode(report));
        report.setFlowAuthorities(reportService.getFlowAuthority(report, userId));

        List<GrantTag> grantTags = grantService.getTagsForGrant(report.getGrant().getId());
        /*List<GrantTagVO> grantTagsVoList = new ArrayList<>();
        for(GrantTag tag: grantTags){
            GrantTagVO vo =new GrantTagVO();
            vo.setGrantId(report.getGrant().getId());
            vo.setId(tag.getId());
            vo.setOrgTagId(tag.getOrgTagId());
            vo.setTagName(orgTagService.getOrgTagById(tag.getOrgTagId()).getName());
            grantTagsVoList.add(vo);
        }*/
        report.getGrant().setGrantTags(grantTags);

        return report;
    }
}
