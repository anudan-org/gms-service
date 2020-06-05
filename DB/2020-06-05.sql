
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