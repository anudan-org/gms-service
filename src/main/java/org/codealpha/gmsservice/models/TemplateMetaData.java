package org.codealpha.gmsservice.models;

public class TemplateMetaData {
    private String description;
    private boolean publish;
    private boolean privateToGrant;

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public boolean isPrivateToGrant() {
        return privateToGrant;
    }

    public void setPrivateToGrant(boolean privateToGrant) {
        this.privateToGrant = privateToGrant;
    }
}
