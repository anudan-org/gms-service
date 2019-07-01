package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Template;
import org.codealpha.gmsservice.repositories.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

  @Autowired
  private TemplateRepository templateRepository;

  public Template findByTemplateId(Long templateId){
    if(templateRepository.findById(templateId).isPresent()) {
      return templateRepository.findById(templateId).get();
    }
    return null;
  }
}
