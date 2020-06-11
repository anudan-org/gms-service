
alter table disbursements add column grantee_entry boolean default false;
alter table disbursements add column other_sources real;
alter table actual_disbursements add column other_sources real;

alter table disbursement_history add column grantee_entry boolean default false;
alter table disbursement_history add column other_sources real;

CREATE OR REPLACE FUNCTION process_disbursement_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
            INSERT INTO disbursement_history (
id, requested_amount, reason, requested_on, requested_by, status_id,grant_id,note,note_added,note_added_by,created_at,created_by,updated_at,updated_by,moved_on,grantee_entry,other_sources) select OLD.*;

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

-- 
alter table actual_disbursements add column order_position int; 
update actual_disbursements set created_at=now() where created_at is null;
update actual_disbursements set order_position=0 where order_position is null;

alter table disbursements add column report_id bigint;
alter table disbursement_history add column report_id bigint;

CREATE OR REPLACE FUNCTION process_disbursement_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
            INSERT INTO disbursement_history (
id, requested_amount, reason, requested_on, requested_by, status_id,grant_id,note,note_added,note_added_by,created_at,created_by,updated_at,updated_by,moved_on,grantee_entry,other_sources,report_id) select OLD.*;

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

update disbursements set report_id=0 where report_id is null;
