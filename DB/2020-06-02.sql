create sequence actual_disbursements_seq start 1;

create table actual_disbursements(
    id bigint not null default nextval('actual_disbursements_seq'::regclass),
    disbursement_date timestamp,
    actual_amount real,
    note text,
    disbursement_id bigint,
    created_at timestamp,
    created_by bigint,
    updated_at timestamp,
    updated_by bigint,
    constraint act_disb_pk primary key(id)
);