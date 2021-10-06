create sequence grant_closures_seq start 1;
create table grant_closures(
	id bigint not null default nextval('grant_closures_seq'::regclass),
	reason text,
	template_id bigint,
	grant_id bigint,
	moved_on timestamp,
	create_by bigint,
    created_at timestamp,
	update_by bigint,
	updated_at timestamp,
	constraint grant_closure_pk primary key(id)
);