CREATE OR REPLACE FUNCTION process_grant_state_change()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.grant_status_id!=NEW.grant_status_id) THEN
            INSERT INTO grant_history (
id, amount, created_at, created_by, description, end_date, name, representative, start_date, status_name, template_id, updated_at, updated_by, grant_status_id, grantor_org_id, organization_id, substatus_id, note, note_added,note_added_by,moved_on,reference_no,deleted) select OLD.id, OLD.amount, OLD.created_at, OLD.created_by, OLD.description, OLD.end_date, OLD.name, OLD.representative, OLD.start_date, OLD.status_name, OLD.template_id, OLD.updated_at, OLD.updated_by, OLD.grant_status_id, OLD.grantor_org_id, OLD.organization_id, OLD.substatus_id, NEW.note, NEW.note_added,NEW.note_added_by,OLD.moved_on,OLD.reference_no,OLD.deleted;

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$;

