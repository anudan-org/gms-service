package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.ClosureReason;
import org.codealpha.gmsservice.repositories.ClosureReasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClosureReasonService {

  @Autowired
  private ClosureReasonRepository closureReasonRepository;

  public ClosureReason saveClosureReason(ClosureReason reason) {
    return closureReasonRepository.save(reason);
  }

  public List<ClosureReason> getByOrganization(Long orgId) {
    return closureReasonRepository.getClosureReasonsForOrg(orgId);
  }

  public ClosureReason getById(Long id) {
    return closureReasonRepository.findById(id).get();
  }

  public void deleteClosureReason(ClosureReason reason) {
    closureReasonRepository.delete(reason);
  }

  public Long getReasonUsageCount(Long orgId,Long reasonId) {

    return closureReasonRepository.getReasonUsageCount(orgId, reasonId);
  }


  
  


}
