package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.repositories.GrantQuantitativeDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantQuantitativeDataService {

  @Autowired
  private GrantQuantitativeDataRepository grantQuantitativeDataRepository;

  public GrantQuantitativeKpiData saveData(GrantQuantitativeKpiData data){
    return grantQuantitativeDataRepository.save(data);
  }

  public GrantQuantitativeKpiData findById(Long id){
    return grantQuantitativeDataRepository.findById(id).get();
  }
}
