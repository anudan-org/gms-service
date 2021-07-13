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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ScheduledJobs {

    @Autowired
    private GranterService granterService;
    @Autowired
    private ReportService reportService;
    @Autowired
    AppConfigService appConfigService;

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

    private boolean appLevelSettingsProcessed = false;

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
        configs.keySet().forEach(c -> {
            grantIdsToSkip.put(c, configs.get(c));
        });

        for (Long configId : configs.keySet()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(configs.get(configId).getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if (configId == 0) {

                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                        int[] daysBefore = taskConfiguration.getConfiguration().getDaysBefore();
                        for (int db : daysBefore) {
                            Date dueDate = now.withTimeAtStartOfDay().plusDays(db).toDate();
                            List<Report> reportsToNotify = reportService.getDueReportsForPlatform(dueDate,
                                    grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                            for (Report report : reportsToNotify) {
                                notifyGranteeUserAndCCInternalUsers(taskConfiguration, report);
                                /*
                                 * for (ReportAssignment reportAssignment :
                                 * reportService.getAssignmentsForReport(report)) { User userToNotify =
                                 * userService.getUserById(reportAssignment.getAssignment());
                                 * 
                                 * 
                                 * emailSevice.sendMail(userToNotify.getEmailId(),null,messageMetadata[0],
                                 * messageMetadata[1],new String[]{appConfigService
                                 * .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId()
                                 * , AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()}); }
                                 */
                            }
                        }

                    }
                } else {
                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {
                        int[] daysBefore = taskConfiguration.getConfiguration().getDaysBefore();
                        for (int db : daysBefore) {
                            Date dueDate = now.withTimeAtStartOfDay().plusDays(db).toDate();
                            List<Report> reportsToNotify = reportService.getDueReportsForGranter(dueDate, configId);
                            for (Report report : reportsToNotify) {
                                notifyGranteeUserAndCCInternalUsers(taskConfiguration, report);
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyGranteeUserAndCCInternalUsers(ScheduledTaskVO taskConfiguration, Report report) {
        List<ReportAssignment> assignments = reportService.getAssignmentsForReport(report);
        User granteeToNotify = userService
                .getUserById(
                        assignments.stream()
                                .filter(ass -> userService.getUserById(ass.getAssignment()).getOrganization()
                                        .getOrganizationType().equalsIgnoreCase("GRANTEE"))
                                .findFirst().get().getAssignment());
        List<ReportAssignment> ccAssignments = assignments.stream().filter(ass -> !userService
                .getUserById(ass.getAssignment()).getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE"))
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
        WorkflowStatus grantActiveState = workflowStatusService
                .getTenantWorkflowStatuses("GRANT", report.getGrant().getGrantorOrganization().getId()).stream()
                .filter(s -> s.getInternalStatus().equalsIgnoreCase("ACTIVE")).findFirst().get();
        List<GrantAssignments> grantAssignments = grantService.getGrantWorkflowAssignments(report.getGrant());
        User owner = userService.getUserById(
                grantAssignments.stream().filter(ass -> Long.valueOf(ass.getStateId()) == grantActiveState.getId())
                        .findFirst().get().getAssignments());
        String[] messageMetadata = reportService.buildEmailNotificationContent(report, granteeToNotify,
                granteeToNotify.getFirstName() + " " + granteeToNotify.getLastName(), "", null,
                taskConfiguration.getSubjectReport(), taskConfiguration.getMessageReport(), "", "", "", "", "", "", "",
                "", "", link, owner, null, null, null);

        emailSevice.sendMail(new String[] { !granteeToNotify.isDeleted() ? granteeToNotify.getEmailId() : null },
                otherUsersToNotify.toArray(new String[] {}), messageMetadata[0], messageMetadata[1],
                new String[] { appConfigService
                        .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                        .getConfigValue()
                        .replaceAll("%RELEASE_VERSION%", releaseService.getCurrentRelease().getVersion()) });
    }

    private String buildLink(String environment, boolean forTenant, String tenant) {
        if (!forTenant) {
            switch (environment) {
                case "local":
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
                case "local":
                    return "http://" + tenant + ".localhost:4200";
                case "dev":
                    return "https://" + tenant + ".dev.anudan.org";
                case "qa":
                    return "https://" + tenant + ".qa.anudan.org";
                case "uat":
                    return "https://" + tenant + ".uat.anudan.org";
                default:
                    return "https://" + tenant + ".anudan.org";
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
        configs.keySet().forEach(c -> {
            grantIdsToSkip.put(c, configs.get(c));
        });

        for (Long configId : configs.keySet()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(configs.get(configId).getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if (configId == 0) {

                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {
                        List<ReportAssignment> usersToNotify = reportService.getActionDueReportsForPlatform(
                                grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        if (usersToNotify != null && usersToNotify.size() > 0) {
                            for (ReportAssignment reportAssignment : usersToNotify) {
                                Report report = reportService.getReportById(reportAssignment.getReportId());

                                List<ReportAssignment> reportAssignments = reportService
                                        .getAssignmentsForReport(report);

                                /*
                                 * reportAssignments.removeIf(u -> u.getAssignment().longValue() ==
                                 * reportAssignment .getAssignment().longValue());
                                 */
                                reportAssignments.removeIf(u -> userService.getUserById(u.getAssignment())
                                        .getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE"));
                                String[] ccList = new String[reportAssignments.size()];

                                if (reportAssignments != null && reportAssignments.size() > 0) {
                                    List<User> uList = new ArrayList<>();
                                    for (ReportAssignment ass : reportAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignment()));
                                    }
                                    uList.removeIf(u -> u.isDeleted());
                                    ccList = uList.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
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
                                                user, user.getFirstName() + " " + user.getLastName(), "", null,
                                                taskConfiguration.getSubjectReport(),
                                                taskConfiguration.getMessageReport(), "", "", "", "", "", "", "", "",
                                                "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice
                                                .sendMail(new String[] { !user.isDeleted() ? user.getEmailId() : null },
                                                        ccList, messageMetadata[0], messageMetadata[1],
                                                        new String[] { appConfigService
                                                                .getAppConfigForGranterOrg(
                                                                        report.getGrant().getGrantorOrganization()
                                                                                .getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                                .getConfigValue()
                                                                .replaceAll("%RELEASE_VERSION%", releaseService
                                                                        .getCurrentRelease().getVersion()) });
                                    }
                                }

                            }
                        }
                    }
                } else {
                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                        List<ReportAssignment> usersToNotify = reportService.getActionDueReportsForGranterOrg(configId);
                        if (usersToNotify != null && usersToNotify.size() > 0) {
                            for (ReportAssignment reportAssignment : usersToNotify) {
                                Report report = reportService.getReportById(reportAssignment.getReportId());

                                List<ReportAssignment> reportAssignments = reportService
                                        .getAssignmentsForReport(report);

                                /*
                                 * reportAssignments.removeIf(u -> u.getAssignment().longValue() ==
                                 * reportAssignment .getAssignment().longValue());
                                 */
                                reportAssignments.removeIf(u -> userService.getUserById(u.getId()).getOrganization()
                                        .getOrganizationType().equalsIgnoreCase("GRANTEE"));
                                String[] ccList = new String[reportAssignments.size()];

                                if (reportAssignments != null && reportAssignments.size() > 0) {
                                    List<User> uList = new ArrayList<>();
                                    for (ReportAssignment ass : reportAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignment()));
                                    }
                                    uList.removeIf(u -> u.isDeleted());
                                    ccList = uList.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
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
                                                user, user.getFirstName() + " " + user.getLastName(), "", null,
                                                taskConfiguration.getSubjectReport(),
                                                taskConfiguration.getMessageReport(), "", "", "", "", "", "", "", "",
                                                "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice
                                                .sendMail(new String[] { !user.isDeleted() ? user.getEmailId() : null },
                                                        ccList, messageMetadata[0], messageMetadata[1],
                                                        new String[] { appConfigService
                                                                .getAppConfigForGranterOrg(
                                                                        report.getGrant().getGrantorOrganization()
                                                                                .getId(),
                                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                                .getConfigValue()
                                                                .replaceAll("%RELEASE_VERSION%", releaseService
                                                                        .getCurrentRelease().getVersion()) });
                                    }
                                }

                            }
                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
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
        configs.keySet().forEach(c -> {
            grantIdsToSkip.put(c, configs.get(c));
        });

        for (Long configId : configs.keySet()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(configs.get(configId).getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if (configId == 0) {

                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {
                        List<GrantAssignments> usersToNotify = grantService.getActionDueGrantsForPlatform(
                                grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        if (usersToNotify != null && usersToNotify.size() > 0) {
                            for (GrantAssignments grantAssignment : usersToNotify) {
                                Grant grant = grantService.getById(grantAssignment.getGrant().getId());

                                List<GrantAssignments> grantAssignments = grantService
                                        .getGrantWorkflowAssignments(grant);

                                /*
                                 * grantAssignments.removeIf(u -> u.getAssignments().longValue() ==
                                 * grantAssignment .getAssignments().longValue());
                                 */

                                String[] ccList = new String[grantAssignments.size()];

                                if (grantAssignments != null && grantAssignments.size() > 0) {
                                    List<User> uList = new ArrayList<>();
                                    for (GrantAssignments ass : grantAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignments()));
                                    }
                                    uList.removeIf(u -> u.isDeleted());

                                    ccList = uList.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
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
                                                user, user.getFirstName() + " " + user.getLastName(), "", null,
                                                taskConfiguration.getSubjectGrant(),
                                                taskConfiguration.getMessageGrant(), "", "", "", "", "", "", "", "", "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice.sendMail(
                                                new String[] { !user.isDeleted() ? user.getEmailId() : null }, ccList,
                                                messageMetadata[0], messageMetadata[1],
                                                new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                grant.getGrantorOrganization().getId(),
                                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replaceAll("%RELEASE_VERSION%",
                                                                releaseService.getCurrentRelease().getVersion()) });
                                    }
                                }

                            }
                        }
                    }
                } else {
                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                        List<GrantAssignments> usersToNotify = grantService.getActionDueGrantsForGranterOrg(configId);
                        if (usersToNotify != null && usersToNotify.size() > 0) {
                            for (GrantAssignments grantAssignment : usersToNotify) {
                                Grant grant = grantService.getById(grantAssignment.getGrant().getId());

                                List<GrantAssignments> grantAssignments = grantService
                                        .getGrantWorkflowAssignments(grant);

                                /*
                                 * grantAssignments.removeIf(u -> u.getAssignments().longValue() ==
                                 * grantAssignment .getAssignments().longValue());
                                 */

                                String[] ccList = new String[grantAssignments.size()];

                                if (grantAssignments != null && grantAssignments.size() > 0) {
                                    List<User> uList = new ArrayList<>();
                                    for (GrantAssignments ass : grantAssignments) {
                                        uList.add(userService.getUserById(ass.getAssignments()));
                                    }
                                    uList.removeIf(u -> u.isDeleted());
                                    ccList = uList.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
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
                                                user, user.getFirstName() + " " + user.getLastName(), "", null,
                                                taskConfiguration.getSubjectGrant(),
                                                taskConfiguration.getMessageGrant(), "", "", "", "", "", "", "", "", "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice.sendMail(
                                                new String[] { !user.isDeleted() ? user.getEmailId() : null }, ccList,
                                                messageMetadata[0], messageMetadata[1],
                                                new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                grant.getGrantorOrganization().getId(),
                                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replaceAll("%RELEASE_VERSION%",
                                                                releaseService.getCurrentRelease().getVersion()) });
                                    }
                                }

                            }
                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
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
        configs.keySet().forEach(c -> {
            grantIdsToSkip.put(c, configs.get(c));
        });

        for (Long configId : configs.keySet()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(configs.get(configId).getConfigValue(),
                        ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if (configId == 0) {

                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {
                        List<DisbursementAssignment> usersToNotify = disbursementService
                                .getActionDueDisbursementsForPlatform(
                                        grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        if (usersToNotify != null && usersToNotify.size() > 0) {
                            for (DisbursementAssignment disbursementtAssignment : usersToNotify) {
                                Disbursement disbursement = disbursementService
                                        .getDisbursementById(disbursementtAssignment.getDisbursementId());

                                List<DisbursementAssignment> disbursementAssignments = disbursementService
                                        .getDisbursementAssignments(disbursement);

                                /*
                                 * disbursementAssignments.removeIf(u -> u.getOwner() .longValue() ==
                                 * disbursementtAssignment.getOwner().longValue());
                                 */
                                String[] ccList = new String[disbursementAssignments.size()];

                                if (disbursementAssignments != null && disbursementAssignments.size() > 0) {
                                    List<User> uList = new ArrayList<>();
                                    for (DisbursementAssignment ass : disbursementAssignments) {
                                        uList.add(userService.getUserById(ass.getOwner()));
                                    }
                                    uList.removeIf(u -> u.isDeleted());
                                    ccList = uList.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
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
                                                disbursement, user, user.getFirstName() + " " + user.getLastName(), "",
                                                null, taskConfiguration.getSubjectDisbursement(),
                                                taskConfiguration.getMessageDisbursement(), "", "", "", "", "", "", "",
                                                "", "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice.sendMail(
                                                new String[] { !user.isDeleted() ? user.getEmailId() : null }, ccList,
                                                messageMetadata[0], messageMetadata[1],
                                                new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                disbursement.getGrant().getGrantorOrganization()
                                                                        .getId(),
                                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replaceAll("%RELEASE_VERSION%",
                                                                releaseService.getCurrentRelease().getVersion()) });
                                    }
                                }

                            }
                        }
                    }
                } else {
                    if (Integer.valueOf(hourAndMinute[0]) == now.hourOfDay().get()
                            && Integer.valueOf(hourAndMinute[1]) == now.minuteOfHour().get()) {

                        List<DisbursementAssignment> usersToNotify = disbursementService
                                .getActionDueDisbursementsForGranterOrg(configId);
                        if (usersToNotify != null && usersToNotify.size() > 0) {
                            for (DisbursementAssignment disbursementAssignment : usersToNotify) {
                                Disbursement disbursement = disbursementService
                                        .getDisbursementById(disbursementAssignment.getDisbursementId());

                                List<DisbursementAssignment> disbursementAssignments = disbursementService
                                        .getDisbursementAssignments(disbursement);

                                /*
                                 * disbursementAssignments.removeIf( u -> u.getOwner().longValue() ==
                                 * disbursementAssignment.getOwner().longValue());
                                 */
                                String[] ccList = new String[disbursementAssignments.size()];

                                if (disbursementAssignments != null && disbursementAssignments.size() > 0) {
                                    List<User> uList = new ArrayList<>();
                                    for (DisbursementAssignment ass : disbursementAssignments) {
                                        uList.add(userService.getUserById(ass.getOwner()));
                                    }
                                    uList.removeIf(u -> u.isDeleted());
                                    ccList = uList.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
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
                                                disbursement, user, user.getFirstName() + " " + user.getLastName(), "",
                                                null, taskConfiguration.getSubjectGrant(),
                                                taskConfiguration.getMessageGrant(), "", "", "", "", "", "", "", "", "",
                                                buildLink(environment, true,
                                                        user.getOrganization().getCode().toLowerCase()),
                                                null, minuetsLapsed / (24 * 60), null, null);
                                        emailSevice.sendMail(
                                                new String[] { !user.isDeleted() ? user.getEmailId() : null }, ccList,
                                                messageMetadata[0], messageMetadata[1],
                                                new String[] { appConfigService
                                                        .getAppConfigForGranterOrg(
                                                                disbursement.getGrant().getGrantorOrganization()
                                                                        .getId(),
                                                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                                                        .getConfigValue().replaceAll("%RELEASE_VERSION%",
                                                                releaseService.getCurrentRelease().getVersion()) });
                                    }
                                }

                            }
                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void readAndStoreReleaseVersion() {

        Path path = Paths.get("/opt/gms/release.json");
        Path hotfixVersionPath = Paths.get("/opt/gms/hotfix-version.json");
        try {
            String entry = Files.readAllLines(path).get(0);
            ObjectMapper mapper = new ObjectMapper();
            AppRelease release = mapper.readValue(entry, AppRelease.class);
            Release version = new Release();
            switch (environment) {
                case "local":
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
            }

            releaseService.deleteAllEntries();
            releaseService.saveRelease(version);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    //@Scheduled(cron = "0 * * * * *")
    public void remindAdminsAboutDisabledUsers() {

        List<DisabledUsersEntity> grantsWithDisabledUsers = grantService.getGrantsWithDisabledUsers();
        if (grantsWithDisabledUsers != null && grantsWithDisabledUsers.size() > 0) {
            for (DisabledUsersEntity entity : grantsWithDisabledUsers) {
                Grant g = grantService.getById(entity.getId());
                if(!g.getGrantStatus().getInternalStatus().equalsIgnoreCase("ACTIVE") && !g.getGrantStatus().getInternalStatus().equalsIgnoreCase("CLOSED")) {
                    processDisabledUserNotification(entity, g);
                } else if(g.getGrantStatus().getInternalStatus().equalsIgnoreCase("ACTIVE") ){
                    GrantAssignments ga = grantService.getGrantCurrentAssignments(g).stream().filter(x -> x.getStateId().longValue()==g.getGrantStatus().getId().longValue()).findFirst().get();
                    if(ga.getAssignments()!=null && userService.getUserById(ga.getAssignments()).isDeleted()){
                        processDisabledUserNotification(entity, g);
                    }
                }
            }
        }

            /*List<DisabledUsersEntity> reportsWithDisabledUsers = reportService.getReportsWithDisabledUsers();
            if (reportsWithDisabledUsers != null && reportsWithDisabledUsers.size() > 0) {
                for (DisabledUsersEntity entity : reportsWithDisabledUsers) {
                    Report report = reportService.getReportById(entity.getId());

                    Grant grant = report.getGrant();
                    Organization tenantOrg = grant.getGrantorOrganization();
                    List<User> tenantUsers = userService.getAllTenantUsers(tenantOrg);
                    List<User> admins = tenantUsers.stream().filter(u -> {
                        Boolean isAdmin = u.getUserRoles().stream().filter(r -> r.getRole().getName().equalsIgnoreCase("ADMIN")).findFirst().isPresent();
                        if(isAdmin){
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());

                    List<User> nonAdminUsers = tenantUsers.stream().filter(u -> {
                        Boolean isNotAdmin = u.getUserRoles().stream().filter(r -> !r.getRole().getName().equalsIgnoreCase("ADMIN")).findAny().isPresent();
                        if(isNotAdmin){
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());
                    admins.removeIf(u -> u.isDeleted());
                    nonAdminUsers.removeIf(u -> u.isDeleted());
                    String[] toList = admins.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                            .toArray(new String[admins.size()]);
                    String[] ccList = nonAdminUsers.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                            .toArray(new String[nonAdminUsers.size()]);

                    String mailSubject = "Workflow Alert: Disabled Users for " + entity.getEntityName();
                    String mailMessage = appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                            AppConfiguration.DISABLED_USERS_IN_WORKFLOW_EMAIL_TEMPLATE).getConfigValue();
                    mailMessage = mailMessage.replaceAll("%ENTITY_TYPE%", entity.getEntityType()).replaceAll("%ENTITY_NAME%", entity.getEntityName());

                    emailSevice.sendMail(toList, ccList, mailSubject, mailMessage, new String[]{appConfigService
                            .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                    AppConfiguration.PLATFORM_EMAIL_FOOTER)
                            .getConfigValue().replaceAll("%RELEASE_VERSION%",
                            releaseService.getCurrentRelease().getVersion())});
                }
            }

        List<DisabledUsersEntity> disbursementsWithDisabledUsers = disbursementService.getDisbursementsWithDisabledUsers();
        if (disbursementsWithDisabledUsers != null && disbursementsWithDisabledUsers.size() > 0) {
            for (DisabledUsersEntity entity : disbursementsWithDisabledUsers) {
                Disbursement disbursement = disbursementService.getDisbursementById(entity.getId());

                Grant grant = disbursement.getGrant();
                Organization tenantOrg = grant.getGrantorOrganization();
                List<User> tenantUsers = userService.getAllTenantUsers(tenantOrg);
                List<User> admins = tenantUsers.stream().filter(u -> {
                    Boolean isAdmin = u.getUserRoles().stream().filter(r -> r.getRole().getName().equalsIgnoreCase("ADMIN")).findFirst().isPresent();
                    if(isAdmin){
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                List<User> nonAdminUsers = tenantUsers.stream().filter(u -> {
                    Boolean isNotAdmin = u.getUserRoles().stream().filter(r -> !r.getRole().getName().equalsIgnoreCase("ADMIN")).findAny().isPresent();
                    if(isNotAdmin){
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                admins.removeIf(u -> u.isDeleted());
                nonAdminUsers.removeIf(u -> u.isDeleted());
                String[] toList = admins.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                        .toArray(new String[admins.size()]);
                String[] ccList = nonAdminUsers.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                        .toArray(new String[nonAdminUsers.size()]);

                String mailSubject = "Workflow Alert: Disabled Users for " + entity.getEntityName();
                String mailMessage = appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                        AppConfiguration.DISABLED_USERS_IN_WORKFLOW_EMAIL_TEMPLATE).getConfigValue();
                mailMessage = mailMessage.replaceAll("%ENTITY_TYPE%", entity.getEntityType()).replaceAll("%ENTITY_NAME%", entity.getEntityName());

                emailSevice.sendMail(toList, ccList, mailSubject, mailMessage, new String[]{appConfigService
                        .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                AppConfiguration.PLATFORM_EMAIL_FOOTER)
                        .getConfigValue().replaceAll("%RELEASE_VERSION%",
                        releaseService.getCurrentRelease().getVersion())});
            }
        }*/
        }

    private void processDisabledUserNotification(DisabledUsersEntity entity, Grant byId) {
        Grant grant = byId;
        Organization tenantOrg = grant.getGrantorOrganization();
        List<User> tenantUsers = userService.getAllTenantUsers(tenantOrg);
        List<User> admins = tenantUsers.stream().filter(u -> {
            Boolean isAdmin = u.getUserRoles().stream().filter(r -> r.getRole().getName().equalsIgnoreCase("ADMIN")).findFirst().isPresent();
            if (isAdmin) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        List<User> grantUsers = grantService.getGrantWorkflowAssignments(grant).stream().map(u -> userService.getUserById(u.getAssignments())).collect(Collectors.toList());
        List<User> nonAdminUsers = grantUsers.stream().filter(u -> {
            Boolean isNotAdmin = u.getUserRoles().stream().filter(r -> !r.getRole().getName().equalsIgnoreCase("ADMIN")).findAny().isPresent();
            if (isNotAdmin) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        admins.removeIf(u -> u.isDeleted());
        nonAdminUsers.removeIf(u -> u.isDeleted());
        String[] toList = admins.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                .toArray(new String[admins.size()]);
        String[] ccList = nonAdminUsers.stream().map(u -> u.getEmailId()).collect(Collectors.toList())
                .toArray(new String[nonAdminUsers.size()]);

        String mailSubject = "Workflow Alert: Disabled Users for " + entity.getEntityName();
        String mailMessage = appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                AppConfiguration.DISABLED_USERS_IN_WORKFLOW_EMAIL_TEMPLATE).getConfigValue();
        mailMessage = mailMessage.replaceAll("%ENTITY_TYPE%", entity.getEntityType()).replaceAll("%ENTITY_NAME%", entity.getEntityName());

        emailSevice.sendMail(toList, ccList, mailSubject, mailMessage, new String[]{appConfigService
                .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                        AppConfiguration.PLATFORM_EMAIL_FOOTER)
                .getConfigValue().replaceAll("%RELEASE_VERSION%",
                releaseService.getCurrentRelease().getVersion())});
    }
}
