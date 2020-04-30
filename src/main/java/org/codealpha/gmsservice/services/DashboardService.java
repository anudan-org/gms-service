package org.codealpha.gmsservice.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.entities.dashboard.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.dashboard.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private User user;

    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private GranterGrantTemplateService granterGrantTemplateService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private TemplateLibraryService templateLibraryService;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private GranterCountAndAmountTotalRepository granterCountAndAmountTotalRepository;
    @Autowired
    private GranterGranteeRepository granterGranteeRepository;
    @Autowired
    private GranterActiveUserRepository granterActiveUserRepository;
    @Autowired
    private GranterActiveGrantSummaryCommittedRepository granterActiveGrantSummaryCommittedRepository;
    @Autowired
    private GranterActiveGrantSummaryDisbursedRepository granterActiveGrantSummaryDisbursedRepository;
    @Autowired
    private GranterReportStatusRepository granterReportStatusRepository;

    List<Tenant> tenants;


    public DashboardService build(User user, List<Grant> grants, Organization tenantOrg) {
        this.user = user;
        List<String> tenantNames = new ArrayList<>();
        if (!tenantNames.contains(tenantOrg.getCode())) {
            tenantNames.add(tenantOrg.getCode());
        }

        tenants = new ArrayList<>();
        for (String name : tenantNames) {
            Tenant tenant = new Tenant();
            tenant.setName(name);
            List<Grant> grantsList = new ArrayList<>();
            tenant.setGrants(grantsList);
            tenant.setGrantTemplates(granterGrantTemplateService.findByGranterIdAndPublishedStatusAndPrivateStatus(user.getOrganization().getId(),true,false));
            if(user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")) {
                tenant.setTemplateLibrary(templateLibraryService.getTemplateLibraryForGranter((Granter) user.getOrganization()));
            }
            tenants.add(tenant);
        }

        for (Grant grant : grants) {
            for (Tenant tenant : tenants) {
                if ((user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER") && tenant.getName().equalsIgnoreCase(grant.getGrantorOrganization().getCode()))
                || (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE"))) {
                    List<Grant> grantList = tenant.getGrants();

                    grant.setActionAuthorities(workflowPermissionService
                            .getGrantActionPermissions(grant.getGrantorOrganization().getId(),
                                    user.getUserRoles(), grant.getGrantStatus().getId(),user.getId(),grant.getId()));

                    grant.setFlowAuthorities(workflowPermissionService
                            .getGrantFlowPermissions(grant.getGrantorOrganization().getId(),
                                    user.getUserRoles(), grant.getGrantStatus().getId()));

                    for (Submission submission : grant.getSubmissions()) {
                        submission.setActionAuthorities(workflowPermissionService
                                .getSubmissionActionPermission(grant.getGrantorOrganization().getId(),
                                        user.getUserRoles()));

                        AppConfig submissionWindow = appConfigService
                                .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
                                        AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS);
                        Date submissionWindowStart = new DateTime(submission.getSubmitBy())
                                .minusDays(Integer.valueOf(submissionWindow.getConfigValue()) + 1).toDate();

                        List<WorkFlowPermission> flowPermissions = workflowPermissionService
                                .getSubmissionFlowPermissions(grant.getGrantorOrganization().getId(),
                                        user.getUserRoles(), submission.getSubmissionStatus().getId());

                        if (!flowPermissions.isEmpty() && DateTime.now().toDate()
                                .after(submissionWindowStart)) {
                            submission.setFlowAuthorities(flowPermissions);
                        }

                        if (DateTime.now().toDate()
                                .after(submissionWindowStart)) {
                            submission.setOpenForReporting(true);
                        } else {
                            submission.setOpenForReporting(false);
                        }
                    }

                    GrantVO grantVO = new GrantVO();
                    grantVO = grantVO.build(grant, grantService.getGrantSections(grant), workflowPermissionService, user, appConfigService
                            .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                    AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS),userService);
                    grant.setGrantDetails(grantVO.getGrantDetails());
                    //grant.setNoteAddedBy(grantVO.getNoteAddedBy());
                    grant.setNoteAddedByUser(userService.getUserByEmailAndOrg(grant.getNoteAddedBy(),grant.getGrantorOrganization()));
                    grant.setGrantTemplate(granterGrantTemplateService.findByTemplateId(grant.getTemplateId()));
                    List<GrantAssignmentsVO> workflowAssignments = new ArrayList<>();
                    for (GrantAssignments assignment : grantService.getGrantWorkflowAssignments(grant)) {
                        GrantAssignmentsVO assignmentsVO = new GrantAssignmentsVO();
                        assignmentsVO.setId(assignment.getId());
                        assignmentsVO.setAnchor(assignment.isAnchor());
                        assignmentsVO.setAssignments(assignment.getAssignments());
                        if(assignment.getAssignments()!=null && assignment.getAssignments()>0) {
                            assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignments()));
                        }
                        assignmentsVO.setGrantId(assignment.getGrantId());
                        assignmentsVO.setStateId(assignment.getStateId());
                        assignmentsVO.setStateName(workflowStatusService.findById(assignment.getStateId()));
                        workflowAssignments.add(assignmentsVO);
                    }
                    grant.setWorkflowAssignment(workflowAssignments);

                    List<GrantAssignments> grantAssignments = grantService.getGrantCurrentAssignments(grant);
                    if (grantAssignments != null) {
                        for (GrantAssignments assignment : grantAssignments) {
                            if (grant.getCurrentAssignment() == null) {
                                List<AssignedTo> assignedToList = new ArrayList<>();
                                grant.setCurrentAssignment(assignedToList);
                            }
                            AssignedTo newAssignedTo = new AssignedTo();
                            if(assignment.getAssignments()!=null && assignment.getAssignments()>0) {
                                newAssignedTo.setUser(userService.getUserById(assignment.getAssignments()));
                            }
                            grant.getCurrentAssignment().add(newAssignedTo);
                        }
                    }
                    grant.getWorkflowAssignment().sort((a,b) -> a.getId().compareTo(b.getId()));

                    grant.getGrantDetails().getSections().sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
                    for (SectionVO section : grant.getGrantDetails().getSections()) {
                        if (section.getAttributes() != null) {
                            section.getAttributes().sort((a, b) -> Long.valueOf(a.getAttributeOrder()).compareTo(Long.valueOf(b.getAttributeOrder())));
                        }
                    }
                    grant.setSecurityCode(grantService.buildHashCode(grant));
                    grantList.add(grant);
                    tenant.setGrants(grantList);
                }
            }
        }
        return this;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }


    public GranterCountAndAmountTotal getSummaryForGranter(Long granterId){
        return granterCountAndAmountTotalRepository.getSummaryForGranter(granterId);
    }

    public GranterGrantee getGranteesSummaryForGranter(Long granterId){
        return granterGranteeRepository.getGranteeSummaryForGranter(granterId);
    }

    public GranterActiveUser getActiveUserSummaryForGranter(Long granterId){
        return granterActiveUserRepository.getActiveUserSummaryForGranter(granterId);
    }

    public GranterActiveGrantSummaryCommitted getActiveGrantCommittedSummaryForGranter(Long granterId){
        return granterActiveGrantSummaryCommittedRepository.getActiveGrantCommittedSummaryForGranter(granterId);
    }

    public Long getActiveGrantDisbursedAmountForGranter(Long granterId) {
        List<GranterActiveGrantSummaryDisbursed> disbursedList = granterActiveGrantSummaryDisbursedRepository.getActiveGrantDisbursedSummaryForGranter(granterId);
        AtomicReference<Long> disbursedAmount = new AtomicReference<>(0l);
        if(disbursedList!=null && disbursedList.size()>0){
            disbursedList.forEach(d ->{
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

                try {
                    List<TableData> dataList = mapper.readValue(d.getDisbursementData(),new TypeReference<List<TableData>>() {});
                    if(dataList!=null){
                        dataList.forEach(a -> {

                            for (ColumnData column : a.getColumns()) {
                                if(column.getName().equalsIgnoreCase("Actual Disbursement") && column.getValue()!=null && column.getValue().trim()!="" && column.getDataType().equalsIgnoreCase("currency")){
                                    disbursedAmount.updateAndGet(v -> v + Long.valueOf(column.getValue()));
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        return disbursedAmount.get();
    }

    public List<GranterReportStatus> getReportStatusSummaryForGranterAndStatus(Long granterId,String status){
        return granterReportStatusRepository.getReportStatusesForGranter(granterId, status);
    }

}
