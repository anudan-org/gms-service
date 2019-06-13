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
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.GranterGrantSectionAttribute;
import org.codealpha.gmsservice.entities.QualitativeKpiNotes;
import org.codealpha.gmsservice.entities.QuantKpiDataDocument;
import org.codealpha.gmsservice.entities.QuantitativeKpiNotes;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.entities.SubmissionNote;
import org.codealpha.gmsservice.entities.Template;
import org.codealpha.gmsservice.entities.User;
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
  public GrantVO saveGrant(@RequestBody GrantVO grantToSave, @PathVariable("userId") Long userId,
      @RequestHeader("X-TENANT-CODE") String tenantCode) {
    Grant grant = null;
    if (grantToSave.getId() != null) {
      grant = grantService.getById(grantToSave.getId());
      grant.setName(grantToSave.getName());
      grant.setDescription(grantToSave.getDescription());
      grant.setStartDate(DateTime.parse(grantToSave.getStartDate()).toDate());
      grant.setEndDate(DateTime.parse(grantToSave.getEndDate()).toDate());
    } else {
      grant = new Grant();
      grant.setStartDate(DateTime.parse(grantToSave.getStartDate()).toDate());
      grant.setEndDate(DateTime.parse(grantToSave.getEndDate()).toDate());
      grant.setSubstatus(workflowStatusService
          .findInitialStatusByObjectAndGranterOrgId("SUBMISSION",
              organizationService.findOrganizationByTenantCode(tenantCode).getId()));
      grant.setGrantStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("GRANT",
          organizationService.findOrganizationByTenantCode(tenantCode).getId()));
      grant.setOrganization(organizationService.get(1l));
      grant.setGrantorOrganization(organizationService.findOrganizationByTenantCode(tenantCode));
      grant.setCreatedAt(DateTime.now().toDate());
      grant.setCreatedBy(userService.getUserById(userId).getEmailId());
      grant.setName(grantToSave.getName());
      grant.setDescription(grantToSave.getDescription());
      grant.setStartDate(DateTime.parse(grantToSave.getStartDate()).toDate());
      grant.setEndDate(DateTime.parse(grantToSave.getEndDate()).toDate());
      List<Submission> subs = new ArrayList<>();
      grant.setSubmissions(subs);
      List<GrantKpi> kpis = new ArrayList<>();
      grant.setKpis(kpis);
      grant = grantService.saveGrant(grant);
    }

    for (SubmissionVO submissionVO : grantToSave.getSubmissions()) {
      Submission sub = submissionService.getById(submissionVO.getId());
      if (sub != null) {
        sub.setTitle(submissionVO.getTitle());
        sub.setSubmitBy(DateTime.parse(submissionVO.getSubmitBy()).toDate());
        submissionService.saveSubmission(sub);
      }
    }

    saveSectionAndFieldsChanges(grantToSave, grant);
    saveReportingAndGoalChanges(grantToSave, grant, userId, tenantCode);

    grantToSave = new GrantVO()
        .build(grantService.getById(grant.getId()), workflowPermissionService,
            userService.getUserById(userId), appConfigService
                .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                    AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
    return grantToSave;
  }

  private void saveReportingAndGoalChanges(GrantVO grantToSave, Grant grant, Long userId,
      String tenantCode) {

    for (GrantKpi kpi : grantToSave.getKpis()) {
      GrantKpi grantKpi = grantService.getGrantKpiById(kpi.getId());

      if (grantKpi == null) {
        grantKpi = new GrantKpi();
        grantKpi.setScheduled(kpi.isScheduled());
        grantKpi.setPeriodicity(kpi.getPeriodicity());
        grantKpi.setFrequency(kpi.getFrequency());
        grantKpi.setKpiType(kpi.getKpiType());
        grantKpi.setGrant(grant);
        grantKpi.setDescription(kpi.getDescription());
        grantKpi.setCreatedAt(DateTime.now().toDate());
        grantKpi.setCreatedBy(userService.getUserById(userId).getEmailId());
      }

      grantKpi.setTitle(kpi.getTitle());
      grantKpi = grantService.saveGrantKpi(grantKpi);
      grant.getKpis().add(grantKpi);

      List<Template> templates = kpi.getTemplates();
      if (!templates.isEmpty()) {
        for (Template template : templates) {
          Template storedTemplate = grantService.getKpiTemplateById(template.getId());
          if (storedTemplate == null) {
            storedTemplate = new Template();
            storedTemplate
                .setFileType(template.getName().substring(template.getName().lastIndexOf(".") + 1));
            storedTemplate.setDescription(template.getName());
            storedTemplate.setKpi(grantKpi);
            String fileLocation = tenantCode + "/grants/" + grant.getId() + "/templates/kpi/"
                + grantKpi.getId() + "/";
            storedTemplate.setLocation(fileLocation);

            try {

              Files.createDirectories(Paths.get(uploadLocation + fileLocation));
              FileOutputStream fileOutputStream = new FileOutputStream(
                  uploadLocation+fileLocation + template.getName());
              byte[] dataBytes = Base64.getDecoder()
                  .decode(template.getData().substring(template.getData().indexOf(",") + 1));
              fileOutputStream.write(dataBytes);
            } catch (IOException e) {
              logger.error(e.getMessage(), e);
            }

            storedTemplate.setName(template.getName());
            storedTemplate.setType("kpi");
            storedTemplate.setVersion(1);
          }

          storedTemplate = grantService.saveKpiTemplate(storedTemplate);
          if (grantKpi.getTemplates() == null) {
            grantKpi.setTemplates(new ArrayList<>());
          }
          grantKpi.getTemplates().add(storedTemplate);
          grantKpi = grantService.saveGrantKpi(grantKpi);
        }
      }
    }
    grant = grantService.saveGrant(grant);

    for (SubmissionVO submissionVO : grantToSave.getSubmissions()) {
      processQuantitativeData(grant, userId, submissionVO);
      processQualitativeData(grant, userId, submissionVO);
    }
  }

  private void processQuantitativeData(Grant grant, Long userId, SubmissionVO submissionVO) {
    for (GrantQuantitativeKpiData quantitativeKpiData : submissionVO
        .getQuantitiaveKpisubmissions()) {
      GrantQuantitativeKpiData storedKpiData = grantService
          .getGrantQuantitativeKpiDataById(quantitativeKpiData.getId());
      if (storedKpiData != null) {
        if (storedKpiData.getToReport() != quantitativeKpiData.getToReport()) {
          storedKpiData.setToReport(quantitativeKpiData.getToReport());
        }
        if (storedKpiData.getGoal() != quantitativeKpiData.getGoal()) {
          storedKpiData.setGoal(quantitativeKpiData.getGoal());
        }
        if (storedKpiData.getActuals() != quantitativeKpiData.getActuals()) {
          storedKpiData.setActuals(quantitativeKpiData.getActuals());
        }
        grantService.saveGrantQunatitativeKpiData(storedKpiData);
      } else {

        GrantKpi existingKpi = grantService
            .getGrantKpiByNameAndTypeAndGrant(quantitativeKpiData.getGrantKpi().getTitle(),
                quantitativeKpiData.getGrantKpi().getKpiType(), grant);
        Date now = DateTime.now().toDate();
        String userName = userService.getUserById(userId).getEmailId();
        if (existingKpi == null) {
          existingKpi = new GrantKpi();
          existingKpi.setCreatedAt(now);
          existingKpi.setCreatedBy(userName);
          existingKpi.setDescription(quantitativeKpiData.getGrantKpi().getDescription());
          existingKpi.setFrequency(quantitativeKpiData.getGrantKpi().getFrequency());
          existingKpi.setGrant(grant);
          existingKpi.setKpiType(quantitativeKpiData.getGrantKpi().getKpiType());
          existingKpi.setPeriodicity(quantitativeKpiData.getGrantKpi().getPeriodicity());
          existingKpi.setScheduled(quantitativeKpiData.getGrantKpi().isScheduled());
          existingKpi.setTitle(quantitativeKpiData.getGrantKpi().getTitle());
          existingKpi = grantService.saveGrantKpi(existingKpi);
        }

        GrantQuantitativeKpiData grantQuantitativeKpiData = new GrantQuantitativeKpiData();
        grantQuantitativeKpiData.setGoal(quantitativeKpiData.getGoal());
        grantQuantitativeKpiData.setToReport(quantitativeKpiData.getToReport());
        Submission sub = submissionService.getById(submissionVO.getId());
        if (sub == null) {
          sub = new Submission();
          sub.setSubmitBy(DateTime.parse(submissionVO.getSubmitBy()).toDate());
          sub.setSubmissionStatus(submissionVO.getSubmissionStatus());
          sub.setCreatedAt(DateTime.now().toDate());
          sub.setCreatedBy(userService.getUserById(userId).getEmailId());
          sub.setSubmissionStatus(submissionVO.getSubmissionStatus());
          sub.setTitle(submissionVO.getTitle());
          sub.setSubmissionStatus(workflowStatusService
              .findInitialStatusByObjectAndGranterOrgId("SUBMISSION",
                  grant.getGrantorOrganization().getId()));
          List<GrantQuantitativeKpiData> quantitativeKpiDataList = new ArrayList<>();
          sub.setQuantitiaveKpisubmissions(quantitativeKpiDataList);
          sub = submissionService.saveSubmission(sub);
          grant.getSubmissions().add(sub);
          grant = grantService.saveGrant(grant);
          sub.setGrant(grant);
          submissionService.saveSubmission(sub);

        } else {
          sub.setTitle(submissionVO.getTitle());
          sub.setSubmitBy(DateTime.parse(submissionVO.getSubmitBy()).toDate());
          submissionService.saveSubmission(sub);
        }
        grantQuantitativeKpiData.setSubmission(sub);
        grantQuantitativeKpiData.setGrantKpi(existingKpi);
        grantQuantitativeKpiData.setCreatedAt(now);
        grantQuantitativeKpiData.setCreatedBy(userName);

        grantQuantitativeKpiData = grantService
            .saveGrantQunatitativeKpiData(grantQuantitativeKpiData);
        sub.getQuantitiaveKpisubmissions().add(grantQuantitativeKpiData);
        submissionService.saveSubmission(sub);
      }
    }
  }

  private void processQualitativeData(Grant grant, Long userId, SubmissionVO submissionVO) {
    for (GrantQualitativeKpiData qualitativeKpiData : submissionVO
        .getQualitativeKpiSubmissions()) {
      GrantQualitativeKpiData storedKpiData = grantService
          .getGrantQualitativeKpiDataById(qualitativeKpiData.getId());
      if (storedKpiData != null) {
        if (storedKpiData.getToReport() != qualitativeKpiData.getToReport()) {
          storedKpiData.setToReport(qualitativeKpiData.getToReport());
        }
        if (storedKpiData.getGoal() != qualitativeKpiData.getGoal()) {
          storedKpiData.setGoal(qualitativeKpiData.getGoal());
        }
        if (storedKpiData.getActuals() != qualitativeKpiData.getActuals()) {
          storedKpiData.setActuals(qualitativeKpiData.getActuals());
        }
        grantService.saveGrantQualitativeKpiData(storedKpiData);
      } else {

        GrantKpi existingKpi = grantService
            .getGrantKpiByNameAndTypeAndGrant(qualitativeKpiData.getGrantKpi().getTitle(),
                qualitativeKpiData.getGrantKpi().getKpiType(), grant);
        Date now = DateTime.now().toDate();
        String userName = userService.getUserById(userId).getEmailId();
        if (existingKpi == null) {
          existingKpi = new GrantKpi();
          existingKpi.setCreatedAt(now);
          existingKpi.setCreatedBy(userName);
          existingKpi.setDescription(qualitativeKpiData.getGrantKpi().getDescription());
          existingKpi.setFrequency(qualitativeKpiData.getGrantKpi().getFrequency());
          existingKpi.setGrant(grant);
          existingKpi.setKpiType(qualitativeKpiData.getGrantKpi().getKpiType());
          existingKpi.setPeriodicity(qualitativeKpiData.getGrantKpi().getPeriodicity());
          existingKpi.setScheduled(qualitativeKpiData.getGrantKpi().isScheduled());
          existingKpi.setTitle(qualitativeKpiData.getGrantKpi().getTitle());
          existingKpi = grantService.saveGrantKpi(existingKpi);
        }

        GrantQualitativeKpiData grantQualitativeKpiData = new GrantQualitativeKpiData();
        grantQualitativeKpiData.setGoal(qualitativeKpiData.getGoal());
        grantQualitativeKpiData.setToReport(qualitativeKpiData.getToReport());
        Submission sub = submissionService.getById(submissionVO.getId());
        if (sub == null) {
          sub = new Submission();
          sub.setSubmitBy(DateTime.parse(submissionVO.getSubmitBy()).toDate());
          sub.setSubmissionStatus(submissionVO.getSubmissionStatus());
          sub.setCreatedAt(DateTime.now().toDate());
          sub.setCreatedBy(userService.getUserById(userId).getEmailId());
          sub.setSubmissionStatus(submissionVO.getSubmissionStatus());
          sub.setTitle(submissionVO.getTitle());
          List<GrantQualitativeKpiData> qualitativeKpiDataList = new ArrayList<>();
          sub.setQualitativeKpiSubmissions(qualitativeKpiDataList);
          sub.setSubmissionStatus(workflowStatusService
              .findInitialStatusByObjectAndGranterOrgId("SUBMISSION",
                  grant.getGrantorOrganization().getId()));
          sub = submissionService.saveSubmission(sub);
          grant.getSubmissions().add(sub);
          grant = grantService.saveGrant(grant);
          sub.setGrant(grant);
          submissionService.saveSubmission(sub);
        }
        grantQualitativeKpiData.setSubmission(sub);
        grantQualitativeKpiData.setGrantKpi(existingKpi);
        grantQualitativeKpiData.setCreatedAt(now);
        grantQualitativeKpiData.setCreatedBy(userName);

        grantQualitativeKpiData = grantService
            .saveGrantQualitativeKpiData(grantQualitativeKpiData);
        sub.getQualitativeKpiSubmissions().add(grantQualitativeKpiData);
        submissionService.saveSubmission(sub);
      }
    }
  }

  private void processDocumentData(Grant grant, Long userId, SubmissionVO submissionVO) {
    for (GrantDocumentKpiData documentKpiData : submissionVO
        .getDocumentKpiSubmissions()) {
      GrantDocumentKpiData storedKpiData = grantService
          .getGrantDocumentKpiDataById(documentKpiData.getId());
      if (storedKpiData != null) {
        if (storedKpiData.getToReport() != documentKpiData.getToReport()) {
          storedKpiData.setToReport(documentKpiData.getToReport());
        }
        if (storedKpiData.getGoal() != documentKpiData.getGoal()) {
          storedKpiData.setGoal(documentKpiData.getGoal());
        }
        if (storedKpiData.getActuals() != documentKpiData.getActuals()) {
          storedKpiData.setActuals(documentKpiData.getActuals());
        }
        grantService.saveGrantDocumentKpiData(storedKpiData);
      } else {

        GrantKpi existingKpi = grantService
            .getGrantKpiByNameAndTypeAndGrant(documentKpiData.getGrantKpi().getTitle(),
                documentKpiData.getGrantKpi().getKpiType(), grant);
        Date now = DateTime.now().toDate();
        String userName = userService.getUserById(userId).getEmailId();
        if (existingKpi == null) {
          existingKpi = new GrantKpi();
          existingKpi.setCreatedAt(now);
          existingKpi.setCreatedBy(userName);
          existingKpi.setDescription(documentKpiData.getGrantKpi().getDescription());
          existingKpi.setFrequency(documentKpiData.getGrantKpi().getFrequency());
          existingKpi.setGrant(grant);
          existingKpi.setKpiType(documentKpiData.getGrantKpi().getKpiType());
          existingKpi.setPeriodicity(documentKpiData.getGrantKpi().getPeriodicity());
          existingKpi.setScheduled(documentKpiData.getGrantKpi().isScheduled());
          existingKpi.setTitle(documentKpiData.getGrantKpi().getTitle());
          existingKpi = grantService.saveGrantKpi(existingKpi);
        }

        GrantDocumentKpiData grantDocumentKpiData = new GrantDocumentKpiData();
        grantDocumentKpiData.setGoal(documentKpiData.getGoal());
        grantDocumentKpiData.setToReport(documentKpiData.getToReport());
        Submission sub = submissionService.getById(submissionVO.getId());
        if (sub == null) {
          sub = new Submission();
          sub.setSubmitBy(DateTime.parse(submissionVO.getSubmitBy()).toDate());
          sub.setSubmissionStatus(submissionVO.getSubmissionStatus());
          sub.setCreatedAt(DateTime.now().toDate());
          sub.setCreatedBy(userService.getUserById(userId).getEmailId());
          sub.setSubmissionStatus(workflowStatusService
              .findInitialStatusByObjectAndGranterOrgId("SUBMISSION",
                  grant.getGrantorOrganization().getId()));
          sub.setTitle(submissionVO.getTitle());
          List<GrantDocumentKpiData> documentList = new ArrayList<>();
          sub.setDocumentKpiSubmissions(documentList);
          sub = submissionService.saveSubmission(sub);
          grant.getSubmissions().add(sub);
          grant = grantService.saveGrant(grant);
          sub.setGrant(grant);
          submissionService.saveSubmission(sub);
        }
        grantDocumentKpiData.setSubmission(sub);
        grantDocumentKpiData.setGrantKpi(existingKpi);
        grantDocumentKpiData.setCreatedAt(now);
        grantDocumentKpiData.setCreatedBy(userName);

        grantDocumentKpiData = grantService
            .saveGrantDocumentKpiData(grantDocumentKpiData);
        sub.getDocumentKpiSubmissions().add(grantDocumentKpiData);
        submissionService.saveSubmission(sub);
      }
    }
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
