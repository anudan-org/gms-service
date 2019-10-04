alter table grants add column note_added_by text;
alter table grant_history add column note_added_by text;

CREATE OR REPLACE FUNCTION process_grant_state_change() RETURNS TRIGGER AS $grant_audit$
    BEGIN
        --
        -- Create a row in emp_audit to reflect the operation performed on emp,
        -- make use of the special variable TG_OP to work out the operation.
        --
        IF (TG_OP = 'UPDATE' AND OLD.grant_status_id!=NEW.grant_status_id) THEN
            INSERT INTO grant_history (
	id, amount, created_at, created_by, description, end_date, name, representative, start_date, status_name, template_id, updated_at, updated_by, grant_status_id, grantor_org_id, organization_id, substatus_id, note, note_added,note_added_by) select OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$grant_audit$ LANGUAGE plpgsql;