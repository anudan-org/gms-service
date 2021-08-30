update workflow_statuses set name='Record Disbursement',display_name='Record Disbursement' where id = 33;
update workflow_statuses set name='Record Disbursement',display_name='Record Disbursement' where id = 92;
update workflow_status_transitions set action='Publish to Partner' where id=57;
update workflow_status_transitions set action='Submit to Finance' where id=59;
update workflow_status_transitions set action='Submit to Hub' where id=60;
update workflow_status_transitions set action='Approve Report' where id=61;
update workflow_status_transitions set action='Submit Internally' where id=125;
update workflow_status_transitions set action='Submit to Finance' where id=130;
update workflow_status_transitions set action='Submit to Hub' where id=126;
update workflow_status_transitions set action='Approve Report' where id=127;
update workflow_status_transitions set action='Submit to Hub' where id=69;
update workflow_status_transitions set action='Submit to Finance' where id=70;
update workflow_status_transitions set action='Submit to CEO/ED' where id=71;
update workflow_status_transitions set action='Approve Request' where id=75;
update workflow_status_transitions set action='Submit Disbursement' where id=76;
update workflow_status_transitions set action='Submit to Hub' where id=140;
update workflow_status_transitions set action='Submit to Finance' where id=141;
update workflow_status_transitions set action='Submit to CEO/ED' where id=142;
update workflow_status_transitions set action='Approve Request' where id=143;
update workflow_status_transitions set action='Submit Disbursement' where id=144;

CREATE OR REPLACE FUNCTION get_owner_disbursement(
	disbid bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare _owner bigint;
begin
select a.owner into _owner from disbursement_assignments a
inner join disbursements b on b.id=a.disbursement_id where b.status_id=a.state_id
and b.id=disbId;
return _owner;
end;
$BODY$;


CREATE OR REPLACE FUNCTION get_owner_grant(
	grantid bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare _owner bigint;
begin
select a.assignments into _owner from grant_assignments a
inner join grants b on b.id=a.grant_id where b.grant_status_id=a.state_id
and b.id=grantId;
return _owner;
end;
$BODY$;


CREATE OR REPLACE FUNCTION get_owner_report(
	reportid bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare _owner bigint;
begin
select a.assignment into _owner from report_assignments a
inner join reports b on b.id=a.report_id where b.status_id=a.state_id
and b.id=reportId;
return _owner;
end;
$BODY$;