CREATE SEQUENCE disbursements_id_seq START 1;

create table disbursements(
	id bigint NOT NULL DEFAULT nextval('disbursements_id_seq'::regclass),
	requested_amount real,
	reason text,
	requested_on timestamp,
	requested_by bigint,
	status_id bigint,
	CONSTRAINT disbursements_id_pk PRIMARY KEY(id)
);

alter table disbursements add column grant_id bigint;

CREATE SEQUENCE disbursement_hist_id_seq START 1;

create table disbursement_history(
	seqid bigint NOT NULL DEFAULT nextval('disbursement_hist_id_seq'::regclass),
	id bigint,
	requested_amount real,
	reason text,
	requested_on timestamp,
	requested_by bigint,
	status_id bigint,
	CONSTRAINT disbursement_hist_id_pk PRIMARY KEY(seqid)
);

alter table disbursement_history add column grant_id bigint;

CREATE OR REPLACE FUNCTION process_disbursement_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
            INSERT INTO disbursement_history (
id, requested_amount, reason, requested_on, requested_by, status_id,grant_id) select OLD.*;

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER disbursement_audit AFTER UPDATE ON disbursements FOR EACH ROW EXECUTE PROCEDURE process_disbursement_state_change();

INSERT INTO workflows(created_at, created_by, description, name, object, granter_id)
	VALUES (now(),'System',null,'IHF Disbursement Workflow','DISBURSEMENT',2);

INSERT INTO workflows(created_at, created_by, description, name, object, granter_id)
	VALUES (now(),'System',null,'SUSPLUS Disbursement Workflow','DISBURSEMENT',11);

INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Draft',true,'DRAFT','Draft',false,8);
INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Finance Review',false,'REVIEW','Finance Review',false,8);
INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Approved',false,'ACTIVE','Approved',false,8);
INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Closed',false,'CLOSED','Closed',true,8);

delete from workflow_status_transitions where workflow_id=8;
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Submit for Finance Approval', now(), 'System', true, 25, 26, 8,0);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Approve', now(), 'System', true, 26, 27, 8,0);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Closed', now(), 'System', true, 27, 28, 8,0);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Request Modifications', now(), 'System', true, 26, 25, 8,1);

INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Draft',true,'DRAFT','Draft',false,9);
INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Hub Review',false,'REVIEW','Hub Review',false,9);
INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Finance Review',false,'REVIEW','Finance Review',false,9);
INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','CEO/ED Review',false,'REVIEW','CEO/ED Review',false,9);
INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Approved',false,'ACTIVE','Approved',false,9);
INSERT INTO workflow_statuses(created_at, created_by, display_name, initial, internal_status, name, terminal, workflow_id)
VALUES (now(),'System','Closed',false,'CLOSED','Closed',true,9);

delete from workflow_status_transitions where workflow_id=9;
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Submit for Hub Approval', now(), 'System', true, 29, 30, 9,0);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Submit for Finance Review', now(), 'System', true, 30, 31, 9,0);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Submit for Final Approval', now(), 'System', true, 31, 32, 9,0);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Request Modifications', now(), 'System', true, 32, 31, 9,1);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Request Modifications', now(), 'System', true, 30, 29, 9,1);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Request Modifications', now(), 'System', true, 31, 30, 9,1);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Approve', now(), 'System', true, 32, 33, 9,0);
INSERT INTO workflow_status_transitions(action, created_at, created_by, note_required, from_state_id, to_state_id, workflow_id,seq_order)VALUES ('Close', now(), 'System', true, 33, 34, 9,0);


create sequence disbursement_assignment_seq start 1;
create table disbursement_assignments(
	id bigint not null default nextval('disbursement_assignment_seq'::regclass),
	disbursement_id bigint,
	owner bigint,
	anchor boolean,
	constraint disbursement_assig_pk primary key(id)
);

alter table disbursement_assignments add column state_id bigint;