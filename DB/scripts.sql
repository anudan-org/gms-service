-- 2-April-2019
create sequence organizations_id_seq;

alter table organizations alter column id set default nextval('public.organizations_id_seq');

alter sequence organizations_id_seq owned by organizations.id;

create sequence rfps_id_seq;

alter table rfps alter column id set default nextval('public.rfps_id_seq');

alter sequence rfps_id_seq owned by rfps.id;

