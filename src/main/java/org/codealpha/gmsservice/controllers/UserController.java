package org.codealpha.gmsservice.controllers;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.PasswordResetRequest;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.dashboard.*;
import org.codealpha.gmsservice.exceptions.ApplicationException;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.models.ErrorMessage;
import org.codealpha.gmsservice.models.ResetPwdData;
import org.codealpha.gmsservice.models.UserCheck;
import org.codealpha.gmsservice.models.UserVO;
import org.codealpha.gmsservice.models.dashboard.*;
import org.codealpha.gmsservice.services.*;
import org.codealpha.gmsservice.validators.DashboardValidator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping("/users")
public class UserController {


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
    @Autowired private AppConfigService appConfigService;
    @Autowired private PasswordResetRequestService passwordResetRequestService;

    @GetMapping(value = "/{userId}")
    public User get(@PathVariable(name = "userId") Long id,
                    @RequestHeader("X-TENANT-CODE") String tenantCode) {
        User user = userService.getUserById(id);

        return user;
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional
    public User create(@RequestBody UserVO user, @RequestHeader("X-TENANT-CODE") String tenantCode, HttpServletResponse response, HttpServletRequest request) {

        Organization org = null;
        if (tenantCode.equalsIgnoreCase("ANUDAN")) {
            org = organizationService.findByNameAndOrganizationType(user.getOrganizationName(), "GRANTEE");
        } else {
            org = organizationService.findOrganizationByTenantCode(tenantCode);
        }
        User newUser = userService.getUserByEmailAndOrg(user.getEmailId(), org);
        if (newUser == null) {
            newUser = new User();

            //BCryptPasswordEncoder a  = new BCryptPasswordEncoder
            newUser.setCreatedAt(DateTime.now().toDate());
            newUser.setCreatedBy("Api");
            newUser.setEmailId(user.getEmailId());

            newUser.setOrganization(org);
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));


            UriComponents urlComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();

            String scheme = urlComponents.getScheme();
            String host = urlComponents.getHost();
            int port = urlComponents.getPort();

            String verificationLink =
                    scheme + "://" + host + (port != -1 ? ":" + port : "") + "/grantee/verification?emailId="
                            + user.getEmailId() + "&code=" + RandomStringUtils.randomAlphanumeric(127);

            System.out.println(verificationLink);
            commonEmailSevice
                    .sendMail(user.getEmailId(), null, "Anudan.org - Verification Link", verificationLink, null);
        } else {
            newUser.setActive(true);
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser = userService.save(newUser);
        }


        return newUser;
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User update(@ApiParam(name = "user", value = "User details") @RequestBody UserVO user, @ApiParam(name = "userId", value = "Unique identifier of user") @PathVariable("userId") Long userId, @ApiParam(name = "X-TENANT-CODE", value = "Tenant code") @RequestHeader("X-TENANT-CODE") String tenantCode) {
        //BCryptPasswordEncoder a  = new BCryptPasswordEncoder
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
    public HttpStatus verifyUser(@RequestParam("emailId") String email,
                                 @RequestParam("code") String code) {
        return HttpStatus.OK;
    }

    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<DashboardService> getDashbaord(
            @RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {

        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        Organization userOrg = user.getOrganization();
        Organization tenantOrg = null;
        tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        List<Grant> grants = null;
        switch (userOrg.getType()) {
            case "GRANTEE":
                grants = granteeService
                        .getGrantsOfGranteeForGrantor(userOrg.getId(), tenantOrg, user.getUserRoles());
                return new ResponseEntity<>(dashboardService.build(user, grants, tenantOrg), HttpStatus.OK);
            case "GRANTER":
                grants = granterService.getGrantsOfGranterForGrantor(userOrg.getId(), tenantOrg, user.getId());
                return new ResponseEntity<>(dashboardService.build(user, grants, tenantOrg), HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/{userId}/dashboard/in-progress")
    public ResponseEntity<Long> getInProgressGrantsOfUser(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {
        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        Organization userOrg = user.getOrganization();
        Organization tenantOrg = null;
        tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        List<Grant> grants = null;
        switch (userOrg.getType()) {
            case "GRANTEE":
                return new ResponseEntity<>(granterService.getActiveGrantsOfGranteeForGrantor(userOrg.getId(), tenantOrg, user.getId()), HttpStatus.OK);
            case "GRANTER":
                return new ResponseEntity<>(granterService.getInProgressGrantsOfGranterForGrantor(userOrg.getId(), tenantOrg, user.getId()), HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/{userId}/dashboard/active")
    public ResponseEntity<Long> getActiveGrantsOfUser(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {
        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        Organization userOrg = user.getOrganization();
        Organization tenantOrg = null;
        tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        List<Grant> grants = null;
        switch (userOrg.getType()) {
            case "GRANTEE":
                return new ResponseEntity<>(granterService.getActiveGrantsOfGranteeForGrantor(userOrg.getId(), tenantOrg, user.getId()), HttpStatus.OK);
            case "GRANTER":
                return new ResponseEntity<>(granterService.getActiveGrantsOfGranterForGrantor(userOrg.getId(), tenantOrg, user.getId()), HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/{userId}/dashboard/closed")
    public ResponseEntity<Long> getClosedGrantsOfUser(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {
        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        Organization userOrg = user.getOrganization();
        Organization tenantOrg = null;
        tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        List<Grant> grants = null;
        switch (userOrg.getType()) {
            case "GRANTEE":
                return new ResponseEntity<>(granterService.getClosedGrantsOfGranteeForGrantor(userOrg.getId(), tenantOrg, user.getId()), HttpStatus.OK);
            case "GRANTER":
                return new ResponseEntity<>(granterService.getClosedGrantsOfGranterForGrantor(userOrg.getId(), tenantOrg, user.getId()), HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/{id}/validate-pwd")
    public ResponseEntity<ErrorMessage> validatePassword(@PathVariable("id") Long userId,
                                                         @RequestBody String pwd, @RequestHeader("X-TENANT-CODE") String tenantCode) {
        dashboardValidator.validate(userId, tenantCode);
        User user = userService.getUserById(userId);
        if (passwordEncoder.matches(pwd,user.getPassword())) {
            return new ResponseEntity<>(new ErrorMessage(true, ""), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("You have entered an invalid previous password");
        }
    }

    @PostMapping("/{id}/pwd")
    public ResponseEntity<User> changePassword(@PathVariable("id") Long userId,
                                               @RequestBody String[] pwds, @RequestHeader("X-TENANT-CODE") String tenantCode) {
        dashboardValidator.validate(userId, tenantCode);
        if (pwds.length != 3) {
            throw new ResourceNotFoundException("Invalid information sent for setting password");
        }
        if (!pwds[1].equalsIgnoreCase(pwds[2])) {
            throw new ResourceNotFoundException("New passwords do not match");
        }
        if (!pwds[1].matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})") && !pwds[2].matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})")) {
            throw new ResourceNotFoundException("Password must contain at least one digit, one lowercase character, one uppercase character, one special symbols in the list \"@#$%\" and between 6-20 characters.");
        }
        User user = userService.getUserById(userId);
        if(user.isPlain()){
            if (!user.getPassword().equalsIgnoreCase(pwds[0])) {
                throw new ResourceNotFoundException("You have entered an invalid previous password");
            }
        }else{
            if(!passwordEncoder.matches(pwds[0],user.getPassword())){
                throw new ResourceNotFoundException("You have entered an invalid previous password");
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
        if (user != null && user.isActive()) {
            return true;
        }

        return false;
    }

    @GetMapping("/{userId}/dashboard/summary")
    public ResponseEntity<Category> getDasboardSummary(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("userId") Long userId) {
        DashboardSummary dashboardSummary = new DashboardSummary();
        Category dashboardCategory = null;
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);

        GranterCountAndAmountTotal countAndAmountTotal = dashboardService.getSummaryForGranter(tenantOrg.getId());
        GranterGrantee granteeSummary = dashboardService.getGranteesSummaryForGranter(tenantOrg.getId());
        GranterActiveUser granterActiveUserSummary = dashboardService.getActiveUserSummaryForGranter(tenantOrg.getId());
        Summary categorySummary = new Summary(Long.valueOf(countAndAmountTotal==null?0l:countAndAmountTotal.getTotalGrants()), Long.valueOf(granteeSummary==null?0l:granteeSummary.getGranteeTotals()), Long.valueOf(countAndAmountTotal==null?0l:countAndAmountTotal.getTotalGrantAmount()), Long.valueOf(granterActiveUserSummary==null?0l:granterActiveUserSummary.getActiveUsers()));


        List<Filter> categoryFilters = new ArrayList<>();

        Filter activeFilter = getFilterForGrantsByStatus(tenantOrg,"ACTIVE");
        if(activeFilter!=null) {
            categoryFilters.add(activeFilter);
        }
        Filter closedFilter = getFilterForGrantsByStatus(tenantOrg,"CLOSED");
        if(closedFilter!=null) {
            categoryFilters.add(getFilterForGrantsByStatus(tenantOrg, "CLOSED"));
        }

        dashboardCategory = new Category("CEO", categorySummary, categoryFilters);


        return new ResponseEntity(dashboardCategory, HttpStatus.OK);
    }

    private Filter getFilterForGrantsByStatus(Organization tenantOrg,String status) {
        GranterGrantSummaryCommitted activeGrantSummaryCommitted = dashboardService.getActiveGrantCommittedSummaryForGranter(tenantOrg.getId(),status);
        if(activeGrantSummaryCommitted==null){
            return null;
        }
        Long disbursedAmount = dashboardService.getActiveGrantDisbursedAmountForGranter(tenantOrg.getId(),status);
        Filter categoryFilter = new Filter();
        categoryFilter.setName(WordUtils.capitalizeFully(status+" Grants"));
        categoryFilter.setTotalGrants(Long.valueOf(activeGrantSummaryCommitted.getGrantCount()));
        SimpleDateFormat sd = new SimpleDateFormat("yyyy");
        categoryFilter.setPeriod(sd.format(activeGrantSummaryCommitted.getPeriodStart()) + "-" + sd.format(activeGrantSummaryCommitted.getPeriodEnd()));
        categoryFilter.setCommittedAmount(Long.valueOf(activeGrantSummaryCommitted.getCommittedAmount()));
        categoryFilter.setDisbursedAmount(Long.valueOf(disbursedAmount));
        List<GranterReportStatus> reportStatuses = dashboardService.getReportStatusSummaryForGranterAndStatus(tenantOrg.getId(), status);

        List<DetailedSummary> reportSummaryList = new ArrayList<>();
        if (reportStatuses != null && reportStatuses.size() > 0) {
            for (GranterReportStatus reportStatus : reportStatuses) {
                reportSummaryList.add(new ReportSummary(reportStatus.getStatus(), Long.valueOf(reportStatus.getCount())));
            }

            if(!reportSummaryList.stream().filter(l -> l.getName().equalsIgnoreCase("due")).findAny().isPresent()){
                reportSummaryList.add(new ReportSummary("Due",Long.valueOf(0)));
            }
            if(!reportSummaryList.stream().filter(l -> l.getName().equalsIgnoreCase("unapproved")).findAny().isPresent()){
                reportSummaryList.add(new ReportSummary("Unapproved",Long.valueOf(0)));
            }
            if(!reportSummaryList.stream().filter(l -> l.getName().equalsIgnoreCase("approved")).findAny().isPresent()){
                reportSummaryList.add(new ReportSummary("Approved",Long.valueOf(0)));
            }
            if(!reportSummaryList.stream().filter(l -> l.getName().equalsIgnoreCase("overdue")).findAny().isPresent()){
                reportSummaryList.add(new ReportSummary("Overdue",Long.valueOf(0)));
            }
        }

        List<DetailedSummary> disbursalSummaryList = new ArrayList<>();

        Map<Integer,String> periodsMap = dashboardService.getActiveGrantsCommittedPeriodsForGranterAndStatus(tenantOrg.getId(),status);
        List<String> periods = periodsMap.entrySet().stream().map( p -> new String(p.getValue())).collect(Collectors.toList());
        String[] strings = {"Committed", "Disbursed"};

        for (Integer period : periodsMap.keySet()) {
            Long[] disbursalTotalAndCount = dashboardService.getDisbursedAmountForGranterAndPeriodAndStatus(period,tenantOrg.getId(),status);
            Long[] committedTotalAndCount = dashboardService.getCommittedAmountForGranterAndPeriodAndStatus(period,tenantOrg.getId(),status);
            disbursalSummaryList.add(new DisbursalSummary(periodsMap.get(period),new DisbursementData[]{new DisbursementData("Disbursed",String.valueOf(disbursalTotalAndCount[0]/100000),disbursalTotalAndCount[1]),new DisbursementData("Committed",String.valueOf(committedTotalAndCount[0]/100000),committedTotalAndCount[1])}));
        }



        List<Detail> filterDetails = new ArrayList<>();
        filterDetails.add(new Detail("Reports", reportSummaryList));
        filterDetails.add(new Detail("Disbursements", disbursalSummaryList));
        categoryFilter.setDetails(filterDetails);
        return categoryFilter;
    }


    @GetMapping("/forgot/{emailId}")
    public ResponseEntity<PasswordResetRequest> forgotPassword(@RequestHeader("X-TENANT-CODE") String tenantCode, @PathVariable("emailId") String emailId){
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        User user = null;
        PasswordResetRequest response = new PasswordResetRequest();
        if(tenantOrg.getOrganizationType().equalsIgnoreCase("PLATFORM")){
            List<User> users = userService.getUsersByEmail(emailId);
            if(users.size()==1){
                user = users.get(0);
            }else{
                response.setMessage("Multiple organizations found for this user.");
                return new ResponseEntity(response,HttpStatus.EXPECTATION_FAILED);
            }


        }else if(tenantOrg.getOrganizationType().equalsIgnoreCase("GRANTER")){
            user = userService.getUserByEmailAndOrg(emailId,tenantOrg);
        }

        if(user!=null && user.isActive()){
            response = sendPasswordResetLink(user);
            response.setMessage("Password reset email sent to " + user.getEmailId());
            response.setStatus("registered");
            return new ResponseEntity(response,HttpStatus.OK);
        }else if(user!=null && !user.isActive()){
            response.setOrg(user.getOrganization().getName());
            response.setStatus("unregistered");
            return new ResponseEntity(response,HttpStatus.OK);
        }else{
            response.setMessage("Invalid email address.");
            return new ResponseEntity(response,HttpStatus.EXPECTATION_FAILED);
        }
    }

    private PasswordResetRequest sendPasswordResetLink(User user) {
        Organization tenantOrg = null;
        if(user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE") || user.getOrganization().getOrganizationType().equalsIgnoreCase("PLATFORM")){
            tenantOrg = organizationService.findOrganizationByTenantCode("ANUDAN");
        }else if(user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")){
            tenantOrg = user.getOrganization();
        }

        String mailSubject = appConfigService.getAppConfigForGranterOrg(tenantOrg.getId(), AppConfiguration.FORGOT_PASSWORD_MAIL_SUBJECT).getConfigValue();
        String mailMessage = appConfigService.getAppConfigForGranterOrg(tenantOrg.getId(), AppConfiguration.FORGOT_PASSWORD_MAIL_MESSAGE).getConfigValue();
        String mailFooter = appConfigService.getAppConfigForGranterOrg(tenantOrg.getId(),AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue();

        return userService.sendPasswordResetMail(user,mailSubject,mailMessage,mailFooter);
    }


    @PostMapping("/set-password")
    public ResponseEntity<User> resetPassword(@RequestHeader("X-TENANT-CODE") String tenantCode, @RequestBody ResetPwdData resetPwdData){

        User user = null;
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        Organization userOrg = null;
        if(tenantOrg.getOrganizationType().equalsIgnoreCase("PLATFORM")){
            userOrg = organizationService.findByNameAndOrganizationType(resetPwdData.getOrg(),"GRANTEE");

        }else if(tenantOrg.getOrganizationType().equalsIgnoreCase("GRANTER")){
            userOrg = tenantOrg;
        }
        user = userService.getUserByEmailAndOrg(resetPwdData.getEmail(),userOrg);

        PasswordResetRequest resetRequest = passwordResetRequestService.findByUnvalidatedUserIdAndKeyAndOrgId(user.getId(),resetPwdData.getKey(),userOrg.getId());
        if(resetRequest!=null) {
            if (passwordEncoder.matches(resetPwdData.getKey().concat(resetPwdData.getEmail().concat(String.valueOf(user.getOrganization().getId()))),resetRequest.getCode())){
                if (resetPwdData.getPwd1().equalsIgnoreCase(resetPwdData.getPwd2())) {
                    user.setPassword(passwordEncoder.encode(resetPwdData.getPwd1()));
                    user.setPlain(false);
                    user = userService.save(user);
                    resetRequest.setValidated(true);
                    resetRequest.setValidatedOn(DateTime.now().toDate());
                    passwordResetRequestService.savePasswordResetRequest(resetRequest);
                } else {
                    return new ResponseEntity(null,HttpStatus.METHOD_NOT_ALLOWED);
                }

            }else{
                return new ResponseEntity(null,HttpStatus.METHOD_NOT_ALLOWED);
            }
        }else{
            return new ResponseEntity(null,HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity(user,HttpStatus.OK);
    }
}
