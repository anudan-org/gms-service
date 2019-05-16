package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.QuantKpiDataDocument;
import org.codealpha.gmsservice.repositories.QuantKpiDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuantKpiDocumentService {

  @Autowired
  private QuantKpiDocumentRepository quantKpiDocumentRepository;

  public QuantKpiDataDocument saveFile(QuantKpiDataDocument doc){
    return quantKpiDocumentRepository.save(doc);
  }
}
