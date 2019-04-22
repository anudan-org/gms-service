package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.KpiSubmission;
import org.codealpha.gmsservice.repositories.KpiSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KpiSubmissionService {

  @Autowired
  private KpiSubmissionRepository kpiSubmissionRepository;

  public KpiSubmission findById(Long id){
    return kpiSubmissionRepository.findById(id).get();
  }

  public KpiSubmission saveKpiSubmission(KpiSubmission kpiSubmission){
  return kpiSubmissionRepository.save(kpiSubmission);
  }
}
