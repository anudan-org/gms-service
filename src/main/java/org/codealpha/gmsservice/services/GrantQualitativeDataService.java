package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.repositories.GrantQualitativeDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantQualitativeDataService {

  @Autowired
  private GrantQualitativeDataRepository grantQualitativeDataRepository;

  public GrantQualitativeKpiData saveData(GrantQualitativeKpiData data){
    return grantQualitativeDataRepository.save(data);
  }

  public GrantQualitativeKpiData findById(Long id){
    return grantQualitativeDataRepository.findById(id).orElse(null);
  }
}
