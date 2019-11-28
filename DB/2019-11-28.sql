alter table report_history add column note text,add column note_added timestamp,add column note_added_by varchar(255);
alter table report_history add column template_id bigint;
alter table report_history add column type text;

CREATE OR REPLACE FUNCTION process_report_state_change()
    RETURNS TRIGGER AS $report_audit$
    BEGIN
        --
        -- Create a row in emp_audit to reflect the operation performed on emp,
        -- make use of the special variable TG_OP to work out the operation.
        --
        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
			INSERT INTO report_history(
	id, name, start_date, end_date, due_date, status_id, created_at, created_by, updated_at, updated_by, grant_id,type, note, note_added,note_added_by,template_id)
	select OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;

$report_audit$ LANGUAGE plpgsql;

CREATE TRIGGER report_audit AFTER UPDATE ON reports FOR EACH ROW EXECUTE PROCEDURE process_report_state_change();

