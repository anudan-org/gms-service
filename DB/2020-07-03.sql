alter table grant_assignments add column assigned_on timestamp;
alter table grant_assignment_history add column assigned_on timestamp;

alter table grant_assignments add column updated_by bigint;
alter table grant_assignment_history add column updated_by bigint;

alter table report_assignments add column assigned_on timestamp;
alter table report_assignment_history add column assigned_on timestamp;

alter table report_assignments add column updated_by bigint;
alter table report_assignment_history add column updated_by bigint;

alter table disbursement_assignments add column assigned_on timestamp;
alter table disbursement_assignment_history add column assigned_on timestamp;

alter table disbursement_assignments add column updated_by bigint;
alter table disbursement_assignment_history add column updated_by bigint;

CREATE OR REPLACE FUNCTION process_grant_assignment_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.assignments!=NEW.assignments AND OLD.assignments!=0) THEN
            INSERT INTO grant_assignment_history (
id, assignments, state_id,grant_id,assigned_on,updated_by,updated_on) select OLD.id, OLD.assignments, OLD.state_id,OLD.grant_id,OLD.assigned_on,OLD.updated_by,now();

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION process_report_assignment_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.assignment!=NEW.assignment AND OLD.assignment!=0) THEN
            INSERT INTO report_assignment_history (
id, assignment, state_id,report_id,assigned_on,updated_by,updated_on) select OLD.id, OLD.assignment, OLD.state_id,OLD.report_id,OLD.assigned_on,OLD.updated_by,now();

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION process_disbursement_assignment_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.owner!=NEW.owner AND OLD.owner!=0) THEN
            INSERT INTO disbursement_assignment_history (
id, owner, state_id,disbursement_id,assigned_on,updated_by,updated_on) select OLD.id, OLD.owner, OLD.state_id,OLD.disbursement_id,OLD.assigned_on,OLD.updated_by,now();

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';