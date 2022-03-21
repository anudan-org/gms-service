package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantSpecificSection;
import org.codealpha.gmsservice.entities.GrantSpecificSectionAttribute;
import org.codealpha.gmsservice.entities.GrantStringAttributeAttachments;

import java.util.List;

public class GrantStringAttributeDTO {
  private Long id;
  private GrantSpecificSectionAttribute sectionAttribute;
  private String value;
  private String target;
  private String frequency;
  private Grant grant;
  private GrantSpecificSection section;
  private List<GrantStringAttributeAttachments> attachments;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GrantSpecificSectionAttribute getSectionAttribute() {
    return sectionAttribute;
  }

  public void setSectionAttribute(
          GrantSpecificSectionAttribute sectionAttribute) {
    this.sectionAttribute = sectionAttribute;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Grant getGrant() {
    return grant;
  }

  public void setGrant(Grant grant) {
    this.grant = grant;
  }

  public GrantSpecificSection getSection() {
    return section;
  }

  public void setSection(GrantSpecificSection section) {
    this.section = section;
  }

  public void setTarget(String tr) {
    this.target = tr;
  }

  public String getTarget() {
    return this.target;
  }

  public void setFrequency(String fq) {
    this.frequency = fq;
  }

  public String getFrequency() {
    return this.frequency;
  }

  public List<GrantStringAttributeAttachments> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<GrantStringAttributeAttachments> attachments) {
    this.attachments = attachments;
  }
}
