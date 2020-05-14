alter table grant_snapshot add column moved_on timestamp;
alter table grant_snapshot add column assigned_by bigint;
update app_config set config_value=replace(config_value,'by an email reply','by writing to admin@anudan.org') where config_name='PLATFORM_EMAIL_FOOTER';
