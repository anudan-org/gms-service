update app_config set config_value=replace(config_value,'The Report workflow','The Report approval workflow') where config_name='ACTION_DUE_REPORTS_REMINDER_SETTINGS';
update app_config set config_value=replace(config_value,'<b>%REPORT_NAME%</b>','<b>%REPORT_NAME%</b> for <b>%GRANT_NAME%</b>') where config_name='ACTION_DUE_REPORTS_REMINDER_SETTINGS';
update app_config set config_value=replace(config_value,'number of days','day(s)') where config_name='ACTION_DUE_REPORTS_REMINDER_SETTINGS';
update app_config set config_value=replace(config_value,'by an email reply','by writing to admin@anudan.org') where config_name='PLATFORM_EMAIL_FOOTER';
