package org.codealpha.gmsservice.models;

public class ReportDueConfiguration {
    private int daysBefore;
    private int afterNoOfHours ;

    public int getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    public int getAfterNoOfHours() {
        return afterNoOfHours;
    }

    public void setAfterNoOfHours(int afterNoOfHours) {
        this.afterNoOfHours = afterNoOfHours;
    }
}
