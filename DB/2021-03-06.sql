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
