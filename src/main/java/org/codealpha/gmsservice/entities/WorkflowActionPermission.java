package org.codealpha.gmsservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class WorkflowActionPermission {

  @Id
  private Long Id;
  @Column
  private String permissionsString;
  @Transient
  private String[] permissions;

  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
  }


  public void setPermissionsString(String permissionsString) {
    this.permissionsString = permissionsString;
  }

  public String[] getPermissions() {
    return permissionsString.split(",");
  }

  public String getPermissionsString() {
    return permissionsString;
  }

  public void setPermissions(String[] permissions) {
    this.permissions = permissions;
  }
}
