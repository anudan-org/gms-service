update grant_assignment_history set updated_by=102 where seqid=42;
update grant_assignments set assignments=122 where id=1495;

CREATE OR REPLACE FUNCTION process_grant_assignment_change()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.assignments!=NEW.assignments AND OLD.assignments!=0) THEN
            INSERT INTO grant_assignment_history (
id, assignments, state_id,grant_id,assigned_on,updated_by,updated_on) select OLD.id, OLD.assignments, OLD.state_id,OLD.grant_id,OLD.assigned_on,NEW.updated_by,now();

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$;