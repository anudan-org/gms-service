package org.codealpha.gmsservice.models;

public class EmailValidationReponse {
    private boolean exists;

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public EmailValidationReponse(boolean exists) {
        this.exists = exists;
    }

}