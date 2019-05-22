package org.codealpha.gmsservice.services;

import java.util.ArrayList;
import java.util.List;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.repositories.AppConfigRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppConfigServiceTest {

  @MockBean
  private AppConfigRepository appConfigRepository;
  @Autowired
  private AppConfigService appConfigService;

  @Test
  public void getAllAppConfigForGrantorOrg() {
    Mockito.doReturn(new ArrayList<AppConfig>()).when(appConfigRepository)
        .getAllAppConfigForOrg(Mockito.anyLong());

    List<AppConfig> configs = appConfigService.getAllAppConfigForGrantorOrg(1L);
    Assert.assertTrue(configs.size() == 0);
  }

  @Test
  public void getAppConfigForGranterOrg() {
    Mockito.doReturn(new AppConfig()).when(appConfigRepository)
        .getAppConfigForOrg(Mockito.anyLong(), Mockito.any());

    AppConfig config = appConfigService
        .getAppConfigForGranterOrg(1L, AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS);
    Assert.assertNotNull(config);
  }
}
