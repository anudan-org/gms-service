--Changes related to closure cover note and related validations

ALTER TABLE grant_closure ADD covernote_attributes text NULL;
ALTER TABLE grant_closure ADD covernote_content text NULL;
alter table grant_types add column closure_covernote boolean default false;




INSERT INTO public.messages
(id, message, subject)
VALUES(nextval('messages_id_seq'), 'Project report(s) in progress: %report_count% ', NULL);


INSERT INTO public.workflow_validations
(id, "object", validation_query, "type", active, message_id)
VALUES(nextval('workflow_validations_id_seq'), 'CLOSURE', 'select (case when count(*) > 0 then true else false end) _failed ,
count(*) report_count from reports r, workflow_statuses s, grant_closure c where c.id = %closureId% and  r.grant_id =c.grant_id  and r.status_id = s.id and ( s.internal_status in (''REVIEW'', ''ACTIVE'') or 	 ( s.internal_status = ''DRAFT'' and exists (select 1 from report_history h where h.id = r.id )) 	 )', 'WARN', false, 20);



INSERT INTO public.messages
(id, message, subject)
VALUES(nextval('messages_id_seq'), 'Closure Cover Note is incomplete.', NULL);



INSERT INTO public.workflow_validations
(id, "object", validation_query, "type", active, message_id)
VALUES(nextval('workflow_validations_id_seq'), 'CLOSURE', 'select (case when count(*) > 0 then true else false end ) _failed from grant_closure gc cross join json_array_elements(covernote_attributes::json) elements where id = %closureId% and coalesce(covernote_content,'''') <>'''' 
and (elements->>''fieldValue'')=''''', 'WARN', true, 21);




CREATE OR REPLACE FUNCTION public.planned_fund_from_others(grantid bigint)
 RETURNS bigint
 LANGUAGE plpgsql
AS $function$
declare otherAmount bigint default 0;
declare orgGrantId bigint default null;
begin
    select sum((colvals ->>'value')::numeric) into otherAmount from (
    select s.id, a.section_id, (elements->>'name') as name,  (elements->>'header') as he,  (elements->>'columns') as col
    from grant_string_attributes a 
    inner join grant_specific_sections s on s.id = a.section_id 
    inner join grant_specific_section_attributes t on t.section_id  = s.id and t.field_type ='disbursement' and t.id =a.section_attribute_id 
    cross join json_array_elements(a.value::json) elements
   	where  a.grant_id =grantId ) c
    cross join json_array_elements(c.col::json) colvals
    where (colvals ->>'name') ='Funds from other Sources'
    and (colvals ->>'value') > 0::text;
	
	
 if otherAmount is null then
     otherAmount = 0;
 end if;

-- select orig_grant_id into orgGrantId from grants where id=grantId;

-- if orgGrantId is not null then
 -- otherAmount = otherAmount + planned_fund_from_others(orgGrantId);
 --end if;

 return otherAmount;
end;
$function$
;

CREATE OR REPLACE FUNCTION public.actual_fund_from_others(grantid bigint)
 RETURNS bigint
 LANGUAGE plpgsql
AS $function$declare actualAmount bigint default 0;
declare orgGrantId bigint default null;
begin
 select sum(ad.other_sources) into actualAmount from disbursements d
 inner join workflow_statuses w on w.id=d.status_id
 inner join actual_disbursements ad on ad.disbursement_id=d.id
 where w.internal_status='CLOSED' and d.grant_id=grantId
 group by d.grant_id;

 if actualAmount is null then
     actualAmount = 0;
 end if;

 select orig_grant_id into orgGrantId from grants where id=grantId;

 if orgGrantId is not null then
  actualAmount = actualAmount + actual_fund_from_others(orgGrantId);
 end if;

 return actualAmount;
end;
$function$
;


INSERT INTO app_config
(id, config_name, config_value, description, configurable, "key", "type")
VALUES(nextval('app_config_id_seq'), 'GRANTCLOSURE_COVER_NOTE', 'default cover note', 'Closure Cover Letter Template', false, NULL, NULL);



INSERT INTO public.org_config
(id, config_name, config_value, granter_id, description, configurable, "key", "type")
VALUES(nextval('org_config_id_seq'), 'GRANTCLOSURE_COVER_NOTE', '
<b><span id="id11"></span></b>

<b><span id="id1"></span></b> <b><span id="id2"></span></b>
<b><span id="id3"></span></b>
<span style="font-weight:600;color:darkgray;">%GRANTEE_NAME%</span>
<b><span id="id4"></span></b>
Contact number: <b><span id="id5"></span></b>
Email: <b><span id="id6"></span></b>


Subject: Grant closure letter for the grant issued to <span style="font-weight:600;color:darkgray;">%GRANTEE_NAME%</span> for <span style="font-weight:600;color:darkgray;" >"%GRANT_NAME%"</span>. 

Dear <b><span id="id7"></span></b>, 

This is with reference to your project titled <span style="font-weight:600;color:darkgray;" >"%GRANT_NAME%"</span> supported by the Collectives for Integrated Livelihood Initiatives (CInI) under Sustain Plus Initiative, approved via grant letter. The project duration was <span style="font-weight:600;color:darkgray;">%START_DATE%</span> to <span style="font-weight:600;color:darkgray;">%END_DATE%</span>. 

CInI has reviewed the progress reports (Narrative and Financial) as well as the Project end report shared by <span style="font-weight:600;color:darkgray;">%GRANTEE_NAME%</span> and found them satisfactory. As all the relevant documents have been received by the CInI, we formally close the Grant.

CInI reserves their right to communicate to you on submission of the audited statement of accounts for the year ended <b><span id="id8"></span></b>, in case of any discrepancies. 

With best wishes,
																Yours Sincerely,
																<b><span id="id9"></span></b>
																<b><span id="id10"></span></b>
', 11, 'Closure Cover Letter Template', false, NULL, NULL);
