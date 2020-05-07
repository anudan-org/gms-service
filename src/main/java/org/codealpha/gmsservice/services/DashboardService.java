package org.codealpha.gmsservice.services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
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
    private GranterGrantSummaryCommittedRepository granterActiveGrantSummaryCommittedRepository;
    @Autowired
    private GranterGrantSummaryDisbursedRepository granterActiveGrantSummaryDisbursedRepository;
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

    public GranterGrantSummaryCommitted getActiveGrantCommittedSummaryForGranter(Long granterId,String status){
        return granterActiveGrantSummaryCommittedRepository.getGrantCommittedSummaryForGranter(granterId,status);
    }

    public Long getActiveGrantDisbursedAmountForGranter(Long granterId,String status) {
        List<GranterGrantSummaryDisbursed> disbursedList = granterActiveGrantSummaryDisbursedRepository.getGrantDisbursedSummaryForGranter(granterId,status);
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

    public Map<Integer,String> getActiveGrantsCommittedPeriodsForGranterAndStatus(Long granterId, String status) {

        List<GranterGrantSummaryDisbursed> disbursedList = null;
        if(status.equalsIgnoreCase("ACTIVE")){
            disbursedList = granterActiveGrantSummaryDisbursedRepository.getActiveGrantCommittedSummaryForGranter(granterId);
        } else if(status.equalsIgnoreCase("CLOSED")){
            disbursedList = granterActiveGrantSummaryDisbursedRepository.getClosedGrantCommittedSummaryForGranter(granterId);
        }
        Map<Integer,String> periods = new HashMap<>();
        if(disbursedList!=null && disbursedList.size()>0){
            for (GranterGrantSummaryDisbursed granterGrantSummaryDisbursed : disbursedList) {
                DateTime grantDate = new DateTime(granterGrantSummaryDisbursed.getStartDate());
                DateTime calendarYearStart = new DateTime().withYear(grantDate.getYear()).withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);
                String period = null;
                if(grantDate.isAfter(calendarYearStart)){
                    period = String.valueOf(grantDate.getYear())+" - "+ String.valueOf(grantDate.getYear()+1);
                    periods.put(grantDate.getYear(),period);
                }else {
                    period = String.valueOf(grantDate.getYear()-1)+" - "+ String.valueOf(grantDate.getYear());
                    periods.put(grantDate.getYear()-1,period);
                }
            }
        }
        return periods;
    }

    public Long getDisbursedAmountForGranterAndPeriodAndStatus(Integer period,Long granterId,String status) {
        List<GranterGrantSummaryDisbursed> disbursedList = granterActiveGrantSummaryDisbursedRepository.getGrantDisbursedSummaryForGranter(granterId, status);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        Long total = 0l;
        if (disbursedList != null && disbursedList.size() > 0) {
            for (GranterGrantSummaryDisbursed granterGrantSummaryDisbursed : disbursedList) {

                try {
                    List<TableData> tableData = mapper.readValue(granterGrantSummaryDisbursed.getDisbursementData(),new TypeReference<List<TableData>>(){});
                    for (TableData tableDatum : tableData) {
                        int datePos=0, amtPos=0;
                        for (int i = 0; i < tableDatum.getColumns().length; i++) {
                            if(tableDatum.getColumns()[i].getName().equalsIgnoreCase("Disbursement Date") && tableDatum.getColumns()[i].getValue().trim()!="" && tableDatum.getColumns()[i].getDataType().equalsIgnoreCase("date")){
                                datePos=i;
                            }
                            if(tableDatum.getColumns()[i].getName().equalsIgnoreCase("Actual Disbursement")  && tableDatum.getColumns()[i].getDataType().equalsIgnoreCase("currency")){
                                amtPos=i;
                            }
                        }
                        SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yyyy");
                        DateTime disbursementDate = new DateTime(sd.parse(tableDatum.getColumns()[datePos].getValue()));
                        Long disbursementAmt = Long.valueOf(tableDatum.getColumns()[amtPos].getValue()==null?"0":tableDatum.getColumns()[amtPos].getValue().trim().equalsIgnoreCase("")?"0":tableDatum.getColumns()[amtPos].getValue());
                        DateTime calendarYearStart = new DateTime().withYear(disbursementDate.getYear()).withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);

                        int disbursementYear=0;
                        if(disbursementDate.isAfter(calendarYearStart)){
                            disbursementYear = disbursementDate.getYear();
                        }else {
                            disbursementYear = disbursementDate.getYear()-1;
                        }
                        if(period==disbursementYear){
                            total += disbursementAmt;
                        }

                    }
                } catch (IOException | ParseException | NumberFormatException e) {
                    continue;
                }

            }
        }
        return total;
    }

    public Long getCommittedAmountForGranterAndPeriodAndStatus(Integer period,Long granterId,String status) {
        List<GranterGrantSummaryDisbursed> disbursedList = null;
        if(status.equalsIgnoreCase("ACTIVE")){
            disbursedList = granterActiveGrantSummaryDisbursedRepository.getActiveGrantCommittedSummaryForGranter(granterId);
        } else if(status.equalsIgnoreCase("CLOSED")){
            disbursedList = granterActiveGrantSummaryDisbursedRepository.getClosedGrantCommittedSummaryForGranter(granterId);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        Long total = 0l;
        if (disbursedList != null && disbursedList.size() > 0) {
            for (GranterGrantSummaryDisbursed granterGrantSummaryDisbursed : disbursedList) {


                        DateTime committedDate = new DateTime(granterGrantSummaryDisbursed.getStartDate());
                        Long disbursementAmt = Long.valueOf(granterGrantSummaryDisbursed.getGrantAmount());
                        DateTime calendarYearStart = new DateTime().withYear(committedDate.getYear()).withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);

                        int disbursementYear=0;
                        if(committedDate.isAfter(calendarYearStart)){
                            disbursementYear = committedDate.getYear();
                        }else {
                            disbursementYear = committedDate.getYear()-1;
                        }
                        if(period==disbursementYear){
                            total += disbursementAmt;
                        }
            }
        }
        return total;
    }
}
