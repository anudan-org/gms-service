package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.QualitativeKpiNotes;
import org.codealpha.gmsservice.entities.QuantitativeKpiNotes;
import org.codealpha.gmsservice.repositories.QualitativeKpiNotesRepository;
import org.codealpha.gmsservice.repositories.QuantitativeKpiNotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QualitativeKpiNotesService {

  @Autowired
  private QualitativeKpiNotesRepository qualitativeKpiNotesRepository;

  public QualitativeKpiNotes saveQualitativeKpiNotes(QualitativeKpiNotes note){
    return qualitativeKpiNotesRepository.save(note);
  }

}
