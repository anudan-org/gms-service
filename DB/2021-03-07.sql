CREATE SEQUENCE org_tags_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE org_tags
(
    id bigint NOT NULL DEFAULT nextval('org_tags_seq'::regclass),
    name text,
    tenant bigint,
    CONSTRAINT org_tags_pkey PRIMARY KEY (id)
);