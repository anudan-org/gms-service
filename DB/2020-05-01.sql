update app_config set config_value='You have been invited to access Report: %REPORT_NAME% for Grant: %GRANT_NAME% from %TENANT_NAME%.<br><br>Please sign up or login to view the grant by clicking on the link below.<br><br>%LINK%' where config_name='REPORT_INVITE_MESSAGE';

