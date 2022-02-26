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