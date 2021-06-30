CREATE SEQUENCE public.grant_types_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE public.grant_types
(
    id bigint NOT NULL DEFAULT nextval('grant_types_id_seq'::regclass),
    name text,
    description text,
    internal boolean default false,
    CONSTRAINT grant_type_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE public.grant_type_workflow_mapping_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE public.grant_type_workflow_mapping
(
    id bigint NOT NULL DEFAULT nextval('grant_type_workflow_mapping_id_seq'::regclass),
    grant_type_id bigint,
    workflow_id bigint,
    internal boolean default false,
    CONSTRAINT grant_type_workflow_pkey PRIMARY KEY (id)
);

--alter table grants drop column internal;
alter table grants add column grant_type_id bigint;
alter table grant_types add column granter_id bigint;
alter table grant_types add column color_code varchar(50);
alter table workflows add column _default boolean default false;
alter table workflows add column internal boolean default false;
update workflows set _default=true;


insert into workflows(name,description,object,_default,internal,granter_id) values('Sustainplus Direct Implementation Report Workflow','Sustainplus Direct Implementation Report Workflow','REPORT',false,true,11);
insert into workflow_statuses(display_name,initial,internal_status,name,terminal,workflow_id) values('Draft',true,'DRAFT','Draft',false,(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'));
insert into workflow_statuses(display_name,initial,internal_status,name,terminal,workflow_id) values('Finance Review',false,'REVIEW','Finance Review',false,(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'));
insert into workflow_statuses(display_name,initial,internal_status,name,terminal,workflow_id) values('Hub Review',false,'REVIEW','Hub Review',false,(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'));
insert into workflow_statuses(display_name,initial,internal_status,name,terminal,workflow_id) values('Approved',false,'CLOSED','Approved',true,(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'));
insert into workflow_status_transitions(action,note_required,from_state_id,to_state_id,workflow_id,seq_order) values('Submit',true,(select id from workflow_statuses where name='Draft' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflow_statuses where name='Finance Review' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'),1);
insert into workflow_status_transitions(action,note_required,from_state_id,to_state_id,workflow_id,seq_order) values('Submit',true,(select id from workflow_statuses where name='Finance Review' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflow_statuses where name='Hub Review' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'),2);
insert into workflow_status_transitions(action,note_required,from_state_id,to_state_id,workflow_id,seq_order) values('Submit',true,(select id from workflow_statuses where name='Hub Review' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflow_statuses where name='Approved' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'),3);
insert into workflow_status_transitions(action,note_required,from_state_id,to_state_id,workflow_id,seq_order) values('Request Modifications',true,(select id from workflow_statuses where name='Finance Review' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflow_statuses where name='Draft' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'),51);
insert into workflow_status_transitions(action,note_required,from_state_id,to_state_id,workflow_id,seq_order) values('Request Modifications',true,(select id from workflow_statuses where name='Hub Review' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflow_statuses where name='Finance Review' and workflow_id=(select id from workflows where name='Sustainplus Direct Implementation Report Workflow')),(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'),52);


insert into grant_types(name,description,internal,granter_id,color_code) values('Internal Implementation','Implemented via internal team',true,11,'#fffbf6');
insert into grant_type_workflow_mapping(grant_type_id,workflow_id,internal) values((select id from grant_types where name='Internal Implementation' and granter_id=11),(select id from workflows where name='Sustainplus Direct Implementation Report Workflow'),true);
insert into grant_type_workflow_mapping(grant_type_id,workflow_id,internal) values((select id from grant_types where name='Internal Implementation' and granter_id=11),(select id from workflows where granter_id=11 and object='GRANT' and _default=true),true);
insert into grant_type_workflow_mapping(grant_type_id,workflow_id,internal) values((select id from grant_types where name='Internal Implementation' and granter_id=11),(select id from workflows where granter_id=11 and object='DISBURSEMENT' and _default=true),true);