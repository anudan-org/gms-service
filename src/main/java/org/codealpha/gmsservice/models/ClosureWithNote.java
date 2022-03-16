package org.codealpha.gmsservice.models;

public class ClosureWithNote {
    private GrantClosureDTO closure;
    private String note;

    public GrantClosureDTO getClosure() {
        return closure;
    }

    public void setClosure(GrantClosureDTO closure) {
        this.closure = closure;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
