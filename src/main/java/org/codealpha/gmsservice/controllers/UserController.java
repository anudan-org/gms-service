package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.entities.dashboard.*;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.models.ErrorMessage;
import org.codealpha.gmsservice.models.ResetPwdData;
import org.codealpha.gmsservice.models.UserCheck;
import org.codealpha.gmsservice.models.UserVO;
import org.codealpha.gmsservice.models.dashboard.Detail;
import org.codealpha.gmsservice.models.dashboard.Filter;
import org.codealpha.gmsservice.models.dashboard.Summary;
import org.codealpha.gmsservice.models.dashboard.*;
import org.codealpha.gmsservice.models.dashboard.mydashboard.Disbursement;
import org.codealpha.gmsservice.models.dashboard.mydashboard.*;
import org.codealpha.gmsservice.services.*;
import org.codealpha.gmsservice.validators.DashboardValidator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Developer code-alpha.org
 **/
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public static final String ACTIVE = "ACTIVE";
    public static final String CLOSED = "CLOSED";
    public static final String CEO = "CEO";
    public static final String GRANTS = " Grants";
    public static final String OVERDUE = "overdue";
    public static final String OVERDUE_TITLE = "Overdue";
    public static final String REPORTS = "Reports";
    public static final String GRANTEE = "GRANTEE";
    public static final String GRANTER = "GRANTER";
    public static final String YOU_HAVE_ENTERED_AN_INVALID_PREVIOUS_PASSWORD = "You have entered an invalid previous password";
    public static final String COMMITTED = "Committed";
    public static final String DISBURSED = "Disbursed";
    public static final String DISBURSEMENTS = "Disbursements";
    public static final String PLATFORM = "PLATFORM";
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private CommonEmailSevice commonEmailSevice;
    @Autowired
    private UserService userService;
    @Autowired
    private GranteeService granteeService;
    @Autowired
    private GranterService granterService;
    @Autowired
    private DisbursementService disbursementService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    DashboardValidator dashboardValidator;
    @Autowired
    private GrantService grantService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private PasswordResetRequestService passwordResetRequestService;
    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private GrantTypeWorkflowMappingService grantTypeWorkflowMappingService;
    @Autowired
    private GrantTypeService grantTypeService;
    @org.springframework.beans.factory.annotation.Value("${spring.upload-file-location}")
    private String uploadLocation;

    @GetMapping(value = "/{userId}")
    public User get(@PathVariable(name = "userId") Long id, @RequestHeader("X-TENANT-CODE") String tenantCode) {
        return userService.getUserById(id);
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional
    public User create(@RequestBody UserVO user, @RequestHeader("X-TENANT-CODE") String tenantCode,
                       HttpServletResponse response, HttpServletRequest request) {

        Organization org = null;
        if (tenantCode.equalsIgnoreCase("ANUDAN")) {
            org = organizationService.findByNameAndOrganizationType(user.getOrganizationName(), GRANTEE);
        } else {
            org = organizationService.findOrganizationByTenantCode(tenantCode);
        }
        User newUser = userService.getUserByEmailAndOrg(user.getEmailId(), org);
        if (newUser == null) {
            newUser = new User();

            // BCryptPasswordEncoder a = new BCryptPasswordEncoder
            newUser.setCreatedAt(DateTime.now().toDate());
            newUser.setCreatedBy("Api");
            newUser.setEmailId(user.getEmailId());

            newUser.setOrganization(org);
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));

            UriComponents urlComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();

            String scheme = urlComponents.getScheme();
            String host = urlComponents.getHost();
            int port = urlComponents.getPort();

            String verificationLink = scheme + "://" + host + (port != -1 ? ":" + port : "")
                    + "/grantee/verification?emailId=" + user.getEmailId() + "&code="
                    + RandomStringUtils.random(127, 0, 0, true, true, null, new SecureRandom());

            commonEmailSevice.sendMail(new String[]{user.getEmailId()}, null, "Anudan.org - Verification Link",
                    verificationLink, null);
        } else {
            newUser.setActive(true);
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser = userService.save(newUser);
        }

        return newUser;
    }

    @PostMapping(value = "/{userId}/profile",consumes = {"multipart/form-data" })
    public void setUserProfilePic(@PathVariable("userId")Long userId,
                                  @RequestParam("file") MultipartFile[] files,
                                  @RequestHeader("X-TENANT-CODE") String tenantCode){
        String filePath = uploadLocation + userService.getUserById(userId).getOrganization().getCode() + "/users/" + userId;
        File dir = new File(filePath);
        dir.mkdirs();
        String fileName = files[0].getOriginalFilename();
        File fileToCreate = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(fileToCreate)) {
            fos.write(files[0].getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        User user = userService.getUserById(userId);
        user.setUserProfile(fileToCreate.getAbsolutePath());
        userService.save(user);
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User update(@ApiParam(name = "user", value = "User details") @RequestBody UserVO user,
                       @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId,
                       @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        // BCryptPasswordEncoder a = new BCryptPasswordEncoder
        dashboardValidator.validate(userId, tenantCode);
        if (userService.getUserById(userId).getOrganization().getId().longValue() != user.getOrganization().getId()) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        if (userId.longValue() != user.getId().longValue()) {
            throw new ResourceNotFoundException("Invalid credentials");
        }
        User savedUser = userService.getUserById(user.getId());
        savedUser.setFirstName(user.getFirstName());
        savedUser.setLastName(user.getLastName());

        Organization userOrg = savedUser.getOrganization();
        userOrg.setName(user.getOrganization().getName());

        userOrg = organizationService.save(userOrg);
        savedUser.setOrganization(userOrg);
        savedUser = userService.save(savedUser);
        return savedUser;
    }


    @PostMapping("/activation")
    public HttpStatus verifyUser(@RequestParam("emailId") String email, @RequestParam("code") String code) {
        return HttpStatus.OK;
    }

    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<DashboardService> getDashbaord(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                         @PathVariable("userId") Long userId,
                                                         @RequestParam(value = "forStatus",required = false,defaultValue = "inprogress")String forStatus) {

        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        Organization userOrg = user.getOrganization();
        Organization tenantOrg = null;
        tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        List<GrantCard> grants = null;
        if (GRANTEE.equalsIgnoreCase(userOrg.getType())) {
            grants = granteeService.getGrantCardsOfGranteeForGrantor(userOrg.getId(), tenantOrg, user.getUserRoles());
            return new ResponseEntity<>(dashboardService.build(user, grants, tenantOrg), HttpStatus.OK);
        } else if (GRANTER.equalsIgnoreCase(userOrg.getType())) {
            grants = granterService.getGrantsOfGranterForGrantor(userOrg.getId(), tenantOrg, user.getId(), forStatus);
            return new ResponseEntity<>(dashboardService.build(user, grants, tenantOrg), HttpStatus.OK);
        }

        return new ResponseEntity<>(new DashboardService(), HttpStatus.OK);
    }

    @GetMapping("/{userId}/dashboard/in-progress")
    public ResponseEntity<Long> getInProgressGrantsOfUser(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                          @PathVariable("userId") Long userId) {
        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        Organization userOrg = user.getOrganization();
        Organization tenantOrg = null;
        tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        switch (userOrg.getType()) {
            case GRANTEE:
                return new ResponseEntity<>(
                        granterService.getActiveGrantsOfGranteeForGrantor(userOrg.getId(), tenantOrg, user.getId()),
                        HttpStatus.OK);
            case GRANTER:
                return new ResponseEntity<>(
                        granterService.getInProgressGrantsOfGranterForGrantor(userOrg.getId(), tenantOrg, user.getId()),
                        HttpStatus.OK);
            default:
                return new ResponseEntity<>(0l, HttpStatus.OK);
        }

    }

    @GetMapping("/{userId}/dashboard/active")
    public ResponseEntity<Long> getActiveGrantsOfUser(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                      @PathVariable("userId") Long userId) {
        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        Organization userOrg = user.getOrganization();
        Organization tenantOrg = null;
        tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        switch (userOrg.getType()) {
            case GRANTEE:
                return new ResponseEntity<>(
                        granterService.getActiveGrantsOfGranteeForGrantor(userOrg.getId(), tenantOrg, user.getId()),
                        HttpStatus.OK);
            case GRANTER:
                return new ResponseEntity<>(
                        granterService.getActiveGrantsOfGranterForGrantor(userOrg.getId(), tenantOrg, user.getId()),
                        HttpStatus.OK);
            default:
                return new ResponseEntity<>(0l, HttpStatus.OK);
        }
    }

    @GetMapping("/{userId}/dashboard/closed")
    public ResponseEntity<Long> getClosedGrantsOfUser(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                      @PathVariable("userId") Long userId) {
        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        Organization userOrg = user.getOrganization();
        Organization tenantOrg = null;
        tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        if (GRANTEE.equalsIgnoreCase(userOrg.getType())) {
            return new ResponseEntity<>(
                    granterService.getClosedGrantsOfGranteeForGrantor(userOrg.getId(), tenantOrg, user.getId()),
                    HttpStatus.OK);
        } else if (GRANTER.equalsIgnoreCase(userOrg.getType())) {
            return new ResponseEntity<>(
                    granterService.getClosedGrantsOfGranterForGrantor(userOrg.getId(), tenantOrg, user.getId()),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(0l, HttpStatus.OK);
    }

    @PostMapping("/{id}/validate-pwd")
    public ResponseEntity<ErrorMessage> validatePassword(@PathVariable("id") Long userId, @RequestBody String pwd,
                                                         @RequestHeader("X-TENANT-CODE") String tenantCode) {
        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        if (passwordEncoder.matches(pwd, user.getPassword())) {
            return new ResponseEntity<>(new ErrorMessage(true, ""), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(YOU_HAVE_ENTERED_AN_INVALID_PREVIOUS_PASSWORD);
        }
    }

    @PostMapping("/{id}/pwd")
    public ResponseEntity<User> changePassword(@PathVariable("id") Long userId, @RequestBody String[] pwds,
                                               @RequestHeader("X-TENANT-CODE") String tenantCode) {
        dashboardValidator.validate(userId, tenantCode);
        if (pwds.length != 3) {
            throw new ResourceNotFoundException("Invalid information sent for setting password");
        }
        if (!pwds[1].equalsIgnoreCase(pwds[2])) {
            throw new ResourceNotFoundException("New passwords do not match");
        }
        if (!pwds[1].matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})")
                && !pwds[2].matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})")) {
            throw new ResourceNotFoundException(
                    "Password must contain at least one digit, one lowercase character, one uppercase character, one special symbols in the list \"@#$%\" and between 6-20 characters.");
        }
        User user = userService.getUserById(userId);
        if (user.isPlain()) {
            if (!user.getPassword().equalsIgnoreCase(pwds[0])) {
                throw new ResourceNotFoundException(YOU_HAVE_ENTERED_AN_INVALID_PREVIOUS_PASSWORD);
            }
        } else {
            if (!passwordEncoder.matches(pwds[0], user.getPassword())) {
                throw new ResourceNotFoundException(YOU_HAVE_ENTERED_AN_INVALID_PREVIOUS_PASSWORD);
            }
        }

        user.setPassword(passwordEncoder.encode(pwds[1]));
        user.setPlain(false);
        user = userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/check")
    public boolean checkIfUserExists(@RequestBody UserCheck userdata) {
        Organization org = null;
        if (userdata.getType().equalsIgnoreCase("grant")) {
            Long grantId = Long.valueOf(Base64.getDecoder().decode(userdata.getObject())[0]);
            org = grantService.getById(grantId).getOrganization();
        } else if (userdata.getType().equalsIgnoreCase("report")) {
            Long reportId = Long.valueOf(Base64.getDecoder().decode(userdata.getObject())[0]);
            org = reportService.getReportById(reportId).getGrant().getOrganization();
        }

        User user = userService.getUserByEmailAndOrg(userdata.getEmail(), org);
        return user != null && user.isActive();
    }

    @GetMapping("/{userId}/dashboard/summary")
    public ResponseEntity<Category> getDasboardSummary(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                       @PathVariable("userId") Long userId) {

        Category dashboardCategory = null;
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);

        GranterCountAndAmountTotal countAndAmountTotal = dashboardService.getSummaryForGranter(tenantOrg.getId());
        GranterGrantee granteeSummary = dashboardService.getGranteesSummaryForGranter(tenantOrg.getId());
        GranterActiveUser granterActiveUserSummary = dashboardService.getActiveUserSummaryForGranter(tenantOrg.getId());
        Summary categorySummary = new Summary(
                Long.valueOf(countAndAmountTotal == null ? 0l : countAndAmountTotal.getTotalGrants()),
                Long.valueOf(granteeSummary == null ? 0l : granteeSummary.getGranteeTotals()),
                Long.valueOf(countAndAmountTotal == null ? 0l : countAndAmountTotal.getTotalGrantAmount()),
                Long.valueOf(granterActiveUserSummary == null ? 0l : granterActiveUserSummary.getActiveUsers()));

        List<Filter> categoryFilters = new ArrayList<>();

        Filter activeFilter = getFilterForGrantsByStatus(tenantOrg, ACTIVE);
        if (activeFilter != null) {
            categoryFilters.add(activeFilter);
        }
        Filter closedFilter = getFilterForGrantsByStatus(tenantOrg, CLOSED);
        if (closedFilter != null) {
            categoryFilters.add(closedFilter);
        }

        dashboardCategory = new Category(CEO, categorySummary, categoryFilters);

        return new ResponseEntity<>(dashboardCategory, HttpStatus.OK);
    }

    @GetMapping("/{userId}/dashboard/summary/grantee/{granteeOrgId}")
    public ResponseEntity<Category> getGranteeDasboardSummary(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                       @PathVariable("userId") Long userId,@PathVariable("granteeOrgId")Long granteeOrgId) {

        Category dashboardCategory = null;
        Organization granteeOrg = organizationService.get(granteeOrgId);


        Summary categorySummary = new Summary(
                0l,
                0l,0l,0l);

        List<Filter> categoryFilters = new ArrayList<>();

        Filter activeFilter = getFilterForGranteeGrantsByStatus(granteeOrg, ACTIVE);
        if (activeFilter != null) {
            categoryFilters.add(activeFilter);
        }
        Filter closedFilter = getFilterForGranteeGrantsByStatus(granteeOrg, CLOSED);
        if (closedFilter != null) {
            categoryFilters.add(closedFilter);
        }

        dashboardCategory = new Category(CEO, categorySummary, categoryFilters);

        return new ResponseEntity<>(dashboardCategory, HttpStatus.OK);
    }

    @GetMapping("/{userId}/dashboard/mysummary")
    public ResponseEntity<MyCategory> getMyDashboardSummary(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                       @PathVariable("userId") Long userId) {

        MyCategory category = new MyCategory();
        category.setCanShowDashboard(grantService.isUserPartOfActiveWorkflow(userId));

        org.codealpha.gmsservice.models.dashboard.mydashboard.Summary summary = new org.codealpha.gmsservice.models.dashboard.mydashboard.Summary();

        summary.setActionsPending(getActionsPending(userId));
        summary.setUpcomingGrants(getUpcomingGrants(userId));
        summary.setUpcomingReports(getUpcomingReports(userId));
        summary.setUpcomingDisbursements(getUpcomingDisbursements(userId));

        List<org.codealpha.gmsservice.models.dashboard.mydashboard.Filter> filters = new ArrayList<>();
        org.codealpha.gmsservice.models.dashboard.mydashboard.Filter activeGrantFilter = getFilter(ACTIVE, userId);
        org.codealpha.gmsservice.models.dashboard.mydashboard.Filter closedGrantFilter = getFilter(CLOSED, userId);
        if (activeGrantFilter.getTotalGrants() > 0) {
            filters.add(activeGrantFilter);
        }
        if (closedGrantFilter.getTotalGrants() > 0) {
            filters.add(closedGrantFilter);
        }

        category.setSummary(summary);
        category.setFilters(filters);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    private org.codealpha.gmsservice.models.dashboard.mydashboard.Filter getFilter(String status,Long userId) {
        org.codealpha.gmsservice.models.dashboard.mydashboard.Filter grantFilter = new org.codealpha.gmsservice.models.dashboard.mydashboard.Filter();
        //Populating active filter
        Long committedAmnt = grantService.getCommittedAmountByUserAndStatus(userId,status);
        grantFilter.setCommittedAmount(committedAmnt==null?0:committedAmnt);
        grantFilter.setDetails(getDetails(userId,status));
        Long disbursedAmt = grantService.getDisbursedAmountByUserAndStatus(userId,status);
        grantFilter.setDisbursedAmount(disbursedAmt==null?0:disbursedAmt);
        grantFilter.setGranteeOrgs(grantService.getGranteeOrgsCountByUserAndStatus(userId,status));
        grantFilter.setGrantswithnoapprovedreports(grantService.getGrantsWithNoApprovedReportsByUserAndStatus(userId,status));
        Long noKpis = grantService.getGrantsWithNoAKPIsByUserAndStatus(userId,status);
        grantFilter.setGrantswithnokpis(noKpis == null ? 0 : noKpis);
        grantFilter.setName(org.apache.commons.text.WordUtils.capitalizeFully(status + GRANTS));
        GranterGrantSummaryCommitted grantSummaryCommitted =  dashboardService.getDisbursementPeriodsForUserAndStatus(userId,status);
        if(grantSummaryCommitted!=null){
            SimpleDateFormat sd = new SimpleDateFormat("yyyy");
            grantFilter.setPeriod(sd.format(grantSummaryCommitted.getPeriodStart()) + "-"
                    + sd.format(grantSummaryCommitted.getPeriodEnd()));
        }

        grantFilter.setTotalGrants(grantService.getGrantsTotalForUserByStatus(userId,status));
        return grantFilter;
    }

    private List<org.codealpha.gmsservice.models.dashboard.mydashboard.Detail> getDetails(Long userId,String status) {
        List<org.codealpha.gmsservice.models.dashboard.mydashboard.Detail> details = new ArrayList<>();

        Map<Integer, String> periodsMap = dashboardService
                .getGrantsCommittedPeriodsForUserAndStatus(userService.getUserById(userId), status);

        List<Disbursement> disbursalSummaryList = new ArrayList<>();
        for (Integer period : Collections.unmodifiableSet(periodsMap.keySet())) {
            Double[] disbursalTotalAndCount = dashboardService.getDisbursedAmountForUserAndPeriodAndStatus(period,
                    userService.getUserById(userId), status);
            Long[] committedTotalAndCount = dashboardService.getCommittedAmountForUserAndPeriodAndStatus(period,
                    userService.getUserById(userId), status);
            disbursalSummaryList.add(new Disbursement(periodsMap.get(period), new Value[]{
                    new Value(DISBURSED,
                            String.valueOf(BigDecimal.valueOf(disbursalTotalAndCount[0] / 100000.00).setScale(2,
                                    RoundingMode.HALF_UP))),
                    new Value(COMMITTED,
                            String.valueOf(new BigDecimal(Double.toString(committedTotalAndCount[0] / 100000.00))
                                    .setScale(2, RoundingMode.HALF_UP)))}));
        }

        Summary__1 summary1 = new Summary__1();
        summary1.setDisbursement(disbursalSummaryList);
        org.codealpha.gmsservice.models.dashboard.mydashboard.Detail detail = new org.codealpha.gmsservice.models.dashboard.mydashboard.Detail();
        detail.setName(DISBURSEMENTS);
        detail.setSummary(summary1);
        details.add(detail);

        //report status
        List<Summary__2> reportSummaryList = new ArrayList<>();
        List<GranterReportStatus> reportStatuses = dashboardService.getReportStatusSummaryForUserAndStatus(userId, status);
        if (reportStatuses != null && !reportStatuses.isEmpty()) {
            for (GranterReportStatus reportStatus : reportStatuses) {
                reportSummaryList
                        .add(new Summary__2(reportStatus.getStatus(), Long.valueOf(reportStatus.getCount())));
            }

            if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase("due"))) {
                reportSummaryList.add(new Summary__2("Due", Long.valueOf(0)));
            }

            if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase(OVERDUE))) {
                reportSummaryList.add(new Summary__2(OVERDUE_TITLE, Long.valueOf(0)));
            }
        }

        List<String> dueOverdueOrder = Arrays.asList("Due", OVERDUE_TITLE);
        reportSummaryList.sort(Comparator.comparing(c ->
            dueOverdueOrder.indexOf(c.getName())
        ));

        Summary__1 summaryReports = new Summary__1();
        summaryReports.setSummary(reportSummaryList);




        List<StatusSummary> statusSummaryList = new ArrayList<>();
        statusSummaryList.add(new StatusSummary("On schedule",reportService.approvedReportsInTimeForUser(userId)));
        statusSummaryList.add(new StatusSummary("After due date",reportService.approvedReportsNotInTimeForUser(userId)));
        summaryReports.setStatusSummary(statusSummaryList);

        org.codealpha.gmsservice.models.dashboard.mydashboard.Detail reportSummaryDetail = new org.codealpha.gmsservice.models.dashboard.mydashboard.Detail();
        reportSummaryDetail.setName(REPORTS);
        reportSummaryDetail.setSummary(summaryReports);
        details.add(reportSummaryDetail);

        return details;
    }

    private UpcomingGrants getUpcomingGrants(Long userId) {
        Long upcomingDibursementAmount = getUpcomingGrantsDisbursementAmount(userId);
        return new UpcomingGrants(getUpComingDraftGrants(userId), getGrantsInWorkflow(userId), upcomingDibursementAmount==null?0:upcomingDibursementAmount);
    }

    private UpcomingReports getUpcomingReports(Long userId) {
        Long upcomingDibursementAmount = getUpcomingReportsDisbursementAmount(userId);
        return new UpcomingReports(getUpComingDraftReports(userId), getReportsInWorkflow(userId), upcomingDibursementAmount==null?0:upcomingDibursementAmount);
    }

    private UpcomingDisbursements getUpcomingDisbursements(Long userId) {
        Long upcomingDibursementAmount = getUpcomingDisbursementsDisbursementAmount(userId);
        return new UpcomingDisbursements(getUpComingDraftDisbursements(userId), getDisbursementsInWorkflow(userId), upcomingDibursementAmount==null?0:upcomingDibursementAmount);
    }

    private Long getUpcomingGrantsDisbursementAmount(Long userId) {
        return grantService.getUpcomingGrantsDisbursementAmount(userId);
    }

    private Long getUpcomingReportsDisbursementAmount(Long userId) {
        return grantService.getUpcomingReportsDisbursementAmount(userId);
    }

    private Long getUpcomingDisbursementsDisbursementAmount(Long userId) {
        return disbursementService.getUpcomingDisbursementsDisbursementAmount(userId);
    }

    private Long getGrantsInWorkflow(Long userId) {
        return grantService.getGrantsInWorkflow(userId);
    }

    private Long getReportsInWorkflow(Long userId) {
        return reportService.getReportsInWorkflow(userId);
    }

    private Long getDisbursementsInWorkflow(Long userId) {
        return disbursementService.getDisbursementsInWorkflow(userId);
    }

    private Long getUpComingDraftGrants(Long userId) {
        return grantService.getUpComingDraftGrants(userId);
    }

    private Long getUpComingDraftReports(Long userId) {
        return reportService.getUpComingDraftReports(userId);
    }

    private Long getUpComingDraftDisbursements(Long userId) {
        return disbursementService.getUpComingDraftDisbursements(userId);
    }

    private ActionsPending getActionsPending(Long userId) {
        Long pendingDisbursement = getPendingActionDisbursements(userId);
        Long pendingGrants = getPendingActionGrants(userId);
        Long pendingReports = getPendingActionReports(userId);
        return new ActionsPending(pendingGrants==null?0:pendingGrants, pendingReports==null?0:pendingReports,pendingDisbursement==null?0:pendingDisbursement );
    }

    private Long getPendingActionDisbursements(Long userId) {
        return disbursementService.getPendingActionDisbursements(userId);
    }

    private Long getPendingActionReports(Long userId) {
        return grantService.getActionDueReportsForUser(userId);
    }

    private Long getPendingActionGrants(Long userId) {

        return grantService.getActionDueGrantsForUser(userId);
    }

    private Filter getFilterForGrantsByStatus(Organization tenantOrg, String status) {
        GranterGrantSummaryCommitted activeGrantSummaryCommitted = ACTIVE.equalsIgnoreCase(status) ? dashboardService
                .getActiveStatusGrantCommittedSummaryForGranter(tenantOrg.getId(), status) :
                dashboardService
                        .getClosedGrantCommittedSummaryForGranter(tenantOrg.getId(), status);
        if (activeGrantSummaryCommitted == null) {
            return null;
        }
        Double disbursedAmount = dashboardService.getActiveGrantDisbursedAmountForGranter(tenantOrg.getId(), status);
        Filter categoryFilter = new Filter();
        categoryFilter.setName(StringUtils.capitalize(status + GRANTS));
        categoryFilter.setTotalGrants(activeGrantSummaryCommitted.getGrantCount());
        SimpleDateFormat sd = new SimpleDateFormat("yyyy");
        categoryFilter.setPeriod(sd.format(activeGrantSummaryCommitted.getPeriodStart()) + "-"
                + sd.format(activeGrantSummaryCommitted.getPeriodEnd()));
        categoryFilter.setCommittedAmount(activeGrantSummaryCommitted.getCommittedAmount());
        categoryFilter.setDisbursedAmount(disbursedAmount.longValue());
        List<GranterReportStatus> reportStatuses = null;
        List<GranterReportSummaryStatus> reportsByStatuses = null;
        List<DetailedSummary> reportSummaryList = new ArrayList<>();
        List<DetailedSummary> reportStatusSummaryList = new ArrayList<>();
        if (status.equalsIgnoreCase(ACTIVE)) {
            reportStatuses = dashboardService.getReportStatusSummaryForGranterAndStatus(tenantOrg.getId(), status);
            if (reportStatuses != null && !reportStatuses.isEmpty()) {
                for (GranterReportStatus reportStatus : reportStatuses) {
                    reportSummaryList
                            .add(new ReportSummary(reportStatus.getStatus(), Long.valueOf(reportStatus.getCount())));
                }

                if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase("due"))) {
                    reportSummaryList.add(new ReportSummary("Due", Long.valueOf(0)));
                }

                if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase(OVERDUE))) {
                    reportSummaryList.add(new ReportSummary(OVERDUE_TITLE, Long.valueOf(0)));
                }
            }

            List<String> dueOverdueOrder = Arrays.asList("Due", OVERDUE_TITLE);
            reportSummaryList.sort(Comparator.comparing(c ->
                dueOverdueOrder.indexOf(c.getName())
            ));

            reportsByStatuses = dashboardService.getReportByStatusForGranter(tenantOrg.getId());

            if (reportsByStatuses != null && !reportsByStatuses.isEmpty()) {

                List<Workflow> workflows = workflowService.getAllWorkflowsForGranterByType(tenantOrg.getId(),"REPORT");
                for(Workflow wf : workflows){
                    List<GrantTypeWorkflowMapping> mappings = grantTypeWorkflowMappingService.findByWorkflow(wf.getId());
                    for(GrantTypeWorkflowMapping mapping:mappings){
                        List<TransitionStatusOrder> orderedTransitions = dashboardService.getStatusTransitionOrderByWorflowAndGrantType(wf.getId(),mapping.getGrantTypeId());
                        orderedTransitions.add(0, dashboardService.getStatusTransitionOrderForTerminalState(wf.getId(),mapping.getGrantTypeId()));

                        List<String> statusOrder = orderedTransitions.stream().map(TransitionStatusOrder::getState).collect(Collectors.toList());
                        Comparator<GranterReportSummaryStatus> comparator = Comparator
                                .comparing(c ->
                                            statusOrder.indexOf(c.getStatus())

                                );

                        reportsByStatuses.sort(comparator);

                        for(TransitionStatusOrder order : orderedTransitions){
                            Optional<GranterReportSummaryStatus> optionalReportStatus = reportsByStatuses.stream().filter(r -> r.getStatusId().longValue()==order.getFromStateId().longValue() && r.getGrantTypeId().longValue()==mapping.getGrantTypeId().longValue() && r.getWorkflowId().longValue()==wf.getId().longValue()).findFirst();
                            GranterReportSummaryStatus reportStatus = optionalReportStatus.isPresent() ? optionalReportStatus.get() : initNewGranterReportSummaryStatus(tenantOrg, order);


                            reportStatusSummaryList
                                    .add(new ReportStatusSummary(reportStatus.getStatus(), reportStatus.getInternalStatus(), Long.valueOf(reportStatus.getCount()), reportStatus.getGrantType()));

                        }
                    }
                }
            }
        } else if (status.equalsIgnoreCase(CLOSED)) {
            reportStatuses = dashboardService.findGrantCountsByReportNumbersAndStatusForGranter(tenantOrg.getId(),
                    status);
            if (reportStatuses != null && !reportStatuses.isEmpty()) {
                for (GranterReportStatus reportStatus : reportStatuses) {
                    reportSummaryList
                            .add(new ReportSummary(reportStatus.getStatus(), Long.valueOf(reportStatus.getCount())));
                }
            }
        }

        List<DetailedSummary> disbursalSummaryList = new ArrayList<>();

        Map<Integer, String> periodsMap = dashboardService
                .getActiveGrantsCommittedPeriodsForGranterAndStatus(tenantOrg.getId(), status);

        periodsMap.keySet().forEach(period -> {
            Double[] disbursalTotalAndCount = dashboardService.getDisbursedAmountForGranterAndPeriodAndStatus(period,
                    tenantOrg.getId(), status);
            Long[] committedTotalAndCount = dashboardService.getCommittedAmountForGranterAndPeriodAndStatus(period,
                    tenantOrg.getId(), status);
            disbursalSummaryList.add(new DisbursalSummary(periodsMap.get(period), new DisbursementData[]{
                    new DisbursementData(DISBURSED,
                            String.valueOf(BigDecimal.valueOf(disbursalTotalAndCount[0] / 100000.00).setScale(2,
                                    RoundingMode.HALF_UP)),
                            0l),
                    new DisbursementData(COMMITTED,
                            String.valueOf(new BigDecimal(Double.toString(committedTotalAndCount[0] / 100000.00))
                                    .setScale(2, RoundingMode.HALF_UP)),
                            committedTotalAndCount[1])}));
        });

        List<Detail> filterDetails = new ArrayList<>();
        Map<String, List<DetailedSummary>> reportSummaryMap = new HashMap<>();
        Map<String, List<DetailedSummary>> disbursementSummaryMap = new HashMap<>();
        reportSummaryMap.put("summary", reportSummaryList);
        reportSummaryMap.put("statusSummary", reportStatusSummaryList);
        filterDetails.add(new Detail(REPORTS, reportSummaryMap));
        disbursementSummaryMap.put("disbursement", disbursalSummaryList);
        filterDetails.add(new Detail(DISBURSEMENTS, disbursementSummaryMap));
        categoryFilter.setDetails(filterDetails);

        List<String> detailsOrder = Arrays.asList(DISBURSEMENTS, REPORTS);
        categoryFilter.getDetails().sort(Comparator.comparing(c->
            detailsOrder.indexOf(c.getName())
        ));
        return categoryFilter;
    }

    private Filter getFilterForGranteeGrantsByStatus(Organization granteeOrg, String status) {
        GranterGrantSummaryCommitted activeGrantSummaryCommitted = dashboardService
                .getActiveGrantCommittedSummaryForGrantee(granteeOrg.getId(), status);
        if (activeGrantSummaryCommitted == null) {
            return null;
        }
        Double disbursedAmount = dashboardService.getActiveGrantDisbursedAmountForGrantee(granteeOrg.getId(), status);
        Filter categoryFilter = new Filter();
        categoryFilter.setName(StringUtils.capitalize(status + GRANTS));
        categoryFilter.setTotalGrants(activeGrantSummaryCommitted.getGrantCount());
        SimpleDateFormat sd = new SimpleDateFormat("yyyy");
        categoryFilter.setDonors(granteeService.getDonorsByState(granteeOrg.getId(),status));
        categoryFilter.setPeriod(sd.format(activeGrantSummaryCommitted.getPeriodStart()) + "-"
                + sd.format(activeGrantSummaryCommitted.getPeriodEnd()));
        categoryFilter.setCommittedAmount(activeGrantSummaryCommitted.getCommittedAmount());
        categoryFilter.setDisbursedAmount(disbursedAmount.longValue());
        List<GranterReportStatus> reportStatuses = null;
        List<GranteeReportStatus> reportAprrovedStatuses = null;
        List<DetailedSummary> reportSummaryList = new ArrayList<>();
        List<DetailedSummary> reportStatusSummaryList = new ArrayList<>();
        if (status.equalsIgnoreCase(ACTIVE)) {
            reportStatuses = dashboardService.getReportStatusSummaryForGranteeAndStatus(granteeOrg.getId(), status);
            if (reportStatuses != null && !reportStatuses.isEmpty()) {
                for (GranterReportStatus reportStatus : reportStatuses) {
                    reportSummaryList
                            .add(new ReportSummary(reportStatus.getStatus(), Long.valueOf(reportStatus.getCount())));
                }

                if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase("due"))) {
                    reportSummaryList.add(new ReportSummary("Due", Long.valueOf(0)));
                }

                if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase(OVERDUE))) {
                    reportSummaryList.add(new ReportSummary(OVERDUE_TITLE, Long.valueOf(0)));
                }

                if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase("submitted"))) {
                    reportSummaryList.add(new ReportSummary("Submitted", Long.valueOf(0)));
                }
            }

            List<String> dueOverdueOrder = Arrays.asList("Due", OVERDUE_TITLE);
            reportSummaryList.sort(Comparator.comparing(c ->
                dueOverdueOrder.indexOf(c.getName())
            ));

            reportAprrovedStatuses = dashboardService.getReportApprovedStatusSummaryForGranteeAndStatusByGranter(granteeOrg.getId(), status);
            if (reportAprrovedStatuses != null && !reportAprrovedStatuses.isEmpty()) {
                for (GranteeReportStatus reportStatus : reportAprrovedStatuses) {
                    reportStatusSummaryList
                            .add(new ReportSummary(reportStatus.getInternalStatus(), Long.valueOf(reportStatus.getCount())));
                }
            }


        } else if (status.equalsIgnoreCase(CLOSED)) {
            reportStatuses = dashboardService.getReportStatusSummaryForGranteeAndStatus(granteeOrg.getId(),
                    status);
            if (reportStatuses != null && !reportStatuses.isEmpty()) {
                for (GranterReportStatus reportStatus : reportStatuses) {
                    reportSummaryList
                            .add(new ReportSummary(reportStatus.getStatus(), Long.valueOf(reportStatus.getCount())));
                }

                if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase("due"))) {
                    reportSummaryList.add(new ReportSummary("Due", Long.valueOf(0)));
                }

                if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase(OVERDUE))) {
                    reportSummaryList.add(new ReportSummary(OVERDUE_TITLE, Long.valueOf(0)));
                }

                if (reportSummaryList.stream().noneMatch(l -> l.getName().equalsIgnoreCase("submitted"))) {
                    reportSummaryList.add(new ReportSummary("Submitted", Long.valueOf(0)));
                }

                reportAprrovedStatuses = dashboardService.getReportApprovedStatusSummaryForGranteeAndStatusByGranter(granteeOrg.getId(), status);
                if (reportAprrovedStatuses != null && !reportAprrovedStatuses.isEmpty()) {
                    for (GranteeReportStatus reportStatus : reportAprrovedStatuses) {
                        reportStatusSummaryList
                                .add(new ReportSummary(reportStatus.getInternalStatus(), Long.valueOf(reportStatus.getCount())));
                    }
                }
            }
        }




        List<Detail> filterDetails = new ArrayList<>();
        Map<String, List<DetailedSummary>> reportSummaryMap = new HashMap<>();
        reportSummaryMap.put("summary", reportSummaryList);
        reportSummaryMap.put("approvedSummary", reportStatusSummaryList);
        filterDetails.add(new Detail(REPORTS, reportSummaryMap));

        categoryFilter.setDetails(filterDetails);

        List<String> detailsOrder = Arrays.asList(REPORTS);
        categoryFilter.getDetails().sort(Comparator.comparing(c->
            detailsOrder.indexOf(c.getName())
        ));
        return categoryFilter;
    }

    private GranterReportSummaryStatus initNewGranterReportSummaryStatus(Organization tenantOrg,TransitionStatusOrder order) {
        GranterReportSummaryStatus reportStatus = new GranterReportSummaryStatus();
        reportStatus.setCount(0);
        reportStatus.setGranterId(tenantOrg.getId());
        reportStatus.setId(1L);
        reportStatus.setInternalStatus(order.getInternalStatus());
        reportStatus.setStatus(order.getState());
        reportStatus.setGrantTypeId(order.getGrantTypeId());
        reportStatus.setGrantType(grantTypeService.findById(order.getGrantTypeId()).getName());
        return reportStatus;
    }

    @GetMapping("/forgot/{emailId}")
    public ResponseEntity<PasswordResetRequest> forgotPassword(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                                               @PathVariable("emailId") String emailId) {
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        User user = null;
        PasswordResetRequest response = new PasswordResetRequest();
        if (tenantOrg.getOrganizationType().equalsIgnoreCase(PLATFORM)) {
            List<User> users = userService.getUsersByEmail(emailId);
            if (users.size() == 1) {
                user = users.get(0);
            } else {
                response.setMessage("Multiple organizations found for this user.");
                return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
            }

        } else if (tenantOrg.getOrganizationType().equalsIgnoreCase(GRANTER)) {
            user = userService.getUserByEmailAndOrg(emailId, tenantOrg);
        }

        if (user != null && user.isActive() && !user.isDeleted()) {
            response = sendPasswordResetLink(user);
            if(response!=null){
                response.setMessage("Password reset email sent to " + user.getEmailId());
                response.setStatus("registered");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (user != null && !user.isActive()) {
            response.setOrg(user.getOrganization().getName());
            response.setStatus("unregistered");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (user != null && user.isActive() && user.isDeleted()) {
            response.setOrg(user.getOrganization().getName());
            response.setStatus("inactive");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("Invalid email address.");
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    private PasswordResetRequest sendPasswordResetLink(User user) {
        Organization tenantOrg = null;
        if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE)
                || user.getOrganization().getOrganizationType().equalsIgnoreCase(PLATFORM)) {
            tenantOrg = organizationService.findOrganizationByTenantCode("ANUDAN");
        } else if (user.getOrganization().getOrganizationType().equalsIgnoreCase(GRANTER)) {
            tenantOrg = user.getOrganization();
        }

        if (tenantOrg != null) {
            String mailSubject = appConfigService
                    .getAppConfigForGranterOrg(tenantOrg.getId(), AppConfiguration.FORGOT_PASSWORD_MAIL_SUBJECT)
                    .getConfigValue();
            String mailMessage = appConfigService
                    .getAppConfigForGranterOrg(tenantOrg.getId(), AppConfiguration.FORGOT_PASSWORD_MAIL_MESSAGE)
                    .getConfigValue();
            String mailFooter = appConfigService
                    .getAppConfigForGranterOrg(tenantOrg.getId(), AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()
                    .replace("%RELEASE_VERSION%", releaseService.getCurrentRelease().getVersion()).replace("%TENANT%", tenantOrg.getName());

            return userService.sendPasswordResetMail(user, mailSubject, mailMessage, mailFooter);
        }
        return null;
    }

    @PostMapping("/set-password")
    public ResponseEntity<User> resetPassword(@RequestHeader("X-TENANT-CODE") String tenantCode,
                                              @RequestBody ResetPwdData resetPwdData) {

        User user = null;
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        Organization userOrg = null;
        if (tenantOrg.getOrganizationType().equalsIgnoreCase(PLATFORM)) {
            userOrg = organizationService.findByNameAndOrganizationType(resetPwdData.getOrg(), GRANTEE);

        } else if (tenantOrg.getOrganizationType().equalsIgnoreCase(GRANTER)) {
            userOrg = tenantOrg;
        }
        user = userService.getUserByEmailAndOrg(resetPwdData.getEmail(), userOrg);

        PasswordResetRequest resetRequest = passwordResetRequestService
                .findByUnvalidatedUserIdAndKeyAndOrgId(user.getId(), resetPwdData.getKey(), userOrg!=null?userOrg.getId():0l);
        if (resetRequest != null) {
            if (passwordEncoder.matches(
                    resetPwdData.getKey()
                            .concat(resetPwdData.getEmail().concat(String.valueOf(user.getOrganization().getId()))),
                    resetRequest.getCode())) {
                if (resetPwdData.getPwd1().equalsIgnoreCase(resetPwdData.getPwd2())) {
                    user.setPassword(passwordEncoder.encode(resetPwdData.getPwd1()));
                    user.setPlain(false);
                    user = userService.save(user);
                    resetRequest.setValidated(true);
                    resetRequest.setValidatedOn(DateTime.now().toDate());
                    passwordResetRequestService.savePasswordResetRequest(resetRequest);
                } else {
                    return new ResponseEntity<>(new User(), HttpStatus.METHOD_NOT_ALLOWED);
                }

            } else {
                return new ResponseEntity<>(new User(), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } else {
            return new ResponseEntity<>(new User(), HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{userId}/dashboard/mysummary/pendinggrants")
    public List<GrantCard> getPendingDetailedGrantsForUser(@PathVariable("userId")Long userId){
        return grantService.getDetailedActionDueGrantsForUser(userId);
    }
    @GetMapping("/{userId}/dashboard/mysummary/pendingreports")
    public List<ReportCard> getPendingDetailedReportForUser(@PathVariable("userId")Long userId){
        return grantService.getDetailedActionDueReportsForUser(userId);
    }
    @GetMapping("/{userId}/dashboard/mysummary/pendingdisbursements")
    public List<org.codealpha.gmsservice.entities.Disbursement> getPendingDetaileddisbursementsForUser(@PathVariable("userId")Long userId){
        List<org.codealpha.gmsservice.entities.Disbursement> disbursements=  disbursementService.getDetailedPendingActionDisbursements(userId);
        for(org.codealpha.gmsservice.entities.Disbursement disbursement: disbursements){
            disbursementService.disbursementToReturn(disbursement,userId);
        }
        return disbursements;
    }

    @GetMapping("/{userId}/dashboard/mysummary/upcomingdraftgrants")
    public List<GrantCard> getUpcomingDetailedGrantsForUser(@PathVariable("userId")Long userId){
        return grantService.getDetailedUpComingDraftGrants(userId);
    }

    @GetMapping("/{userId}/dashboard/mysummary/grants/{status}")
    public List<Grant> getGrantsForUserByStatus(@PathVariable("userId")Long userId,@PathVariable("status")String status){
        List<Grant> grants =  grantService.getgrantsByStatusForUser(userId,status.toUpperCase());
        for(Grant grant : grants){
            grantService.grantToReturn(userId, grant);
        }
        return grants;
    }

    @GetMapping("/{userId}/dashboard/mysummary/upcomingdraftreports")
    public List<ReportCard> getUpcomingDetailedReportsForUser(@PathVariable("userId")Long userId){
        return reportService.getDetailedUpComingDraftReports(userId);
    }

    @GetMapping("/{userId}/dashboard/mysummary/upcomingdraftdisbursements")
    public List<org.codealpha.gmsservice.entities.Disbursement> getUpcomingDetailedDisbursementsForUser(@PathVariable("userId")Long userId){
        List<org.codealpha.gmsservice.entities.Disbursement> disbursements=  disbursementService.getDetailedUpComingDraftDisbursements(userId);
        for(org.codealpha.gmsservice.entities.Disbursement disbursement: disbursements){
            disbursementService.disbursementToReturn(disbursement,userId);
        }
        return disbursements;
    }


}
