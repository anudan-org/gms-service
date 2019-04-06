package org.codealpha.gmsservice.schedulers;

import java.util.List;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.services.AppConfigService;
import org.codealpha.gmsservice.services.GranterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

  @Autowired
  private GranterService granterService;
  @Autowired
  private AppConfigService appConfigService;

  @Scheduled(cron = "0 * * * * *")
  public void markUpComingKPIReminders() {
    List<Granter> granters = granterService.getAllGranters();

    for (Granter granter : granters) {
      AppConfig granterConfig = appConfigService.getAppConfigForGranterOrg(granter.getId(),
          AppConfiguration.KPI_REMINDER_NOTIFICATION_DAYS);
      int interval = Integer.valueOf(granterConfig.getConfigValue());


    }
  }
}
