package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.TemplateLibrary;
import org.codealpha.gmsservice.repositories.TemplateLibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateLibraryService {
    @Autowired
    private TemplateLibraryRepository templateLibraryRepository;

    public List<TemplateLibrary> getTemplateLibraryForGranter(Granter granter){
        return templateLibraryRepository.findByGranterId(granter.getId());
    }

    public TemplateLibrary getTemplateLibraryDocumentById(Long id){
        return templateLibraryRepository.findById(id).get();
    }

    public List<TemplateLibrary> getTemplateLibraryForOrganization(Long orgId){
        return templateLibraryRepository.findByGranterId(orgId);
    }
}
