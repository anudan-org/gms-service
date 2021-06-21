truncate table messages;
truncate table workflow_validations;
INSERT INTO public.messages (id, message) VALUES (2, '%_for% has missing header information.');
INSERT INTO public.messages (id, message) VALUES (1, 'Planned Disbursements does not match the Grant Amount of %grantamount%.');
INSERT INTO public.messages (id, message) VALUES (3, 'Planned Disbursements is missing for this project.');
INSERT INTO public.messages (id, message) VALUES (4, 'Requested amount cannot be greater than project''s undisbursed amount.');
INSERT INTO public.messages (id, message) VALUES (5, 'The disbursed total cannot be greater than the approved amount of %requested_amount%.');
INSERT INTO public.messages (id, message) VALUES (6, 'This grant is missing project KPIs.');
INSERT INTO public.messages (id, message) VALUES (7, 'This grant is missing project documents.');


INSERT INTO public.workflow_validations (id, object, validation_query, type, active, message_id) VALUES (2, 'GRANT', 'select case when amount is null or amount=0 or representative is null or representative='''' or name is null or name='''' or organization_id is null or start_date is null or end_date is null then true else false end _failed,''GRANT'' _for
from grants where id=%grantId%;', 'WARN', true, 2);
INSERT INTO public.workflow_validations (id, object, validation_query, type, active, message_id) VALUES (3, 'GRANT', 'select not exists(select  from grant_specific_section_attributes a
inner join grant_string_attributes b on b.section_attribute_id=a.id
where b.grant_id=%grantId% and a.field_type=''disbursement'' group by b.grant_id) _failed;
', 'WARN', true, 3);
INSERT INTO public.workflow_validations (id, object, validation_query, type, active, message_id) VALUES (1, 'GRANT', 'select case when grantamount!=sum(_value::double precision) or grantamount =0 then true else false end _failed,grantamount from (select _name, case when grantamount is null or grantamount=0 then 0 else grantamount end,cast ((case when X._value is null or X._value='''' then ''0'' else X._value end) as double precision) from (select
c.amount grantamount,
json_extract_path_text(json_array_elements(json_extract_path(json_array_elements(a.value::json),''columns'')),''name'') _name,
json_extract_path_text(json_array_elements(json_extract_path(json_array_elements(a.value::json),''columns'')),''value'') _value from grant_string_attributes a
inner join grant_specific_section_attributes b on b.id=a.section_attribute_id
inner join grants c on c.id=a.grant_id
where b.field_type=''disbursement'' and a.grant_id=%grantId%) X where X._name=''Amount'') Y group by _name,grantamount;
', 'WARN', true, 1);
INSERT INTO public.workflow_validations (id, object, validation_query, type, active, message_id) VALUES (4, 'REPORT', 'select case when name is null or name='''' or start_date is null or end_date is null or due_date is null then true else false end _failed,''REPORT'' _for from reports where id=%reportId%;', 'WARN', true, 2);
INSERT INTO public.workflow_validations (id, object, validation_query, type, active, message_id) VALUES (5, 'DISBURSEMENT', 'select case when requested_amount>(b.amount-disbursed_amount_for_grant(b.id)) then true else false end _failed,(b.amount-disbursed_amount_for_grant(b.id)) _undisbursed  from disbursements a
left join actual_disbursements c on c.disbursement_id = a.id
inner join grants b on b.id=a.grant_id
where a.id=%disbursementId% ', 'WARN', true, 4);
INSERT INTO public.workflow_validations (id, object, validation_query, type, active, message_id) VALUES (6, 'DISBURSEMENT', 'select case when %amount_to_record%>requested_amount then true else false end _failed,requested_amount from disbursements where id=%disbursementId%;', 'WARN', true, 5);
INSERT INTO public.workflow_validations (id, object, validation_query, type, active, message_id) VALUES (7, 'GRANT', 'select not exists(select * from grant_specific_section_attributes a
inner join grant_string_attributes b on a.id=b.section_attribute_id
where b.grant_id=%grantId% and a.field_type=''kpi'') _failed;', 'INFO', true, 6);
INSERT INTO public.workflow_validations (id, object, validation_query, type, active, message_id) VALUES (8, 'GRANT', 'select not exists(select * from grant_documents a
where a.grant_id=%grantId%) _failed;', 'INFO', true, 7);
