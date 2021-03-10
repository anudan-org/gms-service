CREATE SEQUENCE data_export_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE data_export_config
(
    id bigint NOT NULL DEFAULT nextval('data_export_seq'::regclass),
    name text COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    query text COLLATE pg_catalog."default",
    tenant bigint,
    category text COLLATE pg_catalog."default",
    CONSTRAINT data_export_pkey PRIMARY KEY (id)
);

insert into data_export_config (name,query,category) values ('Active Grants','select b.name as organization_name,a.reference_no,a.name as grant_name,d.name as grant_type,a.start_date,a.end_date,a.amount, case when d.internal then b.name else (select name from organizations where id=a.organization_id) end as implementing_organization, a.representative as implementing_org_rep, case when orig_grant_id is not null then ''Yes'' else ''No'' end as is_amended from grants a inner join organizations b on b.id=a.grantor_org_id inner join workflow_statuses c on c.id=a.grant_status_id inner join grant_types d on d.id=a.grant_type_id where b.id=%tenantId% and c.internal_status=''ACTIVE''','ACTIVE_GRANTS_DETAILS');
insert into data_export_config (name,query,category) values('Approved Reports','select b.reference_no,e.name as grant_type,a.name as report_name,a.start_date as report_start_date,a.end_date as report_end_date,a.due_date as report_due_date,(select name from workflow_statuses where id=a.status_id) as report_status from reports a inner join grants b on b.id=a.grant_id inner join grant_types e on e.id=b.grant_type_id inner join workflow_statuses c on c.id=b.grant_status_id inner join workflow_statuses d on d.id=a.status_id where b.grantor_org_id=%tenantId% and c.internal_status=''ACTIVE'' and d.internal_status=''CLOSED''','ACTIVE_GRANTS_DETAILS');
insert into data_export_config (name,query,category) values('Recorded Disbursements','select b.reference_no,requested_amount,f.actual_amount,a.reason,f.disbursement_date from disbursements a inner join grants b on b.id=a.grant_id inner join workflow_statuses c on c.id=b.grant_status_id inner join workflow_statuses d on d.id=a.status_id inner join actual_disbursements f on f.disbursement_id=a.id where c.internal_status=''ACTIVE'' and d.internal_status=''CLOSED'' and b.grantor_org_id=%tenantId%','ACTIVE_GRANTS_DETAILS');