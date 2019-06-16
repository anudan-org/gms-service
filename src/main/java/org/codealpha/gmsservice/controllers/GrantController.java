package org.codealpha.gmsservice.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.Frequency;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.DocKpiDataDocument;
import org.codealpha.gmsservice.entities.DocumentKpiNotes;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.entities.GrantDocumentKpiData;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.GrantStringAttribute;
import org.codealpha.gmsservice.entities.Grantee;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.GranterGrantSectionAttribute;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.QualitativeKpiNotes;
import org.codealpha.gmsservice.entities.QuantKpiDataDocument;
import org.codealpha.gmsservice.entities.QuantitativeKpiNotes;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.entities.SubmissionNote;
import org.codealpha.gmsservice.entities.Template;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.models.GrantQuantitativeKpiDataVO;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.models.KpiSubmissionData;
import org.codealpha.gmsservice.models.SectionAttributesVO;
import org.codealpha.gmsservice.models.SectionVO;
import org.codealpha.gmsservice.models.SubmissionData;
import org.codealpha.gmsservice.models.SubmissionVO;
import org.codealpha.gmsservice.models.UploadFile;
import org.codealpha.gmsservice.services.AppConfigService;
import org.codealpha.gmsservice.services.CommonEmailSevice;
import org.codealpha.gmsservice.services.DocKpiDataDocumentService;
import org.codealpha.gmsservice.services.DocumentKpiNotesService;
import org.codealpha.gmsservice.services.GrantDocumentDataService;
import org.codealpha.gmsservice.services.GrantQualitativeDataService;
import org.codealpha.gmsservice.services.GrantQuantitativeDataService;
import org.codealpha.gmsservice.services.GrantService;
import org.codealpha.gmsservice.services.OrganizationService;
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
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
  @Autowired
  private OrganizationService organizationService;

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

              try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {

                byte[] dataBytes = Base64.getDecoder().decode(uploadedFile.getValue());
                fileOutputStream.write(dataBytes);
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


  @PutMapping("/")
  public Grant saveGrant(@RequestBody Grant grantToSave, @PathVariable("userId") Long userId,
      @RequestHeader("X-TENANT-CODE") String tenantCode) {

    /*Grant existingGrant = grantService.getById(grantToSave.getId());
    if (existingGrant == null) {
      existingGrant = _createGrant(grantToSave);
    }*/

    grantToSave  = grantService.saveGrant(grantToSave);
    User user = userService.getUserById(userId);

    grantToSave.setActionAuthorities(workflowPermissionService
        .getGrantActionPermissions(grantToSave.getGrantorOrganization().getId(),
            user.getUserRoles(), grantToSave.getGrantStatus().getId()));

    grantToSave.setFlowAuthorities(workflowPermissionService
        .getGrantFlowPermissions(grantToSave.getGrantorOrganization().getId(),
            user.getUserRoles()));

    for (Submission submission : grantToSave.getSubmissions()) {
      submission.setActionAuthorities(workflowPermissionService
          .getSubmissionActionPermission(grantToSave.getGrantorOrganization().getId(),
              user.getUserRoles()));

      AppConfig submissionWindow = appConfigService
          .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
              AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS);
      Date submissionWindowStart = new DateTime(submission.getSubmitBy())
          .minusDays(Integer.valueOf(submissionWindow.getConfigValue()) + 1).toDate();

      List<WorkFlowPermission> flowPermissions = workflowPermissionService
          .getSubmissionFlowPermissions(grantToSave.getGrantorOrganization().getId(),
              user.getUserRoles(), submission.getSubmissionStatus().getId());

      if (!flowPermissions.isEmpty() && DateTime.now().toDate()
          .after(submissionWindowStart)) {
        submission.setFlowAuthorities(flowPermissions);
      }
    }

    GrantVO grantVO = new GrantVO();
    grantVO = grantVO.build(grantToSave, workflowPermissionService, user, appConfigService
        .getAppConfigForGranterOrg(grantToSave.getGrantorOrganization().getId(),
            AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
    grantToSave.setGrantDetails(grantVO.getGrantDetails());

    //submissionService.saveSubmissions(grantToSave.getSubmissions());

    return grantToSave;
  }

  private Grant _createGrant(GrantVO grantToSave, Organization userOrg, Organization tenantOrg) {
    Grant grant = new Grant();
    grant.setEndDate(grantToSave.getEndDate());
    grant.setStartDate(grantToSave.getEndDate());
    grant.setDescription(grantToSave.getDescription());
    grant.setName(grantToSave.getName());
    grant.setName(grantToSave.getName());
    grant.setOrganization((Grantee) userOrg);
    grant.setGrantorOrganization((Granter)tenantOrg);
    grant.setGrantStatus(
        workflowStatusService.findInitialStatusByObjectAndGranterOrgId("GRANT", tenantOrg.getId()));
    //grant.set

    for (GrantKpi sentGrantKpi : grantToSave.getKpis()) {
      GrantKpi existingGrantKpi = grantService.getGrantKpiById(sentGrantKpi.getId());
      if (existingGrantKpi == null) {
        existingGrantKpi = _createGrantKpi(grant, sentGrantKpi);
      }
    }
    return grant;
  }

  private GrantKpi _createGrantKpi(Grant grant, GrantKpi sentGrantKpi) {
    GrantKpi grantKpi = new GrantKpi();
    grantKpi.setDescription(sentGrantKpi.getDescription());
    grantKpi.setGrant(grant);
return null;
  }


  @PostMapping("/{grantId}/flow/{fromState}/{toState}")
  public GrantVO MoveGrantState(@PathVariable("userId") Long userId,
      @PathVariable("grantId") Long grantId, @PathVariable("fromState") Long fromStateId,
      @PathVariable("toState") Long toStateId) {

    Grant grant = grantService.getById(grantId);
    grant.setGrantStatus(workflowStatusService.findById(toStateId));
    grant.setUpdatedAt(DateTime.now().toDate());
    grant.setUpdatedBy(userService.getUserById(userId).getEmailId());
    grant = grantService.saveGrant(grant);
    return new GrantVO().build(grant, workflowPermissionService, userService.getUserById(userId),
        appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
            AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));

  }

  @PutMapping("/{grantId}/submission/flow/{fromState}/{toState}")
  public SubmissionVO saveAndMoveSubmissionState(@RequestBody SubmissionVO submissionVO,
      @PathVariable("userId") Long userId,
      @PathVariable("grantId") Long grantId, @PathVariable("fromState") Long fromStateId,
      @PathVariable("toState") Long toStateId) {

    Submission submission = submissionService.getById(submissionVO.getId());

    for (GrantQuantitativeKpiData grantQuantitativeKpiData : submissionVO
        .getQuantitiaveKpisubmissions()) {
      GrantQuantitativeKpiData quantitativeKpiData = grantService
          .getGrantQuantitativeKpiDataById(grantQuantitativeKpiData.getId());
      if (quantitativeKpiData.getActuals() != grantQuantitativeKpiData.getActuals()) {
        quantitativeKpiData.setActuals(grantQuantitativeKpiData.getActuals());
        grantService.saveGrantQunatitativeKpiData(quantitativeKpiData);
      }
    }
    for (GrantQualitativeKpiData grantQualitativeKpiData : submissionVO
        .getQualitativeKpiSubmissions()) {
      GrantQualitativeKpiData qualititativeKpiData = grantService
          .getGrantQualitativeKpiDataById(grantQualitativeKpiData.getId());
      if (qualititativeKpiData.getActuals() != grantQualitativeKpiData.getActuals()) {
        qualititativeKpiData.setActuals(grantQualitativeKpiData.getActuals());
        grantService.saveGrantQualitativeKpiData(qualititativeKpiData);
      }
    }

    if (submission.getSubmissionStatus().getId() != toStateId) {
      submission.setSubmissionStatus(
          workflowStatusService.findById(toStateId));
      Date now = DateTime.now().toDate();
      submission.setSubmittedOn(now);
      submission.setUpdatedBy(userService.getUserById(userId).getEmailId());
      submission.setUpdatedAt(now);

      submissionService.saveSubmission(submission);
    }

    Grant grant = grantService.getById(submission.getGrant().getId());
    grant.setSubstatus(workflowStatusService.findById(toStateId));
    grantService.saveGrant(grant);
    return new SubmissionVO()
        .build(submission, workflowPermissionService, userService.getUserById(userId),
            appConfigService
                .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
                    AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));

  }

  private void saveSectionAndFieldsChanges(
      @RequestBody GrantVO grantToSave, Grant grant) {
    for (SectionVO sectionVO : grantToSave.getGrantDetails().getSections()) {
      GranterGrantSection grantSection = grantService.getGrantSectionBySectionId(sectionVO.getId());

      if (grantSection == null) {
        GranterGrantSection newSection = new GranterGrantSection();
        newSection.setDeletable(true);
        newSection.setGranter((Granter) grant.getGrantorOrganization());
        newSection.setSectionName(sectionVO.getName());
        newSection = grantService.saveSection(newSection);
        grantSection = newSection;
      }

      for (SectionAttributesVO sectionAttributesVO : sectionVO.getAttributes()) {
        GranterGrantSectionAttribute sectionAttribute = grantService
            .getSectionAttributeByAttributeIdAndType(sectionAttributesVO.getId(),
                sectionAttributesVO.getFieldType());

        if (sectionAttribute == null) {
          GranterGrantSectionAttribute newSectionAttrib = new GranterGrantSectionAttribute();
          newSectionAttrib.setDeletable(true);
          newSectionAttrib.setFieldName(sectionAttributesVO.getFieldName());
          newSectionAttrib.setFieldType(sectionAttributesVO.getFieldType());
          newSectionAttrib.setGranter(grantSection.getGranter());
          newSectionAttrib.setRequired(false);
          newSectionAttrib.setSection(grantSection);

          newSectionAttrib = grantService.saveSectionAttribute(newSectionAttrib);
          sectionAttribute = newSectionAttrib;
        }

        switch (sectionAttributesVO.getFieldType()) {
          case "string":
            GrantStringAttribute grantStringAttribute = grantService
                .getStringAttributeByAttribute(sectionAttribute);

            if (grantStringAttribute == null) {
              GrantStringAttribute newGrantStringAttribute = new GrantStringAttribute();
              newGrantStringAttribute.setValue(sectionAttributesVO.getFieldValue());
              newGrantStringAttribute.setGrant(grant);
              newGrantStringAttribute.setSection(grantSection);
              newGrantStringAttribute.setSectionAttribute(sectionAttribute);

              newGrantStringAttribute = grantService.saveStringAttribute(newGrantStringAttribute);
              grantStringAttribute = newGrantStringAttribute;
            }

            if (!grantStringAttribute.getValue()
                .equalsIgnoreCase(sectionAttributesVO.getFieldValue())) {
              grantStringAttribute.setValue(sectionAttributesVO.getFieldValue());
              grantService.saveStringAttribute(grantStringAttribute);
            }
            break;
          case "document":
            break;
        }
      }
    }
  }
}
