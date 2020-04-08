package org.codealpha.gmsservice.models;

public class AppConfigVO {

    private Long id;
    private String configName;
    private String configValue;
    private ScheduledTaskVO scheduledTaskConfiguration;
    private String description;
    private Boolean configurable;
    private Long key;
    private String type;

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

    public ScheduledTaskVO getScheduledTaskConfiguration() {
        return scheduledTaskConfiguration;
    }

    public void setScheduledTaskConfiguration(ScheduledTaskVO scheduledTaskConfiguration) {
        this.scheduledTaskConfiguration = scheduledTaskConfiguration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getConfigurable() {
        return configurable;
    }

    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
