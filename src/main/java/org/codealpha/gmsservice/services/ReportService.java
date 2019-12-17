package org.codealpha.gmsservice.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.OAuth2Definition;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.SecureReportEntity;
import org.codealpha.gmsservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

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

        GrantAssignments anchorAssignment = grantAssignmentRepository.findByGrantIdAndAnchor(report.getGrant().getId(),true);
        List<ReportAssignment> assignments = new ArrayList<>();
        for (WorkflowStatus status : statuses) {
            if (!status.getTerminal()) {
                assignment = new ReportAssignment();
                if (status.isInitial()) {
                    assignment.setAnchor(true);
                    assignment.setAssignment(anchorAssignment.getAssignments());
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

    public List<Report> getAllAssignedReportsForUser(Long userId, Long granterOrgId){

        return reportRepository.findAllAssignedReportsForUser(userId,granterOrgId);
    }

    public GranterReportTemplate getDefaultTemplate(){
        return granterReportTemplateRepository.findByDefaultTemplate(true);
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
        if(reportAssignmentRepository.findByReportId(report.getId()).stream().filter(ass -> ass.getStateId()==report.getStatus().getId() && ass.getAssignment()==userId).findFirst().isPresent()){
            List<WorkflowStatusTransition> allowedTransitions = workflowStatusTransitionRepository.findByWorkflow(workflowStatusRepository.getById(report.getStatus().getId()).getWorkflow()).stream().filter(st -> st.getFromState().getId()==report.getStatus().getId()).collect(Collectors.toList());
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

    public String[] buildNotificationContent(Report finalReport, String userName, String action, String date, String subConfigValue, String msgConfigValue, String currentState, String currentOwner, String previousState, String previousOwner, String previousAction, String hasChanges, String hasChangesComment,String hasNotes,String hasNotesComment) {

        String message = msgConfigValue.replace("%GRANT_NAME%", finalReport.getName()).replace("%CURRENT_STATE%", currentState).replace("%CURRENT_OWNER%", currentOwner).replace("%PREVIOUS_STATE%", previousState).replace("%PREVIOUS_OWNER%", previousOwner).replace("%PREVIOUS_ACTION%", previousAction).replace("%HAS_CHANGES%", hasChanges).replace("%HAS_CHANGES_COMMENT%", hasChangesComment).replace("%HAS_NOTES%",hasNotes).replace("%HAS_NOTES_COMMENT%",hasNotesComment);
        String subject = subConfigValue.replace("%GRANT_NAME%", finalReport.getName());

        return new String[]{subject, message};
    }

    public List<ReportHistory> getReportHistory(Long reportId) {
        return reportHistoryRepository.findByReportId(reportId);
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

}
