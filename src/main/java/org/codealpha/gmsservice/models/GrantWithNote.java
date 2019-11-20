package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

public class GrantWithNote {
    private Grant grant;
    private String note;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
