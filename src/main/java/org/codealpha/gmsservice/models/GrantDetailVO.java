package org.codealpha.gmsservice.models;

import java.util.ArrayList;
import java.util.List;
import org.codealpha.gmsservice.entities.GrantStringAttributes;

public class GrantDetailVO {

  private List<SectionVO> sections;

  public List<SectionVO> getSections() {
    return sections;
  }

  public void setSections(List<SectionVO> sections) {
    this.sections = sections;
  }

  public GrantDetailVO build(List<GrantStringAttributes> value) {
    sections = new ArrayList<>();
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
      if(stringAttribute.getSectionAttribute().getAttribute()==null){
        sectionAttribute.setFieldType("string");
      }else {
        sectionAttribute
            .setFieldType(stringAttribute.getSectionAttribute().getAttribute().getFieldType());
      }
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
}
