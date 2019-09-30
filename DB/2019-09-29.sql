CREATE SEQUENCE grant_history_id_seq START 1;

CREATE TABLE grant_history
(
    seqid bigint NOT NULL DEFAULT nextval('grant_history_id_seq'::regclass),
    id bigint,
    amount double precision,
    created_at timestamp without time zone,
    created_by character varying(255) COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    end_date timestamp without time zone,
    name text COLLATE pg_catalog."default",
    representative character varying(255) COLLATE pg_catalog."default",
    start_date timestamp without time zone,
    status_name character varying(255) COLLATE pg_catalog."default",
    template_id bigint,
    updated_at timestamp without time zone,
    updated_by character varying(255) COLLATE pg_catalog."default",
    grant_status_id bigint,
    grantor_org_id bigint,
    organization_id bigint,
    substatus_id bigint,
    CONSTRAINT grant_history_pkey PRIMARY KEY (seqid),
    CONSTRAINT fk881g56ucqjflq4o7hyyrlx2a2 FOREIGN KEY (substatus_id)
        REFERENCES public.workflow_statuses (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkcmlj43405rmsfqlm0x4gs1cli FOREIGN KEY (grantor_org_id)
        REFERENCES public.granters (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkfxhc0yhlrne4obtxvc11skonn FOREIGN KEY (organization_id)
        REFERENCES public.grantees (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkldpdqi1vkhahlhaxn5o25vdfa FOREIGN KEY (grant_status_id)
        REFERENCES public.workflow_statuses (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


CREATE OR REPLACE FUNCTION process_grant_state_change() RETURNS TRIGGER AS $grant_audit$
    BEGIN
        --
        -- Create a row in emp_audit to reflect the operation performed on emp,
        -- make use of the special variable TG_OP to work out the operation.
        --
        IF (TG_OP = 'UPDATE' AND OLD.grant_status_id!=NEW.grant_status_id) THEN
            INSERT INTO grant_history (
	id, amount, created_at, created_by, description, end_date, name, representative, start_date, status_name, template_id, updated_at, updated_by, grant_status_id, grantor_org_id, organization_id, substatus_id, note) select OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$grant_audit$ LANGUAGE plpgsql;

CREATE TRIGGER grant_audit AFTER UPDATE ON grants FOR EACH ROW EXECUTE PROCEDURE process_grant_state_change();


ALTER TABLE grants ADD COLUMN note text;
ALTER TABLE grant_history ADD COLUMN note text;
