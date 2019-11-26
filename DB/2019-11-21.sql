CREATE SEQUENCE granter_report_templates_id_seq INCREMENT 1 START 1;

CREATE TABLE granter_report_templates
(
    id bigint NOT NULL DEFAULT nextval('granter_report_templates_id_seq'::regclass),
    description text,
    granter_id bigint,
    name text,
    published boolean,
    private_to_report boolean DEFAULT true,
    CONSTRAINT granter_report_templates_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE granter_report_sections_id_seq INCREMENT 1 START 1;

CREATE TABLE granter_report_sections
(
    id bigint NOT NULL DEFAULT nextval('granter_report_sections_id_seq'::regclass),
    deletable boolean,
    section_name character varying(255) COLLATE pg_catalog."default",
    section_order integer,
    report_template_id bigint,
    granter_id bigint,
    CONSTRAINT granter_report_sections_pkey PRIMARY KEY (id),
    CONSTRAINT fkey_report_section_report_template FOREIGN KEY (report_template_id)
        REFERENCES granter_report_templates (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkey_report_section_granter FOREIGN KEY (granter_id)
        REFERENCES granters (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE SEQUENCE granter_report_section_attributes_id_seq INCREMENT 1 START 1;


CREATE TABLE granter_report_section_attributes
(
    id bigint NOT NULL DEFAULT nextval('granter_report_section_attributes_id_seq'::regclass),
    attribute_order integer,
    deletable boolean,
    extras text,
    field_name character varying(255),
    field_type character varying(255),
    required boolean,
    granter_id bigint,
    section_id bigint,
    CONSTRAINT granter_report_section_attributes_pkey PRIMARY KEY (id),
    CONSTRAINT fkey_report_section_attr_report_section FOREIGN KEY (section_id)
        REFERENCES granter_report_sections (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkey_report_section_attrib_granter FOREIGN KEY (granter_id)
        REFERENCES granters (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE SEQUENCE report_specific_sections_id_seq INCREMENT 1 START 1;

CREATE TABLE report_specific_sections
(
    id bigint NOT NULL DEFAULT nextval('report_specific_sections_id_seq'::regclass),
    deletable boolean,
    report_id bigint,
    report_template_id bigint,
    section_name character varying(255),
    section_order integer,
    granter_id bigint,
    CONSTRAINT report_specific_sections_pkey PRIMARY KEY (id),
    CONSTRAINT fkey_report_specific_section_granter FOREIGN KEY (granter_id)
        REFERENCES granters (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE SEQUENCE report_specific_section_attributes_id_seq INCREMENT 1 START 1;


CREATE TABLE report_specific_section_attributes
(
    id bigint NOT NULL DEFAULT nextval('report_specific_section_attributes_id_seq'::regclass),
    attribute_order integer,
    deletable boolean,
    extras text,
    field_name text,
    field_type text,
    required boolean,
    granter_id bigint,
    section_id bigint,
    CONSTRAINT report_specific_section_attributes_pkey PRIMARY KEY (id),
    CONSTRAINT fkey_report_sp_sec_attr_granter FOREIGN KEY (granter_id)
        REFERENCES granters (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkey_report_sp_sec_attr_section FOREIGN KEY (section_id)
        REFERENCES report_specific_sections (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


CREATE SEQUENCE report_string_attributes_id_seq INCREMENT 1 START 1;

CREATE TABLE report_string_attributes
(
    id bigint NOT NULL DEFAULT nextval('report_string_attributes_id_seq'::regclass),
    frequency character varying(255),
    target character varying(255),
    value text,
    report_id bigint,
    section_id bigint,
    section_attribute_id bigint,
    CONSTRAINT report_string_attributes_pkey PRIMARY KEY (id),
    CONSTRAINT fkey_string_section_attribs FOREIGN KEY (section_attribute_id)
        REFERENCES report_specific_section_attributes (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkey_report_string_report FOREIGN KEY (report_id)
        REFERENCES reports (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkey_string_section FOREIGN KEY (section_id)
        REFERENCES report_specific_sections (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


CREATE SEQUENCE report_string_attribute_attachments_id_seq INCREMENT 1 START 1;

CREATE TABLE report_string_attribute_attachments
(
    id bigint NOT NULL DEFAULT nextval('report_string_attribute_attachments_id_seq'::regclass),
    created_by character varying(255),
    created_on timestamp without time zone,
    description text,
    location character varying(255),
    name text,
    title character varying(255),
    type character varying(255),
    updated_by character varying(255),
    updated_on timestamp without time zone,
    version integer,
    report_string_attribute_id bigint,
    CONSTRAINT report_string_attribute_attachments_pkey PRIMARY KEY (id),
    CONSTRAINT fkey_string_attach_attrib FOREIGN KEY (report_string_attribute_id)
        REFERENCES report_string_attributes (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

alter table granter_report_templates add column default_template boolean default false;

INSERT INTO public.granter_report_templates(
	description, granter_id, name, published, private_to_report, default_template)
	VALUES ('Default Anudan report template', 2, 'Default Anudan Report Template', true, false, true);

INSERT INTO granter_report_sections(
	deletable, section_name, section_order, report_template_id, granter_id)
	VALUES (true, 'Measurements', 1, (select id from granter_report_templates where default_template=true), 2);

alter table reports add column note text, add column note_added timestamp,add column note_added_by varchar(255);
