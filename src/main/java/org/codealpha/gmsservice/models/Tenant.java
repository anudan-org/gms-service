package org.codealpha.gmsservice.models;

import java.util.List;
import java.util.Objects;
import org.codealpha.gmsservice.entities.Grant;

public class Tenant {

  private String name;
  private List<Grant> grants;

  public Tenant() {
  }

  public Tenant(List<Grant> grants) {
    this.grants = grants;
  }

  public List<Grant> getGrants() {
    return grants;
  }

  public void setGrants(List<Grant> grants) {
    this.grants = grants;
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
    Tenant tenant = (Tenant) o;
    return name.equals(tenant.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
