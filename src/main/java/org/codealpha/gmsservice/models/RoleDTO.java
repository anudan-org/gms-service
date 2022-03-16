package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.RolesPermission;
import org.codealpha.gmsservice.entities.WorkflowStatePermission;
import org.codealpha.gmsservice.entities.WorkflowStatusTransition;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class RoleDTO {

  private Long id;
  private Organization organization;
  private String name;
  private String description;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
  private List<WorkflowStatePermission> statePermissionList;
  private List<WorkflowStatusTransition> statusTransitionList;
  List<RolesPermission> permissions;
  private boolean hasUsers;
  private int linkedUsers;
  private boolean internal;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public List<RolesPermission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<RolesPermission> permissions) {
    this.permissions = permissions;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isHasUsers() {
    return hasUsers;
  }

  public void setHasUsers(boolean hasUsers) {
    this.hasUsers = hasUsers;
  }

  public int getLinkedUsers() {
    return linkedUsers;
  }

  public void setLinkedUsers(int linkedUsers) {
    this.linkedUsers = linkedUsers;
  }

  public boolean isInternal() {
    return internal;
  }

  public void setInternal(boolean internal) {
    this.internal = internal;
  }
}
