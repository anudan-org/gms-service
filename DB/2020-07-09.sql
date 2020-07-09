update app_config set config_value = '<p>Hi</p><p>The workflow assignments for %ENTITY_TYPE% <strong>%GRANT_NAME%</strong> has changed.</p> <p>Current:</p> <p>%CURRENT_ASSIGNMENTS%</p> <p>Previous:</p> <p>%PREVIOUS_ASSIGNMENTS%</p>' where config_name='OWNERSHIP_CHANGED_EMAIL_MESSAGE';
update app_config set config_value = 'Workflow Assignment Alert: Change of review state owner(s)' where config_name='OWNERSHIP_CHANGED_EMAIL_SUBJECT';
update app_config set config_value=  '<p>The workflow assignments for %ENTITY_TYPE% <strong>%GRANT_NAME%</strong> has changed.</p> <p>Current:</p> <p>%CURRENT_ASSIGNMENTS%</p> <p>Previous:</p> <p>%PREVIOUS_ASSIGNMENTS%</p>' where config_name='OWNERSHIP_CHANGED_NOTIFICATION_MESSAGE';
update app_config set config_value = 'Workflow Assignment Alert: Change of review state owner(s)' where config_name='OWNERSHIP_CHANGED_NOTIFICATION_SUBJECT';


create sequence grant_documents_seq start 1;
create table grant_documents(
	id bigint not null default nextval('grant_documents_seq'::regclass),
	location text,
	uploaded_on timestamp,
	uploaded_by bigint,
	name text,
	extension text,
    grant_id bigint,
	constraint grant_document_pk primary key(id)
);