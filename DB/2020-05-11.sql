insert into app_config (config_name,config_value,configurable) values('FORGOT_PASSWORD_MAIL_MESSAGE','Hi %USER_NAME% We have received a request to reset your password for %ORGANIZATION% on Anudan. Please click the link below to reset your password. %RESET_LINK% Note: This link will work only once and cannot be reused.',true);
insert into app_config (config_name,config_value,configurable) values('FORGOT_PASSWORD_MAIL_SUBJECT','Password Reset Request | Anudan',true);

CREATE SEQUENCE password_reset_id_seq START 1;
create table password_reset_request (
    id bigint NOT NULL DEFAULT nextval('password_reset_id_seq'::regclass),
    key varchar(255),
    user_id bigint,
    validated boolean default false,
    requested_on timestamp default now(),
    validated_on timestamp,
    CONSTRAINT password_reset_req_id_pk PRIMARY KEY (id)
);

alter table password_reset_request add column org_id bigint;