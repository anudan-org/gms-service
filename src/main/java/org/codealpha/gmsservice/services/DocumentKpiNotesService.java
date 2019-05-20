package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.DocumentKpiNotes;
import org.codealpha.gmsservice.entities.QualitativeKpiNotes;
import org.codealpha.gmsservice.repositories.DocumentKpiNotesRepository;
import org.codealpha.gmsservice.repositories.QualitativeKpiNotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentKpiNotesService {

  @Autowired
  private DocumentKpiNotesRepository documentKpiNotesRepository;

  public DocumentKpiNotes saveDocumentKpiNotes(DocumentKpiNotes note){
    return documentKpiNotesRepository.save(note);
  }

}
