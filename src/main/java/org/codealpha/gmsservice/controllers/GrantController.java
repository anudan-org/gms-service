package org.codealpha.gmsservice.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.transaction.Transactional;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.DocKpiDataDocument;
import org.codealpha.gmsservice.entities.DocumentKpiNotes;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentKpiData;
import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.QualitativeKpiNotes;
import org.codealpha.gmsservice.entities.QuantKpiDataDocument;
import org.codealpha.gmsservice.entities.QuantitativeKpiNotes;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.entities.SubmissionNote;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.models.KpiSubmissionData;
import org.codealpha.gmsservice.models.SubmissionData;
import org.codealpha.gmsservice.models.UploadFile;
import org.codealpha.gmsservice.services.AppConfigService;
import org.codealpha.gmsservice.services.CommonEmailSevice;
import org.codealpha.gmsservice.services.DocKpiDataDocumentService;
import org.codealpha.gmsservice.services.DocumentKpiNotesService;
import org.codealpha.gmsservice.services.GrantDocumentDataService;
import org.codealpha.gmsservice.services.GrantQualitativeDataService;
import org.codealpha.gmsservice.services.GrantQuantitativeDataService;
import org.codealpha.gmsservice.services.GrantService;
import org.codealpha.gmsservice.services.QualitativeKpiNotesService;
import org.codealpha.gmsservice.services.QuantKpiDocumentService;
import org.codealpha.gmsservice.services.QuantitativeKpiNotesService;
import org.codealpha.gmsservice.services.SubmissionNoteService;
import org.codealpha.gmsservice.services.SubmissionService;
import org.codealpha.gmsservice.services.UserService;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.codealpha.gmsservice.services.WorkflowStatusService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/{userId}/grant")
public class GrantController {

  private static Logger logger = LoggerFactory.getLogger(GrantController.class);

  @Autowired
  private GrantQuantitativeDataService quantitativeDataService;
  @Autowired
  GrantQualitativeDataService qualitativeDataService;
  @Autowired
  private WorkflowPermissionService workflowPermissionService;
  @Autowired
  private AppConfigService appConfigService;
  @Autowired
  private UserService userService;
  @Autowired
  private WorkflowStatusService workflowStatusService;
  @Autowired
  private GrantService grantService;
  @Autowired
  private SubmissionService submissionService;
  @Autowired
  private GrantDocumentDataService grantDocumentDataService;
  @Autowired
  private QuantitativeKpiNotesService quantitativeKpiNotesService;
  @Autowired
  private QualitativeKpiNotesService qualitativeKpiNotesService;
  @Autowired
  private DocumentKpiNotesService documentKpiNotesService;
  @Autowired
  private DocKpiDataDocumentService docKpiDataDocumentService;
  @Autowired
  private QuantKpiDocumentService quantKpiDocumentService;
  @Autowired
  private SubmissionNoteService submissionNoteService;

  @Value("${spring.upload-file-location}")
  private String uploadLocation;

  @Autowired
  private CommonEmailSevice commonEmailSevice;

  @GetMapping("/{id}")
  public GrantVO getGrant(@PathVariable("id") Long grantId, @PathVariable("userId") Long userId) {

    User user = userService.getUserById(userId);
    Grant grant = grantService.getById(grantId);
    return new GrantVO()
        .build(grant, workflowPermissionService, user,
            appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
  }

  @PutMapping(value = "/kpi")
  @Transactional
  public GrantVO saveKpiSubmissions(@RequestBody SubmissionData submissionData,
      @PathVariable("userId") Long userId) {

    User user = userService.getUserById(userId);
    for (KpiSubmissionData data : submissionData.getKpiSubmissionData()) {
      switch (data.getType()) {
        case "QUANTITATIVE":
          GrantQuantitativeKpiData quantitativeKpiData = quantitativeDataService
              .findById(data.getKpiDataId());
          quantitativeKpiData.setActuals(Integer.valueOf(data.getValue()));
          quantitativeKpiData.setUpdatedAt(DateTime.now().toDate());
          quantitativeKpiData.setUpdatedBy(user.getEmailId());

          if (data.getNotes() != null) {
            for (String note : data.getNotes()) {
              QuantitativeKpiNotes kpiNote = new QuantitativeKpiNotes();
              kpiNote.setMessage(note);
              kpiNote.setPostedBy(user);
              kpiNote.setPostedOn(DateTime.now().toDate());
              kpiNote.setKpiData(quantitativeKpiData);
              kpiNote = quantitativeKpiNotesService.saveQuantitativeKpiNotes(kpiNote);
              quantitativeKpiData.getNotesHistory().add(kpiNote);
            }
          }

          List<QuantKpiDataDocument> kpiDocs = new ArrayList<>();
          if ((data.getFiles() != null && !data
              .getFiles().isEmpty())) {
            for (UploadFile uploadedFile : data.getFiles()) {
              String fileName = uploadLocation + uploadedFile.getFileName();
              QuantKpiDataDocument quantKpiDataDocument = new QuantKpiDataDocument();
              quantKpiDataDocument.setFileName(uploadedFile.getFileName());
              if (quantitativeKpiData.getSubmissionDocs().contains(quantKpiDataDocument)) {
                quantKpiDataDocument = quantitativeKpiData.getSubmissionDocs()
                    .get(quantitativeKpiData.getSubmissionDocs().indexOf(quantKpiDataDocument));
                quantKpiDataDocument.setVersion(quantKpiDataDocument.getVersion() + 1);
              } else {
                quantKpiDataDocument.setFileType(uploadedFile.getFileType());
                quantKpiDataDocument.setQuantKpiData(quantitativeKpiData);
              }

              try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {

                byte[] dataBytes = Base64.getDecoder().decode(uploadedFile.getValue());
                fileOutputStream.write(dataBytes);
              } catch (IOException e) {
                logger.error(e.getMessage(), e);
              }

              quantKpiDataDocument = quantKpiDocumentService.saveFile(quantKpiDataDocument);
              kpiDocs.add(quantKpiDataDocument);
            }

            quantitativeKpiData.setSubmissionDocs(kpiDocs);
            quantitativeKpiData = quantitativeDataService.saveData(quantitativeKpiData);

          }
          break;
        case "QUALITATIVE":
          GrantQualitativeKpiData qualitativeKpiData = qualitativeDataService
              .findById(data.getKpiDataId());
          qualitativeKpiData.setActuals(data.getValue());
          qualitativeKpiData.setCreatedAt(DateTime.now().toDate());
          qualitativeKpiData.setUpdatedBy(user.getEmailId());

          qualitativeDataService.saveData(qualitativeKpiData);
          if (data.getNotes() != null) {
            for (String note : data.getNotes()) {
              QualitativeKpiNotes kpiNote = new QualitativeKpiNotes();
              kpiNote.setMessage(note);
              kpiNote.setPostedBy(user);
              kpiNote.setPostedOn(DateTime.now().toDate());
              kpiNote.setKpiData(qualitativeKpiData);
              kpiNote = qualitativeKpiNotesService.saveQualitativeKpiNotes(kpiNote);
              qualitativeKpiData.getNotesHistory().add(kpiNote);
            }
          }
          break;
        case "DOCUMENT":
          GrantDocumentKpiData documentKpiData = grantDocumentDataService
              .findById(data.getKpiDataId());

          List<DocKpiDataDocument> docKpiDataDocuments = documentKpiData.getSubmissionDocs();
          List<DocKpiDataDocument> submissionDocs = new ArrayList<>();

          if (documentKpiData.getActuals() != null && (data.getFiles() == null || data
              .getFiles().isEmpty())) {
          } else {
            for (UploadFile uploadedFile : data.getFiles()) {
              String fileName = uploadLocation + uploadedFile.getFileName();
              DocKpiDataDocument docKpiDataDocument = new DocKpiDataDocument();
              docKpiDataDocument.setFileName(uploadedFile.getFileName());
              if (docKpiDataDocuments.contains(docKpiDataDocument)) {
                docKpiDataDocument = docKpiDataDocuments
                    .get(docKpiDataDocuments.indexOf(docKpiDataDocument));
                docKpiDataDocument.setVersion(docKpiDataDocument.getVersion() + 1);
              } else {
                docKpiDataDocument.setFileType(uploadedFile.getFileType());
                docKpiDataDocument.setDocKpiData(documentKpiData);
              }

              try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                byte[] dataBytes = Base64.getDecoder().decode(uploadedFile.getValue());
                fileOutputStream.write(dataBytes);
                fileOutputStream.close();
              } catch (IOException e) {
                logger.error(e.getMessage(), e);
              }

              docKpiDataDocument = docKpiDataDocumentService.saveKpiDoc(docKpiDataDocument);
              submissionDocs.add(docKpiDataDocument);
            }

          }
          if (data.getNotes() != null) {
            for (String note : data.getNotes()) {
              DocumentKpiNotes kpiNote = new DocumentKpiNotes();
              kpiNote.setMessage(note);
              kpiNote.setPostedBy(user);
              kpiNote.setPostedOn(DateTime.now().toDate());
              kpiNote.setKpiData(documentKpiData);
              kpiNote = documentKpiNotesService.saveDocumentKpiNotes(kpiNote);
              documentKpiData.getNotesHistory().add(kpiNote);
            }
          }
          documentKpiData.setSubmissionDocs(submissionDocs);
          grantDocumentDataService.saveDocumentKpi(documentKpiData);
          break;

        default:
          logger.info("Nothing to do");
      }
    }

    Submission submission = submissionService.getById(submissionData.getId());

    for (String msg : submissionData.getNotes()) {
      SubmissionNote note = new SubmissionNote();
      note.setMessage(msg);
      note.setSubmission(submission);
      note.setPostedBy(user);
      note.setPostedOn(DateTime.now().toDate());
      submissionNoteService.saveSubmissionNote(note);
    }
    submission.setSubmittedOn(DateTime.now().toDate());
    submission
        .setSubmissionStatus(workflowStatusService
            .findById(submissionData.getKpiSubmissionData().get(0).getToStatusId()));

    submission = submissionService.saveSubmission(submission);

    List<User> usersToNotify = userService
        .usersToNotifyOnSubmissionStateChangeTo(submission.getSubmissionStatus().getId());

    for (User userToNotify : usersToNotify) {
      commonEmailSevice.sendMail(userToNotify.getEmailId(), appConfigService
              .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
                  AppConfiguration.SUBMISSION_ALTER_MAIL_SUBJECT).getConfigValue(),
          submissionService.buildMailContent(submission, appConfigService
              .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
                  AppConfiguration.SUBMISSION_ALTER_MAIL_CONTENT).getConfigValue()));
    }

    Grant grant = submission.getGrant();
    grant.setSubstatus(submission.getSubmissionStatus());
    grant = grantService.saveGrant(grant);
    grant = grantService.getById(grant.getId());
    return new GrantVO()
        .build(grant, workflowPermissionService, user,
            appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
  }
}
