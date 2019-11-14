alter table reports add column grant_id bigint;

INSERT INTO public.workflows(
	created_at, created_by, description, name, object, granter_id)
	VALUES (now(), 'System', 'Reports flow', 'Report flow', 'REPORT', 2);

insert into workflow_statuses (created_at,created_by,display_name,initial,internal_status,name,terminal,verb,workflow_id)
select created_at,created_by,display_name,initial,internal_status,name,terminal,verb,(select id from workflows where object='REPORT' and granter_id=2) from workflow_statuses where workflow_id=1;

alter table org_config add column description text;
alter table app_config add column description text;

INSERT INTO public.app_config(
	config_name, config_value,description)
	VALUES ('REPORT_DUE_DATE_INTERVAL', 15,'Days after end date');

INSERT INTO public.app_config(
	config_name, config_value,description)
	VALUES ('REPORT_SETUP_INTERVAL', 30,'Days before end date when report needs to be setup');

INSERT INTO public.app_config(
	config_name, config_value,description)
	VALUES ('REPORT_PERIOD_INTERVAL', 30,'Calculated start date of report');

CREATE SEQUENCE report_history_id_seq START 1;

CREATE TABLE report_history
(
    seqid bigint NOT NULL DEFAULT nextval('report_history_id_seq'::regclass),
	id bigint,
    name text COLLATE pg_catalog."default",
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    due_date timestamp without time zone,
    status_id bigint,
    created_at timestamp without time zone,
    created_by bigint,
    updated_at timestamp without time zone,
    updated_by bigint,
    grant_id bigint,
    CONSTRAINT report_history_pkey PRIMARY KEY (seqid)
);

CREATE FUNCTION process_report_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        --
        -- Create a row in emp_audit to reflect the operation performed on emp,
        -- make use of the special variable TG_OP to work out the operation.
        --
        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
			INSERT INTO report_history(
	id, name, start_date, end_date, due_date, status_id, created_at, created_by, updated_at, updated_by, grant_id)
	select OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE plpgsql;


alter table reports add column type text;

CREATE SEQUENCE report_assignments_id_seq
    INCREMENT 1
    START 768
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE report_assignments
(
    id bigint NOT NULL DEFAULT nextval('report_assignments_id_seq'::regclass),
    report_id bigint,
    state_id bigint,
    assignment bigint,
    anchor boolean
);