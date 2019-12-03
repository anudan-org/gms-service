package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GranterReportTemplate;
import org.codealpha.gmsservice.repositories.GranterReportTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GranterReportTemplateService {
    @Autowired private GranterReportTemplateRepository granterReportTemplateRepository;

    public GranterReportTemplate saveReportTemplate(GranterReportTemplate template){
        return granterReportTemplateRepository.save(template);
    }

    public void markAllAsNotDefault(){
        granterReportTemplateRepository.findAll().forEach(t ->{
            t.setDefaultTemplate(false);
            granterReportTemplateRepository.save(t);
        });
    }
}
