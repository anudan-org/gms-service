

create sequence mail_id_seq start 1;
create table mail_logs(
    id bigint not null default nextval('mail_id_seq'::regclass),
    sent_on timestamp,
    cc text,
    sent_to text,
    msg text,
    subject text,
    status boolean,
    constraint mail_log_pk primary key(id)
);