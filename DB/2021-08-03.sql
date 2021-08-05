CREATE OR REPLACE FUNCTION public.get_owner_grant_name(
	grantid bigint)
    RETURNS varchar
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare _owner bigint;
declare _name varchar(255);
begin
select a.assignments into _owner from grant_assignments a
inner join grants b on b.id=a.grant_id where b.grant_status_id=a.state_id
and b.id=grantId;

select case when active = true then concat(first_name,' ',last_name) else concat('Unregistered: ',email_id) end into _name from users where id=_owner;

return _name;
end;
$BODY$;


-- FUNCTION: public.get_owner_report_name(bigint)

-- DROP FUNCTION public.get_owner_report_name(bigint);

CREATE OR REPLACE FUNCTION public.get_owner_report_name(
	reportid bigint)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare _owner bigint;
declare _name varchar(255);

begin
select a.assignment into _owner from report_assignments a
inner join reports b on b.id=a.report_id where b.status_id=a.state_id
and b.id=reportId;

select case when active = true then concat(first_name,' ',last_name) else concat('Unregistered: ',email_id) end into _name from users where id=_owner;

return _name;
end;
$BODY$;


CREATE OR REPLACE FUNCTION public.get_owner_disbursement_name(
	disbid bigint)
    RETURNS varchar
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare _owner bigint;
declare _name varchar(255);

begin
select a.owner into _owner from disbursement_assignments a
inner join disbursements b on b.id=a.disbursement_id where b.status_id=a.state_id
and b.id=disbId;

select case when active = true then concat(first_name,' ',last_name) else concat('Unregistered: ',email_id) end into _name from users where id=_owner;

return _name;
end;
$BODY$;


insert into grant_assignments(anchor,assignments,grant_id,state_id)   (select false,a.assignments,a.grant_id,(select id from workflow_statuses where workflow_id=b.workflow_id and internal_status='CLOSED') closed_id from grant_assignments a
inner join workflow_statuses b on b.id=a.state_id
where b.internal_status='ACTIVE'
order by a.grant_id );

alter table disbursements add column owner_id int(11), add column owner_name varchar(255);