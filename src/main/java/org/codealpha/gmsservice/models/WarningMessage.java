package org.codealpha.gmsservice.models;

public class WarningMessage {
    private String type;
    private String message;

    public WarningMessage() {
    }

    public WarningMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
