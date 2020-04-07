package org.codealpha.gmsservice.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.ScheduledTaskEntryModel;
import org.codealpha.gmsservice.models.ScheduledTaskVO;
import org.codealpha.gmsservice.schedulers.DueReportsReminderTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DueReportReminderScheduler {

    private static Logger logger = LoggerFactory.getLogger(DueReportReminderScheduler.class);
    @Autowired
    private GranterService granterService;
    @Autowired AppConfigService appConfigService;
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private boolean appLevelSettingsProcessed = false;

    public void configure(){

        List<Granter> granters = granterService.getAllGranters();

        for (Granter granter : granters) {
            AppConfig config = appConfigService.getSpecialAppConfigForGranterOrg(granter.getId(), AppConfiguration.DUE_REPORTS_REMINDER_SETTINGS);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            try {
                ScheduledTaskVO taskConfiguration = mapper.readValue(config.getConfigValue(), ScheduledTaskVO.class);
                if(!appLevelSettingsProcessed){
                    String[] hourAndMinute = taskConfiguration.getTime().split(":");
                    if(config.getId()==0){
                        appLevelSettingsProcessed=true;
                    }
                    taskScheduler.schedule(new DueReportsReminderTask(config.getId()),new CronTrigger("0 "+ hourAndMinute[1] + " " + hourAndMinute[0]+ " * * *"));
                }


            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }
}
