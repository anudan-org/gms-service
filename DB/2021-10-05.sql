INSERT INTO workflows(created_at, created_by, description, name, object, granter_id) select now(),'System',null,concat(name,' default closure workflow'),'GRANTCLOSURE',id from organizations where organization_type='GRANTER';


create sequence grant_closure_seq start 1;
create table grant_closure(
	id bigint not null default nextval('grant_closure_seq'::regclass),
	reason text,
	template_id bigint,
	grant_id bigint,
	moved_on timestamp,
	create_by bigint,
    created_at timestamp,
	updated_by bigint,
	update_at timestamp,
	status_id bigint,
	note_added_by bigint,
	note text,
	deleted boolean default false,
	closure_detail text,
	linked_approved_reports text,
	constraint grant_closure_pk primary key(id)
);

create sequence closure_reasons_seq start 1;
create table closure_reasons(
	id bigint not null default nextval('closure_reasons_seq'::regclass),
	reason text,
	organization_id bigint,
	created_by bigint,
    created_at timestamp,
	updated_by bigint,
	updated_at timestamp,
	enabled boolean default true,
	deleted boolean default false,
	constraint closure_reasons_pk primary key(id)
);

create sequence closure_assignments_seq start 1;
create table closure_assignments(
	id bigint not null default nextval('closure_assignments_seq'::regclass),
	closure_id bigint,
	state_id bigint,
	assignment bigint,
	anchor boolean,
	assigned_on timestamp,
    updated_by bigint,
	constraint closure_assignments_pk primary key(id)
);

create sequence closure_specific_sections_seq start 1;
create table closure_specific_sections(
	id bigint not null default nextval('closure_specific_sections_seq'::regclass),
	section_name text,
	section_order bigint,
	deletable boolean,
	granter_id bigint,
	closure_id bigint,
    closure_template_id bigint,
	constraint closure_specific_sections_pk primary key(id)
);


create sequence closure_specific_section_attributes_seq start 1;
create table closure_specific_section_attributes(
	id bigint not null default nextval('closure_specific_section_attributes_seq'::regclass),
	field_name text,
	field_type text,
	attribute_order bigint,
	deletable boolean,
	required boolean,
    extras text,
	section_id bigint,
	granter_id bigint,
	can_edit boolean,
	constraint closure_specific_section_attributes_pk primary key(id)
);


create sequence closure_string_attributes_seq start 1;
create table closure_string_attributes(
	id bigint not null default nextval('closure_string_attributes_seq'::regclass),
	section_attribute_id bigint,
	value text,
	target text,
	actual_target double precision,
	frequency text,
    closure_id bigint,
	section_id bigint,
	grant_level_target text,
	constraint closure_string_attributes_pk primary key(id)
);

create sequence closure_string_attribute_attachments_seq start 1;
create table closure_string_attribute_attachments(
	id bigint not null default nextval('closure_string_attribute_attachments_seq'::regclass),
	name text,
	description text,
	location text,
	version int,
	title text,
    type text,
	created_on timestamp,
	created_by text,
	updated_on timestamp,
	updated_by text,
	closure_string_attribute_id bigint,
	constraint closure_string_attribute_attachments_pk primary key(id)
);


create sequence closure_assignment_history_seq start 1;
create table closure_assignment_history(
	seqid bigint not null default nextval('closure_assignment_history_seq'::regclass),
	id bigint,
	grant_id bigint,
	state_id bigint,
	assignment bigint,
	updated_on timestamp,
    assigned_on timestamp,
	updated_by bigint,
	constraint closure_assignment_history_pk primary key(seqid)
);


create sequence granter_closure_templates_seq start 1;
create table granter_closure_templates(
	id bigint not null default nextval('granter_closure_templates_seq'::regclass),
	name text,
	description text,
	published boolean,
	private_to_closure boolean,
	granter_id bigint,
    default_template boolean,
	constraint granter_closure_templates_pk primary key(id)
);


create sequence granter_closure_sections_seq start 1;
create table granter_closure_sections(
	id bigint not null default nextval('granter_closure_sections_seq'::regclass),
	section_name text,
	section_order bigint,
	deletable boolean,
	granter_id bigint,
	closure_template_id bigint,
	constraint granter_closure_sections_pk primary key(id)
);


create sequence granter_closure_section_attributes_seq start 1;
create table granter_closure_section_attributes(
	id bigint not null default nextval('granter_closure_section_attributes_seq'::regclass),
	field_name text,
	field_type text,
	extras text,
	attribute_order bigint,
	deletable boolean,
	required boolean,
	section_id bigint,
	granter_id bigint,
	constraint granter_closure_section_attributes_pk primary key(id)
);


create sequence grant_closure_history_seq start 1;
create table grant_closure_history(
	seqid bigint not null default nextval('grant_closure_history_seq'::regclass),
	id bigint,
	reason text,
	template_id bigint,
	grant_id bigint,
	moved_on timestamp,
	create_by bigint,
    created_at timestamp,
	updated_by bigint,
	update_at timestamp,
	status_id bigint,
	note_added_by bigint,
	note text,
	closureDetail text,
	deleted boolean,
	closure_detail text,
	linked_approved_reports text,
	constraint grant_closure_history_pk primary key(seqid)
);

alter table grant_closure add column description text;
alter table grant_closure_history add column description text;
alter table grant_closure add column note_added timestamp;
alter table grant_closure_history add column note_added timestamp;
alter table grant_closure rename column update_at to updated_at;
alter table grant_closure_history rename column update_at to updated_at;
alter table closure_assignment_history rename column grant_id to closure_id;

create sequence closure_snapshot_seq start 1;
create table closure_snapshot(
	id bigint not null default nextval('closure_snapshot_seq'::regclass),
	reason bigint,
	description text,
	assigned_to_id bigint,
	closure_id bigint,
	string_attributes text,
	status_id bigint,
	from_state_id bigint,
	to_state_id bigint,
	from_note text,
	moved_by bigint,
	from_string_attributes text,
	moved_on timestamp,
	constraint closure_snapshot_pk primary key(id)
);

insert into app_config(config_name,config_value,configurable) values('CLOSURE_STATE_CHANGED_MAIL_SUBJECT','The Grant Closure for Grant %GRANT_NAME% has been initiated',false);
insert into app_config(config_name,config_value,configurable) values('CLOSURE_STATE_CHANGED_MAIL_MESSAGE','<p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <a class="go-to-disbursement-class" href="%DISBURSEMENT_LINK%">Grant Closure Request Note for %GRANT_NAME%</a> to review.</p> <p>Grant Closure Request workflow status changed for <strong>%GRANTEE%</strong></p> <p style="color: #000;"><strong>Change Summary: </strong></p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Closure Request for:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;"><span style="font-size: 14px; color: #000; font-weight: normal;">"%GRANT_NAME%"</span> </span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <p>This is an automatically generated email. Please do not reply to this message.</p>',false);

CREATE OR REPLACE FUNCTION get_owner_closure(
	closureid bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare _owner bigint;
begin
select a.assignment into _owner from closure_assignments a
inner join grant_closure b on b.id=a.closure_id where b.status_id=a.state_id
and b.id=closureid;
return _owner;
end;
$BODY$;


CREATE OR REPLACE FUNCTION get_owner_closure_name(
	closureid bigint)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare _owner bigint;
declare _name varchar(255);

begin
select a.assignment into _owner from closure_assignments a
inner join grant_closure b on b.id=a.closure_id where b.status_id=a.state_id
and b.id=closureid;

select case when active = true then concat(first_name,' ',last_name) else concat('Unregistered: ',email_id) end into _name from users where id=_owner;

return _name;
end;
$BODY$;


alter table grant_closure add column owner_id bigint;
alter table grant_closure add column owner_name text;


CREATE FUNCTION process_closure_state_change()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
    BEGIN

        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
INSERT INTO grant_closure_history(
id, reason,description, status_id, created_at, create_by, updated_at, updated_by, grant_id,note, note_added,note_added_by,template_id,moved_on,linked_approved_reports,closure_detail)
select OLD.id, OLD.reason, OLD.description, OLD.status_id, OLD.created_at, OLD.create_by, OLD.updated_at, OLD.updated_by, OLD.grant_id,NEW.note, NEW.note_added,NEW.note_added_by,OLD.template_id,OLD.moved_on,OLD.linked_approved_reports,OLD.closure_detail;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;

$BODY$;

CREATE TRIGGER closure_audit
    AFTER UPDATE
    ON grant_closure
    FOR EACH ROW
    EXECUTE PROCEDURE process_closure_state_change();


CREATE FUNCTION process_closure_assignment_change()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.assignment!=NEW.assignment AND OLD.assignment!=0) THEN
            INSERT INTO closure_assignment_history (
id, assignment, state_id,closure_id,assigned_on,updated_by,updated_on) select OLD.id, OLD.assignment, OLD.state_id,OLD.closure_id,OLD.assigned_on,OLD.updated_by,now();

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$BODY$;

alter table grant_closure alter column reason TYPE bigint using reason::bigint;
alter table grant_closure_history alter column reason TYPE bigint using reason::bigint;
alter table grants add column closure_in_progress boolean default false;

