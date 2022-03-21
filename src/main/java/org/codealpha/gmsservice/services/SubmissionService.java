package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {

  @Autowired
  private SubmissionRepository submissionRepository;

  public Submission saveSubmission(Submission submission) {
    return submissionRepository.save(submission);
  }

  public String buildMailContent(Submission submission, String configValue) {
    return configValue.replace("%SUBMISSION_TITLE%",
        (submission.getGrant().getName() + " - " + submission.getTitle()).toUpperCase())
            .replace("%SUBMISSION_STATUS%", submission.getSubmissionStatus().getDisplayName().toUpperCase());
  }

  public Submission getById(Long id){
    Optional<Submission> subissionById = submissionRepository.findById(id);
    if(subissionById.isPresent()) {
      return subissionById.get();
    }
    return null;
  }

  public List<Submission> saveSubmissions(List<Submission> submissions) {
    return (List<Submission>) submissionRepository.saveAll(submissions);
  }
}
