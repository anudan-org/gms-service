-- FUNCTION: public.approved_reports_for_grant(bigint)

-- DROP FUNCTION public.approved_reports_for_grant(bigint);

CREATE OR REPLACE FUNCTION approved_reports_for_grant(
	grantid bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$declare reportCount bigint;
declare origGrantId bigint;
begin
	select count(*) into reportCount from reports r
	inner join workflow_statuses w on w.id=r.status_id
	where w.internal_status='CLOSED' and r.grant_id=grantId;

	select orig_grant_id into origGrantId from grants where id=grantId;

	if origGrantId is not null then
		reportCount = reportCount + approved_reports_for_grant(origGrantId);
	end if;
	return reportCount;
end;
$BODY$;

CREATE OR REPLACE FUNCTION disbursed_amount_for_grant(
	grantid bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$declare disbursedAmount bigint default 0;
declare orgGrantId bigint default null;
begin
	select sum(ad.actual_amount) into disbursedAmount from disbursements d
	inner join workflow_statuses w on w.id=d.status_id
	inner join actual_disbursements ad on ad.disbursement_id=d.id
	where w.internal_status='CLOSED' and d.grant_id=grantId
	group by d.id;

	if disbursedAmount is null then
	    disbursedAmount = 0;
	end if;

	select orig_grant_id into orgGrantId from grants where id=grantId;

	if orgGrantId is not null then
		disbursedAmount = disbursedAmount + disbursed_amount_for_grant(orgGrantId);
	end if;

	return disbursedAmount;
end;
$BODY$;


CREATE OR REPLACE FUNCTION project_documents_for_grant(
	grantid bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$declare projectDocsCount bigint;
declare origGrantId bigint;
begin
	select count(*) into projectDocsCount from grant_documents
	where grant_id=grantId;

	select orig_grant_id into origGrantId from grants where id=grantId;

	if origGrantId is not null then
		projectDocsCount = projectDocsCount+ project_documents_for_grant(origGrantId);
	end if;

	return projectDocsCount;
end;
$BODY$;