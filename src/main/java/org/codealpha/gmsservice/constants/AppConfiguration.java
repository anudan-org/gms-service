package org.codealpha.gmsservice.constants;

public enum AppConfiguration {

  KPI_REMINDER_NOTIFICATION_DAYS("KPI_REMINDER_NOTIFICATION_DAYS"),
  KPI_SUBMISSION_WINDOW_DAYS("KPI_SUBMISSION_WINDOW_DAYS");

  private String val;

  AppConfiguration(String value) {
    this.val = value;
  }
}
