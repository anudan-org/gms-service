alter table roles add column internal boolean default false;
update roles set internal=true where name='Admin';
insert into app_config(config_name,config_value) values('INVITE_SUBJECT','Invitation to join %ORG_NAME%');
insert into app_config(config_name,config_value) values('INVITE_MESSAGE','You have been invited to join %ORG_NAME% as %ROLE_NAME%. This invite has been sent on behalf of %INVITE_FROM%<br><br>Please complete your registration by clicking on the link below.<br><br>%LINK%');