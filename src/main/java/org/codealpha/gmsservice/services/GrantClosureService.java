package org.codealpha.gmsservice.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GrantClosureService {

    private static final Logger logger = LoggerFactory.getLogger(GrantClosureService.class);

    public static final String GRANT_NAME = "%GRANT_NAME%";
    public static final String TENANT_NAME = "%TENANT_NAME%";
    public static final String HOME_ACTION_LOGIN_ORG = "/home/?action=login&org=";
    public static final String R = "&r=";
    public static final String EMAIL_TYPE_CLOSURE = "&email=&type=closure";
    public static final String TD = "</td>";
    @Autowired
    private ClosureAssignmentRepository closureAssignmentRepository;
    @Autowired
    private GranterClosureTemplateRepository granterClosureTemplateRepository;
    @Autowired
    private ClosureSpecificSectionRepository closureSpecificSectionRepository;
    @Autowired
    private ClosureSpecificSectionAttributeRepository closureSpecificSectionAttributeRepository;
    @Autowired
    private ClosureStringAttributeRepository closureStringAttributeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkflowStatusTransitionRepository workflowStatusTransitionRepository;
    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;
    @Autowired
    private GrantClosureRepository grantClosureRepository;
    @Autowired
    private ClosureAssignmentHistoryRepository assignmentHistoryRepository;
    @Autowired
    private GranterClosureSectionRepository granterClosureSectionRepository;
    @Autowired
    private GranterClosureSectionAttributeRepository granterClosureSectionAttributeRepository;
    @Autowired
    private ClosureStringAttributeAttachmentsRepository closureStringAttributeAttachmentsRepository;
    @Autowired
    private GrantClosureHistoryRepository grantClosureHistoryRepository;
    @Autowired
    private GrantClosureRepository closureRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ClosureReasonsRepository closureReasonsRepository;

    public List<GranterClosureTemplate> findTemplatesAndPublishedStatusAndPrivateStatus(Long grantId, boolean isPublished, boolean isPrivate) {
        return granterClosureTemplateRepository.findByGranterIdAndPublishedAndPrivateToClosure(grantId, isPublished,
                isPrivate);
    }

    public ClosureAssignments saveAssignmentForClosure(ClosureAssignments assignment) {
        return closureAssignmentRepository.save(assignment);
    }

    public GranterClosureTemplate findByTemplateId(Long templateId) {
        Optional<GranterClosureTemplate> optionalGranterClosureTemplate = granterClosureTemplateRepository.findById(templateId);
        if(optionalGranterClosureTemplate.isPresent()){
            return optionalGranterClosureTemplate.get();
        }
        return null;
    }

    public ClosureSpecificSection saveClosureSpecificSection(ClosureSpecificSection specificSection) {
        return closureSpecificSectionRepository.save(specificSection);
    }

    public ClosureSpecificSectionAttribute saveClosureSpecificSectionAttribute(ClosureSpecificSectionAttribute sectionAttribute) {
        return closureSpecificSectionAttributeRepository.save(sectionAttribute);
    }

    public ClosureStringAttribute saveClosureStringAttribute(ClosureStringAttribute stringAttribute) {
        return closureStringAttributeRepository.save(stringAttribute);
    }

    public List<ClosureSpecificSection> getClosureSections(GrantClosure closure) {
        return closureSpecificSectionRepository.findByClosureId(closure.getId());
    }

    public List<ClosureSpecificSectionAttribute> getAttributesBySection(ClosureSpecificSection closureSection) {
        return closureSpecificSectionAttributeRepository.findBySection(closureSection);
    }

    public List<ClosureStringAttribute> getStringAttributesForClosure(GrantClosure closure) {
        return closureStringAttributeRepository.findByClosure(closure);
    }

    public List<ClosureAssignments> getAssignmentsForClosure(GrantClosure closure) {
        return closureAssignmentRepository.findByClosureId(closure.getId());
    }

    public void setAssignmentHistory(ClosureAssignmentsVO assignmentsVO) {
        if (!grantClosureRepository.findClosuresThatMovedAtleastOnce(assignmentsVO.getClosureId()).isEmpty()) {
            List<ClosureAssignmentHistory> assignmentHistories = assignmentHistoryRepository
                    .findByClosureIdAndStateIdOrderByUpdatedOnDesc(assignmentsVO.getClosureId(),
                            assignmentsVO.getStateId());
            for (ClosureAssignmentHistory closureAss : assignmentHistories) {
                if (closureAss.getAssignment() != null && closureAss.getAssignment() != 0) {
                    Optional<User> optionalUser = userRepository.findById(closureAss.getAssignment());
                    closureAss.setAssignmentUser(optionalUser.isPresent()?optionalUser.get():null);
                }
                if (closureAss.getUpdatedBy() != null && closureAss.getUpdatedBy() != 0) {
                    Optional<User> optionalUser=userRepository.findById(closureAss.getUpdatedBy());
                    closureAss.setUpdatedByUser(optionalUser.isPresent()?optionalUser.get():null);
                }

            }
            assignmentsVO.setHistory(assignmentHistories);
        }
    }

    public List<WorkFlowPermission> getFlowAuthority(GrantClosure closure, Long userId) {
        List<WorkFlowPermission> permissions = new ArrayList<>();
        Optional<User> optionalUser = userRepository.findById(userId);

        if ((closureAssignmentRepository.findByClosureId(closure.getId()).stream()
                .anyMatch(ass -> ass.getStateId().longValue() == closure.getStatus().getId().longValue()
                        && (ass.getAssignment() == null ? 0 : ass.getAssignment().longValue()) == userId)
                )

                || (optionalUser.isPresent()?optionalUser.get().getOrganization().getOrganizationType().equalsIgnoreCase(
                "GRANTEE") && closure.getStatus().getInternalStatus().equalsIgnoreCase("ACTIVE"):null)) {

            List<WorkflowStatusTransition> allowedTransitions = workflowStatusTransitionRepository
                    .findByWorkflow(workflowStatusRepository.getById(closure.getStatus().getId()).getWorkflow()).stream()
                    .filter(st -> st.getFromState().getId().longValue() == closure.getStatus().getId().longValue())
                    .collect(Collectors.toList());
            if (allowedTransitions != null && !allowedTransitions.isEmpty()) {
                allowedTransitions.forEach(tr -> {
                    WorkFlowPermission workFlowPermission = new WorkFlowPermission();
                    workFlowPermission.setAction(tr.getAction());
                    workFlowPermission.setFromName(tr.getFromState().getName());
                    workFlowPermission.setFromStateId(tr.getFromState().getId());
                    workFlowPermission.setId(tr.getId());
                    workFlowPermission.setNoteRequired(tr.getNoteRequired());
                    workFlowPermission.setToName(tr.getToState().getName());
                    workFlowPermission.setToStateId(tr.getToState().getId());
                    workFlowPermission.setSeqOrder(tr.getSeqOrder());
                    permissions.add(workFlowPermission);
                });
            }
        }
        return permissions;
    }

    public Long getApprovedClosuresActualSumForGrant(Long grantId, String attributeName) {
        return grantClosureRepository.getApprovedClosuresActualSumForGrantAndAttribute(grantId, attributeName);
    }

    public List<GranterClosureTemplate> findByGranterIdAndPublishedStatusAndPrivateStatus(Long id, boolean publishedStatus, boolean isPrivate) {
            return granterClosureTemplateRepository.findByGranterIdAndPublishedAndPrivateToClosure(id, publishedStatus,
                    isPrivate);

    }

    public GrantClosure saveClosure(GrantClosure closure) {
        return grantClosureRepository.save(closure);
    }

    public GrantClosure getClosureById(Long closureId) {
        return grantClosureRepository.findByClosureId(closureId);
    }

    public ClosureSpecificSection getClosureSpecificSectionById(Long id) {
        Optional<ClosureSpecificSection> optionalClosureSpecificSection = closureSpecificSectionRepository.findById(id);
        if(optionalClosureSpecificSection.isPresent()){
            return optionalClosureSpecificSection.get();
        }
        return null;
    }

    public ClosureStringAttribute getClosureStringByStringAttributeId(Long id) {
        Optional<ClosureStringAttribute> optionalClosureStringAttribute = closureStringAttributeRepository.findById(id);
        if(optionalClosureStringAttribute.isPresent()) {
            return optionalClosureStringAttribute.get();
        }
        return null;
    }

    public ClosureStringAttribute getClosureStringAttributeBySectionAttributeAndSection(ClosureSpecificSectionAttribute sectionAttribute, ClosureSpecificSection closureSpecificSection) {
        return closureStringAttributeRepository.findBySectionAttributeAndSection(sectionAttribute, closureSpecificSection);

    }

    public Integer getNextSectionOrder(Long id, Long templateId) {
        return closureSpecificSectionRepository.getNextSectionOrder(id, templateId);

    }

    public ClosureSpecificSection saveSection(ClosureSpecificSection specificSection) {
        return closureSpecificSectionRepository.save(specificSection);
    }


    public boolean checkIfClosureTemplateChanged(GrantClosure closure, ClosureSpecificSection newSection, ClosureSpecificSectionAttribute newAttribute) {
        GranterClosureTemplate currentClosureTemplate = findByTemplateId(closure.getTemplate().getId());
        for (GranterClosureSection closureSection : currentClosureTemplate.getSections()) {
            if (!closureSection.getSectionName().equalsIgnoreCase(newSection.getSectionName())) {
                return true;
            }
            if (newAttribute != null) {
                for (GranterClosureSectionAttribute sectionAttribute : closureSection.getAttributes()) {
                    if (!sectionAttribute.getFieldName().equalsIgnoreCase(newAttribute.getSection().getSectionName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public GranterClosureTemplate _createNewClosureTemplateFromExisiting(GrantClosure closure) {
        GranterClosureTemplate currentClosureTemplate = findByTemplateId(closure.getTemplate().getId());
        GranterClosureTemplate newTemplate = null;
        if (!currentClosureTemplate.isPublished()) {
            deleteClosureTemplate(currentClosureTemplate);
        }
        newTemplate = new GranterClosureTemplate();
        newTemplate.setName("Custom Template");
        newTemplate.setGranterId(closure.getGrant().getGrantorOrganization().getId());
        newTemplate.setPublished(false);
        newTemplate = saveClosureTemplate(newTemplate);

        List<GranterClosureSection> newSections = new ArrayList<>();
        for (ClosureSpecificSection currentSection : getClosureSections(closure)) {
            GranterClosureSection newSection = new GranterClosureSection();
            newSection.setSectionOrder(currentSection.getSectionOrder());
            newSection.setSectionName(currentSection.getSectionName());
            newSection.setClosureTemplate(newTemplate);
            newSection.setGranter((Granter) closure.getGrant().getGrantorOrganization());
            newSection.setDeletable(currentSection.getDeletable());

            newSection = saveClosureTemplateSection(newSection);
            newSections.add(newSection);

            currentSection.setClosureTemplateId(newTemplate.getId());
            currentSection = saveSection(currentSection);

            for (ClosureSpecificSectionAttribute currentAttribute : getSpecificSectionAttributesBySection(
                    currentSection)) {
                GranterClosureSectionAttribute newAttribute = new GranterClosureSectionAttribute();
                newAttribute.setDeletable(currentAttribute.getDeletable());
                newAttribute.setFieldName(currentAttribute.getFieldName());
                newAttribute.setFieldType(currentAttribute.getFieldType());
                newAttribute.setGranter((Granter) currentAttribute.getGranter());
                newAttribute.setRequired(currentAttribute.getRequired());
                newAttribute.setAttributeOrder(currentAttribute.getAttributeOrder());
                newAttribute.setSection(newSection);
                if (currentAttribute.getFieldType().equalsIgnoreCase("table")) {
                    ClosureStringAttribute stringAttribute = getClosureStringAttributeBySectionAttributeAndSection(
                            currentAttribute, currentSection);

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        List<TableData> tableData = mapper.readValue(stringAttribute.getValue(),
                                new TypeReference<List<TableData>>() {
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


                saveClosureTemplateSectionAttribute(newAttribute);

            }
        }

        newTemplate.setSections(newSections);
        newTemplate = saveClosureTemplate(newTemplate);

        closure.setTemplate(newTemplate);
        saveClosure(closure);
        return newTemplate;
    }

    private GranterClosureSectionAttribute saveClosureTemplateSectionAttribute(GranterClosureSectionAttribute newAttribute) {
        return granterClosureSectionAttributeRepository.save(newAttribute);
    }

    public List<ClosureSpecificSectionAttribute> getSpecificSectionAttributesBySection(ClosureSpecificSection currentSection) {
        return closureSpecificSectionAttributeRepository.findBySection(currentSection);
    }

    private GranterClosureSection saveClosureTemplateSection(GranterClosureSection newSection) {
        return granterClosureSectionRepository.save(newSection);

    }

    public GranterClosureTemplate saveClosureTemplate(GranterClosureTemplate newTemplate) {
        return granterClosureTemplateRepository.save(newTemplate);
    }

    private void deleteClosureTemplate(GranterClosureTemplate currentClosureTemplate) {
        granterClosureTemplateRepository.delete(currentClosureTemplate);

    }

    public int getNextAttributeOrder(Long granterId, Long sectionId) {
        return closureSpecificSectionAttributeRepository.getNextAttributeOrder(granterId, sectionId);

    }

    public List<ClosureStringAttribute> getClosureStringAttributesByAttribute(ClosureSpecificSectionAttribute attrib) {
        return closureStringAttributeRepository.findBySectionAttribute(attrib);
    }

    public void deleteStringAttribute(ClosureStringAttribute stringAttrib) {
        closureStringAttributeRepository.delete(stringAttrib);
    }

    public void deleteSectionAttributes(List<ClosureSpecificSectionAttribute> specificSectionAttributesBySection) {
        closureSpecificSectionAttributeRepository.deleteAll(specificSectionAttributesBySection);
    }

    public void deleteSection(ClosureSpecificSection section) {
        closureSpecificSectionRepository.delete(section);
    }

    public ClosureStringAttributeAttachments saveClosureStringAttributeAttachment(ClosureStringAttributeAttachments attachment) {
        return closureStringAttributeAttachmentsRepository.save(attachment);
    }

    public List<ClosureStringAttributeAttachments> getStringAttributeAttachmentsByStringAttribute(ClosureStringAttribute stringAttribute) {
        return closureStringAttributeAttachmentsRepository.findByClosureStringAttribute(stringAttribute);

    }

    public ClosureStringAttributeAttachments getStringAttributeAttachmentsByAttachmentId(Long attachmentId) {

        Optional<ClosureStringAttributeAttachments> optionalClosureStringAttributeAttachments=closureStringAttributeAttachmentsRepository.findById(attachmentId);
        if(optionalClosureStringAttributeAttachments.isPresent()){
            return optionalClosureStringAttributeAttachments.get();
        }
        return null;
    }

    public void deleteStringAttributeAttachments(List<ClosureStringAttributeAttachments> attachments) {
        closureStringAttributeAttachmentsRepository.deleteAll(attachments);
    }

    public ClosureStringAttribute findClosureStringAttributeById(Long attributeId) {
        Optional<ClosureStringAttribute> optionalClosureStringAttribute = closureStringAttributeRepository.findById(attributeId);
        if(optionalClosureStringAttribute.isPresent()){
            return optionalClosureStringAttribute.get();
        }
        return null;
    }

    public void deleteSectionAttribute(ClosureSpecificSectionAttribute attribute) {
        closureSpecificSectionAttributeRepository.delete(attribute);
    }

    public GrantClosureHistory getSingleClosureHistoryByStatusAndClosureId(String status, Long closureId) {
        return grantClosureHistoryRepository.getSingleClosureHistoryByStatusAndClosureId(status, closureId);

    }

    public boolean checkIfClosureMovedThroughWFAtleastOnce(Long closureId) {
        return closureRepository.findClosuresThatMovedAtleastOnce(closureId).size() > 0;
    }

    public ClosureAssignments getClosureAssignmentById(Long id) {
        Optional<ClosureAssignments> optionalClosureAssignments = closureAssignmentRepository.findById(id);
        if(optionalClosureAssignments.isPresent()){
            return optionalClosureAssignments.get();
        }
        return null;
    }


    public String[] buildClosureInvitationContent(GrantClosure closure, String sub, String msg, String url) {
        sub = sub.replace(GRANT_NAME, closure.getGrant().getName());
        msg = msg.replace(GRANT_NAME, closure.getGrant().getName())
                .replace(TENANT_NAME, closure.getGrant().getGrantorOrganization().getName()).replace("%LINK%", url);
        return new String[] { sub, msg };
    }

    public String[] buildEmailNotificationContent(GrantClosure finalClosure, User u, String subConfigValue, String msgConfigValue, String currentState, String currentOwner,
                                                  String previousState, String previousOwner, String previousAction, String hasChanges,
                                                  String hasChangesComment, String hasNotes, String hasNotesComment, String link, User owner,
                                                  Integer noOfDays, Map<Long, Long> previousApprover, List<ClosureAssignments> newApprover) {

        String code = Base64.getEncoder().encodeToString(String.valueOf(finalClosure.getId()).getBytes());

        String granteeHost = "";
        String granteeUrl = "";
        String granterUrl = "";
        UriComponents uriComponents = null;
        UriComponentsBuilder uriBuilder;
        String granterHost = "";
        try {
            uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
            if (u.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE")) {
                granteeHost = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
                granterHost = uriComponents.getHost();

            } else {
                granterHost = uriComponents.getHost();
                granteeHost = uriComponents.getHost().substring(uriComponents.getHost().indexOf(".") + 1);
            }
            uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme()).host(granteeHost)
                    .port(uriComponents.getPort());
            granteeUrl = uriBuilder.toUriString();

            granteeUrl = granteeUrl + HOME_ACTION_LOGIN_ORG
                    + URLEncoder.encode(finalClosure.getGrant().getGrantorOrganization().getName(),
                    StandardCharsets.UTF_8.toString())
                    + R + code + EMAIL_TYPE_CLOSURE;

            uriBuilder = UriComponentsBuilder.newInstance().scheme(uriComponents.getScheme()).host(granterHost)
                    .port(uriComponents.getPort());
            granterUrl = uriBuilder.toUriString();

            granterUrl = granterUrl + HOME_ACTION_LOGIN_ORG
                    + URLEncoder.encode(finalClosure.getGrant().getGrantorOrganization().getName(),
                    StandardCharsets.UTF_8.toString())
                    + R + code + EMAIL_TYPE_CLOSURE;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            granteeUrl = link;
            try {

                granteeUrl = granteeUrl + HOME_ACTION_LOGIN_ORG
                        + URLEncoder.encode(finalClosure.getGrant().getGrantorOrganization().getName(),
                        StandardCharsets.UTF_8.toString())
                        + R + code + EMAIL_TYPE_CLOSURE;
                granterUrl = granterUrl + HOME_ACTION_LOGIN_ORG
                        + URLEncoder.encode(finalClosure.getGrant().getGrantorOrganization().getName(),
                        StandardCharsets.UTF_8.toString())
                        + R + code + EMAIL_TYPE_CLOSURE;
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        String grantName = "";
        if(finalClosure.getGrant().getReferenceNo()!=null){
            grantName = "[".concat(finalClosure.getGrant().getReferenceNo()).concat("] ").concat(finalClosure.getGrant().getName());
        }else{
            grantName = finalClosure.getGrant().getName();
        }

        String message = msgConfigValue.replace(GRANT_NAME, grantName)
                .replace("%CLOSURE_LINK%", granteeUrl)
                .replace("%CURRENT_STATE%", currentState).replace("%CURRENT_OWNER%", currentOwner)
                .replace("%PREVIOUS_STATE%", previousState).replace("%PREVIOUS_OWNER%", previousOwner)
                .replace("%PREVIOUS_ACTION%", previousAction).replace("%HAS_CHANGES%", hasChanges)
                .replace("%HAS_CHANGES_COMMENT%", hasChangesComment).replace("%HAS_NOTES%", hasNotes)
                .replace("%HAS_NOTES_COMMENT%", hasNotesComment)
                .replace("%TENANT%", finalClosure.getGrant().getGrantorOrganization().getName())
                .replace("%OWNER_NAME%", owner == null ? "" : owner.getFirstName() + " " + owner.getLastName())
                .replace("%OWNER_EMAIL%", owner == null ? "" : owner.getEmailId())
                .replace("%NO_DAYS%", noOfDays == null ? "" : String.valueOf(noOfDays))
                .replace("%GRANTEE%", finalClosure.getGrant().getOrganization()!=null?finalClosure.getGrant().getOrganization().getName():finalClosure.getGrant().getGrantorOrganization().getName())
                .replace("%GRANTEE_REPORT_LINK%", granteeUrl).replace("%GRANTER_REPORT_LINK%", granterUrl)
                .replace("%GRANTER%", finalClosure.getGrant().getGrantorOrganization().getName())
                .replace("%ENTITY_TYPE%", "report")
                .replace("%PREVIOUS_ASSIGNMENTS%", getAssignmentsTable(previousApprover, newApprover))
                .replace("%ENTITY_NAME%", "Closure Request of grant " + grantName);
        String subject = subConfigValue.replace("%CLOSURE_NAME%", "Closure Request for Grant " + finalClosure.getGrant().getName());

        return new String[] { subject, message };
    }

    private String getAssignmentsTable(Map<Long, Long> assignments, List<ClosureAssignments> newAssignments) {
        if (assignments == null) {
            return "";
        }
        newAssignments.sort(Comparator.comparing(ClosureAssignments::getId, (a, b) -> {
            return a.compareTo(b);
        }));
        String[] table = {
                "<table width='100%' border='1' cellpadding='2' cellspacing='0'><tr><td><b>Review State</b></td><td><b>Current State Owners</b></td><td><b>Previous State Owners</b></td></tr>" };
        newAssignments.forEach(a -> {

            Long prevAss = assignments.keySet().stream().filter(b -> b.longValue() == a.getStateId().longValue()).findFirst().get();

            String check = a.getAssignment() != null
                    ? userService.getUserById(a.getAssignment()).getLastName()
                    : "";
            String check2 = assignments.get(prevAss) != null
                    ? userService.getUserById(assignments.get(prevAss)).getLastName()
                    : "";
            table[0] = table[0].concat("<tr>").concat("<td width='30%'>")
                    .concat(workflowStatusRepository.findById(a.getStateId()).get().getName()).concat(TD)
                    .concat("<td>")
                    .concat(a.getAssignment() != null ? userService.getUserById(a.getAssignment()).getFirstName()
                            : "".concat("-")
                            .concat(check))

                    .concat(TD)

                    .concat("<td>")
                    .concat(assignments.get(prevAss) != null
                            ? userService.getUserById(assignments.get(prevAss)).getFirstName()
                            : "".concat("-")
                            .concat(check2)

                            .concat(TD).concat("</tr>"));
        });

        table[0] = table[0].concat("</table>");
        return table[0];

    }

    public PlainClosure closureToPlain(GrantClosure closure) throws IOException {


        PlainClosure plainClosure = new PlainClosure();
        plainClosure.setReason(closure.getReason());
        plainClosure.setDescription(closure.getDescription());
        plainClosure.setName(closure.getGrant().getName());
        plainClosure.setReferenceNo(closure.getGrant().getReferenceNo());

        plainClosure.setCurrentInternalStatus(closure.getStatus().getInternalStatus());
        plainClosure.setCurrentStatus(closure.getStatus().getName());

        Optional<ClosureAssignments> assignment =  getAssignmentsForClosure(closure).stream().filter(ass -> ass.getStateId().longValue()==closure.getStatus().getId()).findFirst();
        if(assignment.isPresent()){
            User owner = userService.getUserById(assignment.get().getAssignment());
            plainClosure.setCurrentOwner(owner.getFirstName()+" "+owner.getLastName());
        }

        if(closure.getClosureDetails().getSections()!=null && closure.getClosureDetails().getSections().size()>0){
            List<PlainSection> plainSections = new ArrayList<>();

            for(SectionVO section : closure.getClosureDetails().getSections()){
                List<PlainAttribute> plainAttributes = new ArrayList<>();
                if(section.getAttributes()!=null && section.getAttributes().size()>0) {
                    ObjectMapper mapper = new ObjectMapper();
                    for (SectionAttributesVO attribute : section.getAttributes()) {
                        PlainAttribute plainAttribute = new PlainAttribute();
                        plainAttribute.setId(attribute.getId());
                        plainAttribute.setName(attribute.getFieldName());
                        plainAttribute.setType(attribute.getFieldType());
                        plainAttribute.setValue(attribute.getFieldValue());
                        plainAttribute.setOrder(attribute.getAttributeOrder());
                        switch (attribute.getFieldType()) {

                            case "kpi":
                                plainAttribute.setFrequency(attribute.getFrequency());
                                if(attribute.getTarget()!=null) {
                                    plainAttribute.setTarget(Long.valueOf(attribute.getTarget()));
                                }
                                if(attribute.getActualTarget()!=null) {
                                    plainAttribute.setActualTarget(attribute.getActualTarget());
                                }
                                break;
                            case "disbursement":
                            case "table":
                                if(attribute.getFieldValue()!=null && !"".equalsIgnoreCase(attribute.getFieldValue())) {
                                    plainAttribute.setTableValue(mapper.readValue(attribute.getFieldValue(), new TypeReference<List<TableData>>() {
                                    }));
                                }
                                break;
                            case "document":
                                if(attribute.getFieldValue()!=null && !"".equalsIgnoreCase(attribute.getFieldValue().trim())) {
                                    plainAttribute.setAttachments(mapper.readValue(attribute.getFieldValue(), new TypeReference<List<GrantStringAttributeAttachments>>() {
                                    }));
                                }
                                break;
                            default:

                        }

                        plainAttributes.add(plainAttribute);
                    }

                }
                plainSections.add(new PlainSection(section.getId(),section.getName(),section.getOrder(), plainAttributes));
            }
            plainClosure.setSections(plainSections);
        }

        return plainClosure;
    }

    public List<GrantClosure> getClosuresForUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            return closureRepository.findAllAssignedClosuresForGranterUser(userId,optionalUser.get().getOrganization().getId());
        }

        return new ArrayList<>();
    }

    public void deleteClosure(GrantClosure closure) {

        if(checkIfClosureMovedThroughWFAtleastOnce(closure.getId())){
            closure.setDeleted(true);
            saveClosure(closure);
        }else {
            for (ClosureSpecificSection section : getClosureSections(closure)) {
                List<ClosureSpecificSectionAttribute> attribs = getSpecificSectionAttributesBySection(section);
                for (ClosureSpecificSectionAttribute attribute : attribs) {
                    List<ClosureStringAttribute> strAttribs = getClosureStringAttributesByAttribute(attribute);
                    deleteStringAttributes(strAttribs);
                }
                deleteSectionAttributes(attribs);
                deleteSection(section);
            }

            closureRepository.delete(closure);


            Optional<GranterClosureTemplate> optionalGranterClosureTemplate = granterClosureTemplateRepository.findById(closure.getTemplate().getId());
            GranterClosureTemplate template = optionalGranterClosureTemplate.isPresent()?optionalGranterClosureTemplate.get():null;
            if (template !=null && !template.isPublished()) {
                deleteClosureTemplate(template);
            }
        }
    }

    public void deleteStringAttributes(List<ClosureStringAttribute> strAttribs) {
        closureStringAttributeRepository.deleteAll(strAttribs);
    }

    public ClosureReason saveReason(ClosureReason newReason) {
        return closureReasonsRepository.save(newReason);
    }

    public List<GrantClosureHistory> getClosureHistory(Long closureId) {
        return grantClosureHistoryRepository.findByClosureId(closureId);
    }

    public List<GrantClosure> getClosuresForGrant(Long grantId) {
        return closureRepository.findByGrant(grantId);
    }
}
