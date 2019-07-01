package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.repositories.GrantDocumentAttributesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantDocumentAttributesService {

  @Autowired
  private GrantDocumentAttributesRepository grantDocumentAttributesRepository;

  public GrantDocumentAttributes findByGrantAndName(Grant grant, String name){
    return grantDocumentAttributesRepository.findByGrantAndName(grant,name);
  }
}
