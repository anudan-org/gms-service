package org.codealpha.gmsservice.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.controllers.ReportController;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.ColumnData;
import org.codealpha.gmsservice.models.SecureReportEntity;
import org.codealpha.gmsservice.models.TableData;
import org.codealpha.gmsservice.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final String SECRET = "78yughvdbfv87ny4w87rbshfiv8aw4tr87awvyeruvbhdkjfhbity834t";
    @Autowired private ReportRepository reportRepository;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private WorkflowStatusRepository workflowStatusRepository;
    @Autowired private ReportAssignmentRepository reportAssignmentRepository;
    @Autowired private GrantAssignmentRepository grantAssignmentRepository;
    @Autowired private GranterReportTemplateRepository granterReportTemplateRepository;
    @Autowired private GranterReportSectionRepository granterReportSectionRepository;
    @Autowired private ReportSpecificSectionRepository reportSpecificSectionRepository;
    @Autowired private ReportSpecificSectionAttributeRepository reportSpecificSectionAttributeRepository;
    @Autowired private ReportStringAttributeRepository reportStringAttributeRepository;
    @Autowired private WorkflowRepository workflowRepository;
    @Autowired private WorkflowStatusTransitionRepository workflowStatusTransitionRepository;
    @Autowired private TemplateLibraryRepository templateLibraryRepository;
    @Autowired private GranterReportSectionAttributeRepository granterReportSectionAttributeRepository;
    @Autowired private ReportStringAttributeAttachmentsRepository reportStringAttributeAttachmentsRepository;
    @Autowired private ReportHistoryRepository reportHistoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired
    private ServletContext servletContext;
    public Report saveReport(Report report){
        return reportRepository.save(report);
    }

    public List<Report> getAllReports(){
        return (List<Report>) reportRepository.findAll();
    }

    public List<ReportAssignment> saveAssignments(Report report, String tenantCode,Long userId) {
        ReportAssignment assignment = null;

        Organization granterOrg = organizationRepository.findByCode(tenantCode);
        List<WorkflowStatus> statuses = workflowStatusRepository.getAllTenantStatuses("REPORT", report.getGrant().getGrantorOrganization().getId());

        Optional<WorkflowStatus> grantActiveStatus = workflowStatusRepository.getAllTenantStatuses("GRANT", report.getGrant().getGrantorOrganization().getId()).stream().filter(s -> s.getInternalStatus().equalsIgnoreCase("ACTIVE")).findFirst();
        GrantAssignments anchorAssignment = null;
        if(grantActiveStatus.isPresent()) {
            anchorAssignment = grantAssignmentRepository.findByGrantIdAndStateId(report.getGrant().getId(), grantActiveStatus.get().getId()).get(0);
        }
        List<ReportAssignment> assignments = new ArrayList<>();
        for (WorkflowStatus status : statuses) {
            if (!status.getTerminal()) {
                assignment = new ReportAssignment();
                if (status.isInitial()) {
                    assignment.setAnchor(true);
                    assignment.setAssignment(anchorAssignment!=null?anchorAssignment.getAssignments():null);
                } else {
                    assignment.setAnchor(false);
                }
                assignment.setReportId(report.getId());
                assignment.setStateId(status.getId());
                assignment = _saveAssignmentForReport(assignment);
                assignments.add(assignment);
            }
        }

        return assignments;
    }

    public List<ReportAssignment> saveNewAssignmentForGrantee(Report report, String tenantCode,Long granteeUserId) {
        ReportAssignment assignment = null;

        Organization granterOrg = organizationRepository.findByCode(tenantCode);
        List<WorkflowStatus> statuses = workflowStatusRepository.getAllTenantStatuses("REPORT", report.getGrant().getGrantorOrganization().getId());

        List<ReportAssignment> assignments = new ArrayList<>();
        for (WorkflowStatus status : statuses) {
            if (!status.getTerminal()) {
                assignment = new ReportAssignment();
                if (status.getInternalStatus().equalsIgnoreCase("ACTIVE")) {
                    assignment.setAssignment(granteeUserId);
                    assignment.setAnchor(false);
                    assignment.setReportId(report.getId());
                    assignment.setStateId(status.getId());
                    assignment = _saveAssignmentForReport(assignment);
                    assignments.add(assignment);
                }

            }
        }
        return assignments;
    }

    private ReportAssignment _saveAssignmentForReport(ReportAssignment assignment) {
        return reportAssignmentRepository.save(assignment);
    }

    public List<ReportAssignment> getAssignmentsForReport(Report report){
        return reportAssignmentRepository.findByReportId(report.getId());
    }

    public List<Report> getAllAssignedReportsForGranteeUser(Long userId, Long granteeOrgId,String status){

        return reportRepository.findAllAssignedReportsForGranteeUser(userId,granteeOrgId, status);
    }

    public List<Report> getAllAssignedReportsForGranterUser(Long userId, Long granterOrgId){

        return reportRepository.findAllAssignedReportsForGranterUser(userId,granterOrgId);
    }

   public List<Report> getUpcomingReportsForGranterUserByDateRange(Long userId, Long granterOrgId,Date start, Date end){

        return reportRepository.findUpcomingReportsForGranterUserByDateRange(userId,granterOrgId,start,end);
    }

    public List<Report> getFutureReportForGranterUserByDateRangeAndGrant(Long userId, Long granterOrgId,Date end,Long grantId){

        return reportRepository.findFutureReportsToSubmitForGranterUserByDateRangeAndGrant(userId,granterOrgId,end,grantId);
    }

    public List<Report> getReadyToSubmitReportsForGranterUserByDateRange(Long userId, Long granterOrgId,Date start, Date end){

        return reportRepository.findReadyToSubmitReportsForGranterUserByDateRange(userId,granterOrgId,start,end);
    }

    public List<Report> getSubmittedReportsForGranterUserByDateRange(Long userId, Long granterOrgId){

        return reportRepository.findSubmittedReportsForGranterUserByDateRange(userId,granterOrgId);
    }

    public List<Report> getApprovedReportsForGranterUserByDateRange(Long userId, Long granterOrgId){

        return reportRepository.findApprovedReportsForGranterUserByDateRange(userId,granterOrgId);
    }

    public GranterReportTemplate getDefaultTemplate(Long granterId){
        return granterReportTemplateRepository.findByGranterIdAndDefaultTemplate(granterId,true);
    }

    public List<GranterReportSection> findSectionsForReportTemplate(GranterReportTemplate template){
        return granterReportSectionRepository.findByReportTemplate(template);
    }

    public ReportSpecificSection saveReportSpecificSection(ReportSpecificSection reportSpecificSection){
        return reportSpecificSectionRepository.save(reportSpecificSection);
    }

    public ReportSpecificSectionAttribute saveReportSpecificSectionAttribute(ReportSpecificSectionAttribute sectionAttribute) {
        return reportSpecificSectionAttributeRepository.save(sectionAttribute);
    }

    public ReportStringAttribute saveReportStringAttribute(ReportStringAttribute stringAttribute) {
        return reportStringAttributeRepository.save(stringAttribute);
    }

    public List<ReportSpecificSection> getReportSections(Report report) {
        return reportSpecificSectionRepository.findByReportId(report.getId());
    }

    public String buildHashCode(Report report) {
        SecureReportEntity secureEntity = new SecureReportEntity();
        secureEntity.setReportId(report.getId());
        secureEntity.setTemplateId(report.getTemplate().getId());
        secureEntity.setSectionAndAtrribIds(new HashMap<>());
        if(report.getGrant()!=null) {
            secureEntity.setGranterId(report.getGrant().getGrantorOrganization().getId());
        }
        Map<Long, List<Long>> map = new HashMap<>();
        report.getReportDetails().getSections().forEach(sec -> {
            List<Long> attribIds = new ArrayList<>();
            if(sec.getAttributes()!=null) {
                sec.getAttributes().forEach(a -> {
                    attribIds.add(a.getId());
                });
            }

            map.put(sec.getId(), attribIds);
        });
        secureEntity.setSectionAndAtrribIds(map);
        List<Long> templateIds = new ArrayList<>();
        if(report.getGrant()!=null) {
            granterReportTemplateRepository.findByGranterId(report.getGrant().getGrantorOrganization().getId()).forEach(t -> {
                templateIds.add(t.getId());
            });
        }
        secureEntity.setGrantTemplateIds(templateIds);

        List<Long> grantWorkflowIds = new ArrayList<>();
        Map<Long,List<Long>> grantWorkflowStatusIds = new HashMap<>();
        Map<Long,Long[][]> grantWorkflowTransitionIds = new HashMap<>();
        if(report.getGrant()!=null) {
            workflowRepository.findByGranterAndObject(report.getGrant().getGrantorOrganization(), WorkflowObject.REPORT).forEach(w -> {
                grantWorkflowIds.add(w.getId());
                List<Long> wfStatusIds = new ArrayList<>();
                workflowStatusRepository.findByWorkflow(w).forEach(ws -> {
                    wfStatusIds.add(ws.getId());
                });
                grantWorkflowStatusIds.put(w.getId(), wfStatusIds);

                List<WorkflowStatusTransition> transitions = workflowStatusTransitionRepository.findByWorkflow(w);
                Long[][] stransitions = new Long[transitions.size()][2];
                final int[] counter = {0};
                workflowStatusTransitionRepository.findByWorkflow(w).forEach(st -> {
                    stransitions[counter[0]][0] = st.getFromState().getId();
                    stransitions[counter[0]][1] = st.getToState().getId();
                    counter[0]++;
                });
                grantWorkflowTransitionIds.put(w.getId(), stransitions);
            });
        }

        secureEntity.setGrantWorkflowIds(grantWorkflowIds);
        secureEntity.setWorkflowStatusIds(grantWorkflowStatusIds);
        secureEntity.setWorkflowStatusTransitionIds(grantWorkflowTransitionIds);
        if(report.getGrant()!=null) {
            secureEntity.setTenantCode(report.getGrant().getGrantorOrganization().getCode());
        }

        List<Long> tLibraryIds = new ArrayList<>();
        if(report.getGrant()!=null) {
            templateLibraryRepository.findByGranterId(report.getGrant().getGrantorOrganization().getId()).forEach(tl -> {
                tLibraryIds.add(tl.getId());
            });
        }
        secureEntity.setTemplateLibraryIds(tLibraryIds);

        try {
            String secureCode = Jwts.builder().setSubject(new ObjectMapper().writeValueAsString(secureEntity))
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
            return secureCode;
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return "";
    }

    public SecureReportEntity unBuildGrantHashCode(Report grant){
        String grantSecureCode = Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(grant.getSecurityCode()).getBody().getSubject();
        SecureReportEntity secureHash = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            secureHash = mapper.readValue(grantSecureCode, SecureReportEntity.class);
        } catch (JsonParseException e) {

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return secureHash;
        }
    }

    public List<ReportStringAttribute> getReportStringAttributesForReport(Report report) {
        return reportStringAttributeRepository.findByReport(report);
    }

    public Report getReportById(Long reportId){
        return reportRepository.findById(reportId).get();
    }

    public ReportSpecificSection getReportSpecificSectionById(Long reportSpecificSectionId){
        return reportSpecificSectionRepository.findById(reportSpecificSectionId).get();
    }

    public ReportSpecificSectionAttribute getReportSpecificSectionAttributeById(Long reportSpecificSectionAttributeId){
        return reportSpecificSectionAttributeRepository.findById(reportSpecificSectionAttributeId).get();
    }

    public ReportStringAttribute getReportStringAttributeBySectionAttributeAndSection(ReportSpecificSectionAttribute sectionAttribute,ReportSpecificSection section){
        return reportStringAttributeRepository.findBySectionAttributeAndSection(sectionAttribute,section);
    }

    public ReportStringAttribute getReportStringByStringAttributeId(Long stringAttributeId){
        return reportStringAttributeRepository.findById(stringAttributeId).get();
    }

    public int getNextAttributeOrder(Long granterId, Long sectionId) {
        return reportSpecificSectionAttributeRepository.getNextAttributeOrder(granterId, sectionId);
    }

    public GranterReportTemplate findByTemplateId(Long id) {
        return granterReportTemplateRepository.findById(id).get();
    }

    public void deleteReportTemplate(GranterReportTemplate currentReportTemplate) {
        granterReportTemplateRepository.delete(currentReportTemplate);
    }

    public GranterReportTemplate saveReportTemplate(GranterReportTemplate newTemplate) {
        return granterReportTemplateRepository.save(newTemplate);
    }

    public GranterReportSection saveReportTemplateSection(GranterReportSection newSection) {
        return granterReportSectionRepository.save(newSection);
    }

    public ReportSpecificSection saveSection(ReportSpecificSection currentSection) {
        return reportSpecificSectionRepository.save(currentSection);
    }

    public List<ReportSpecificSectionAttribute> getSpecificSectionAttributesBySection(ReportSpecificSection currentSection) {
        return reportSpecificSectionAttributeRepository.findBySection(currentSection);
    }

    public GranterReportSectionAttribute saveReportTemplateSectionAttribute(GranterReportSectionAttribute newAttribute) {
        return granterReportSectionAttributeRepository.save(newAttribute);
    }

    public ReportStringAttributeAttachments saveReportStringAttributeAttachment(ReportStringAttributeAttachments attachment) {
        return reportStringAttributeAttachmentsRepository.save(attachment);
    }

    public List<ReportStringAttributeAttachments> getStringAttributeAttachmentsByStringAttribute(ReportStringAttribute stringAttribute) {
        return reportStringAttributeAttachmentsRepository.findByReportStringAttribute(stringAttribute);
    }

    public Integer getNextSectionOrder(Long id, Long templateId) {
        return reportSpecificSectionRepository.getNextSectionOrder(id,templateId);
    }

    public List<ReportStringAttribute> getReportStringAttributesByAttribute(ReportSpecificSectionAttribute attrib) {
        return reportStringAttributeRepository.findBySectionAttribute(attrib);
    }

    public void deleteStringAttribute(ReportStringAttribute stringAttrib) {
        reportStringAttributeRepository.delete(stringAttrib);
    }


    public void deleteSectionAttributes(List<ReportSpecificSectionAttribute> specificSectionAttributesBySection) {
        reportSpecificSectionAttributeRepository.deleteAll(specificSectionAttributesBySection);
    }

    public void deleteSection(ReportSpecificSection section) {
        reportSpecificSectionRepository.delete(section);
    }

    public List<WorkFlowPermission> getFlowAuthority(Report report, Long userId) {
        List<WorkFlowPermission> permissions = new ArrayList<>();
        if((reportAssignmentRepository.findByReportId(report.getId()).stream().filter(ass -> ass.getStateId().longValue()==report.getStatus().getId().longValue() && (ass.getAssignment()==null?0:ass.getAssignment().longValue())==userId).findFirst().isPresent()) || (userRepository.findById(userId).get().getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE") && report.getStatus().getInternalStatus().equalsIgnoreCase("ACTIVE"))){

            List<WorkflowStatusTransition> allowedTransitions = workflowStatusTransitionRepository.findByWorkflow(workflowStatusRepository.getById(report.getStatus().getId()).getWorkflow()).stream().filter(st -> st.getFromState().getId().longValue()==report.getStatus().getId().longValue()).collect(Collectors.toList());
            if(allowedTransitions!=null && allowedTransitions.size()>0){
                allowedTransitions.forEach(tr ->{
                    WorkFlowPermission workFlowPermission = new WorkFlowPermission();
                    workFlowPermission.setAction(tr.getAction());
                    workFlowPermission.setFromName(tr.getFromState().getName());
                    workFlowPermission.setFromStateId(tr.getFromState().getId());
                    workFlowPermission.setId(tr.getId());
                    workFlowPermission.setNoteRequired(tr.getNoteRequired());
                    workFlowPermission.setToName(tr.getToState().getName());
                    workFlowPermission.setToStateId(tr.getToState().getId());
                    permissions.add(workFlowPermission);
                });
            }
        }
        return permissions;
    }

    public ReportAssignment getReportAssignmentById(Long id) {
        return reportAssignmentRepository.findById(id).get();
    }

    public ReportAssignment saveAssignmentForReport(ReportAssignment assignment) {
        return reportAssignmentRepository.save(assignment);
    }

    public String[] buildEmailNotificationContent(Report finalReport, User u, String userName, String action, String date, String subConfigValue, String msgConfigValue, String currentState, String currentOwner, String previousState, String previousOwner, String previousAction, String hasChanges, String hasChangesComment, String hasNotes, String hasNotesComment, String link, User owner, Integer noOfDays) {

        String code = Base64.getEncoder().encodeToString(String.valueOf(finalReport.getId()).getBytes());

        String host = "";
        String url = "";
        UriComponents uriComponents = null;
        try {
            uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
            if (u.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")) {
                host = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);

            } else {
                host = uriComponents.getHost();
            }
            UriComponentsBuilder uriBuilder =  UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme()).host(host).port(uriComponents.getPort());
            url = uriBuilder.toUriString();
            url = url+"/home/?action=login&org="+ URLEncoder.encode(finalReport.getGrant().getGrantorOrganization().getName(), StandardCharsets.UTF_8.toString())+"&r=" + code+"&email=&type=report";
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            url = link;
            try {
                url = url+"/home/?action=login&org="+ URLEncoder.encode(finalReport.getGrant().getGrantorOrganization().getName(), StandardCharsets.UTF_8.toString())+"&r=" + code+"&email=&type=report";
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }



        String message = msgConfigValue.replaceAll("%GRANT_NAME%", finalReport.getGrant().getName()).replaceAll("%REPORT_NAME%",finalReport.getName()).replaceAll("%REPORT_LINK%",url).replaceAll("%CURRENT_STATE%", currentState).replaceAll("%CURRENT_OWNER%", currentOwner).replaceAll("%PREVIOUS_STATE%", previousState).replaceAll("%PREVIOUS_OWNER%", previousOwner).replaceAll("%PREVIOUS_ACTION%", previousAction).replaceAll("%HAS_CHANGES%", hasChanges).replaceAll("%HAS_CHANGES_COMMENT%", hasChangesComment).replaceAll("%HAS_NOTES%",hasNotes).replaceAll("%HAS_NOTES_COMMENT%",hasNotesComment).replaceAll("%TENANT%",finalReport.getGrant().getGrantorOrganization().getName()).replaceAll("%DUE_DATE%",new SimpleDateFormat("dd-MMM-yyyy").format(finalReport.getDueDate())).replaceAll("%OWNER_NAME%",owner==null?"":owner.getFirstName()+" "+owner.getLastName()).replaceAll("%OWNER_EMAIL%",owner==null?"":owner.getEmailId()).replaceAll("%NO_DAYS%",noOfDays==null?"":String.valueOf(noOfDays));
        String subject = subConfigValue.replaceAll("%REPORT_NAME%", finalReport.getName());

        return new String[]{subject, message};
    }

    public List<ReportHistory> getReportHistory(Long reportId) {
        return reportHistoryRepository.findByReportId(reportId);
    }
    public List<ReportHistory> getReportHistoryForGrantee(Long reportId,Long granteeUserId) {
        return reportHistoryRepository.findReportHistoryForGranteeByReportId(reportId,granteeUserId);
    }

    public void deleteStringAttributeAttachments(List<ReportStringAttributeAttachments> attachments) {
        reportStringAttributeAttachmentsRepository.deleteAll(attachments);
    }

    public void deleteSectionAttribute(ReportSpecificSectionAttribute attribute) {
        reportSpecificSectionAttributeRepository.delete(attribute);
    }

    public Long getApprovedReportsActualSumForGrant(Long grantId, String attributeName){
        return reportRepository.getApprovedReportsActualSumForGrantAndAttribute(grantId,attributeName);
    }

    public List<GranterReportTemplate> findByGranterIdAndPublishedStatus(Long id, boolean publishedStatus) {
        return granterReportTemplateRepository.findByGranterIdAndPublished(id,publishedStatus);
    }

    public List<GranterReportTemplate> findByGranterIdAndPublishedStatusAndPrivateStatus(Long id, boolean publishedStatus, boolean _private) {
        return granterReportTemplateRepository.findByGranterIdAndPublishedAndPrivateToReport(id,publishedStatus,_private);
    }

    public String[] buildReportInvitationContent(Report report,User user, String sub,String msg,String url){
        sub = sub.replace("%GRANT_NAME%",report.getGrant().getName());
        sub = sub.replace("%REPORT_NAME%",report.getName());
        msg = msg.replace("%GRANT_NAME%",report.getGrant().getName()).replace("%TENANT_NAME%",report.getGrant().getGrantorOrganization().getName()).replace("%LINK%",url);
        msg = msg.replace("%REPORT_NAME%",report.getName());
        return new String[]{sub,msg};
    }

    public ReportStringAttributeAttachments getStringAttributeAttachmentsByAttachmentId(Long attachmentId) {
        return reportStringAttributeAttachmentsRepository.findById(attachmentId).get();
    }

    public ReportStringAttribute findReportStringAttributeById(Long attributeId) {

        return reportStringAttributeRepository.findById(attributeId).get();
    }

    public GranterReportTemplate _createNewReportTemplateFromExisiting(Report report) {
        GranterReportTemplate currentReportTemplate = findByTemplateId(report.getTemplate().getId());
        GranterReportTemplate newTemplate = null;
        if (!currentReportTemplate.isPublished()) {
            deleteReportTemplate(currentReportTemplate);
        }
        newTemplate = new GranterReportTemplate();
        newTemplate.setName("Custom Template");
        newTemplate.setGranterId(report.getGrant().getGrantorOrganization().getId());
        newTemplate.setPublished(false);
        newTemplate = saveReportTemplate(newTemplate);


        List<GranterReportSection> newSections = new ArrayList<>();
        for (ReportSpecificSection currentSection : getReportSections(report)) {
            GranterReportSection newSection = new GranterReportSection();
            newSection.setSectionOrder(currentSection.getSectionOrder());
            newSection.setSectionName(currentSection.getSectionName());
            newSection.setReportTemplate(newTemplate);
            newSection.setGranter((Granter) report.getGrant().getGrantorOrganization());
            newSection.setDeletable(currentSection.getDeletable());

            newSection = saveReportTemplateSection(newSection);
            newSections.add(newSection);

            currentSection.setReportTemplateId(newTemplate.getId());
            currentSection = saveSection(currentSection);

            for (ReportSpecificSectionAttribute currentAttribute : getSpecificSectionAttributesBySection(currentSection)) {
                GranterReportSectionAttribute newAttribute = new GranterReportSectionAttribute();
                newAttribute.setDeletable(currentAttribute.getDeletable());
                newAttribute.setFieldName(currentAttribute.getFieldName());
                newAttribute.setFieldType(currentAttribute.getFieldType());
                newAttribute.setGranter((Granter) currentAttribute.getGranter());
                newAttribute.setRequired(currentAttribute.getRequired());
                newAttribute.setAttributeOrder(currentAttribute.getAttributeOrder());
                newAttribute.setSection(newSection);
                if (currentAttribute.getFieldType().equalsIgnoreCase("table") || currentAttribute.getFieldType().equalsIgnoreCase("disbursement")) {
                    ReportStringAttribute stringAttribute = getReportStringAttributeBySectionAttributeAndSection(currentAttribute, currentSection);

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        List<TableData> tableData = mapper.readValue(stringAttribute.getValue(), new TypeReference<List<TableData>>() {
                        });
                        for (TableData data : tableData) {
                            for (ColumnData columnData : data.getColumns()) {
                                columnData.setValue("");
                            }
                        }
                        newAttribute.setExtras(mapper.writeValueAsString(tableData));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                newAttribute = saveReportTemplateSectionAttribute(newAttribute);

            }
        }

        newTemplate.setSections(newSections);
        newTemplate = saveReportTemplate(newTemplate);

        //grant = grantService.getById(grant.getId());
        report.setTemplate(newTemplate);
        saveReport(report);
        return newTemplate;
    }


    public List<Report> getDueReportsForPlatform(Date dueDate,List<Long> granterIds){
        return reportRepository.getDueReportsForPlatform(dueDate, granterIds);
    }

    public List<Report> getDueReportsForGranter(Date dueDate,Long granterId){
        return reportRepository.getDueReportsForGranter(dueDate, granterId);
    }

    public List<ReportAssignment> getActionDueReportsForPlatform(List<Long> granterIds){
        return reportAssignmentRepository.getActionDueReportsForPlatform(granterIds);
    }

    public List<ReportAssignment> getActionDueReportsForGranterOrg(Long granterId){
        return reportAssignmentRepository.getActionDueReportsForGranterOrg(granterId);
    }

    public Boolean _checkIfReportTemplateChanged(Report report, ReportSpecificSection newSection, ReportSpecificSectionAttribute newAttribute, ReportController reportController) {
        GranterReportTemplate currentReportTemplate = findByTemplateId(report.getTemplate().getId());
        for (GranterReportSection reportSection : currentReportTemplate.getSections()) {
            if (!reportSection.getSectionName().equalsIgnoreCase(newSection.getSectionName())) {
                return true;
            }
            if (newAttribute != null) {
                for (GranterReportSectionAttribute sectionAttribute : reportSection.getAttributes()) {
                    if (!sectionAttribute.getFieldName().equalsIgnoreCase(newAttribute.getFieldName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Report> findByGrantAndStatus(Grant grant, WorkflowStatus status,Long reportId){
        return reportRepository.findByGrantAndStatus(grant.getId(),status.getInternalStatus(),reportId);
    }

    public List<Report> getReportsByIds(String linkedApprovedReports) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Long> reportIds = mapper.readValue(linkedApprovedReports,new TypeReference<List<Long>>(){});
            return reportRepository.findReportsByIds(reportIds);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
