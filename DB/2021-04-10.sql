create or replace function approved_reports_for_grant(grantId bigint) returns bigint as $$
declare reportCount bigint;
begin
	select count(*) into reportCount from reports r
	inner join workflow_statuses w on w.id=r.status_id
	where w.internal_status='CLOSED' and r.grant_id=grantId;

	return reportCount;
end;
$$ language plpgsql;

create or replace function disbursed_amount_for_grant(grantId bigint) returns bigint as $$
declare disbursedAmount bigint;
begin
	select sum(ad.actual_amount) into disbursedAmount from disbursements d
	inner join workflow_statuses w on w.id=d.status_id
	inner join actual_disbursements ad on ad.disbursement_id=d.id
	where w.internal_status='CLOSED' and d.grant_id=grantId
	group by d.id;

	return disbursedAmount;
end;
$$ language plpgsql;

create or replace function project_documents_for_grant(grantId bigint) returns bigint as $$
declare projectDocsCount bigint;
begin
	select count(*) into projectDocsCount from grant_documents
	where grant_id=grantId;

	return projectDocsCount;
end;
$$ language plpgsql;