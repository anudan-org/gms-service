package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public class SectionVO implements Comparable<SectionVO> {

  private Long id;
  @JsonProperty("sectionName")
  private String name;

  private int order;
  
  @JsonProperty("attributes")
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
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
    return id==sectionVO.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public int compareTo(SectionVO o) {
    if (id == null || o.id == null) {
      return 0;
    }
    return id.compareTo(o.id);
  }

}
