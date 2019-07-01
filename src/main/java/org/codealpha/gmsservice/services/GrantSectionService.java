package org.codealpha.gmsservice.services;

import java.util.List;
import org.codealpha.gmsservice.entities.GrantSection;
import org.codealpha.gmsservice.repositories.GrantSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantSectionService {

  @Autowired
  private GrantSectionRepository grantSectionRepository;

  public List<GrantSection> getAllDefaultSections(){
    return grantSectionRepository.getAllDefaultSections();
  }
}
