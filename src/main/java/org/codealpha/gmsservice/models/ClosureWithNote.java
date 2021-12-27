package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantClosure;
import org.codealpha.gmsservice.entities.Report;

public class ClosureWithNote {
    private GrantClosure closure;
    private String note;

    public GrantClosure getClosure() {
        return closure;
    }

    public void setClosure(GrantClosure closure) {
        this.closure = closure;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
