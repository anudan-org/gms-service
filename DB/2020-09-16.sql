alter table grants add column amended boolean default false, add column orig_grant_id bigint,add column amend_grant_id bigint;
alter table grant_history add column amended boolean default false, add column orig_grant_id bigint,add column amend_grant_id bigint;
alter table grant_snapshot add column amended boolean default false, add column orig_grant_id bigint,add column amend_grant_id bigint;

alter table grants add column amendment_no integer default 0;
alter table grant_history add column amendment_no integer default 0;
alter table grant_snapshot add column amendment_no integer default 0;