package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.DocKpiDataDocument;
import org.codealpha.gmsservice.repositories.DocKpiDataDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocKpiDataDocumentService {

  @Autowired
  protected DocKpiDataDocumentRepository docKpiDataDocumentRepository;

  public DocKpiDataDocument saveKpiDoc(DocKpiDataDocument document){
    return docKpiDataDocumentRepository.save(document);
  }

  public DocKpiDataDocument getById(Long id){
    return docKpiDataDocumentRepository.findById(id).get();
  }
}
