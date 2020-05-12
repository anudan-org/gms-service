alter table grants add column deleted boolean default false;
alter table grant_history add column deleted boolean default false;

CREATE OR REPLACE FUNCTION process_grant_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.grant_status_id!=NEW.grant_status_id) THEN
            INSERT INTO grant_history (
id, amount, created_at, created_by, description, end_date, name, representative, start_date, status_name, template_id, updated_at, updated_by, grant_status_id, grantor_org_id, organization_id, substatus_id, note, note_added,note_added_by,moved_on,reference_no,deleted) select OLD.*;

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

