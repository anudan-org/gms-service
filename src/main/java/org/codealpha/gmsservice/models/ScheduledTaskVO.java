package org.codealpha.gmsservice.models;

import javax.persistence.*;

public class ScheduledTaskVO {
    
   
   private String message;
   private String messageDescription;
   private String subject;
   private String subjectDescription;
   private String time;
   private String timeDescription;
   private ReportDueConfiguration configuration;
   private String configurationDescription;
   private String sql;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageDescription() {
        return messageDescription;
    }

    public void setMessageDescription(String messageDescription) {
        this.messageDescription = messageDescription;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeDescription() {
        return timeDescription;
    }

    public void setTimeDescription(String timeDescription) {
        this.timeDescription = timeDescription;
    }


    public String getConfigurationDescription() {
        return configurationDescription;
    }

    public void setConfigurationDescription(String configurationDescription) {
        this.configurationDescription = configurationDescription;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public ReportDueConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ReportDueConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }
}
