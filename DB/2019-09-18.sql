CREATE SEQUENCE public.grant_attrib_attachments_id_seq START 1;
CREATE TABLE public.grant_string_attribute_attachments
(
    id bigint NOT NULL DEFAULT nextval('grant_attrib_attachments_id_seq'::regclass),
    name text,
    description text,
    location text,
    version integer DEFAULT 1,
    title text,
    type text,
    created_on date,
    created_by text,
    updated_on date,
    updated_by text,
    PRIMARY KEY (id)
);

ALTER TABLE public.grant_string_attribute_attachments ADD COLUMN grant_string_attribute_id bigint;

