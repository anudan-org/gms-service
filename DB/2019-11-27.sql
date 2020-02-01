CREATE SEQUENCE report_snapshot_id_seq START 1;

create table report_snapshot (
    id bigint NOT NULL DEFAULT nextval('report_snapshot_id_seq'::regclass),
	assigned_to_id bigint,
	report_id bigint,
	string_attributes text,
	name text,
	description text,
	status_id bigint,
	start_date timestamp,
	end_date timestamp,
	due_date timestamp
);

INSERT INTO public.app_config(
	config_name, config_value)
	VALUES ('REPORT_STATE_CHANGED_MAIL_MESSAGE', '"%GRANT_NAME%" has changed.<br><br>


Current State = <b>%CURRENT_STATE%</b> | Current Owner = <b>%CURRENT_OWNER%</b><br>
Previous State = <b>%PREVIOUS_STATE%</b> | Previous Owner = <b>%PREVIOUS_OWNER%</b> | Previous Action = <b>%PREVIOUS_ACTION%</b><br><br>

Changes from previous state to current state = <b>%HAS_CHANGES%</b>. <b>%HAS_CHANGES_COMMENT%</b><br>
Notes attached with state change = <b>%HAS_NOTES%</b>. <b>%HAS_NOTES_COMMENT%</b><br>');

INSERT INTO public.app_config(
	config_name, config_value)
	VALUES ('REPORT_STATE_CHANGED_MAIL_SUBJECT', 'Status of "%GRANT_NAME%" has changed.');