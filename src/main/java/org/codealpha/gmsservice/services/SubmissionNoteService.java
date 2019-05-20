package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.SubmissionNote;
import org.codealpha.gmsservice.repositories.SubmissionNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionNoteService {

  @Autowired
  private SubmissionNoteRepository submissionNoteRepository;

  public SubmissionNote saveSubmissionNote(SubmissionNote note){
    return submissionNoteRepository.save(note);
  }
}
