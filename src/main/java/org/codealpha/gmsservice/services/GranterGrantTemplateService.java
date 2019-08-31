package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GranterGrantTemplate;
import org.codealpha.gmsservice.repositories.GranterGrantTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GranterGrantTemplateService {

    @Autowired
    private GranterGrantTemplateRepository granterGrantTemplateRepository;

    public List<GranterGrantTemplate> findByGranterId(Long granterId){
        return granterGrantTemplateRepository.findByGranterId(granterId);
    }

    public GranterGrantTemplate findByTemplateId(Long templateId){
        return granterGrantTemplateRepository.findById(templateId).get();
    }
}
