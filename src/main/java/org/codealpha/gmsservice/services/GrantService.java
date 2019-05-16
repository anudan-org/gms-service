package org.codealpha.gmsservice.services;

import java.util.List;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantService {
  @Autowired
  private GrantRepository grantRepository;

  public List<String> getGrantAlerts(Grant grant){
    return null;
  }

  public Grant saveGrant(Grant grant){
    return grantRepository.save(grant);
  }

  public Grant getById(Long id) {
    return grantRepository.findById(id).get();
  }
}
