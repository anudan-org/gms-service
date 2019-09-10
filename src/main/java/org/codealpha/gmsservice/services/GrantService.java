package org.codealpha.gmsservice.services;

import java.util.List;
import java.util.Optional;

import org.codealpha.gmsservice.constants.KpiType;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantService {

    @Autowired
    private GrantRepository grantRepository;
    @Autowired
    private GranterGrantSectionRepository granterGrantSectionRepository;
    @Autowired
    private GranterGrantSectionAttributeRepository granterGrantSectionAttributeRepository;
    @Autowired
    private GrantStringAttributeRepository grantStringAttributeRepository;
    @Autowired
    private GrantDocumentAttributesRepository grantDocumentAttributesRepository;
    @Autowired
    private GrantQuantitativeDataRepository grantQuantitativeDataRepository;
    @Autowired
    private GrantKpiRepository grantKpiRepository;
    @Autowired
    private GrantQualitativeDataRepository grantQualitativeDataRepository;
    @Autowired
    private GrantDocumentDataRepository grantDocumentDataRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DocumentKpiNotesRepository documentKpiNotesRepository;
    @Autowired
    private DocKpiDataDocumentRepository docKpiDataDocumentRepository;
    @Autowired
    private QualKpiDocumentRepository qualKpiDocumentRepository;
    @Autowired
    private QualitativeKpiNotesRepository qualitativeKpiNotesRepository;
    @Autowired
    private QuantitativeKpiNotesRepository quantitativeKpiNotesRepository;
    @Autowired
    private QuantKpiDocumentRepository quantKpiDocumentRepository;
    @Autowired
    private GrantSpecificSectionAttributeRepository grantSpecificSectionAttributeRepository;
    @Autowired
    private GrantSpecificSectionRepository grantSpecificSectionRepository;
    @Autowired
    private GranterGrantTemplateRepository granterGrantTemplateRepository;
    @Autowired
    private GrantAssignmentRepository grantAssignmentRepository;

    public List<String> getGrantAlerts(Grant grant) {
        return null;
    }

    public Grant saveGrant(Grant grant) {
        return grantRepository.save(grant);
    }

    public Grant getById(Long id) {
        return grantRepository.findById(id).get();
    }

    public GrantSpecificSection getGrantSectionBySectionId(Long sectionId) {

        Optional<GrantSpecificSection> granterGrantSection = grantSpecificSectionRepository.findById(sectionId);
        if (granterGrantSection.isPresent()) {
            return granterGrantSection.get();
        }
        return null;
    }

    public GrantSpecificSectionAttribute getAttributeById(Long attributeId){
        return grantSpecificSectionAttributeRepository.findById(attributeId).get();
    }
    public GrantSpecificSectionAttribute getSectionAttributeByAttributeIdAndType(
            Long attributeId, String type) {
        if (type.equalsIgnoreCase("text")) {
            Optional<GrantStringAttribute> grantStringAttribute = grantStringAttributeRepository.findById(attributeId);
            if (grantStringAttribute.isPresent()) {
                return grantStringAttribute.get().getSectionAttribute();
            }
        }else if (type.equalsIgnoreCase("multiline")) {
            Optional<GrantStringAttribute> grantStringAttribute = grantStringAttributeRepository.findById(attributeId);
            if (grantStringAttribute.isPresent()) {
                return grantStringAttribute.get().getSectionAttribute();
            }
        }else if (type.equalsIgnoreCase("document")) {
            Optional<GrantStringAttribute> grantStringAttribute = grantStringAttributeRepository.findById(attributeId);
            if (grantStringAttribute.isPresent()) {
                return grantStringAttribute.get().getSectionAttribute();
            }
        }else if (type.equalsIgnoreCase("kpi")) {
            Optional<GrantStringAttribute> grantStringAttribute = grantStringAttributeRepository.findById(attributeId);
            if (grantStringAttribute.isPresent()) {
                return grantStringAttribute.get().getSectionAttribute();
            }
        }else if (type.equalsIgnoreCase("table")) {
            Optional<GrantStringAttribute> grantStringAttribute = grantStringAttributeRepository.findById(attributeId);
            if (grantStringAttribute.isPresent()) {
                return grantStringAttribute.get().getSectionAttribute();
            }
        }
         
        return null;
    }

    public List<GrantStringAttribute> getStringAttributesByAttribute(
            GrantSpecificSectionAttribute grantSectionAttribute) {
        return grantStringAttributeRepository.findBySectionAttribute(grantSectionAttribute);
    }

    public GrantDocumentAttributes getDocumentAttributeById(Long docAttribId) {
        return grantDocumentAttributesRepository.findById(docAttribId).get();
    }

    public GrantStringAttribute saveStringAttribute(GrantStringAttribute grantStringAttribute) {
        return grantStringAttributeRepository.save(grantStringAttribute);
    }

    public GrantSpecificSectionAttribute saveSectionAttribute(
            GrantSpecificSectionAttribute sectionAttribute) {
        return grantSpecificSectionAttributeRepository.save(sectionAttribute);
    }

    public GrantSpecificSection saveSection(GrantSpecificSection newSection) {
        return grantSpecificSectionRepository.save(newSection);
    }

    public GrantQuantitativeKpiData getGrantQuantitativeKpiDataById(Long quntKpiDataId) {
        if (grantQuantitativeDataRepository.findById(quntKpiDataId).isPresent()) {
            return grantQuantitativeDataRepository.findById(quntKpiDataId).get();
        }
        return null;
    }

    public GrantQualitativeKpiData getGrantQualitativeKpiDataById(Long qualKpiDataId) {
        if (grantQualitativeDataRepository.findById(qualKpiDataId).isPresent()) {
            return grantQualitativeDataRepository.findById(qualKpiDataId).get();
        }
        return null;
    }

    public GrantQuantitativeKpiData saveGrantQunatitativeKpiData(GrantQuantitativeKpiData kpiData) {
        return grantQuantitativeDataRepository.save(kpiData);
    }

    public GrantQualitativeKpiData saveGrantQualitativeKpiData(GrantQualitativeKpiData kpiData) {
        return grantQualitativeDataRepository.save(kpiData);
    }

    public GrantKpi saveGrantKpi(GrantKpi grantKpi) {

        return grantKpiRepository.save(grantKpi);
    }

    public GrantKpi getGrantKpiById(Long id) {
        if (grantKpiRepository.findById(id).isPresent()) {
            return grantKpiRepository.findById(id).get();
        }
        return null;
    }

    public GrantKpi getGrantKpiByNameAndTypeAndGrant(String title, KpiType kpiType, Grant grant) {
        return grantKpiRepository.findByTitleAndKpiTypeAndGrant(title, kpiType, grant);
    }

    public GrantDocumentKpiData getGrantDocumentKpiDataById(Long id) {
        if (grantDocumentDataRepository.findById(id).isPresent()) {
            return grantDocumentDataRepository.findById(id).get();
        }
        return null;
    }

    public GrantDocumentKpiData saveGrantDocumentKpiData(GrantDocumentKpiData kpiData) {
        return grantDocumentDataRepository.save(kpiData);
    }

    public List<Template> getKpiTemplates(GrantKpi kpiId) {

        return templateRepository.findByKpi(kpiId);
    }

    public Template getKpiTemplateById(Long templateId) {
        if (templateRepository.findById(templateId).isPresent()) {
            return templateRepository.findById(templateId).get();
        }
        return null;
    }

    public GrantDocumentAttributes saveGrantDocumentAttribute(GrantDocumentAttributes grantDocumentAttributes) {
        return grantDocumentAttributesRepository.save(grantDocumentAttributes);
    }

    public DocumentKpiNotes getDocKpiNoteById(Long id) {
        return documentKpiNotesRepository.findById(id).get();
    }

    public DocumentKpiNotes saveDocumentKpiNote(DocumentKpiNotes documentKpiNote) {
        return documentKpiNotesRepository.save(documentKpiNote);
    }

    public QualitativeKpiNotes getQualKpiNoteById(Long id) {
        return qualitativeKpiNotesRepository.findById(id).get();
    }

    public QualitativeKpiNotes saveQualKpiNote(QualitativeKpiNotes qualKpiNote) {
        return qualitativeKpiNotesRepository.save(qualKpiNote);
    }

    public QuantitativeKpiNotes getQuantKpiNoteById(Long id) {
        return quantitativeKpiNotesRepository.findById(id).get();
    }

    public QuantitativeKpiNotes saveQuantKpiNote(QuantitativeKpiNotes quantKpiNote) {
        return quantitativeKpiNotesRepository.save(quantKpiNote);
    }

    public DocKpiDataDocument getDockpiDocById(Long id) {
        return docKpiDataDocumentRepository.findById(id).get();
    }

    public DocKpiDataDocument saveDocKpiDataDoc(DocKpiDataDocument dataDocument){
        return docKpiDataDocumentRepository.save(dataDocument);
    }

    public QualKpiDataDocument getQualkpiDocById(Long id) {
        return qualKpiDocumentRepository.findById(id).get();
    }

    public QualKpiDataDocument saveQualKpiDataDoc(QualKpiDataDocument dataDocument){
        return qualKpiDocumentRepository.save(dataDocument);
    }

    public QuantKpiDataDocument getQuantkpiDocById(Long id) {
        return quantKpiDocumentRepository.findById(id).get();
    }

    public QuantKpiDataDocument saveQuantKpiDataDoc(QuantKpiDataDocument dataDocument){
        return quantKpiDocumentRepository.save(dataDocument);
    }

    public GrantStringAttribute findGrantStringBySectionAttribueAndGrant(GrantSpecificSection granterGrantSection,GrantSpecificSectionAttribute granterGrantSectionAttribute, Grant grant){
        return grantStringAttributeRepository.findBySectionAndSectionAttributeAndGrant(granterGrantSection,granterGrantSectionAttribute,grant);
    }

    public GrantStringAttribute findGrantStringBySectionIdAttribueIdAndGrantId(Long granterGrantSectionId,Long granterGrantSectionAttributeId, Long grantId){
        return grantStringAttributeRepository.findBySectionAndSectionIdAttributeIdAndGrantId(granterGrantSectionId,granterGrantSectionAttributeId,grantId);
    }

    public GrantDocumentAttributes findGrantDocumentBySectionAttribueAndGrant(GrantSpecificSection granterGrantSection,GrantSpecificSectionAttribute granterGrantSectionAttribute, Grant grant){
        return grantDocumentAttributesRepository.findBySectionAndSectionAttributeAndGrant(granterGrantSection,granterGrantSectionAttribute,grant);
    }

    public GrantStringAttribute saveGrantStringAttribute(GrantStringAttribute stringAttribute){
        return grantStringAttributeRepository.save(stringAttribute);
    }

    public Template saveKpiTemplate(Template storedTemplate) {
        return templateRepository.save(storedTemplate);
    }

    public GrantSpecificSectionAttribute findBySectionAndFieldName(GrantSpecificSection section, String fieldName ){
        return grantSpecificSectionAttributeRepository.findBySectionAndFieldName(section, fieldName);
    }

    public GrantSpecificSection findByGranterAndSectionName(Granter granter, String sectionName){
        return grantSpecificSectionRepository.findByGranterAndSectionName(granter,sectionName);
    }

    public Grant findGrantByNameAndGranter(String name, Granter granter){
        return grantRepository.findByNameAndGrantorOrganization(name, granter);
    }

    public String buildNotificationContent(Grant grant,WorkflowStatus status, String configValue) {
        return configValue.replace("%GRANT_NAME%",
                grant.getName())
                .replace("%GRANT_STATUS%", status.getVerb());
    }

    public List<GrantSpecificSection> getGrantSections(Grant grant){
        return grantSpecificSectionRepository.findByGranterAndGrantId((Granter)grant.getGrantorOrganization(), grant.getId());
    }

    public List<GrantSpecificSectionAttribute> getAttributesBySection(GrantSpecificSection section){
        return grantSpecificSectionAttributeRepository.findBySection(section);
    }


    public void deleteSections(List<GrantSpecificSection> sections){
        grantSpecificSectionRepository.deleteAll(sections);
    }

    public void deleteSection(GrantSpecificSection section){
        grantSpecificSectionRepository.delete(section);
    }

    public void deleteAtttribute(GrantSpecificSectionAttribute attrib){
        grantSpecificSectionAttributeRepository.delete(attrib);
    }

    public void deleteSectionAttributes(List<GrantSpecificSectionAttribute> attributes){
        grantSpecificSectionAttributeRepository.deleteAll(attributes);
    }

    public void deleteStringAttributes(List<GrantStringAttribute> stringAttributes){
        grantStringAttributeRepository.deleteAll(stringAttributes);
    }

    public void deleteStringAttribute(GrantStringAttribute stringAttribute){
        grantStringAttributeRepository.delete(stringAttribute);
    }

    public GranterGrantTemplate saveGrantTemplate(GranterGrantTemplate newTemplate) {
        return granterGrantTemplateRepository.save(newTemplate);
    }

    public GranterGrantSection saveGrantTemaplteSection(GranterGrantSection section){
        return granterGrantSectionRepository.save(section);
    }

    public GranterGrantSectionAttribute saveGrantTemaplteSectionAttribute(GranterGrantSectionAttribute attribute){
        return granterGrantSectionAttributeRepository.save(attribute);
    }

    public void deleteGrantTemplateSections(List<GranterGrantSection> sections){
        granterGrantSectionRepository.deleteAll(sections);
    }

    public void deleteGrantTemplate(GranterGrantTemplate template){
        granterGrantTemplateRepository.delete(template);
    }

    public int getNextAttributeOrder(Long granterId, Long sectionId){
        return grantSpecificSectionAttributeRepository.getNextAttributeOrder(granterId,sectionId);
    }

    public int getNextSectionOrder(Long granterId, Long templateId){
        return grantSpecificSectionRepository.getNextSectionOrder(granterId,templateId);
    }

    public void deleteGrant(Grant grant){
        grantRepository.delete(grant);
    }

    public GrantAssignments saveAssignmentForGrant(GrantAssignments assignment){
       return grantAssignmentRepository.save(assignment);
    }

    public List<GrantAssignments> getGrantCurrentAssignments(Grant grant){
        return grantAssignmentRepository.findByGrantIdAndStateId(grant.getId(),grant.getGrantStatus().getId());
    }

    public List<GrantAssignments> getGrantWorkflowAssignments(Grant grant){
        return grantAssignmentRepository.findByGrantId(grant.getId());
    }

    public GrantAssignments getGrantAssignmentById(Long assignmentId){
        if(grantAssignmentRepository.findById(assignmentId).isPresent()) {
            return grantAssignmentRepository.findById(assignmentId).get();
        }
        return null;
    }
}
