create sequence disbursement_snapshot_seq_id start 1;

create table disbursement_snapshot(
    id bigint not null default nextval('disbursement_snapshot_seq_id'::regclass),
    assigned_to_id bigint,
    disbursement_id bigint,
    status_id bigint,
    requested_amount real,
    reason text,
    constraint disb_snapshot_pk primary key(id)
);

alter table disbursements add column note text;
alter table disbursements add column note_added timestamp;
alter table disbursements add column note_added_by bigint;
alter table disbursements add column created_at timestamp;
alter table disbursements add column created_by text;
alter table disbursements add column updated_at timestamp;
alter table disbursements add column updated_by text;
alter table disbursements add column moved_on timestamp;

alter table disbursement_history add column note text;
alter table disbursement_history add column note_added timestamp;
alter table disbursement_history add column note_added_by bigint;
alter table disbursement_history add column created_at timestamp;
alter table disbursement_history add column created_by text;
alter table disbursement_history add column updated_at timestamp;
alter table disbursement_history add column updated_by text;
alter table disbursement_history add column moved_on timestamp;

CREATE OR REPLACE FUNCTION process_disbursement_state_change()
    RETURNS trigger
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
            INSERT INTO disbursement_history (
id, requested_amount, reason, requested_on, requested_by, status_id,grant_id,note,note_added,note_added_by,created_at,created_by,updated_at,updated_by,moved_on) select OLD.*;

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$ LANGUAGE 'plpgsql';

insert into app_config (config_name,config_value,configurable) values ('DISBURSEMENT_STATE_CHANGED_MAIL_SUBJECT','Workflow Alert | Status of Approval Request for %GRANT_NAME% has changed.',false);
insert into app_config (config_name,config_value,configurable) values ('DISBURSEMENT_STATE_CHANGED_MAIL_MESSAGE','<p style="color: #000;">Hi!</p> <p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <a href="%REPORT_LINK%">%REPORT_NAME%</a> to review.</p> <p>&nbsp;</p> <p style="color: #000;"><strong>Change Summary: </strong></p> <hr /> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Approval Request for:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;"><span style="font-size: 14px; color: #000; font-weight: normal;">"%GRANT_NAME%"</span> </span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <hr /> <p style="text-align: center; color: #000;">This is an automatically generated email. Please do not reply to this message.</p>',false);
insert into app_config (config_name,config_value,configurable) values ('DISBURSEMENT_STATE_CHANGED_NOTIFICATION_MESSAGE','<p style="color: #000;"><strong>Change Summary: </strong></p> <hr /> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Approval Request for:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;"><span style="font-size: 14px; color: #000; font-weight: normal;">"%GRANT_NAME%"</span> </span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table>',false);
insert into app_config (config_name,config_value,configurable) values ('DISBURSEMENT_STATE_CHANGED_NOTIFICATION_SUBJECT','Workflow Alert | Status of Approval Request for %GRANT_NAME% has changed.',false);
