CREATE SEQUENCE reports_id_seq START 1;

create table reports (
    id bigint NOT NULL DEFAULT nextval('reports_id_seq'::regclass),
    name text,
    start_date timestamp,
    end_date timestamp,
    due_date timestamp,
    status_id bigint,
    created_at timestamp,
    created_by bigint,
    updated_at timestamp,
    updated_by bigint,
CONSTRAINT reports_pkey PRIMARY KEY (id));