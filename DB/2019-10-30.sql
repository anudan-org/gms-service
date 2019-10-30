INSERT INTO app_config(config_name, config_value) VALUES ('GRANT_STATE_CHANGED_MAIL_MESSAGE', '%USER% has requested a %ACTION% for grant %TITLE% on %DATE%');
INSERT INTO app_config(config_name, config_value) VALUES ('GRANT_STATE_CHANGED_MAIL_SUBJECT', 'Request for %ACTION%: %TITLE%');
alter table notifications add column title text;
update workflow_statuses set verb='Re-submission' where internal_status='DRAFT';
update workflow_statuses set verb='Review' where internal_status='REVIEW';