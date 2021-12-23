package org.codealpha.gmsservice.constants;

public enum AppConfiguration {

  KPI_SUBMISSION_WINDOW_DAYS("KPI_SUBMISSION_WINDOW_DAYS"),
  GRANT_STATE_CHANGED_MAIL_MESSAGE("GRANT_STATE_CHANGED_MAIL_MESSAGE"),
  GRANT_STATE_CHANGED_MAIL_SUBJECT("GRANT_STATE_CHANGED_MAIL_SUBJECT"),
  REPORT_STATE_CHANGED_MAIL_MESSAGE("REPORT_STATE_CHANGED_MAIL_MESSAGE"),
  REPORT_STATE_CHANGED_MAIL_SUBJECT("REPORT_STATE_CHANGED_MAIL_SUBJECT"),
  CLOSURE_STATE_CHANGED_MAIL_MESSAGE("CLOSURE_STATE_CHANGED_MAIL_MESSAGE"),
  CLOSURE_STATE_CHANGED_MAIL_SUBJECT("CLOSURE_STATE_CHANGED_MAIL_SUBJECT"),
  PLATFORM_EMAIL_FOOTER("PLATFORM_EMAIL_FOOTER"), REPORT_DUE_DATE_INTERVAL("REPORT_DUE_DATE_INTERVAL"),
  REPORT_SETUP_INTERVAL("REPORT_SETUP_INTERVAL"),
  GRANT_INVITE_SUBJECT("GRANT_INVITE_SUBJECT"), GRANT_INVITE_MESSAGE("GRANT_INVITE_MESSAGE"),
  REPORT_INVITE_SUBJECT("REPORT_INVITE_SUBJECT"), REPORT_INVITE_MESSAGE("REPORT_INVITE_MESSAGE"),
  CLOSURE_INVITE_SUBJECT("CLOSURE_INVITE_MESSAGE"), CLOSURE_INVITE_MESSAGE("CLOSURE_INVITE_MESSAGE"),
  INVITE_SUBJECT("INVITE_SUBJECT"), INVITE_MESSAGE("INVITE_MESSAGE"),
  DUE_REPORTS_REMINDER_SETTINGS("DUE_REPORTS_REMINDER_SETTINGS"),
  ACTION_DUE_REPORTS_REMINDER_SETTINGS("ACTION_DUE_REPORTS_REMINDER_SETTINGS"),
  GENERATE_GRANT_REFERENCE("GENERATE_GRANT_REFERENCE"), FORGOT_PASSWORD_MAIL_SUBJECT("FORGOT_PASSWORD_MAIL_SUBJECT"),
  FORGOT_PASSWORD_MAIL_MESSAGE("FORGOT_PASSWORD_MAIL_MESSAGE"),
  DISBURSEMENT_STATE_CHANGED_MAIL_SUBJECT("DISBURSEMENT_STATE_CHANGED_MAIL_SUBJECT"),
  DISBURSEMENT_STATE_CHANGED_MAIL_MESSAGE("DISBURSEMENT_STATE_CHANGED_MAIL_MESSAGE"),
  OWNERSHIP_CHANGED_EMAIL_MESSAGE("OWNERSHIP_CHANGED_EMAIL_MESSAGE"),
  OWNERSHIP_CHANGED_EMAIL_SUBJECT("OWNERSHIP_CHANGED_EMAIL_SUBJECT"),
  DISABLED_USERS_IN_WORKFLOW_EMAIL_TEMPLATE("DISABLED_USERS_IN_WORKFLOW_EMAIL_TEMPLATE"),
  AMENDMENT_INIT_MAIL_SUBJECT ("AMENDMENT_INIT_MAIL_SUBJECT"),
  AMENDMENT_INIT_MAIL_MESSAGE("AMENDMENT_INIT_MAIL_MESSAGE");

  private String val;

  AppConfiguration(String value) {
    this.val = value;
  }
}
