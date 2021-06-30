package org.codealpha.gmsservice.models;

import java.util.List;
import java.util.Objects;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantCard;
import org.codealpha.gmsservice.entities.GranterGrantTemplate;
import org.codealpha.gmsservice.entities.TemplateLibrary;

public class Tenant {

  private String name;
  private List<GrantCard> grants;
  private List<GranterGrantTemplate> grantTemplates;
  private List<TemplateLibrary> templateLibrary;

  public Tenant() {
  }

  public Tenant(List<GrantCard> grants) {
    this.grants = grants;
  }

  public List<GrantCard> getGrants() {
    return grants;
  }

  public void setGrants(List<GrantCard> grants) {
    this.grants = grants;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<GranterGrantTemplate> getGrantTemplates() {
    return grantTemplates;
  }

  public void setGrantTemplates(List<GranterGrantTemplate> grantTemplates) {
    this.grantTemplates = grantTemplates;
  }

  public List<TemplateLibrary> getTemplateLibrary() {
    return templateLibrary;
  }

  public void setTemplateLibrary(List<TemplateLibrary> templateLibrary) {
    this.templateLibrary = templateLibrary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Tenant tenant = (Tenant) o;
    return name.equals(tenant.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
