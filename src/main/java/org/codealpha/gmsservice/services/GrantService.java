package org.codealpha.gmsservice.services;

import java.util.List;
import java.util.Optional;
import org.codealpha.gmsservice.constants.KpiType;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.entities.GrantDocumentKpiData;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.GrantStringAttribute;
import org.codealpha.gmsservice.entities.GranterGrantSection;
import org.codealpha.gmsservice.entities.GranterGrantSectionAttribute;
import org.codealpha.gmsservice.repositories.GrantDocumentAttributesRepository;
import org.codealpha.gmsservice.repositories.GrantDocumentDataRepository;
import org.codealpha.gmsservice.repositories.GrantKpiRepository;
import org.codealpha.gmsservice.repositories.GrantQualitativeDataRepository;
import org.codealpha.gmsservice.repositories.GrantQuantitativeDataRepository;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.codealpha.gmsservice.repositories.GrantStringAttributeRepository;
import org.codealpha.gmsservice.repositories.GranterGrantSectionAttributeRepository;
import org.codealpha.gmsservice.repositories.GranterGrantSectionRepository;
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

  public List<String> getGrantAlerts(Grant grant) {
    return null;
  }

  public Grant saveGrant(Grant grant) {
    return grantRepository.save(grant);
  }

  public Grant getById(Long id) {
    return grantRepository.findById(id).get();
  }

  public GranterGrantSection getGrantSectionBySectionId(Long sectionId) {

    Optional<GranterGrantSection> granterGrantSection = granterGrantSectionRepository.findById(sectionId);
    if(granterGrantSection.isPresent()){
      return granterGrantSection.get();
    }
    return null;
  }

  public GranterGrantSectionAttribute getSectionAttributeByAttributeIdAndType(
      Long attributeId, String type) {
    if(type.equalsIgnoreCase("document")){
      return grantDocumentAttributesRepository.findById(attributeId).get().getSectionAttribute();
    } else if(type.equalsIgnoreCase("string")) {
      Optional<GrantStringAttribute> grantStringAttribute = grantStringAttributeRepository.findById(attributeId);
      if(grantStringAttribute.isPresent()){
        return grantStringAttribute.get().getSectionAttribute();
      }
    }
    return null;
  }

  public GrantStringAttribute getStringAttributeByAttribute(
      GranterGrantSectionAttribute grantSectionAttribute) {
    return grantStringAttributeRepository.findBySectionAttribute(grantSectionAttribute);
  }

  public GrantDocumentAttributes getDocumentAttributeById(Long docAttribId){
    return grantDocumentAttributesRepository.findById(docAttribId).get();
  }

  public GrantStringAttribute saveStringAttribute(GrantStringAttribute grantStringAttribute) {
    return grantStringAttributeRepository.save(grantStringAttribute);
  }

  public GranterGrantSectionAttribute saveSectionAttribute(
      GranterGrantSectionAttribute sectionAttribute) {
    return granterGrantSectionAttributeRepository.save(sectionAttribute);
  }

  public GranterGrantSection saveSection(GranterGrantSection newSection) {
    return granterGrantSectionRepository.save(newSection);
  }

  public GrantQuantitativeKpiData getGrantQuantitativeKpiDataById(Long quntKpiDataId){
    if(grantQuantitativeDataRepository.findById(quntKpiDataId).isPresent()){
      return grantQuantitativeDataRepository.findById(quntKpiDataId).get();
    }
    return null;
  }

  public GrantQualitativeKpiData getGrantQualitativeKpiDataById(Long qualKpiDataId){
    if(grantQualitativeDataRepository.findById(qualKpiDataId).isPresent()){
      return grantQualitativeDataRepository.findById(qualKpiDataId).get();
    }
    return null;
  }

  public GrantQuantitativeKpiData saveGrantQunatitativeKpiData(GrantQuantitativeKpiData kpiData){
    return grantQuantitativeDataRepository.save(kpiData);
  }
public GrantQualitativeKpiData saveGrantQualitativeKpiData(GrantQualitativeKpiData kpiData){
    return grantQualitativeDataRepository.save(kpiData);
  }

  public GrantKpi saveGrantKpi(GrantKpi grantKpi) {

    return grantKpiRepository.save(grantKpi);
  }

  public GrantKpi getGrantKpiById(Long id) {
    if(grantKpiRepository.findById(id).isPresent()){
      return grantKpiRepository.findById(id).get();
    }
    return null;
  }

  public GrantKpi getGrantKpiByNameAndTypeAndGrant(String title, KpiType kpiType, Grant grant) {
    return grantKpiRepository.findByTitleAndKpiTypeAndGrant(title,kpiType,grant);
  }

  public GrantDocumentKpiData getGrantDocumentKpiDataById(Long id) {
    if(grantDocumentDataRepository.findById(id).isPresent()){
      return grantDocumentDataRepository.findById(id).get();
    }
    return null;
  }

  public GrantDocumentKpiData saveGrantDocumentKpiData(GrantDocumentKpiData kpiData) {
    return grantDocumentDataRepository.save(kpiData);
  }
}
