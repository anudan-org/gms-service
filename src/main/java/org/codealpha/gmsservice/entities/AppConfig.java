package org.codealpha.gmsservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import jdk.nashorn.internal.runtime.logging.Logger;

@Entity
public class AppConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String configName;
  @Column
  private String configValue;
  @Column
  private String description;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getConfigName() {
    return configName;
  }

  public void setConfigName(String configName) {
    this.configName = configName;
  }

  public String getConfigValue() {
    return configValue;
  }

  public void setConfigValue(String configValue) {
    this.configValue = configValue;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
