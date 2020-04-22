package org.codealpha.gmsservice.models;

public class ScheduledTaskVO {
    
   
   private String messageReport;
   private String messageGrant;
   private String messageDescription;
   private String subjectReport;
   private String subjectGrant;
   private String subjectDescription;
   private String time;
   private String timeDescription;
   private ReportDueConfiguration configuration;
   private String configurationDescription;
   private String sql;


    public String getMessageReport() {
        return messageReport;
    }

    public void setMessageReport(String messageReport) {
        this.messageReport = messageReport;
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

    public String getSubjectReport() {
        return subjectReport;
    }

    public void setSubjectReport(String subjectReport) {
        this.subjectReport = subjectReport;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }

    public String getMessageGrant() {
        return messageGrant;
    }

    public void setMessageGrant(String messageGrant) {
        this.messageGrant = messageGrant;
    }

    public String getSubjectGrant() {
        return subjectGrant;
    }

    public void setSubjectGrant(String subjectGrant) {
        this.subjectGrant = subjectGrant;
    }
}
