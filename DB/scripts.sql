-- 2-April-2019
create sequence organizations_id_seq;

alter table organizations alter column id set default nextval('public.organizations_id_seq');

alter sequence organizations_id_seq owned by organizations.id;

create sequence rfps_id_seq;

alter table rfps alter column id set default nextval('public.rfps_id_seq');

alter sequence rfps_id_seq owned by rfps.id;

-- 4-April-2019
create table grant_kpis
(
	id bigserial not null
		constraint grant_kpis_pk
			primary key,
	title TEXT,
	description TEXT,
	is_scheduled boolean default false,
	periodicity int,
	periodicity_unit varchar(15),
	status varchar(255),
	created_at TIMESTAMP,
	created_by varchar(255)
);

create table grant_quantitative_kpi_data
(
	id bigserial not null
		constraint grant_quantitative_kpi_data_pk
			primary key,
	goal int,
	actuals int,
	grant_kpi_id bigint
		constraint grant_quantitative_kpi_data_grant_kpis_id_fk
			references grant_kpis,
	created_at timestamp,
	created_by varchar(40),
	updated_at timestamp,
	updated_by varchar(40)
);

alter table grant_kpis
	add kpi_type varchar(255);

create table grant_qualitative_kpi_data
(
	id bigserial not null
		constraint grant_qualitative_kpi_data_pk
			primary key,
	actuals TEXT,
	grant_kpi_id bigint
		constraint grant_qualitative_kpi_data_grant_kpis_id_fk
			references grant_kpis,
	created_at timestamp,
	created_by varchar(40),
	updated_at timestamp,
	updated_by varchar(40)
);

alter table grant_quantitative_kpi_data
	add status TEXT default 'NOT SUBMITTED';

create table if not exists workflows
(
	id bigserial not null
		constraint worflows_pk
			primary key,
	workflow_name text,
	granter_id bigint
		constraint worflows_granters_id_fk
			references granters,
	created_at timestamp,
	created_by varchar(40),
	updated_at timestamp,
	updated_by varchar(40)
);

alter table workflows owner to apple;

alter table grants
	add substatus varchar(40);

alter table grants
	add start_date timestamp;

alter table grants
	add end_date timestamp;


-- 25 Jul 2019
alter table grant_qualitative_kpi_data alter column actuals type text using actuals::text;

-- Hotfix 1,2
