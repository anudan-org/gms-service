CREATE SEQUENCE public.grant_assignments_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE public.grant_assignments
(
    id bigint NOT NULL DEFAULT nextval('grant_assignments_id_seq'::regclass),
    anchor boolean,
    assignments bigint,
    grant_id bigint,
    state_id bigint,
    CONSTRAINT grant_assignments_pkey PRIMARY KEY (id)
);

insert into grant_assignments (anchor, assignments,grant_id,state_id) values(false,14,1,2);
insert into grant_assignments (anchor, assignments,grant_id,state_id) values(false,2,1,3);

