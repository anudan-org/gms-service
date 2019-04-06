package org.codealpha.gmsservice.services;

import java.util.List;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.repositories.AppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

  @Autowired
  private AppConfigRepository appConfigRepository;

  public List<AppConfig> getAllAppConfigForGrantorOrg(Long orgId){
    return appConfigRepository.getAllAppConfigForOrg(orgId);
  }


  public AppConfig getAppConfigForGranterOrg(Long orgId, AppConfiguration appConfiguration){
    return appConfigRepository.getAppConfigForOrg(orgId,appConfiguration.name());
  }
}
