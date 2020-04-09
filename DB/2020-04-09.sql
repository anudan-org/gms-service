update app_config set config_value='{"message":"<p>Hello</p><p>Report <strong>%REPORT_NAME%</strong> for grant&nbsp;<strong>%GRANT_NAME%</strong> is due on %DUE_DATE%.</p>","messageDescription":"Description for message","subject":"Report Due Reminder for %REPORT_NAME%","subjectDescription":"Description for reminder notification subject","time":"14:20","timeDescription":"Description for time","configuration":{"daysBefore":[30,3,2,1],"afterNoOfHours":0},"configurationDescription":"Description for configuration","sql":""}' where config_name='DUE_REPORTS_REMINDER_SETTINGS';
update app_config set config_value='{"message":"<p>Hello</p><p>Report <strong>%REPORT_NAME%</strong> for grant&nbsp;<strong>%GRANT_NAME%</strong> is due on %DUE_DATE%.</p> ","messageDescription":"Description for message","subject":"Report Due Reminder for %REPORT_NAME%","subjectDescription":"Description for reminder notification subject","time":"13:58","timeDescription":"Description for time","configuration":{"daysBefore":[0],"afterNoOfHours":1440},"configurationDescription":"Description for configuration","sql":""}' where config_name='ACTION_DUE_REPORTS_REMINDER_SETTINGS';

update app_config set description='<p>Report due reminder configuration for Grantee organizations<p><br><small>Applicable to unsubmitted reports</small>' where config_name='DUE_REPORTS_REMINDER_SETTINGS';
update app_config set description='Action pending reminder configuration for Granter users' where config_name='ACTION_DUE_REPORTS_REMINDER_SETTINGS';

CREATE SEQUENCE release_id_seq START 1;
create table release(
    id bigint NOT NULL DEFAULT nextval('release_id_seq'::regclass),
    version varchar(255),
    CONSTRAINT release_id_pk PRIMARY KEY (id)
);