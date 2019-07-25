package org.codealpha.gmsservice.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.Frequency;
import org.codealpha.gmsservice.constants.KpiType;
import org.codealpha.gmsservice.entities.*;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;
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
    @Autowired
    private ResourceLoader resourceLoader;

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


        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        User user = userService.getUserById(userId);
        Grant grant = _processGrant(grantToSave, tenantOrg, user);
        //grant = _processGrantKpis(grantToSave, tenantOrg, user);

        //grantToSave = grantService.saveGrant(grantToSave);

        grant.setActionAuthorities(workflowPermissionService
                .getGrantActionPermissions(grant.getGrantorOrganization().getId(),
                        user.getUserRoles(), grant.getGrantStatus().getId()));

        grant.setFlowAuthorities(workflowPermissionService
                .getGrantFlowPermissions(grant.getGrantorOrganization().getId(),
                        user.getUserRoles(), grant.getGrantStatus().getId()));

        for (Submission submission : grant.getSubmissions()) {
            submission.setActionAuthorities(workflowPermissionService
                    .getSubmissionActionPermission(grant.getGrantorOrganization().getId(),
                            user.getUserRoles()));

            AppConfig submissionWindow = appConfigService
                    .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
                            AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS);
            Date submissionWindowStart = new DateTime(submission.getSubmitBy())
                    .minusDays(Integer.valueOf(submissionWindow.getConfigValue()) + 1).toDate();

            List<WorkFlowPermission> flowPermissions = workflowPermissionService
                    .getSubmissionFlowPermissions(grant.getGrantorOrganization().getId(),
                            user.getUserRoles(), submission.getSubmissionStatus().getId());

            if (!flowPermissions.isEmpty() && DateTime.now().toDate()
                    .after(submissionWindowStart)) {
                submission.setFlowAuthorities(flowPermissions);
            }
            if (DateTime.now().toDate()
                    .after(submissionWindowStart)) {
                submission.setOpenForReporting(true);
            } else {
                submission.setOpenForReporting(false);
            }
        }

        GrantVO grantVO = new GrantVO();
        grantVO = grantVO.build(grant, workflowPermissionService, user, appConfigService
                .getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                        AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
        grant.setGrantDetails(grantVO.getGrantDetails());

        //submissionService.saveSubmissions(grantToSave.getSubmissions());

        grant.getSubmissions().sort((a, b) -> a.getSubmitBy().compareTo(b.getSubmitBy()));
        return grant;
    }

    private Grant _processGrant(Grant grantToSave, Organization tenant, User user) {
        Grant grant = null;
        if (grantToSave.getId() < 0) {
            grant = new Grant();
            grant.setGrantStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("GRANT", tenant.getId()));
            grant.setSubstatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("SUBMISSION", tenant.getId()));
            grant.setOrganization((Grantee) grantToSave.getOrganization());
            List<GrantStringAttribute> stringAttributes = new ArrayList<>();
            List<GrantDocumentAttributes> docAttributes = new ArrayList<>();
            grant.setStringAttributes(stringAttributes);
            grant.setDocumentAttributes(docAttributes);
        } else {
            grant = grantService.getById(grantToSave.getId());
        }
        grant.setDescription(grantToSave.getDescription());
        grant.setEnDate(grantToSave.getEnDate());
        grant.setEndDate(grantToSave.getEndDate());
        grant.setGrantorOrganization((Granter) tenant);
        grant.setGrantStatus(grantToSave.getGrantStatus());
        grant.setName(grantToSave.getName());
        //grant.setOrganization((Grantee) user.getOrganization());
        grant.setStartDate(grantToSave.getStartDate());
        grant.setStatusName(grantToSave.getStatusName());
        grant.setStDate(grantToSave.getStDate());
        grant.setSubstatus(grantToSave.getSubstatus());
        grant.setUpdatedAt(DateTime.now().toDate());
        grant.setUpdatedBy(user.getEmailId());
        grant.setSubstatus(grantToSave.getSubstatus());
        grant = grantService.saveGrant(grant);

        _processDocumentAttributes(grant, grantToSave, tenant);
        grant.setKpis(_processGrantKpis(grant, grantToSave, tenant, user));
        _processStringAttributes(grant, grantToSave, tenant);
        grant.setSubmissions(_processGrantSubmissions(grant, grantToSave, tenant, user));


        return grantService.saveGrant(grant);
    }

    private void _processDocumentAttributes(Grant grant, Grant grantToSave, Organization tenant) {
        List<GrantDocumentAttributes> documentAttributes = new ArrayList<>();
        GranterGrantSection granterGrantSection = null;
        for (SectionVO sectionVO : grantToSave.getGrantDetails().getSections()) {
            if (sectionVO.getId() < 0) {
                granterGrantSection = new GranterGrantSection();
            } else {
                granterGrantSection = grantService.getGrantSectionBySectionId(sectionVO.getId());
            }
            granterGrantSection.setSectionName(sectionVO.getName());
            granterGrantSection.setGranter((Granter) tenant);
            granterGrantSection.setDeletable(true);

            granterGrantSection = grantService.saveSection(granterGrantSection);

            GranterGrantSectionAttribute sectionAttribute = null;
            for (SectionAttributesVO sectionAttributesVO : sectionVO.getAttributes()) {
                if (sectionAttributesVO.getId() < 0) {
                    sectionAttribute = new GranterGrantSectionAttribute();
                } else {
                    sectionAttribute = grantService.getSectionAttributeByAttributeIdAndType(sectionAttributesVO.getId(), sectionAttributesVO.getFieldType());
                }
                sectionAttribute.setDeletable(true);
                sectionAttribute.setFieldName(sectionAttributesVO.getFieldName());
                sectionAttribute.setFieldType(sectionAttributesVO.getFieldType());
                sectionAttribute.setGranter((Granter) tenant);
                sectionAttribute.setRequired(true);
                sectionAttribute.setSection(granterGrantSection);
                sectionAttribute = grantService.saveSectionAttribute(sectionAttribute);


                GrantDocumentAttributes grantDocAttribute = grantService.findGrantDocumentBySectionAttribueAndGrant(granterGrantSection, sectionAttribute, grant);
                if (grantDocAttribute == null) {
                    grantDocAttribute = new GrantDocumentAttributes();
                    grantDocAttribute.setSectionAttribute(sectionAttribute);
                    grantDocAttribute.setSection(granterGrantSection);
                    grantDocAttribute.setGrant(grant);
                }
                grantService.saveGrantDocumentAttribute(grantDocAttribute);
                grant.getDocumentAttributes().add(grantDocAttribute);
            }
            grant = grantService.saveGrant(grant);
        }
    }

    private void _processStringAttributes(Grant grant, Grant grantToSave, Organization tenant) {
        List<GrantStringAttribute> stringAttributes = new ArrayList<>();
        GranterGrantSection granterGrantSection = null;
        for (SectionVO sectionVO : grantToSave.getGrantDetails().getSections()) {
            if (sectionVO.getId() < 0) {
                granterGrantSection = new GranterGrantSection();
            } else {
                granterGrantSection = grantService.getGrantSectionBySectionId(sectionVO.getId());
            }
            granterGrantSection.setSectionName(sectionVO.getName());
            granterGrantSection.setGranter((Granter) tenant);
            granterGrantSection.setDeletable(true);

            granterGrantSection = grantService.saveSection(granterGrantSection);

            GranterGrantSectionAttribute sectionAttribute = null;
            for (SectionAttributesVO sectionAttributesVO : sectionVO.getAttributes()) {
                if (sectionAttributesVO.getId() < 0) {
                    sectionAttribute = new GranterGrantSectionAttribute();
                } else {
                    sectionAttribute = grantService.getSectionAttributeByAttributeIdAndType(sectionAttributesVO.getId(), sectionAttributesVO.getFieldType());
                }
                sectionAttribute.setDeletable(true);
                sectionAttribute.setFieldName(sectionAttributesVO.getFieldName());
                sectionAttribute.setFieldType(sectionAttributesVO.getFieldType());
                sectionAttribute.setGranter((Granter) tenant);
                sectionAttribute.setRequired(true);
                sectionAttribute.setSection(granterGrantSection);
                sectionAttribute = grantService.saveSectionAttribute(sectionAttribute);


                GrantStringAttribute grantStringAttribute = grantService.findGrantStringBySectionAttribueAndGrant(granterGrantSection, sectionAttribute, grant);
                if (grantStringAttribute == null) {
                    grantStringAttribute = new GrantStringAttribute();
                    grantStringAttribute.setSectionAttribute(sectionAttribute);
                    grantStringAttribute.setSection(granterGrantSection);
                    grantStringAttribute.setGrant(grant);
                }
                grantStringAttribute.setValue(sectionAttributesVO.getFieldValue());
                grantService.saveGrantStringAttribute(grantStringAttribute);
                grant.getStringAttributes().add(grantStringAttribute);
            }
            grant = grantService.saveGrant(grant);
        }

    }

    private List<GrantKpi> _processGrantKpis(Grant grant, Grant grantToSave, Organization tenant, User user) {
        GrantKpi grantKpi = null;
        List<GrantKpi> kpisList = new ArrayList<>();
        for (GrantKpi kpi : grantToSave.getKpis()) {

            if (kpi.getId() < 0) {
                grantKpi = new GrantKpi();
            } else {
                grantKpi = grantService.getGrantKpiById(kpi.getId());
            }
            grantKpi.setTitle(kpi.getTitle());
            grantKpi.setScheduled(kpi.isScheduled());
            grantKpi.setPeriodicity(kpi.getPeriodicity());
            grantKpi.setKpiType(kpi.getKpiType());
            grantKpi.setDescription(kpi.getDescription());
            grantKpi.setFrequency(kpi.getFrequency());
            grantKpi.setUpdatedAt(DateTime.now().toDate());
            grantKpi.setUpdatedBy(user.getEmailId());
            grantKpi.setKpiReportingType(kpi.getKpiReportingType());
            grantKpi.setGrant(grant);

            grantKpi = grantService.saveGrantKpi(grantKpi);

            grantKpi.setTemplates(_processKpiTemplates(grantKpi, kpi, tenant));

            kpisList.add(grantKpi);
        }
        return kpisList;
    }


    private List<Template> _processKpiTemplates(GrantKpi kpi, GrantKpi kpitoSave, Organization tenant) {
        List<Template> kpiTemplates = new ArrayList<>();
        try {

            Template kpiTemplate = null;
            for (Template template : kpitoSave.getTemplates()) {
                if (template.getId() < 0) {
                    kpiTemplate = new Template();
                } else {
                    kpiTemplate = grantService.getKpiTemplateById(template.getId());
                }
                kpiTemplate.setDescription(template.getDescription());
                kpiTemplate.setFileType(template.getFileType());
                kpiTemplate.setKpi(kpi);
                kpiTemplate.setLocation(template.getLocation());
                kpiTemplate.setName(template.getName());
                kpiTemplate.setType(template.getType());
                kpiTemplate.setVersion(template.getVersion());
                kpiTemplate = grantService.saveKpiTemplate(kpiTemplate);
                kpiTemplates.add(kpiTemplate);

                if (template.getData() != null) {
                    String uploadFolder = uploadLocation + tenant.getCode() + "/grants/" + kpi.getGrant().getId() + "/kpi-templates";


                    Files.createDirectories(Paths.get(uploadFolder));

                    FileOutputStream fileOutputStream = new FileOutputStream(uploadFolder + "/" + template.getName());
                    byte[] dataBytes = Base64.getDecoder().decode(template.getData().substring(template.getData().indexOf(",") + 1));
                    fileOutputStream.write(dataBytes);
                    fileOutputStream.close();


                }
            }
        } catch (IOException e) {
            logger.error("Could not process the uploaded File. Please try again.");
        }
        return kpiTemplates;
    }

    private List<Submission> _processGrantSubmissions(Grant grant, Grant grantToSave, Organization tenant, User user) {
        Submission grantSubmission = null;
        List<Submission> grantSubmissions = new ArrayList<>();

        for (Submission submission : grantToSave.getSubmissions()) {
            if (submission.getId() < 0) {
                grantSubmission = new Submission();
                grantSubmission.setSubmissionStatus(workflowStatusService.findInitialStatusByObjectAndGranterOrgId("SUBMISSION", tenant.getId()));
            } else {
                grantSubmission = submissionService.getById(submission.getId());
            }

            grantSubmission.setGrant(grant);

            grantSubmission.setSubmissionStatus(workflowStatusService.findById(submission.getSubmissionStatus().getId()));
            if (submission.getFlowAuthorities() != null) {
                grant.setSubstatus(workflowStatusService.findById(submission.getSubmissionStatus().getId()));
                grant = grantService.saveGrant(grant);
            }
            grantSubmission.setSubmitBy(DateTime.parse(submission.getSubmitDateStr()).toDate());
            grantSubmission.setSubmitDateStr(submission.getSubmitDateStr());
            grantSubmission.setSubmittedOn(submission.getSubmittedOn());
            grantSubmission.setTitle(submission.getTitle());
            grantSubmission = submissionService.saveSubmission(grantSubmission);

            grantSubmission.setDocumentKpiSubmissions(_processDocumentKpis(grantSubmission, submission, tenant, user));
            grantSubmission.setQualitativeKpiSubmissions(_processQualitativeKpis(grantSubmission, submission, tenant, user));
            grantSubmission.setQuantitiaveKpisubmissions(_processQuantitativeKpis(grantSubmission, submission, tenant, user));
            //grantSubmission.setSubmissionNotes(_processSubmissionNote(grantSubmission, submission, tenant, user));

            grantSubmission = submissionService.saveSubmission(grantSubmission);
            grantSubmissions.add(grantSubmission);
        }
        return grantSubmissions;
    }

    private List<GrantQuantitativeKpiData> _processQuantitativeKpis(Submission submission, Submission submission2Save, Organization tenant, User user) {
        GrantQuantitativeKpiData quantKpiData = null;
        List<GrantQuantitativeKpiData> quantKpiDataList = new ArrayList<>();
        for (GrantQuantitativeKpiData docKpi : submission2Save.getQuantitiaveKpisubmissions()) {
            for (GrantKpi kpi : submission.getGrant().getKpis()) {
                if (kpi.getKpiType() == KpiType.QUANTITATIVE && (kpi.getId() == docKpi.getGrantKpi().getId() || kpi.getTitle().equalsIgnoreCase(docKpi.getGrantKpi().getTitle()))) {
                    if (docKpi.getId() < 0) {
                        quantKpiData = new GrantQuantitativeKpiData();
                    } else {
                        quantKpiData = grantService.getGrantQuantitativeKpiDataById(docKpi.getId());
                    }
                    quantKpiData.setActuals(docKpi.getActuals());
                    quantKpiData.setGoal(docKpi.getGoal());
                    // TODO documentKpiData.setGrantKpi();

                    quantKpiData.setGrantKpi(kpi);
                    quantKpiData.setSubmission(submission);
                    quantKpiData.setToReport(docKpi.getToReport());


                    quantKpiData = grantService.saveGrantQunatitativeKpiData(quantKpiData);

                    quantKpiData.setNotesHistory(_processQuantNotesHistory(quantKpiData, docKpi, tenant, user));
                    quantKpiData.setSubmissionDocs(_processQuantSubmissionDocs(quantKpiData, docKpi, tenant, user));

                    quantKpiData = grantService.saveGrantQunatitativeKpiData(quantKpiData);
                    quantKpiDataList.add(quantKpiData);
                }
            }
        }
        return quantKpiDataList;
    }

    private List<GrantQualitativeKpiData> _processQualitativeKpis(Submission submission, Submission submission2Save, Organization tenant, User user) {
        GrantQualitativeKpiData qualKpiData = null;
        List<GrantQualitativeKpiData> qualKpiDataList = new ArrayList<>();
        for (GrantQualitativeKpiData docKpi : submission2Save.getQualitativeKpiSubmissions()) {
            for (GrantKpi kpi : submission.getGrant().getKpis()) {
                if (kpi.getKpiType() == KpiType.QUALITATIVE && (kpi.getId() == docKpi.getGrantKpi().getId() || kpi.getTitle().equalsIgnoreCase(docKpi.getGrantKpi().getTitle()))) {
                    if (docKpi.getId() < 0) {
                        qualKpiData = new GrantQualitativeKpiData();
                    } else {
                        qualKpiData = grantService.getGrantQualitativeKpiDataById(docKpi.getId());
                    }
                    qualKpiData.setActuals(docKpi.getActuals());
                    qualKpiData.setGoal(docKpi.getGoal());
                    // TODO documentKpiData.setGrantKpi();
                    qualKpiData.setGrantKpi(kpi);
                    qualKpiData.setSubmission(submission);
                    qualKpiData.setToReport(docKpi.getToReport());


                    qualKpiData = grantService.saveGrantQualitativeKpiData(qualKpiData);

                    qualKpiData.setNotesHistory(_processQualNotesHistory(qualKpiData, docKpi, tenant, user));
                    qualKpiData.setSubmissionDocs(_processQualSubmissionDocs(qualKpiData, docKpi, tenant, user));

                    qualKpiData = grantService.saveGrantQualitativeKpiData(qualKpiData);
                    qualKpiDataList.add(qualKpiData);
                }
            }
        }
        return qualKpiDataList;
    }

    private List<GrantDocumentKpiData> _processDocumentKpis(Submission submission, Submission submission2Save, Organization tenant, User user) {
        GrantDocumentKpiData documentKpiData = null;
        List<GrantDocumentKpiData> documentKpiDataList = new ArrayList<>();
        for (GrantDocumentKpiData docKpi : submission2Save.getDocumentKpiSubmissions()) {
            for (GrantKpi kpi : submission.getGrant().getKpis()) {
                if (kpi.getKpiType() == KpiType.DOCUMENT && (kpi.getId() == docKpi.getGrantKpi().getId() || kpi.getTitle().equalsIgnoreCase(docKpi.getGrantKpi().getTitle()))) {
                    if (docKpi.getId() < 0) {
                        documentKpiData = new GrantDocumentKpiData();
                    } else {
                        documentKpiData = grantService.getGrantDocumentKpiDataById(docKpi.getId());
                    }
                    documentKpiData.setActuals(docKpi.getActuals());
                    documentKpiData.setGoal(docKpi.getGoal());
                    // TODO documentKpiData.setGrantKpi();

                    documentKpiData.setSubmission(submission);
                    documentKpiData.setGrantKpi(kpi);
                    documentKpiData.setToReport(docKpi.getToReport());
                    documentKpiData.setType(docKpi.getType());

                    documentKpiData = grantService.saveGrantDocumentKpiData(documentKpiData);

                    documentKpiData.setNotesHistory(_processDocNotesHistory(documentKpiData, docKpi, tenant, user));
                    documentKpiData.setSubmissionDocs(_processDocSubmissionDocs(documentKpiData, docKpi, tenant, user));

                    documentKpiData = grantService.saveGrantDocumentKpiData(documentKpiData);
                    documentKpiDataList.add(documentKpiData);
                }
            }
        }
        return documentKpiDataList;
    }

    public List<QuantKpiDataDocument> _processQuantSubmissionDocs(GrantQuantitativeKpiData documentKpiData, GrantQuantitativeKpiData docKpi2Save, Organization tenant, User user) {
        QuantKpiDataDocument kpiDoc = null;
        List<QuantKpiDataDocument> quantKpiDataDocuments = new ArrayList<>();
        if (docKpi2Save.getSubmissionDocs() != null) {
            for (QuantKpiDataDocument doc : docKpi2Save.getSubmissionDocs()) {
                if (doc.getId() < 0) {
                    kpiDoc = new QuantKpiDataDocument();
                } else {
                    kpiDoc = grantService.getQuantkpiDocById(doc.getId());
                }

                kpiDoc.setFileName(doc.getFileName());
                kpiDoc.setFileType(doc.getFileType());
                kpiDoc.setVersion(doc.getVersion());

                if (doc.getData() != null) {
                    String uploadFolder = uploadLocation + tenant.getCode() + "/grants/" + documentKpiData.getGrantKpi().getGrant().getId() + "/kpi-documents";
                    try {

                        Files.createDirectories(Paths.get(uploadFolder));

                        FileOutputStream fileOutputStream = new FileOutputStream(uploadFolder + "/" + doc.getFileName());
                        byte[] dataBytes = Base64.getDecoder().decode(doc.getData().substring(doc.getData().indexOf(",") + 1));
                        fileOutputStream.write(dataBytes);
                        fileOutputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    kpiDoc.setQuantKpiData(documentKpiData);
                    kpiDoc = grantService.saveQuantKpiDataDoc(kpiDoc);
                    quantKpiDataDocuments.add(kpiDoc);
                }
            }
        }
        return quantKpiDataDocuments;
    }

    public List<QuantitativeKpiNotes> _processQuantNotesHistory(GrantQuantitativeKpiData documentKpiData, GrantQuantitativeKpiData docKpi2Save, Organization tenant, User user) {
        QuantitativeKpiNotes kpiNote = null;
        List<QuantitativeKpiNotes> quantKpiNote = new ArrayList<>();
        if (docKpi2Save.getNotesHistory() != null) {
            for (QuantitativeKpiNotes docNote : docKpi2Save.getNotesHistory()) {
                if (docNote.getId() < 0) {
                    kpiNote = new QuantitativeKpiNotes();
                } else {
                    kpiNote = grantService.getQuantKpiNoteById(docNote.getId());
                }

                kpiNote.setPostedOn(DateTime.now().toDate());
                kpiNote.setPostedBy(user);
                kpiNote.setMessage(docNote.getMessage());
                kpiNote.setKpiData(documentKpiData);
                kpiNote = grantService.saveQuantKpiNote(kpiNote);
                quantKpiNote.add(kpiNote);
            }
        }
        return quantKpiNote;
    }

    public List<QualKpiDataDocument> _processQualSubmissionDocs(GrantQualitativeKpiData documentKpiData, GrantQualitativeKpiData docKpi2Save, Organization tenant, User user) {
        QualKpiDataDocument kpiDoc = null;
        List<QualKpiDataDocument> qualKpiDataDocuments = new ArrayList<>();
        if (docKpi2Save.getSubmissionDocs() != null) {
            for (QualKpiDataDocument doc : docKpi2Save.getSubmissionDocs()) {
                if (doc.getId() < 0) {
                    kpiDoc = new QualKpiDataDocument();
                } else {
                    kpiDoc = grantService.getQualkpiDocById(doc.getId());
                }

                kpiDoc.setFileName(doc.getFileName());
                kpiDoc.setFileType(doc.getFileType());
                kpiDoc.setVersion(doc.getVersion());
                kpiDoc.setQualKpiData(documentKpiData);
                kpiDoc = grantService.saveQualKpiDataDoc(kpiDoc);
                qualKpiDataDocuments.add(kpiDoc);
            }
        }
        return qualKpiDataDocuments;
    }

    public List<QualitativeKpiNotes> _processQualNotesHistory(GrantQualitativeKpiData documentKpiData, GrantQualitativeKpiData docKpi2Save, Organization tenant, User user) {
        QualitativeKpiNotes kpiNote = null;
        List<QualitativeKpiNotes> qualKpiNote = new ArrayList<>();
        if (docKpi2Save.getNotesHistory() != null) {
            for (QualitativeKpiNotes docNote : docKpi2Save.getNotesHistory()) {
                if (docNote.getId() < 0) {
                    kpiNote = new QualitativeKpiNotes();
                } else {
                    kpiNote = grantService.getQualKpiNoteById(docNote.getId());
                }

                kpiNote.setPostedOn(DateTime.now().toDate());
                kpiNote.setPostedBy(user);
                kpiNote.setMessage(docNote.getMessage());
                kpiNote.setKpiData(documentKpiData);
                kpiNote = grantService.saveQualKpiNote(kpiNote);
                qualKpiNote.add(kpiNote);
            }
        }
        return qualKpiNote;
    }

    public List<DocKpiDataDocument> _processDocSubmissionDocs(GrantDocumentKpiData documentKpiData, GrantDocumentKpiData docKpi2Save, Organization tenant, User user) {
        DocKpiDataDocument kpiDoc = null;
        List<DocKpiDataDocument> docKpiDataDocuments = new ArrayList<>();
        if (docKpi2Save.getSubmissionDocs() != null) {
            for (DocKpiDataDocument doc : docKpi2Save.getSubmissionDocs()) {
                if (doc.getId() < 0) {
                    kpiDoc = new DocKpiDataDocument();
                } else {
                    kpiDoc = grantService.getDockpiDocById(doc.getId());
                }

                kpiDoc.setDocKpiData(documentKpiData);
                kpiDoc.setFileName(doc.getFileName());
                kpiDoc.setFileType(doc.getFileType());
                kpiDoc.setVersion(doc.getVersion());
                kpiDoc = grantService.saveDocKpiDataDoc(kpiDoc);
                docKpiDataDocuments.add(kpiDoc);
            }
        }
        return docKpiDataDocuments;
    }

    public List<DocumentKpiNotes> _processDocNotesHistory(GrantDocumentKpiData documentKpiData, GrantDocumentKpiData docKpi2Save, Organization tenant, User user) {
        DocumentKpiNotes kpiNote = null;
        List<DocumentKpiNotes> docKpiNotes = null;
        if (docKpi2Save.getNotesHistory() != null) {
            for (DocumentKpiNotes docNote : docKpi2Save.getNotesHistory()) {
                if (docNote.getId() < 0) {
                    kpiNote = new DocumentKpiNotes();
                } else {
                    kpiNote = grantService.getDocKpiNoteById(docNote.getId());
                }

                kpiNote.setPostedOn(DateTime.now().toDate());
                kpiNote.setPostedBy(user);
                kpiNote.setMessage(docNote.getMessage());
                kpiNote.setKpiData(documentKpiData);
                kpiNote = grantService.saveDocumentKpiNote(kpiNote);
                if (docKpiNotes == null) {
                    docKpiNotes = new ArrayList<>();
                }
                docKpiNotes.add(kpiNote);
            }
        }
        return docKpiNotes;
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


    @PostMapping("/{grantId}/pdf")
    public void getPDFExport(@PathVariable("grantId") Long grantId, @RequestBody String htmlContent, HttpServletRequest request, HttpServletResponse response) {

        Grant grant = grantService.getById(grantId);

        File tempFile = null;
        FileOutputStream fos = null;
        try {
            tempFile = File.createTempFile("myfile", ".tmp");
            fos = new FileOutputStream(tempFile);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder
                    .useFastMode()
                    .withHtmlContent(htmlContent, request.getServletPath())
                    .toStream(fos)
                    .run();

            Resource file = resourceLoader.getResource(tempFile.getPath());
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response
                    .setHeader("Content-Disposition", "attachment; filename=" + grant.getName()+".pdf");

            StreamUtils.copy(file.getInputStream(), response.getOutputStream());


        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }finally {
            tempFile.delete();
            try {
                fos.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

    }


}
