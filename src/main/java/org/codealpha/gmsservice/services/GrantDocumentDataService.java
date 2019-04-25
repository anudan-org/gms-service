package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantDocumentKpiData;
import org.codealpha.gmsservice.repositories.GrantDocumentDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantDocumentDataService {

  @Autowired
  private GrantDocumentDataRepository grantDocumentDataRepository;

  public GrantDocumentKpiData saveDocumentKpi(GrantDocumentKpiData grantDocumentKpiData){
    return grantDocumentDataRepository.save(grantDocumentKpiData);
  }

  public GrantDocumentKpiData findById(Long id){
    return grantDocumentDataRepository.findById(id).get();
  }

  public GrantDocumentKpiData findByKpiIdAndSubmissionId(Long kpidId, Long submissionId){
    return grantDocumentDataRepository.findByGrantKpiIdAndSubmissionId(kpidId,submissionId);
  }
}
