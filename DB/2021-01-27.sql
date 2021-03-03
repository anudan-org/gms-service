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