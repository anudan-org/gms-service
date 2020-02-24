package org.codealpha.gmsservice.models;

import org.joda.time.DateTime;

import java.util.Date;

public class DatePeriod implements Comparable {
    private Date start;
    private Date end;

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

    @Override
    public int compareTo(Object o) {
        o = (DatePeriod)o;

        DateTime thisStDate = new DateTime(this.start);
        DateTime compareStDate = new DateTime(((DatePeriod) o).start);
        DateTime thisEnDate = new DateTime(this.end);
        DateTime compareEnDate = new DateTime(((DatePeriod) o).end);
        if(thisStDate.isEqual(compareStDate) && thisEnDate.isEqual(compareEnDate)){
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        obj = (DatePeriod)obj;

        DateTime thisStDate = new DateTime(this.start);
        DateTime compareStDate = new DateTime(((DatePeriod) obj).start);
        DateTime thisEnDate = new DateTime(this.end);
        DateTime compareEnDate = new DateTime(((DatePeriod) obj).end);
        if(thisStDate.isEqual(compareStDate) && thisEnDate.isEqual(compareEnDate)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return start.hashCode();
    }
}
