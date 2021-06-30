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

CREATE OR REPLACE FUNCTION process_report_state_change()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
    BEGIN

        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
INSERT INTO report_history(
id, name, start_date, end_date, due_date, status_id, created_at, created_by, updated_at, updated_by, grant_id,type, note, note_added,note_added_by,template_id,moved_on,linked_approved_reports,report_detail)
select OLD.id, OLD.name, OLD.start_date, OLD.end_date, OLD.due_date, OLD.status_id, OLD.created_at, OLD.created_by, OLD.updated_at, OLD.updated_by, OLD.grant_id,OLD.type, NEW.note, NEW.note_added,NEW.note_added_by,OLD.template_id,OLD.moved_on,OLD.linked_approved_reports,OLD.report_detail;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;

$BODY$;


CREATE OR REPLACE FUNCTION public.process_disbursement_state_change()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
            INSERT INTO disbursement_history (
id, requested_amount, reason, requested_on, requested_by, status_id,grant_id,note,note_added,note_added_by,created_at,created_by,updated_at,updated_by,moved_on,grantee_entry,other_sources,report_id) select OLD.id, OLD.requested_amount, OLD.reason, OLD.requested_on, OLD.requested_by, OLD.status_id,OLD.grant_id,NEW.note,NEW.note_added,NEW.note_added_by,OLD.created_at,OLD.created_by,OLD.updated_at,OLD.updated_by,OLD.moved_on,OLD.grantee_entry,OLD.other_sources,OLD.report_id;

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$;
