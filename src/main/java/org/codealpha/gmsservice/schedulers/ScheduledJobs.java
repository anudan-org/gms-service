package org.codealpha.gmsservice.schedulers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.AppRelease;
import org.codealpha.gmsservice.models.ScheduledTaskVO;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ScheduledJobs {

    public static final Logger logger = LoggerFactory.getLogger(ScheduledJobs.class);
    public static final String GRANTEE = "GRANTEE";
    public static final String RELEASE_VERSION = "%RELEASE_VERSION%";
    public static final String LOCAL = "local";
    public static final String HTTPS = "https://";
    public static final String ACTIVE = "ACTIVE";
    public static final String TENANT = "%TENANT%";
    @Autowired
    private GranterService granterService;
    @Autowired
    private ReportService reportService;
    @Autowired
    AppConfigService appConfigService;
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommonEmailSevice emailSevice;
    @Autowired
    private UserService userService;
    @Autowired
    private ReleaseService releaseService;
    @Value("${spring.profiles.active}")
    private String environment;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private DisbursementService disbursementService;
    @Value("${spring.timezone}")
    private String timezone;
    @Autowired
    private HygieneCheckService hygieneCheckService;
    @Autowired
    DataSource dataSource;
    @Autowired
    private GrantClosureService closureService;

    @Scheduled(cron = "0 * * * * *")
    public void dueReportsChecker() {
        DateTime now = DateTime.now();

        List<Granter> granters = granterService.getAllGranters();

        Map<Long, AppConfig> configs = new HashMap<>();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(),
                    AppConfiguration.DUE_REPORTS_REMINDER_SETTINGS);
            configs.put(config.getId(), config);
        }

        Map<Long, AppConfig> grantIdsToSkip = new HashMap<>();
        configs.keySet().forEach(c -> grantIdsToSkip.put(c, configs.get(c)));

        for (Map.Entry<Long, AppConfig> entry : configs.entrySet()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(entry.getValue().getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if (entry.getKey() == 0) {

                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                        int[] daysBefore = taskConfiguration.getConfiguration().getDaysBefore();
                        for (int db : daysBefore) {
                            Date dueDate = now.withTimeAtStartOfDay().plusDays(db).toDate();
                            List<Report> reportsToNotify = reportService.getDueReportsForPlatform(dueDate,
                                    grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                            for (Report report : reportsToNotify) {
                                notifyGranteeUserAndCCInternalUsers(taskConfiguration, report);
                            }
                        }

                    }
                } else {
                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {
                        int[] daysBefore = taskConfiguration.getConfiguration().getDaysBefore();
                        for (int db : daysBefore) {
                            Date dueDate = now.withTimeAtStartOfDay().plusDays(db).toDate();
                            List<Report> reportsToNotify = reportService.getDueReportsForGranter(dueDate, entry.getKey());
                            for (Report report : reportsToNotify) {
                                notifyGranteeUserAndCCInternalUsers(taskConfiguration, report);
                            }
                        }
                    }
                }

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void notifyGranteeUserAndCCInternalUsers(ScheduledTaskVO taskConfiguration, Report report) {
        List<ReportAssignment> assignments = reportService.getAssignmentsForReport(report);
        Optional<ReportAssignment> check = assignments.stream()
                .filter(ass -> userService.getUserById(ass.getAssignment()).getOrganization()
                        .getOrganizationType().equalsIgnoreCase(GRANTEE))
                .findFirst();
        User granteeToNotify = userService
                .getUserById(
                        check.isPresent() ? check.get().getAssignment() : 0);
        List<ReportAssignment> ccAssignments = assignments.stream().filter(ass -> !userService
                .getUserById(ass.getAssignment()).getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE))
                .collect(Collectors.toList());
        List<String> otherUsersToNotify = new ArrayList<>();
        for (ReportAssignment ccAssignment : ccAssignments) {
            User u = userService.getUserById(ccAssignment.getAssignment());
            if (u.isDeleted()) {
                continue;
            }
            otherUsersToNotify.add(u.getEmailId());
        }

        String link = buildLink(environment, false, "");
        Optional<WorkflowStatus> wfStatus = workflowStatusService
                .getTenantWorkflowStatuses("GRANT", report.getGrant().getGrantorOrganization().getId()).stream()
                .filter(s -> s.getInternalStatus().equalsIgnoreCase(ACTIVE)).findFirst();
        WorkflowStatus grantActiveState = wfStatus.isPresent() ? wfStatus.get() : null;
        List<GrantAssignments> grantAssignments = grantService.getGrantWorkflowAssignments(report.getGrant());
        Optional<GrantAssignments> wfAssignment = grantAssignments.stream().filter(ass -> ass.getStateId().longValue() == grantActiveState.getId().longValue())
                .findFirst();
        User owner = userService.getUserById(
                wfAssignment.isPresent() ? wfAssignment.get().getAssignments() : 0);
        String[] messageMetadata = reportService.buildEmailNotificationContent(report, granteeToNotify,
                taskConfiguration.getSubjectReport(), taskConfiguration.getMessageReport(), "", "", "", "", "", "", "",
                "", "", link, owner, null, null, null);

        emailSevice.sendMail(new String[]{!granteeToNotify.isDeleted() ? granteeToNotify.getEmailId() : null},
                otherUsersToNotify.toArray(new String[]{}), messageMetadata[0], messageMetadata[1],
                new String[]{appConfigService
                        .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                        .getConfigValue()
                        .replace(RELEASE_VERSION, releaseService.getCurrentRelease().getVersion()).replace(TENANT,report.getGrant()
                        .getGrantorOrganization().getName())});
    }

    private String buildLink(String environment, boolean forTenant, String tenant) {
        if (!forTenant) {
            switch (environment) {
                case LOCAL:
                    return "http://localhost:4200";
                case "dev":
                    return "https://dev.anudan.org";
                case "qa":
                    return "https://qa.anudan.org";
                case "uat":
                    return "https://uat.anudan.org";
                default:
                    return "https://anudan.org";
            }
        } else {
            switch (environment) {
                case LOCAL:
                    return "http://" + tenant + ".localhost:4200";
                case "dev":
                    return HTTPS + tenant + ".dev.anudan.org";
                case "qa":
                    return HTTPS + tenant + ".qa.anudan.org";
                case "uat":
                    return HTTPS + tenant + ".uat.anudan.org";
                default:
                    return HTTPS + tenant + ".anudan.org";
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void reportsActionDueChecker() {
        DateTime now = DateTime.now();

        List<Granter> granters = granterService.getAllGranters();

        Map<Long, AppConfig> configs = new HashMap<>();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(),
                    AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS);
            configs.put(config.getId(), config);
        }

        Map<Long, AppConfig> grantIdsToSkip = new HashMap<>();
        configs.keySet().forEach(c -> grantIdsToSkip.put(c, configs.get(c)));

        for (Map.Entry<Long, AppConfig> entry : configs.entrySet()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(entry.getValue().getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if (entry.getKey() == 0) {

                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {
                        List<ReportAssignment> usersToNotify = reportService.getActionDueReportsForPlatform(
                                grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        if (usersToNotify != null && !usersToNotify.isEmpty()) {
                            for (ReportAssignment reportAssignment : usersToNotify) {
                                Report report = reportService.getReportById(reportAssignment.getReportId());

                                List<ReportAssignment> reportAssignments = reportService
                                        .getAssignmentsForReport(report);
                                reportAssignments.removeIf(u -> userService.getUserById(u.getAssignment())
                                        .getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE));
                                String[] ccList = new String[reportAssignments.size()];

                                if (!reportAssignments.isEmpty()) {
                                    List<User> uList = new ArrayList<>();
                                    for (ReportAssignment ass : reportAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignment()));
                                    }
                                    uList.removeIf(User::isDeleted);
                                    ccList = uList.stream().map(User::getEmailId).collect(Collectors.toList())
                                            .toArray(new String[reportAssignments.size()]);
                                }
                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    int minuetsLapsed = Minutes.minutesBetween(
                                            new DateTime(report.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes();
                                    if (Minutes.minutesBetween(
                                            new DateTime(report.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes() > afterNoOfHour) {
                                        User user = userService.getUserById(reportAssignment.getAssignment());
                                        String[] messageMetadata = reportService.buildEmailNotificationContent(report,
                                                user,
                                                taskConfiguration.getSubjectReport(),
                                                taskConfiguration.getMessageReport(), "", "", "", "", "", "", "", "",
                                                "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice
                                                .sendMail(new String[]{!user.isDeleted() ? user.getEmailId() : null},
                                                        ccList, messageMetadata[0], messageMetadata[1],
                                                        new String[]{appConfigService
                                                                .getAppConfigForGranterOrg(
                                                                        report.getGrant().getGrantorOrganization()
                                                                                .getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                                .getConfigValue()
                                                                .replace(RELEASE_VERSION, releaseService
                                                                .getCurrentRelease().getVersion()).replace(TENANT,report.getGrant()
                                                                .getGrantorOrganization().getName())});
                                    }
                                }

                            }
                        }
                    }
                } else {
                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                        List<ReportAssignment> usersToNotify = reportService.getActionDueReportsForGranterOrg(entry.getKey());
                        if (usersToNotify != null && !usersToNotify.isEmpty()) {
                            for (ReportAssignment reportAssignment : usersToNotify) {
                                Report report = reportService.getReportById(reportAssignment.getReportId());

                                List<ReportAssignment> reportAssignments = reportService
                                        .getAssignmentsForReport(report);
                                reportAssignments.removeIf(u -> userService.getUserById(u.getAssignment()).getOrganization()
                                        .getOrganizationType().equalsIgnoreCase(GRANTEE));
                                String[] ccList = new String[reportAssignments.size()];

                                if (!reportAssignments.isEmpty()) {
                                    List<User> uList = new ArrayList<>();
                                    for (ReportAssignment ass : reportAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignment()));
                                    }
                                    uList.removeIf(User::isDeleted);
                                    ccList = uList.stream().map(User::getEmailId).collect(Collectors.toList())
                                            .toArray(new String[reportAssignments.size()]);
                                }
                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    int minuetsLapsed = Minutes.minutesBetween(
                                            new DateTime(report.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes();
                                    if (Minutes.minutesBetween(
                                            new DateTime(report.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes() > afterNoOfHour) {
                                        User user = userService.getUserById(reportAssignment.getAssignment());
                                        String[] messageMetadata = reportService.buildEmailNotificationContent(report,
                                                user,
                                                taskConfiguration.getSubjectReport(),
                                                taskConfiguration.getMessageReport(), "", "", "", "", "", "", "", "",
                                                "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice
                                                .sendMail(new String[]{!user.isDeleted() ? user.getEmailId() : null},
                                                        ccList, messageMetadata[0], messageMetadata[1],
                                                        new String[]{appConfigService
                                                                .getAppConfigForGranterOrg(
                                                                        report.getGrant().getGrantorOrganization()
                                                                                .getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                                .getConfigValue()
                                                                .replace(RELEASE_VERSION, releaseService
                                                                .getCurrentRelease().getVersion()).replace(TENANT,report.getGrant()
                                                                .getGrantorOrganization().getName())});
                                    }
                                }

                            }
                        }

                    }
                }

            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void grantsActionDueChecker() {
        DateTime now = DateTime.now();

        List<Granter> granters = granterService.getAllGranters();

        Map<Long, AppConfig> configs = new HashMap<>();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(),
                    AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS);
            configs.put(config.getId(), config);
        }

        Map<Long, AppConfig> grantIdsToSkip = new HashMap<>();
        configs.keySet().forEach(c -> grantIdsToSkip.put(c, configs.get(c)));

        for (Map.Entry<Long, AppConfig> entry : configs.entrySet()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(entry.getValue().getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if (entry.getKey() == 0) {

                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {
                        List<GrantAssignments> usersToNotify = grantService.getActionDueGrantsForPlatform(
                                grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        if (usersToNotify != null && !usersToNotify.isEmpty()) {
                            for (GrantAssignments grantAssignment : usersToNotify) {
                                Grant grant = grantService.getById(grantAssignment.getGrant().getId());

                                List<GrantAssignments> grantAssignments = grantService
                                        .getGrantWorkflowAssignments(grant);
                                String[] ccList = new String[grantAssignments.size()];

                                if (!grantAssignments.isEmpty()) {
                                    List<User> uList = new ArrayList<>();
                                    for (GrantAssignments ass : grantAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignments()));
                                    }
                                    uList.removeIf(User::isDeleted);

                                    ccList = uList.stream().map(User::getEmailId).collect(Collectors.toList())
                                            .toArray(new String[grantAssignments.size()]);
                                }
                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    int minuetsLapsed = Minutes
                                            .minutesBetween(
                                                    new DateTime(grant.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes();
                                    if (Minutes
                                            .minutesBetween(
                                                    new DateTime(grant.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes() > afterNoOfHour) {
                                        User user = userService.getUserById(grantAssignment.getAssignments());
                                        String[] messageMetadata = grantService.buildEmailNotificationContent(grant,
                                                user,
                                                taskConfiguration.getSubjectGrant(),
                                                taskConfiguration.getMessageGrant(), "", "", "", "", "", "", "", "", "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice.sendMail(
                                                new String[]{!user.isDeleted() ? user.getEmailId() : null}, ccList,
                                                messageMetadata[0], messageMetadata[1],
                                                new String[]{appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                grant.getGrantorOrganization().getId(),
                                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replace(RELEASE_VERSION,
                                                        releaseService.getCurrentRelease().getVersion()).replace(TENANT,grant
                                                        .getGrantorOrganization().getName())});
                                    }
                                }

                            }
                        }
                    }
                } else {
                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                        List<GrantAssignments> usersToNotify = grantService.getActionDueGrantsForGranterOrg(entry.getKey());
                        if (usersToNotify != null && !usersToNotify.isEmpty()) {
                            for (GrantAssignments grantAssignment : usersToNotify) {
                                Grant grant = grantService.getById(grantAssignment.getGrant().getId());

                                List<GrantAssignments> grantAssignments = grantService
                                        .getGrantWorkflowAssignments(grant);
                                String[] ccList = new String[grantAssignments.size()];

                                if (!grantAssignments.isEmpty()) {
                                    List<User> uList = new ArrayList<>();
                                    for (GrantAssignments ass : grantAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignments()));
                                    }
                                    uList.removeIf(User::isDeleted);
                                    ccList = uList.stream().map(User::getEmailId).collect(Collectors.toList())
                                            .toArray(new String[grantAssignments.size()]);
                                }
                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    int minuetsLapsed = Minutes
                                            .minutesBetween(
                                                    new DateTime(grant.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes();
                                    if (Minutes
                                            .minutesBetween(
                                                    new DateTime(grant.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes() > afterNoOfHour) {
                                        User user = userService.getUserById(grantAssignment.getAssignments());
                                        String[] messageMetadata = grantService.buildEmailNotificationContent(grant,
                                                user,
                                                taskConfiguration.getSubjectGrant(),
                                                taskConfiguration.getMessageGrant(), "", "", "", "", "", "", "", "", "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice.sendMail(
                                                new String[]{!user.isDeleted() ? user.getEmailId() : null}, ccList,
                                                messageMetadata[0], messageMetadata[1],
                                                new String[]{appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                grant.getGrantorOrganization().getId(),
                                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replace(RELEASE_VERSION,
                                                        releaseService.getCurrentRelease().getVersion()).replace(TENANT,grant
                                                        .getGrantorOrganization().getName())});
                                    }
                                }

                            }
                        }

                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void disbursementsActionDueChecker() {
        DateTime now = DateTime.now();

        List<Granter> granters = granterService.getAllGranters();

        Map<Long, AppConfig> configs = new HashMap<>();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(),
                    AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS);
            configs.put(config.getId(), config);
        }

        Map<Long, AppConfig> grantIdsToSkip = new HashMap<>();
        configs.keySet().forEach(c -> grantIdsToSkip.put(c, configs.get(c)));

        for (Map.Entry<Long, AppConfig> entry : configs.entrySet()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(entry.getValue().getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if (entry.getKey() == 0) {

                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {
                        List<DisbursementAssignment> usersToNotify = disbursementService
                                .getActionDueDisbursementsForPlatform(
                                        grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        if (usersToNotify != null && !usersToNotify.isEmpty()) {
                            for (DisbursementAssignment disbursementtAssignment : usersToNotify) {
                                Disbursement disbursement = disbursementService
                                        .getDisbursementById(disbursementtAssignment.getDisbursementId());

                                List<DisbursementAssignment> disbursementAssignments = disbursementService
                                        .getDisbursementAssignments(disbursement);

                                String[] ccList = new String[disbursementAssignments.size()];

                                if (!disbursementAssignments.isEmpty()) {
                                    List<User> uList = new ArrayList<>();
                                    for (DisbursementAssignment ass : disbursementAssignments) {
                                        uList.add(userService.getUserById(ass.getOwner()));
                                    }
                                    uList.removeIf(User::isDeleted);
                                    ccList = uList.stream().map(User::getEmailId).collect(Collectors.toList())
                                            .toArray(new String[disbursementAssignments.size()]);
                                }
                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    int minuetsLapsed = Minutes.minutesBetween(
                                            new DateTime(disbursement.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes();
                                    if (Minutes.minutesBetween(
                                            new DateTime(disbursement.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes() > afterNoOfHour) {
                                        User user = userService.getUserById(disbursementtAssignment.getOwner());
                                        String[] messageMetadata = disbursementService.buildEmailNotificationContent(
                                                disbursement, user,  taskConfiguration.getSubjectDisbursement(),
                                                taskConfiguration.getMessageDisbursement(), "", "", "", "", "", "", "",
                                                "", "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice.sendMail(
                                                new String[]{!user.isDeleted() ? user.getEmailId() : null}, ccList,
                                                messageMetadata[0], messageMetadata[1],
                                                new String[]{appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                disbursement.getGrant().getGrantorOrganization()
                                                                        .getId(),
                                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replace(RELEASE_VERSION,
                                                        releaseService.getCurrentRelease().getVersion()).replace(TENANT,disbursement.getGrant()
                                                        .getGrantorOrganization().getName())});
                                    }
                                }

                            }
                        }
                    }
                } else {
                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                        List<DisbursementAssignment> usersToNotify = disbursementService
                                .getActionDueDisbursementsForGranterOrg(entry.getKey());
                        if (usersToNotify != null && !usersToNotify.isEmpty()) {
                            for (DisbursementAssignment disbursementAssignment : usersToNotify) {
                                Disbursement disbursement = disbursementService
                                        .getDisbursementById(disbursementAssignment.getDisbursementId());

                                List<DisbursementAssignment> disbursementAssignments = disbursementService
                                        .getDisbursementAssignments(disbursement);
                                String[] ccList = new String[disbursementAssignments.size()];

                                if (!disbursementAssignments.isEmpty()) {
                                    List<User> uList = new ArrayList<>();
                                    for (DisbursementAssignment ass : disbursementAssignments) {
                                        uList.add(userService.getUserById(ass.getOwner()));
                                    }
                                    uList.removeIf(User::isDeleted);
                                    ccList = uList.stream().map(User::getEmailId).collect(Collectors.toList())
                                            .toArray(new String[disbursementAssignments.size()]);
                                }
                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    int minuetsLapsed = Minutes.minutesBetween(
                                            new DateTime(disbursement.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes();
                                    if (Minutes.minutesBetween(
                                            new DateTime(disbursement.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes() > afterNoOfHour) {
                                        User user = userService.getUserById(disbursementAssignment.getOwner());
                                        String[] messageMetadata = disbursementService.buildEmailNotificationContent(
                                                disbursement, user, taskConfiguration.getSubjectDisbursement(),
                                                taskConfiguration.getMessageDisbursement(), "", "", "", "", "", "", "", "", "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice.sendMail(
                                                new String[]{!user.isDeleted() ? user.getEmailId() : null}, ccList,
                                                messageMetadata[0], messageMetadata[1],
                                                new String[]{appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                disbursement.getGrant().getGrantorOrganization()
                                                                        .getId(),
                                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replace(RELEASE_VERSION,
                                                        releaseService.getCurrentRelease().getVersion()).replace(TENANT,disbursement.getGrant()
                                                        .getGrantorOrganization().getName())});
                                    }
                                }

                            }
                        }

                    }
                }

            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

  

    @Scheduled(cron = "0 * * * * *")
    public void readAndStoreReleaseVersion() {

        Path path = Paths.get("/opt/gms/release.json");
        try {
            String entry = Files.readAllLines(path).get(0);
            ObjectMapper mapper = new ObjectMapper();
            AppRelease release = mapper.readValue(entry, AppRelease.class);
            Release version = new Release();
            switch (environment) {
                case LOCAL:
                    version.setVersion(release.getReleaseCandidate());
                    break;
                case "dev":
                    version.setVersion(release.getReleaseCandidate());
                    break;
                case "qa":
                    version.setVersion(release.getReleaseCandidate());
                    break;
                case "uat":
                    version.setVersion("UAT R-" + release.getReleaseCandidate());
                    break;
                case "prod":
                    version.setVersion("v" + release.getProductionRelease()
                            + (!release.getHotFixRelease().equalsIgnoreCase("0") ? " HF-" + release.getHotFixRelease()
                            : ""));
                    break;
                default:
                    //Do nothing
            }

            releaseService.deleteAllEntries();
            releaseService.saveRelease(version);

        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    //@Scheduled(cron = "0 * * * * *")
    public void remindAdminsAboutDisabledUsers() {

        List<DisabledUsersEntity> grantsWithDisabledUsers = grantService.getGrantsWithDisabledUsers();
        if (grantsWithDisabledUsers != null && !grantsWithDisabledUsers.isEmpty()) {
            for (DisabledUsersEntity entity : grantsWithDisabledUsers) {
                Grant g = grantService.getById(entity.getId());
                if (!g.getGrantStatus().getInternalStatus().equalsIgnoreCase(ACTIVE) && !g.getGrantStatus().getInternalStatus().equalsIgnoreCase("CLOSED")) {
                    processDisabledUserNotification(entity, g);
                } else if (g.getGrantStatus().getInternalStatus().equalsIgnoreCase(ACTIVE)) {
                    Optional<GrantAssignments> check = grantService.getGrantCurrentAssignments(g).stream().filter(x -> x.getStateId().longValue() == g.getGrantStatus().getId().longValue()).findFirst();
                    GrantAssignments ga = check.isPresent() ? check.get() : null;
                    if (ga != null && ga.getAssignments() != null && userService.getUserById(ga.getAssignments()).isDeleted()) {
                        processDisabledUserNotification(entity, g);
                    }
                }
            }
        }
    }

    private void processDisabledUserNotification(DisabledUsersEntity entity, Grant byId) {
        Grant grant = byId;
        Organization tenantOrg = grant.getGrantorOrganization();
        List<User> tenantUsers = userService.getAllTenantUsers(tenantOrg);
        List<User> admins = tenantUsers.stream().filter(u ->
            u.getUserRoles().stream().anyMatch(r -> r.getRole().getName().equalsIgnoreCase("ADMIN"))).collect(Collectors.toList());
        List<User> grantUsers = grantService.getGrantWorkflowAssignments(grant).stream().map(u -> userService.getUserById(u.getAssignments())).collect(Collectors.toList());
        List<User> nonAdminUsers = grantUsers.stream().filter(u ->
                u.getUserRoles().stream().anyMatch(r -> !r.getRole().getName().equalsIgnoreCase("ADMIN"))
        ).collect(Collectors.toList());
        admins.removeIf(User::isDeleted);
        nonAdminUsers.removeIf(User::isDeleted);
        String[] toList = admins.stream().map(User::getEmailId).collect(Collectors.toList())
                .toArray(new String[admins.size()]);
        String[] ccList = nonAdminUsers.stream().map(User::getEmailId).collect(Collectors.toList())
                .toArray(new String[nonAdminUsers.size()]);

        String mailSubject = "Workflow Alert: Disabled Users for " + entity.getEntityName();
        String mailMessage = appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                AppConfiguration.DISABLED_USERS_IN_WORKFLOW_EMAIL_TEMPLATE).getConfigValue();
        mailMessage = mailMessage.replace("%ENTITY_TYPE%", entity.getEntityType()).replace("%ENTITY_NAME%", entity.getEntityName());

        emailSevice.sendMail(toList, ccList, mailSubject, mailMessage, new String[]{appConfigService
                .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                .getConfigValue().replace(RELEASE_VERSION,
                releaseService.getCurrentRelease().getVersion()).replace(TENANT,grant
                .getGrantorOrganization().getName())});
    }

    @Scheduled(cron = "0 * * * * *")
    public void hygieneCheck() throws SQLException {
        List<HygieneCheck> checks = hygieneCheckService.getChecks();
        Date now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).toDate();
        for(HygieneCheck check : checks){
            CronSequenceGenerator generator = new CronSequenceGenerator(check.getScheduledRun());
            Date runDate = generator.next(new DateTime(now).minusDays(1).toDate());

            runDate = new DateTime(runDate).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
            if(new DateTime(runDate).isEqual(new DateTime((now)))){

                String query = check.getHygieneQuery();
                Connection conn=null;
                conn=DataSourceUtils.getConnection(dataSource);
                try(PreparedStatement ps = conn.prepareStatement(query)){

                    ResultSet result = ps.executeQuery();
                    while(result.next()){

                        if (result.getString("emails_to")==null){
                            continue;
                        }
                        String msg = check.getMessage();
                        msg = msg.replace("%SUMMARY%",result.getString("summary"));
                        String[] toEmails = result.getString("emails_to").split(",");
                        long grantorOrg = result.getLong("grantor_org_id");
                        emailSevice.sendMail(toEmails,null,check.getSubject(),msg,
                                new String[]{appConfigService
                                        .getAppConfigForGranterOrg(grantorOrg,
                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                        .getConfigValue()
                                        .replace(RELEASE_VERSION, releaseService.getCurrentRelease().getVersion()).replace(TENANT,organizationService.get(grantorOrg).getName())});
                    }
                }catch (SQLException throwables) {
                    logger.error(throwables.getMessage(),throwables);
                }finally {
                    DataSourceUtils.doReleaseConnection(conn, dataSource);
                }
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void closureActionDueChecker() {
        DateTime now = DateTime.now();

        List<Granter> granters = granterService.getAllGranters();

        Map<Long, AppConfig> configs = new HashMap<>();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(),
                    AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS);
            configs.put(config.getId(), config);
        }
        
        Map<Long, AppConfig> grantIdsToSkip = new HashMap<>();
        configs.keySet().forEach(c -> grantIdsToSkip.put(c, configs.get(c)));

      
        for (Map.Entry<Long, AppConfig> entry : configs.entrySet()) {
           
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(entry.getValue().getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
             

                if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                    List<ClosureAssignments> usersToNotify ;
                    if (entry.getKey() == 0) {
                         usersToNotify = closureService.getActionDueClosureForPlatform(
                                grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                    }
                    else {
                         usersToNotify = closureService.getActionDueClosuresForGranterOrg(entry.getKey());
                    } 

                        if (usersToNotify != null && !usersToNotify.isEmpty()) {
                            for (ClosureAssignments closureAssignment : usersToNotify) {
                                GrantClosure closure = closureService.getClosureById(closureAssignment.getClosure().getId());

                                List<ClosureAssignments> closureAssignments = closureService
                                        .getAssignmentsForClosure(closure);
                                        closureAssignments.removeIf(u -> userService.getUserById(u.getAssignment())
                                        .getOrganization().getOrganizationType().equalsIgnoreCase(GRANTEE));
                                String[] ccList = new String[closureAssignments.size()];

                                if (!closureAssignments.isEmpty()) {
                                    List<User> uList = new ArrayList<>();
                                    for (ClosureAssignments ass : closureAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignment()));
                                    }
                                    uList.removeIf(User::isDeleted);
                                    ccList = uList.stream().map(User::getEmailId).collect(Collectors.toList())
                                            .toArray(new String[closureAssignments.size()]);
                                }
                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    int minutesLapsed = Minutes.minutesBetween(
                                            new DateTime(closure.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes();
                                    if (Minutes.minutesBetween(
                                            new DateTime(closure.getMovedOn(), DateTimeZone.forID(timezone)), now)
                                            .getMinutes() > afterNoOfHour) {
                                        User user = userService.getUserById(closureAssignment.getAssignment());
                                        String[] messageMetadata = closureService.buildEmailNotificationContent(closure,
                                                user,
                                                taskConfiguration.getSubjectReport(),
                                                taskConfiguration.getMessageReport(), "", "", "", "", "", "", "", "",
                                                "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minutesLapsed / (24 * 60), null, null);
                                        emailSevice
                                                .sendMail(new String[]{!user.isDeleted() ? user.getEmailId() : null},
                                                        ccList, messageMetadata[0], messageMetadata[1],
                                                        new String[]{appConfigService
                                                                .getAppConfigForGranterOrg(
                                                                        closure.getGrant().getGrantorOrganization()
                                                                                .getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                                .getConfigValue()
                                                                .replace(RELEASE_VERSION, releaseService
                                                                .getCurrentRelease().getVersion()).replace(TENANT,closure.getGrant()
                                                                .getGrantorOrganization().getName())});
                                    }
                                }

                            }
                        }
                    }
                

            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }


}
