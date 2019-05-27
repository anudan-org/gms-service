package org.codealpha.gmsservice.models;

import java.util.ArrayList;
import java.util.List;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.entities.GrantStringAttributes;

public class GrantDetailVO {

  private List<SectionVO> sections;

  public List<SectionVO> getSections() {
    return sections;
  }

  public void setSections(List<SectionVO> sections) {
    this.sections = sections;
  }

  public GrantDetailVO buildStringAttributes(List<GrantStringAttributes> value) {
    if(sections==null) {
      sections = new ArrayList<>();
    }
    SectionVO sectionVO = null;
    for (GrantStringAttributes stringAttribute : value) {
      sectionVO = new SectionVO();
      sectionVO.setName(stringAttribute.getSection().getSectionName());

      if (!sections.contains(sectionVO)) {
        sections.add(sectionVO);
      }

      sectionVO = sections.get(sections.indexOf(sectionVO));
      List<SectionAttributesVO> sectionAttributes = sectionVO.getAttributes();
      SectionAttributesVO sectionAttribute = new SectionAttributesVO();
      sectionAttribute.setFieldName(stringAttribute.getSectionAttribute().getFieldName());
        sectionAttribute
            .setFieldType(stringAttribute.getSectionAttribute().getFieldType());

      sectionAttribute.setFieldValue(stringAttribute.getValue());
      if (sectionAttributes == null) {
        sectionAttributes = new ArrayList<>();
      }
      if (!sectionAttributes.contains(sectionAttribute)) {
        sectionAttributes.add(sectionAttribute);
      }
      sectionVO.setAttributes(sectionAttributes);

    }
    return this;
  }

  public GrantDetailVO buildDocumentAttributes(List<GrantDocumentAttributes> value) {
    sections = new ArrayList<>();
    SectionVO sectionVO = null;
    for (GrantDocumentAttributes documentAttribute : value) {
      sectionVO = new SectionVO();
      sectionVO.setName(documentAttribute.getSection().getSectionName());

      if (!sections.contains(sectionVO)) {
        sections.add(sectionVO);
      }

      sectionVO = sections.get(sections.indexOf(sectionVO));
      List<SectionAttributesVO> sectionAttributes = sectionVO.getAttributes();
      SectionAttributesVO sectionAttribute = new SectionAttributesVO();
      sectionAttribute.setFieldName(documentAttribute.getName());
      sectionAttribute
          .setFieldType(documentAttribute.getSectionAttribute().getFieldType());

      sectionAttribute.setFieldValue(documentAttribute.getLocation());
      if (sectionAttributes == null) {
        sectionAttributes = new ArrayList<>();
      }
      if (!sectionAttributes.contains(sectionAttribute)) {
        sectionAttributes.add(sectionAttribute);
      }
      sectionVO.setAttributes(sectionAttributes);

    }
    return this;
  }
}
