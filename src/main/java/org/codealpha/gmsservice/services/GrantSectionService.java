package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantSection;
import org.codealpha.gmsservice.repositories.GrantSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrantSectionService {

  @Autowired
  private GrantSectionRepository grantSectionRepository;

  public List<GrantSection> getAllDefaultSections(){
    return grantSectionRepository.getAllDefaultSections();
  }

  public GrantSection saveGrantSection(GrantSection section){
    return grantSectionRepository.save(section);
  }
}
