package org.codealpha.gmsservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ScheduledTaskEntryModel {

    @Id
    private Long id;
    @Column
    private String configLevel;
    @Column
    private String configName;
    @Column
    private String configValue;
    @Column
    private String description;
    @Column
    private String message;
    @Column
    private String time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigLevel() {
        return configLevel;
    }

    public void setConfigLevel(String configLevel) {
        this.configLevel = configLevel;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
