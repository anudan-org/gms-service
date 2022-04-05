alter table grants add column refund_amount double precision;
alter table grants add column refund_reason text;

CREATE SEQUENCE actual_refunds_id_seq;
CREATE TABLE actual_refunds
(
    id bigint NOT NULL DEFAULT nextval('actual_refunds_id_seq'::regclass),
    amount double precision,
    note text,
    refund_date timestamp,
    created_date timestamp,
    created_by bigint,
    refund_attachment text,
    grant_id bigint,
    CONSTRAINT actual_refunds_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE closure_documents_id_seq;
CREATE TABLE closure_documents
(
    id bigint NOT NULL DEFAULT nextval('closure_documents_id_seq'::regclass),
    location text,
    uploaded_on timestamp,
    uploaded_by bigint,
    name text,
    extension varchar(10),
    closure_id bigint,
    CONSTRAINT closure_documents_pkey PRIMARY KEY (id)
);

alter table actual_refunds rename column grant_id to associated_grant_id;

alter table closure_specific_sections add column is_refund boolean default false;

alter table closure_specific_sections add column is_system_generated boolean default false;

alter table grant_specific_sections add column is_system_generated boolean default false;

alter table report_specific_sections add column is_system_generated boolean default false;

alter table closure_snapshot
add column grant_refund_amount double precision,
add column grant_refund_reason text,
add column actual_refunds text;

update report_specific_sections  set is_system_generated=true where section_name='Project Indicators' or section_name='Project Funds';

alter table grants add column actual_spent double precision;

insert into messages(id,message) values(16,'The request refund amount cannot be greater than the available amount.');
insert into workflow_validations (id,object,validation_query,type,active,message_id) values(11,'CLOSURE','select case when b>(a) then true else false end _failed from ( select id,sum(available) a,sum(requested_refund) b,sum(actual_refunds) c from ( select a.id,sum(ad.actual_amount)-g.actual_spent available,g.refund_amount requested_refund,0 actual_refunds from grant_closure a inner join grants g on g.id=a.grant_id inner join disbursements d on d.grant_id=g.id inner join actual_disbursements ad on ad.disbursement_id=d.id inner join actual_refunds ar on ar.associated_grant_id=g.id where a.id=%closureId% group by a.id,g.id,g.actual_spent,g.refund_amount union select %closureId%,0,0, sum(amount) from grant_closure a inner join actual_refunds b on b.associated_grant_id=a.grant_id where a.id=%closureId% group by a.grant_id)X group by X.id) Y','WARN',true,16)
