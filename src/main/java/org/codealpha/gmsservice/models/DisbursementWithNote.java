package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Disbursement;
import org.codealpha.gmsservice.entities.Report;

public class DisbursementWithNote {
    private Disbursement disbursement;
    private String note;


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Disbursement getDisbursement() {
        return disbursement;
    }

    public void setDisbursement(Disbursement disbursement) {
        this.disbursement = disbursement;
    }
}
