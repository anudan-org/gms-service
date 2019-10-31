update app_config set config_value='Status of "%GRANT_NAME%" has changed.' where config_name='GRANT_STATE_CHANGED_MAIL_SUBJECT';
alter table app_config alter column config_value type text;
alter table notifications alter column message type text;
alter table notifications alter column title type text;

update app_config set config_value='"%GRANT_NAME%" has changed.<br><br>


Current State = <b>%CURRENT_STATE%</b> | Current Owner = <b>%CURRENT_OWNER%</b><br>
Previous State = <b>%PREVIOUS_STATE%</b> | Previous Owner = <b>%PREVIOUS_OWNER%</b> | Previous Action = <b>%PREVIOUS_ACTION%</b><br><br>

Changes from previous state to current state = <b>%HAS_CHANGES%</b>. <b>%HAS_CHANGES_COMMENT%</b>.<br>
Notes attached with state change = <b>%HAS_NOTES%</b>. <b>%HAS_NOTES_COMMENT%</b>.<br>' where config_name='GRANT_STATE_CHANGED_MAIL_MESSAGE';