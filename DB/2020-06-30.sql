create sequence disb_assign_hist start 1;
create table disbursement_assignment_history(
	seqid bigint not null default nextval('disb_assign_hist'::regclass),
	id bigint,
	owner bigint,
	anchor boolean,
	state_id bigint,
    disbursement_id bigint,
    updated_on timestamp
);

CREATE OR REPLACE FUNCTION process_disbursement_assignment_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.owner!=NEW.owner) THEN
            INSERT INTO disbursement_assignment_history (
id, owner, state_id,disbursement_id,updated_on) select OLD.id, OLD.owner, OLD.state_id,OLD.disbursement_id,now();

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER disbursement_assignment_audit AFTER UPDATE ON disbursement_assignments FOR EACH ROW EXECUTE PROCEDURE process_disbursement_assignment_change();



create sequence grant_assign_hist start 1;
create table grant_assignment_history(
	seqid bigint not null default nextval('grant_assign_hist'::regclass),
	id bigint,
	assignments bigint,
	state_id bigint,
    grant_id bigint,
    updated_on timestamp
);

CREATE OR REPLACE FUNCTION process_grant_assignment_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.assignments!=NEW.assignments) THEN
            INSERT INTO grant_assignment_history (
id, assignments, state_id,grant_id,updated_on) select OLD.id, OLD.assignments, OLD.state_id,OLD.grant_id,now();

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER grant_assignment_audit AFTER UPDATE ON grant_assignments FOR EACH ROW EXECUTE PROCEDURE process_grant_assignment_change();



create sequence report_assign_hist start 1;
create table report_assignment_history(
	seqid bigint not null default nextval('report_assign_hist'::regclass),
	id bigint,
	assignment bigint,
	state_id bigint,
    report_id bigint,
    updated_on timestamp
);

CREATE OR REPLACE FUNCTION process_report_assignment_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.assignment!=NEW.assignment) THEN
            INSERT INTO report_assignment_history (
id, assignment, state_id,report_id,updated_on) select OLD.id, OLD.assignment, OLD.state_id,OLD.report_id,now();

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER report_assignment_audit AFTER UPDATE ON report_assignments FOR EACH ROW EXECUTE PROCEDURE process_report_assignment_change();
