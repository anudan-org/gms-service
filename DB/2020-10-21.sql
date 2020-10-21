INSERT INTO app_config(
	config_name, config_value, configurable)
	VALUES ('DISABLED_USERS_IN_WORKFLOW_EMAIL_TEMPLATE', '<p>%ENTITY_TYPE% %ENTITY_NAME% cannot be moved in the workflow because there are disabled users in the workflow assignments.</p> <br> <p>Please carry our reassignment for this&nbsp;%ENTITY_TYPE%</p>', false);