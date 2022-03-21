package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.QuantitativeKpiNotes;
import org.codealpha.gmsservice.repositories.QuantitativeKpiNotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuantitativeKpiNotesService {

  @Autowired
  private QuantitativeKpiNotesRepository quantitativeKpiNotesRepository;

  public QuantitativeKpiNotes saveQuantitativeKpiNotes(QuantitativeKpiNotes note){
    return quantitativeKpiNotesRepository.save(note);
  }

}
