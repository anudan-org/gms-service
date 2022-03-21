package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GranterGrantSection;

import java.util.List;

public class GranterGrantTemplateDTO {
    private Long id;
    private String name;
    private String description;
    private List<GranterGrantSection> sections;
    private boolean published;
    private boolean privateToGrant;
    private Long granterId;
    private boolean defaultTemplate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGranterId() {
        return granterId;
    }

    public void setGranterId(Long granterId) {
        this.granterId = granterId;
    }

    public List<GranterGrantSection> getSections() {
        return sections;
    }

    public void setSections(List<GranterGrantSection> sections) {
        this.sections = sections;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isPrivateToGrant() {
        return privateToGrant;
    }

    public void setPrivateToGrant(boolean privateToGrant) {
        this.privateToGrant = privateToGrant;
    }

    public boolean isDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }
}
