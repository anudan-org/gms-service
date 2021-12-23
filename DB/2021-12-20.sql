alter table messages add column subject text;

CREATE SEQUENCE hygiene_check_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

    CREATE TABLE hygiene_checks
    (
        id bigint NOT NULL DEFAULT nextval('hygiene_check_seq'::regclass),
        hygiene_query text COLLATE pg_catalog."default",
        active boolean,
        object character varying(20) COLLATE pg_catalog."default",
        message_id bigint,
        name character varying(500) COLLATE pg_catalog."default",
        description text COLLATE pg_catalog."default",
        scheduled_run character varying(50) COLLATE pg_catalog."default",
        CONSTRAINT hygiene_checks_pk PRIMARY KEY (id)
    );

insert into app_config(config_name,config_value) values('MISSING_ACTUAL_DISBURSEMENTS_POST_APPROVAL','2');

delete from app_config where config_name='KPI_REMINDER_NOTIFICATION_DAYS';
delete from app_config where config_name='SUBMISSION_ALTER_MAIL_SUBJECT';
delete from app_config where config_name='SUBMISSION_ALTER_MAIL_CONTENT';
delete from app_config where config_name='GRANT_ALERT_NOTIFICATION_MESSAGE';
delete from app_config where config_name='GRANT_STATE_CHANGED_NOTIFICATION_SUBJECT';
delete from app_config where config_name='REPORT_PERIOD_INTERVAL';
delete from app_config where config_name='GRANT_STATE_CHANGED_NOTIFICATION_MESSAGE';
delete from app_config where config_name='REPORT_STATE_CHANGED_NOTIFICATION_SUBJECT';
delete from app_config where config_name='REPORT_STATE_CHANGED_NOTIFICATION_MESSAGE';
delete from app_config where config_name='DISBURSEMENT_STATE_CHANGED_NOTIFICATION_MESSAGE';
delete from app_config where config_name='DISBURSEMENT_STATE_CHANGED_NOTIFICATION_SUBJECT';
delete from app_config where config_name='OWNERSHIP_CHANGED_NOTIFICATION_MESSAGE';
delete from app_config where config_name='OWNERSHIP_CHANGED_NOTIFICATION_SUBJECT';


CREATE OR REPLACE FUNCTION public.get_earliest_report_date(
	months integer,
	fordate date)
    RETURNS date
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare reportDate date;
begin
--forDate = forDate - interval '1 month'::interval;
	if (months=1) then
		reportDate = forDate + interval '1 month'::interval;
	elseif(months=3) then
		reportDate = forDate;-- + interval '3 month'::interval;
		if(extract('month' from reportDate)>=1 and extract('month' from reportDate)<=3) then
			reportDate = make_date(extract('year' from reportDate)::int,3,31);
		elseif(extract('month' from reportDate)>=4 and extract('month' from reportDate)<=6) then
			reportDate = make_date(extract('year' from reportDate)::int,6,30);
		elseif(extract('month' from reportDate)>=7 and extract('month' from reportDate)<=9) then
			reportDate = make_date(extract('year' from reportDate)::int,9,30);
		elseif(extract('month' from reportDate)>=10 and extract('month' from reportDate)<=12) then
			reportDate = make_date(extract('year' from reportDate)::int,12,31);
		end if;
	elseif(months=6) then
		reportDate = forDate;-- + interval '3 month'::interval;
		if(extract('month' from reportDate)>=1 and extract('month' from reportDate)<=6) then
			reportDate = make_date(extract('year' from reportDate)::int,6,30);
		elseif(extract('month' from reportDate)>=7 and extract('month' from reportDate)<=12) then
			reportDate = make_date(extract('year' from reportDate)::int,12,31);
		end if;
	elseif(months=12) then
		reportDate = forDate;-- + interval '3 month'::interval;
		if(extract('month' from reportDate)>=1 and extract('month' from reportDate)<=12) then
			reportDate = make_date(extract('year' from reportDate)::int,12,31);
		end if;
	end if;
 	return reportDate;
end;
$BODY$;

