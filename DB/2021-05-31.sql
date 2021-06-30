CREATE SEQUENCE workflow_validations_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;


CREATE TABLE workflow_validations
(
    id bigint NOT NULL DEFAULT nextval('workflow_validations_id_seq'::regclass),
    object character varying(100),
    validation_query text,
    type varchar(10),
    active boolean DEFAULT true,
    CONSTRAINT workflow_validations_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE messages_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;


CREATE TABLE messages
(
    id bigint NOT NULL DEFAULT nextval('messages_id_seq'::regclass),
    message text,
    CONSTRAINT messages_pkey PRIMARY KEY (id)
);

alter table workflow_status_transitions add column is_forward_direction boolean,add column allow_transition_on_validation_warning boolean;

update workflow_status_transitions set is_forward_direction=true where seq_order<50;
update workflow_status_transitions set is_forward_direction=false where seq_order>=50;

--
create table temp_validation_flag as (select case when _do='true' then TRUE else FALSE end as _do,tid,seq_order from (
select
case when object='GRANT' then
		(case when b.internal_status='ACTIVE' then 'false' else 'true' end)
	else
		case when object='REPORT' then
			(case when b.internal_status='CLOSED' then 'false' else 'true' end)
		else
			(case when b.internal_status='ACTIVE' or b.internal_status='CLOSED' then 'false' else 'true' end)
		end
end
_do,  a.id tid,c.object,is_forward_direction,seq_order from workflow_status_transitions a
inner join workflow_statuses b on b.id=a.to_state_id
inner join workflows c on c.id=a.workflow_id
where is_forward_direction=true) X order by tid);


update workflow_status_transitions set allow_transition_on_validation_warning=(select _do from temp_validation_flag where tid=id);

drop table temp_validation_flag;

alter table workflow_validations add column message_id bigint;

alter table grants drop constraint fkfxhc0yhlrne4obtxvc11skonn;
alter table grant_history drop constraint fkfxhc0yhlrne4obtxvc11skonn;

INSERT INTO public.messages (id, message) VALUES (1, 'Planned Disbursement of Project Funds is not equal to the Grant Amount of %grantamount%');
INSERT INTO public.messages (id, message) VALUES (2, '%_for% has missing header information.');
INSERT INTO public.messages (id, message) VALUES (3, 'There are no Planned Funds for this project');
INSERT INTO public.messages (id, message) VALUES (4, 'Requested amount cannot be greater than project''s undisbursed amount');
INSERT INTO public.messages (id, message) VALUES (5, 'The disbursed total cannot be greater than the approved amount of %requested_amount%');
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

-- FOR CINI MIGRATION
update organizations set name=(select name from organizations where id=69) where id=11;

update organizations set name='Collectives of Integrated Livelihood Initiatives' where id=11;
---CINI POST MIGRATION - After a WEEK
drop function cini_disbursement_status;
drop function cini_grant_status;
drop function cini_report_status;
drop function get_owner_grant;
drop function get_owner_report;
drop function get_owner_disbursement;
drop procedure migrate_cini;
