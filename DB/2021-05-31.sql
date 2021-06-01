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
			(case when b.internal_status='ACTIVE' then 'false' else 'true' end)
		end
end
_do,  a.id tid,c.object,is_forward_direction,seq_order from workflow_status_transitions a
inner join workflow_statuses b on b.id=a.to_state_id
inner join workflows c on c.id=a.workflow_id
where is_forward_direction=true) X order by tid);


update workflow_status_transitions set allow_transition_on_validation_warning=(select _do from temp_validation_flag where tid=id);

drop table temp_validation_flag;

alter table workflow_validations add column message_id bigint;
