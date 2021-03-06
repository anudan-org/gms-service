package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.DataExportConfig;
import org.codealpha.gmsservice.repositories.DataExportConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataExportConfigService {

    @Autowired
    private DataExportConfigRepository dataExportConfigRepository;

    public DataExportConfig getDataExportConfigForTenant(String name,Long tenantId){
        DataExportConfig exportConfig = dataExportConfigRepository.getExportConfigForTenantByName(name, tenantId);
        if(exportConfig==null){
            exportConfig = dataExportConfigRepository.getExportConfigForPlatformByName(name);
        }
        return exportConfig;
    }

    public List<DataExportConfig> getDataExportConfigForTenantByCategory(String category, Long tenantId){
        List<DataExportConfig> exportConfig = dataExportConfigRepository.getExportConfigForTenantByCategory(category, tenantId);
        if(exportConfig.size()==0){
            exportConfig = dataExportConfigRepository.getExportConfigForPlatformByCategory(category);
        }
        return exportConfig;
    }
}
