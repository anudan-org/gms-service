package org.codealpha.gmsservice.services;

import java.util.List;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GranterService {

  @Autowired
  private GranterRepository granterRepository;

  public List<Granter> getAllGranters() {
    return (List<Granter>) granterRepository.findAll();
  }
}
