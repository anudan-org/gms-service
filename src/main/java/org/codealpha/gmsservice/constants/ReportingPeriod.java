package org.codealpha.gmsservice.constants;

public enum ReportingPeriod {
    MONTHLY(1),
    QUARTER(3),
    HALF_YEARLY(6),
    YEARLY(12);

    private int val;

    ReportingPeriod(int val) {
        this.val = val;
    }

    public int value(){
        return val;
    }
}
