package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import net.bytebuddy.agent.builder.AgentBuilder.LambdaInstrumentationStrategy;
import org.hibernate.annotations.Columns;

@Entity(name = "roles")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Organization organization;
  @Column
  private String name;
  @Column(columnDefinition = "text")
  private String description;
  @Column
  private Date createdAt;
  @Column
  private String createdBy;
  @Column
  private Date updatedAt;
  @Column
  private String updatedBy;
  @OneToMany(mappedBy = "role")
  private List<WorkflowStatePermission> statePermissionList;
  @OneToMany(mappedBy = "role")
  private List<WorkflowStatusTransition> statusTransitionList;
  @OneToMany(mappedBy = "role",fetch = FetchType.EAGER)
  List<RolesPermission> permissions;
  @Transient
  private boolean hasUsers;
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
}
