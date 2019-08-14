package org.codealpha.gmsservice.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codealpha.gmsservice.entities.GrantDocumentAttributes;
import org.codealpha.gmsservice.entities.GrantStringAttribute;

public class GrantDetailVO {

  private List<SectionVO> sections;

  public List<SectionVO> getSections() {
    return sections;
  }

  public void setSections(List<SectionVO> sections) {
    Collections.sort(sections);
    this.sections = sections;
  }

  public GrantDetailVO buildStringAttributes(List<GrantStringAttribute> value) {
    if(sections==null) {
      sections = new ArrayList<>();
    }
    SectionVO sectionVO = null;
    if(value!=null) {
      for (GrantStringAttribute stringAttribute : value) {
        sectionVO = new SectionVO();
        sectionVO.setId(stringAttribute.getSection().getId());
        sectionVO.setName(stringAttribute.getSection().getSectionName());

        if (!sections.contains(sectionVO)) {
          sections.add(sectionVO);
        }

        sectionVO = sections.get(sections.indexOf(sectionVO));
        List<SectionAttributesVO> sectionAttributes = sectionVO.getAttributes();
        SectionAttributesVO sectionAttribute = new SectionAttributesVO();
        sectionAttribute.setId(stringAttribute.getId());
        sectionAttribute.setFieldName(stringAttribute.getSectionAttribute().getFieldName());
        sectionAttribute
            .setFieldType(stringAttribute.getSectionAttribute().getFieldType());
        //sectionAttribute.setDeletable(stringAttribute.getSectionAttribute().getDeletable());
        sectionAttribute.setRequired(stringAttribute.getSectionAttribute().getRequired());

        sectionAttribute.setFieldValue(stringAttribute.getValue());
        sectionAttribute.setTarget(stringAttribute.getTarget());
        sectionAttribute.setFrequency(stringAttribute.getFrequency());
        
        if (sectionAttributes == null) {
          sectionAttributes = new ArrayList<>();
        }
        if (!sectionAttributes.contains(sectionAttribute)) {
          sectionAttributes.add(sectionAttribute);
        }
        sectionVO.setAttributes(sectionAttributes);

      }
    }
    return this;
  }

  public GrantDetailVO buildDocumentAttributes(List<GrantDocumentAttributes> value) {
    sections = new ArrayList<>();
    SectionVO sectionVO = null;
    if(value!=null) {
      for (GrantDocumentAttributes documentAttribute : value) {
        sectionVO = new SectionVO();
        sectionVO.setId(documentAttribute.getSection().getId());
        sectionVO.setName(documentAttribute.getSection().getSectionName());

        if (!sections.contains(sectionVO)) {
          sections.add(sectionVO);
        }

        sectionVO = sections.get(sections.indexOf(sectionVO));
        List<SectionAttributesVO> sectionAttributes = sectionVO.getAttributes();
        SectionAttributesVO sectionAttribute = new SectionAttributesVO();
        sectionAttribute.setId(documentAttribute.getId());
        sectionAttribute.setFieldName(documentAttribute.getName());
        sectionAttribute
            .setFieldType(documentAttribute.getSectionAttribute().getFieldType());

        sectionAttribute.setDeletable(documentAttribute.getSectionAttribute().getDeletable());
        sectionAttribute.setRequired(documentAttribute.getSectionAttribute().getRequired());

        sectionAttribute.setFieldValue(documentAttribute.getLocation());
        if (sectionAttributes == null) {
          sectionAttributes = new ArrayList<>();
        }
        if (sectionAttributes.size()>0 && !sectionAttributes.contains(sectionAttribute)) {
          sectionAttributes.add(sectionAttribute);
        }
        sectionVO.setAttributes(sectionAttributes);

      }
    }
    return this;
  }
}
