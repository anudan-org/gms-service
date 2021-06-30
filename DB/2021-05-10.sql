alter table report_string_attributes alter column actual_target type double precision
using actual_target::double precision;

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

 return projectDocsCount;
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
 group by d.grant_id;

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


