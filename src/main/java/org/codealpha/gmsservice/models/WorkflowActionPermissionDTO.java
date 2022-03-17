package org.codealpha.gmsservice.models;

public class WorkflowActionPermissionDTO {
  private Long Id;
  private String permissionsString;
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
