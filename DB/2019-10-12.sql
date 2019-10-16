CREATE SEQUENCE grant_snapshot_id_seq START 1;
create table grant_snapshot(
	id bigint NOT NULL DEFAULT nextval('grant_snapshot_id_seq'::regclass),
	assigned_to_id bigint,
	grant_id bigint,
	grantee text,
	string_attributes text,
	name text,
	description text,
	amount double precision,
	status_name character varying(255),
	start_date timestamp without time zone,
	end_date timestamp without time zone,
	representative character varying(255),
	CONSTRAINT grant_snapshot_pkey PRIMARY KEY (id)
);

alter table grant_snapshot drop column status_name;
alter table grant_snapshot add column grant_status_id bigint;
