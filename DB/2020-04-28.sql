alter table reports add column linked_approved_reports text;
alter table report_history add column linked_approved_reports text;
alter table reports add column report_detail text;
alter table report_history add column report_detail text;

CREATE OR REPLACE FUNCTION process_report_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN

        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
INSERT INTO report_history(
id, name, start_date, end_date, due_date, status_id, created_at, created_by, updated_at, updated_by, grant_id,type, note, note_added,note_added_by,template_id,moved_on,linked_approved_reports,report_detail)
select OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;

$BODY$ LANGUAGE 'plpgsql';



CREATE TRIGGER report_audit AFTER UPDATE ON reports FOR EACH ROW EXECUTE PROCEDURE process_report_state_change();
CREATE TRIGGER grant_audit AFTER UPDATE ON grants FOR EACH ROW EXECUTE PROCEDURE process_grant_state_change();
