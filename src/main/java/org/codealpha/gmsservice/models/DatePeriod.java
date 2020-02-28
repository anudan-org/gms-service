package org.codealpha.gmsservice.models;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Objects;

public class DatePeriod {
    private Date start;
    private Date end;
    private String label;

    public DatePeriod(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatePeriod that = (DatePeriod) o;
        return end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(end);
    }
}
