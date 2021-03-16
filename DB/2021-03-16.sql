CREATE SEQUENCE grant_tags_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE grant_tags
(
    id bigint NOT NULL DEFAULT nextval('grant_tags_seq'::regclass),
    grant_id bigint,
    org_tag_id bigint,
    CONSTRAINT grant_tags_pkey PRIMARY KEY (id)
);