package org.codealpha.gmsservice.models;

public class WorkflowActionPermissionDTO {
  private Long id;
  private String permissionsString;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

}
