package org.codealpha.gmsservice.schedulers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.ScheduledTaskVO;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

                        Date dueDate = now.withTimeAtStartOfDay().plusDays(taskConfiguration.getConfiguration().getDaysBefore()).toDate();
                        List<Report> reportsToNotify = reportService.getDueReportsForPlatform(dueDate,grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        for (Report report : reportsToNotify) {
                            for (ReportAssignment reportAssignment : reportService.getAssignmentsForReport(report)) {
                                User userToNotify = userService.getUserById(reportAssignment.getAssignment());
                                String[] messageMetadata = reportService.buildEmailNotificationContent(report,userToNotify,userToNotify.getFirstName()+" "+userToNotify.getLastName(),"",null,taskConfiguration.getSubject(),taskConfiguration.getMessage(),"","","","","","","","","");

                                emailSevice.sendMail(userToNotify.getEmailId(),messageMetadata[0],messageMetadata[1],new String[]{appConfigService
                                        .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
                            }



                        }
                    }
                }else{
                    if(Integer.valueOf(hourAndMinute[0])==now.hourOfDay().get() && Integer.valueOf(hourAndMinute[1])==now.minuteOfHour().get()){

                        Date dueDate = now.withTimeAtStartOfDay().plusDays(taskConfiguration.getConfiguration().getDaysBefore()).toDate();
                        List<Report> reportsToNotify = reportService.getDueReportsForGranter(dueDate,configId);
                        for (Report report : reportsToNotify) {
                            for (ReportAssignment reportAssignment : reportService.getAssignmentsForReport(report)) {
                                User userToNotify = userService.getUserById(reportAssignment.getAssignment());
                                String[] messageMetadata = reportService.buildEmailNotificationContent(report,userToNotify,userToNotify.getFirstName()+" "+userToNotify.getLastName(),"",null,taskConfiguration.getSubject(),taskConfiguration.getMessage(),"","","","","","","","","");

                                emailSevice.sendMail(userToNotify.getEmailId(),messageMetadata[0],messageMetadata[1],new String[]{appConfigService
                                        .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
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

                        Date dueDate = now.withTimeAtStartOfDay().plusDays(taskConfiguration.getConfiguration().getDaysBefore()).toDate();
                        List<ReportAssignment> usersToNotify = reportService.getActionDueReportsForPlatform(Long.valueOf(taskConfiguration.getConfiguration().getAfterNoOfHours()),grantIdsToSkip.keySet().stream().collect(Collectors.toList()));
                        for (ReportAssignment reportAssignment : usersToNotify) {

                                User user = userService.getUserById(reportAssignment.getAssignment());
                                Report report = reportService.getReportById(reportAssignment.getReportId());
                                emailSevice.sendMail(user.getEmailId(),"Action Pending","Action pending for report "+report.getName(),new String[]{appConfigService
                                        .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});




                        }
                    }
                }else{
                    if(Integer.valueOf(hourAndMinute[0])==now.hourOfDay().get() && Integer.valueOf(hourAndMinute[1])==now.minuteOfHour().get()){

                        Date dueDate = now.withTimeAtStartOfDay().plusDays(taskConfiguration.getConfiguration().getDaysBefore()).toDate();
                        List<Report> reportsToNotify = reportService.getDueReportsForGranter(dueDate,configId);
                        for (Report report : reportsToNotify) {
                            for (ReportAssignment reportAssignment : reportService.getAssignmentsForReport(report)) {
                                User userToNotify = userService.getUserById(reportAssignment.getAssignment());
                                String[] messageMetadata = reportService.buildEmailNotificationContent(report,userToNotify,userToNotify.getFirstName()+" "+userToNotify.getLastName(),"",null,taskConfiguration.getSubject(),taskConfiguration.getMessage(),"","","","","","","","","");

                                emailSevice.sendMail(userToNotify.getEmailId(),messageMetadata[0],messageMetadata[1],new String[]{appConfigService
                                        .getAppConfigForGranterOrg(report.getGrant().getGrantorOrganization().getId(),
                                                AppConfiguration.PLATFORM_EMAIL_FOOTER).getConfigValue()});
                            }



                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
