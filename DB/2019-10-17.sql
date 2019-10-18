alter table granter_grant_templates add column private_to_grant boolean default true;
update granter_grant_templates set private_to_grant=false where published = true;
