package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.AppConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppConfigRepository extends CrudRepository<AppConfig,Long> {

  @Query(value = "select A.id,case when B.config_name is not null then B.config_name else A.config_name end as config_name,case when B.id is null then 'app' else 'org' end as type,case when B.id is null then A.id else B.id end as key, case when B.config_name is not null then B.config_value else A.config_value end as config_value,case when B.description is not null then B.description else A.description end as description,case when B.id is null then A.configurable else B.configurable end as configurable from app_config A left join (select * from org_config where granter_id=?1) B on A.config_name=B.config_name",nativeQuery = true)
  public List<AppConfig> getAllAppConfigForOrg(Long grantorOrgId);

  @Query(value = "select distinct A.id,case when B.config_name is not null then B.config_name else A.config_name end as config_name,case when B.id is null then 'app' else 'org' end as type,case when B.id is null then A.id else B.id end as key,case when B.config_name is not null then B.config_value else A.config_value end as config_value,case when B.description is not null then B.description else A.description end as description,case when B.id is null then A.configurable else B.configurable end as configurable from app_config A left join (select * from org_config where granter_id=?1) B on A.config_name=B.config_name where A.config_name=?2",nativeQuery = true)
  public AppConfig getAppConfigForOrg(Long grantorOrgId,String appConfigName);

  @Query(value = "select distinct case when B.id is null then 0 else B.granter_id end as id,case when B.id is null then 'app' else 'org' end as type,case when B.id is null then A.id else B.id end as key,case when B.config_name is not null then B.config_name else A.config_name end as config_name,case when B.config_name is not null then B.config_value else A.config_value end as config_value,case when B.description is not null then B.description else A.description end as description,case when B.id is null then A.configurable else B.configurable end as configurable from app_config A left join (select * from org_config where granter_id=?1) B on A.config_name=B.config_name where A.config_name=?2",nativeQuery = true)
  public AppConfig getAppConfigForOrgSpecial(Long grantorOrgId,String appConfigName);

  @Query(value = "select B.* from org_config B where B.granter_id=?1",nativeQuery = true)
  public List<AppConfig> getOnlyOrgConfigs(Long orgId);

}
