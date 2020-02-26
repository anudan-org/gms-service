package org.codealpha.gmsservice.models;

import org.joda.time.DateTime;

public class DatePeriodLabel {

    private DateTime dateTime;
    private String periodLabel;

    public DatePeriodLabel(DateTime dateTime, String periodLabel) {
        this.dateTime = dateTime;
        this.periodLabel = periodLabel;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }
}
