package org.codealpha.gmsservice.schedulers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.AppRelease;
import org.codealpha.gmsservice.models.ScheduledTaskVO;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
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

    private boolean appLevelSettingsProcessed = false;

    @Scheduled(cron = "0 * * * * *")
    public void dueReportsChecker(){
        DateTime now = DateTime.now();

        List<Granter> granters = granterService.getAllGranters();

        Map<Long,AppConfig> configs = new HashMap<>();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(), AppConfiguration.DUE_REPORTS_REMINDER_SETTINGS);
            configs.put(config.getId(),config);
        }

        Map<Long,AppConfig> grantIdsToSkip = new HashMap<>();
        configs.keySet().forEach(c -> {
                    grantIdsToSkip.put(c,configs.get(c));
        });



        for(Long configId : configs.keySet()){
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(configs.get(configId).getConfigValue(), ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if(configId==0){

                    if(Integer.valueOf(hourAndMinute[0])==now.hourOfDay().get() && Integer.valueOf(hourAndMinute[1])==now.minuteOfHour().get()){

                        int[] daysBefore = taskConfiguration.getConfiguration().getDaysBefore();
                        for (int db : daysBefore) {
                            Date dueDate = now.withTimeAtStartOfDay().plusDays(db).toDate();
                            List<Report> reportsToNotify = reportService.getDueReportsForPlatform(dueDate,grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                            for (Report report : reportsToNotify) {
                                notifyGranteeUserAndCCInternalUsers(taskConfiguration, report);
                                /*for (ReportAssignment reportAssignment : reportService.getAssignmentsForReport(report)) {
                                    User userToNotify = userService.getUserById(reportAssignment.getAssignment());


                                    emailSevice.sendMail(userToNotify.getEmailId(),null,messageMetadata[0],messageMetadata[1],new String[]{appConfigService
                                            .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                                    AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
                                }*/
                            }
                        }

                    }
                }else{
                    if(Integer.valueOf(hourAndMinute[0])==now.hourOfDay().get() && Integer.valueOf(hourAndMinute[1])==now.minuteOfHour().get()){
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
        User granteeToNotify = userService.getUserById(assignments.stream().filter(ass -> userService.getUserById(ass.getAssignment()).getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")).findFirst().get().getAssignment());
        List<ReportAssignment> ccAssignments = assignments.stream().filter(ass -> !userService.getUserById(ass.getAssignment()).getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")).collect(Collectors.toList());
        List<String> otherUsersToNotify = new ArrayList<>();
        for (ReportAssignment ccAssignment : ccAssignments) {
            otherUsersToNotify.add(userService.getUserById(ccAssignment.getAssignment()).getEmailId());
        }

        String link = buildLink(environment,false,"");
        WorkflowStatus grantActiveState = workflowStatusService.getTenantWorkflowStatuses("GRANT",report.getGrant().getGrantorOrganization().getId()).stream().filter(s -> s.getInternalStatus().equalsIgnoreCase("ACTIVE")).findFirst().get();
        List<GrantAssignments> grantAssignments = grantService.getGrantWorkflowAssignments(report.getGrant());
        User owner = userService.getUserById(grantAssignments.stream().filter(ass -> Long.valueOf(ass.getStateId()) == grantActiveState.getId()).findFirst().get().getAssignments());
        String[] messageMetadata = reportService.buildEmailNotificationContent(report, granteeToNotify, granteeToNotify.getFirstName() + " " + granteeToNotify.getLastName(), "", null, taskConfiguration.getSubjectReport(), taskConfiguration.getMessageReport(), "", "", "", "", "", "", "", "", "", link, owner, null);


        emailSevice.sendMail(granteeToNotify.getEmailId(), otherUsersToNotify.toArray(new String[]{}), messageMetadata[0], messageMetadata[1], new String[]{appConfigService
                .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                        AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
    }

    private String buildLink(String environment,boolean forTenant,String tenant) {
        if(!forTenant) {
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
        }else{
            switch (environment) {
                case "local":
                    return "http://"+tenant+".localhost:4200";
                case "dev":
                    return "https://"+tenant+".dev.anudan.org";
                case "qa":
                    return "https://"+tenant+".qa.anudan.org";
                case "uat":
                    return "https://"+tenant+".uat.anudan.org";
                default:
                    return "https://"+tenant+".anudan.org";
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void reportsActionDueChecker(){
        DateTime now = DateTime.now();

        List<Granter> granters = granterService.getAllGranters();

        Map<Long,AppConfig> configs = new HashMap<>();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(), AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS);
            configs.put(config.getId(),config);
        }

        Map<Long,AppConfig> grantIdsToSkip = new HashMap<>();
        configs.keySet().forEach(c -> {
            grantIdsToSkip.put(c,configs.get(c));
        });



        for(Long configId : configs.keySet()){
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(configs.get(configId).getConfigValue(), ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if(configId==0){

                    if(Integer.valueOf(hourAndMinute[0])==now.hourOfDay().get() && Integer.valueOf(hourAndMinute[1])==now.minuteOfHour().get()){
                        List<ReportAssignment> usersToNotify = reportService.getActionDueReportsForPlatform(grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        if(usersToNotify!=null && usersToNotify.size()>0){
                            for (ReportAssignment reportAssignment : usersToNotify) {
                                Report report = reportService.getReportById(reportAssignment.getReportId());

                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    if(Minutes.minutesBetween(new DateTime(report.getMovedOn()),now).getMinutes()/(afterNoOfHour)==1){
                                        User user = userService.getUserById(reportAssignment.getAssignment());
                                        String[] messageMetadata = reportService.buildEmailNotificationContent(report, user, user.getFirstName() + " " + user.getLastName(), "", null, taskConfiguration.getSubjectReport(), taskConfiguration.getMessageReport(), "", "", "", "", "", "", "", "", "", buildLink(environment,true,user.getOrganization().getCode().toLowerCase()), null, afterNoOfHour/(24*60));
                                        emailSevice.sendMail(user.getEmailId(),null,messageMetadata[0],messageMetadata[1],new String[]{appConfigService
                                                .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
                                    }
                                }

                            }
                        }
                    }
                }else{
                    if(Integer.valueOf(hourAndMinute[0])==now.hourOfDay().get() && Integer.valueOf(hourAndMinute[1])==now.minuteOfHour().get()){

                            List<ReportAssignment> usersToNotify = reportService.getActionDueReportsForGranterOrg(configId);
                            if(usersToNotify!=null && usersToNotify.size()>0){
                                for (ReportAssignment reportAssignment : usersToNotify) {
                                    Report report = reportService.getReportById(reportAssignment.getReportId());

                                    for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                        if(Minutes.minutesBetween(new DateTime(report.getMovedOn()),now).getMinutes()/(afterNoOfHour)==1){
                                            User user = userService.getUserById(reportAssignment.getAssignment());
                                            String[] messageMetadata = reportService.buildEmailNotificationContent(report, user, user.getFirstName() + " " + user.getLastName(), "", null, taskConfiguration.getSubjectReport(), taskConfiguration.getMessageReport(), "", "", "", "", "", "", "", "", "", buildLink(environment,true,user.getOrganization().getCode().toLowerCase()), null, afterNoOfHour/(24*60));
                                            emailSevice.sendMail(user.getEmailId(),null,messageMetadata[0],messageMetadata[1],new String[]{appConfigService
                                                    .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                                            AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
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
    public void grantsActionDueChecker(){
        DateTime now = DateTime.now();

        List<Granter> granters = granterService.getAllGranters();

        Map<Long,AppConfig> configs = new HashMap<>();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(), AppConfiguration.ACTION_DUE_REPORTS_REMINDER_SETTINGS);
            configs.put(config.getId(),config);
        }

        Map<Long,AppConfig> grantIdsToSkip = new HashMap<>();
        configs.keySet().forEach(c -> {
            grantIdsToSkip.put(c,configs.get(c));
        });



        for(Long configId : configs.keySet()){
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(configs.get(configId).getConfigValue(), ScheduledTaskVO.class);
                String[] hourAndMinute = taskConfiguration.getTime().split(":");
                if(configId==0){

                    if(Integer.valueOf(hourAndMinute[0])==now.hourOfDay().get() && Integer.valueOf(hourAndMinute[1])==now.minuteOfHour().get()){
                        List<GrantAssignments> usersToNotify = grantService.getActionDueGrantsForPlatform(grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        if(usersToNotify!=null && usersToNotify.size()>0){
                            for (GrantAssignments grantAssignment : usersToNotify) {
                                Grant grant = grantService.getById(grantAssignment.getGrantId());

                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    if(Minutes.minutesBetween(new DateTime(grant.getMovedOn()),now).getMinutes()/(afterNoOfHour)==1){
                                        User user = userService.getUserById(grantAssignment.getAssignments());
                                        String[] messageMetadata = grantService.buildEmailNotificationContent(grant, user, user.getFirstName() + " " + user.getLastName(), "", null, taskConfiguration.getSubjectGrant(), taskConfiguration.getMessageGrant(), "", "", "", "", "", "", "", "", "", buildLink(environment,true,user.getOrganization().getCode().toLowerCase()), null, afterNoOfHour/(24*60));
                                        emailSevice.sendMail(user.getEmailId(),null,messageMetadata[0],messageMetadata[1],new String[]{appConfigService
                                                .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
                                    }
                                }

                            }
                        }
                    }
                }else{
                    if(Integer.valueOf(hourAndMinute[0])==now.hourOfDay().get() && Integer.valueOf(hourAndMinute[1])==now.minuteOfHour().get()){

                        List<GrantAssignments> usersToNotify = grantService.getActionDueGrantsForGranterOrg(configId);
                        if(usersToNotify!=null && usersToNotify.size()>0){
                            for (GrantAssignments grantAssignments : usersToNotify) {
                                Grant grant = grantService.getById(grantAssignments.getGrantId());

                                for (int afterNoOfHour : taskConfiguration.getConfiguration().getAfterNoOfHours()) {
                                    if(Minutes.minutesBetween(new DateTime(grant.getMovedOn()),now).getMinutes()/(afterNoOfHour)==1){
                                        User user = userService.getUserById(grantAssignments.getAssignments());
                                        String[] messageMetadata = grantService.buildEmailNotificationContent(grant, user, user.getFirstName() + " " + user.getLastName(), "", null, taskConfiguration.getSubjectGrant(), taskConfiguration.getMessageGrant(), "", "", "", "", "", "", "", "", "", buildLink(environment,true,user.getOrganization().getCode().toLowerCase()), null, afterNoOfHour/(24*60));
                                        emailSevice.sendMail(user.getEmailId(),null,messageMetadata[0],messageMetadata[1],new String[]{appConfigService
                                                .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                                        AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
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
    public void readAndStoreReleaseVersion(){

       Path path = Paths.get("/opt/gms/release.json");
        try {
            String entry = Files.readAllLines(path).get(0);
            ObjectMapper mapper = new ObjectMapper();
            AppRelease release = mapper.readValue(entry,AppRelease.class);
            Release version = new Release();
            version.setVersion(release.getVersion());
            releaseService.deleteAllEntries();
            releaseService.saveRelease(version);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
