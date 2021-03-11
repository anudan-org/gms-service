package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.DataExportConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DataExportConfigRepository extends CrudRepository<DataExportConfig,Long> {

    @Query(value = "select * from data_export_config where name=?1 and tenant=?2",nativeQuery = true)
    DataExportConfig getExportConfigForTenantByName(String name,Long tenantId);

    @Query(value = "select * from data_export_config where name=?1 and tenant is null",nativeQuery = true)
    DataExportConfig getExportConfigForPlatformByName(String name);

    @Query(value = "select * from data_export_config where category=?1 and tenant=?2",nativeQuery = true)
    List<DataExportConfig> getExportConfigForTenantByCategory(String name, Long tenantId);

    @Query(value = "select * from data_export_config where category=?1 and tenant is null",nativeQuery = true)
    List<DataExportConfig> getExportConfigForPlatformByCategory(String name);
}
