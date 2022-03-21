package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.OrgConfig;
import org.codealpha.gmsservice.repositories.AppConfigRepository;
import org.codealpha.gmsservice.repositories.OrgConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppConfigService {

  @Autowired
  private AppConfigRepository appConfigRepository;
  @Autowired
  private OrgConfigRepository orgConfigRepository;

  public List<AppConfig> getAllAppConfigForGrantorOrg(Long orgId){
    return appConfigRepository.getAllAppConfigForOrg(orgId);
  }


  public AppConfig getAppConfigForGranterOrg(Long orgId, AppConfiguration appConfiguration){
    return appConfigRepository.getAppConfigForOrg(orgId,appConfiguration.name());
  }

  public AppConfig getSpecialAppConfigForGranterOrg(Long orgId, AppConfiguration appConfiguration){
    return appConfigRepository.getAppConfigForOrgSpecial(orgId,appConfiguration.name());
  }

  public AppConfig getAppConfigById(Long id){
    return appConfigRepository.findById(id).get();
  }

  public OrgConfig getOrgConfigById(Long id){
    return orgConfigRepository.findById(id).get();
  }

  public AppConfig saveAppConfig(AppConfig config){
    return appConfigRepository.save(config);
  }

  public OrgConfig saveOrgConfig(OrgConfig config){
    return orgConfigRepository.save(config);
  }

  public List<AppConfig> getConfigsForGranter(Long granterId){
    return appConfigRepository.getOnlyOrgConfigs(granterId);
  }
}
