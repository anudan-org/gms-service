update app_config set config_value='{"message":"<p>The report <a href=%REPORT_LINK%>%REPORT_NAME%</a> is due on %DUE_DATE%. <p>Please log on to Anudan to submit the report.</p><p>In case you have any questions or need clarifications while submitting the report please reach out to <b>%OWNER_NAME%</b> (%OWNER_EMAIL%).<p><i>This is a system generated reminder for a report submission against <b>%GRANT_NAME%</b> from %TENANT%. Please ignore this reminder if you have already submitted the report.</i></p>","messageDescription":"Description for message","subject":"Report Submission Reminder | Action Required","subjectDescription":"Description for reminder notification subject","time":"17:57","timeDescription":"Description for time","configuration":{"daysBefore":[30,6,2,1],"afterNoOfHours":0},"configurationDescription":"Description for configuration","sql":""}' where config_name='DUE_REPORTS_REMINDER_SETTINGS';

alter table grants add column moved_on timestamp;
alter table grant_history add column moved_on timestamp;
alter table reports add column moved_on timestamp;
alter table report_history add column moved_on timestamp;

CREATE OR REPLACE FUNCTION process_grant_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.grant_status_id!=NEW.grant_status_id) THEN
            INSERT INTO grant_history (
id, amount, created_at, created_by, description, end_date, name, representative, start_date, status_name, template_id, updated_at, updated_by, grant_status_id, grantor_org_id, organization_id, substatus_id, note, note_added,note_added_by,moved_on) select OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$  LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION process_report_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN

        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
INSERT INTO report_history(
id, name, start_date, end_date, due_date, status_id, created_at, created_by, updated_at, updated_by, grant_id,type, note, note_added,note_added_by,template_id,moved_on)
select OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;

$BODY$ LANGUAGE 'plpgsql';

update app_config set config_value='{"message":"<p>The Report workflow for <a href=%REPORT_LINK%>%REPORT_NAME%</a> requires your action.</p><p>This has been in your queue for %NO_DAYS% number of days</p><p> Please log on to Anudan to progress the workflow. </p><p><i>This is a system generated reminder for %TENANT%. Please ignore this reminder if you have already actioned the workflow.</i></p>","messageDescription":"Description for message","subject":"Workflow delays | Your action required","subjectDescription":"Description for reminder notification subject","time":"20:43","timeDescription":"Description for time","configuration":{"daysBefore":[0],"afterNoOfHours":1440},"configurationDescription":"Description for configuration","sql":""}' where config_name='ACTION_DUE_REPORTS_REMINDER_SETTINGS';