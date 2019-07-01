package org.codealpha.gmsservice.services;

import java.util.List;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.models.SubmissionVO;
import org.codealpha.gmsservice.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    if(submissionRepository.findById(id).isPresent()) {
      return submissionRepository.findById(id).get();
    }
    return null;
  }

  public List<Submission> saveSubmissions(List<Submission> submissions) {
    return (List<Submission>) submissionRepository.saveAll(submissions);
  }
}
