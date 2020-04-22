alter table app_config add column configurable boolean default false;
alter table org_config add column configurable boolean default false;
update app_config set configurable=true where config_name='DUE_REPORTS_REMINDER_SETTINGS';
update app_config set configurable=true where config_name='ACTION_DUE_REPORTS_REMINDER_SETTINGS';
alter table org_config add column key bigint, add column type varchar(10);
alter table app_config add column key bigint, add column type varchar(10);