package org.codealpha.gmsservice.schedulers;

import java.text.SimpleDateFormat;
import java.util.List;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.services.AppConfigService;
import org.codealpha.gmsservice.services.GranterService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

public class DueReportsReminderTask implements Runnable {

    private Long orgId;

    public DueReportsReminderTask(Long orgId) {
        this.orgId = orgId;
    }

    @Override
    public void run() {
        System.out.println("Running now for Org ( "+orgId+") at " + DateTime.now().toDate());
    }
}
