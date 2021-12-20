alter table messages add column subject text;

CREATE SEQUENCE hygiene_check_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

    CREATE TABLE hygiene_checks
    (
        id bigint NOT NULL DEFAULT nextval('hygiene_check_seq'::regclass),
        hygiene_query text COLLATE pg_catalog."default",
        active boolean,
        object character varying(20) COLLATE pg_catalog."default",
        message_id bigint,
        name character varying(500) COLLATE pg_catalog."default",
        description text COLLATE pg_catalog."default",
        scheduled_run character varying(50) COLLATE pg_catalog."default",
        CONSTRAINT hygiene_checks_pk PRIMARY KEY (id)
    );

    alter table messages add column subject text;