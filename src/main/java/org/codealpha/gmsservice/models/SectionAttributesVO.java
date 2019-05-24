package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import org.codealpha.gmsservice.entities.GrantStringAttributes;

public class SectionAttributesVO {

  private String fieldName;
  private String fieldType;
  private String fieldValue;

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  public String getFieldValue() {
    return fieldValue;
  }

  public void setFieldValue(String fieldValue) {
    this.fieldValue = fieldValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SectionAttributesVO that = (SectionAttributesVO) o;
    return fieldName.equals(that.fieldName) &&
        fieldType.equals(that.fieldType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName, fieldType);
  }
}
