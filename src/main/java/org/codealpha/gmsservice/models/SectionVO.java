package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public class SectionVO {
  private String name;
  @JsonProperty("attribute")
  private List<SectionAttributesVO> attributes;

  public List<SectionAttributesVO> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<SectionAttributesVO> attributes) {
    this.attributes = attributes;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SectionVO sectionVO = (SectionVO) o;
    return name.equals(sectionVO.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
