package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantStringAttributeAttachments;
import org.codealpha.gmsservice.entities.TemplateLibrary;

import java.util.List;
import java.util.Objects;

public class SectionAttributesVO {

  private Long id;
  private String fieldName;
  private String fieldType;
  private String fieldValue;
  private List<TableData> fieldTableValue;
  private List<TemplateLibrary> docs;
  private List<GrantStringAttributeAttachments> attachments;
  private int attributeOrder;
  private String target;
  private String frequency;
  private boolean deletable;
  private boolean required;
  private boolean canEdit;
  private String grantLevelTarget;

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isDeletable() {
    return deletable;
  }

  public void setDeletable(boolean deletable) {
    this.deletable = deletable;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public void setTarget(String tr){
    this.target = tr;
  }

  public String getTarget(){
    return this.target;
  }

  public void setFrequency(String fq){
    this.frequency = fq;
  }

  public String getFrequency(){
    return this.frequency;
  }

  public List<TableData> getFieldTableValue() {
    return fieldTableValue;
  }

  public void setFieldTableValue(List<TableData> fieldTableValue) {
    this.fieldTableValue = fieldTableValue;
  }

  public int getAttributeOrder() {
    return attributeOrder;
  }

  public void setAttributeOrder(int attributeOrder) {
    this.attributeOrder = attributeOrder;
  }

  public List<TemplateLibrary> getDocs() {
    return docs;
  }

  public void setDocs(List<TemplateLibrary> docs) {
    this.docs = docs;
  }

  public List<GrantStringAttributeAttachments> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<GrantStringAttributeAttachments> attachments) {
    this.attachments = attachments;
  }

  public boolean isCanEdit() {
    return canEdit;
  }

  public void setCanEdit(boolean canEdit) {
    this.canEdit = canEdit;
  }

  public String getGrantLevelTarget() {
    return grantLevelTarget;
  }

  public void setGrantLevelTarget(String grantLevelTarget) {
    this.grantLevelTarget = grantLevelTarget;
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
    return id==that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName, fieldType);
  }
}
