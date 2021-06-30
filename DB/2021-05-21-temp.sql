insert into workflows(created_at,created_by,description,name,object,granter_id)
select now(),created_by,'Sustainplus Internal Grant Workflow','Sustainplus Internal Grant Workflow',object,granter_id from workflows where id=6;

insert into workflow_statuses(created_at,created_by,display_name,name,initial,terminal,internal_status,verb,workflow_id) select created_at,created_by,display_name,name,initial,terminal,internal_status,verb,20 from workflow_statuses where workflow_id=6;

insert into workflow_status_transitions (action,created_at,created_by,note_required,from_state_id,to_state_id,workflow_id,seq_order)
select action,created_at,created_by,note_required,cini_grant_status(from_state_id,6,20),cini_grant_status(to_state_id,6,20),20,seq_order from workflow_status_transitions where workflow_id=6;

--REPORTS
insert into workflows(created_at,created_by,description,name,object,granter_id)
select now(),created_by,'Sustainplus Internal Report Workflow','Sustainplus Internal Report Workflow',object,granter_id from workflows where id=7;

insert into workflow_statuses(created_at,created_by,display_name,name,initial,terminal,internal_status,verb,workflow_id) select created_at,created_by,display_name,name,initial,terminal,internal_status,verb,21 from workflow_statuses where workflow_id=7;

insert into workflow_status_transitions (action,created_at,created_by,note_required,from_state_id,to_state_id,workflow_id,seq_order)
select action,created_at,created_by,note_required,cini_report_status(from_state_id,7,21),cini_report_status(to_state_id,7,21),21,seq_order from workflow_status_transitions where workflow_id=7;


--DISBURSEMENTS
insert into workflows(created_at,created_by,description,name,object,granter_id)
select now(),created_by,'Sustainplus Internal Disbursement Workflow','Sustainplus Internal Disbursement Workflow',object,granter_id from workflows where id=9;

insert into workflow_statuses(created_at,created_by,display_name,name,initial,terminal,internal_status,verb,workflow_id) select created_at,created_by,display_name,name,initial,terminal,internal_status,verb,22 from workflow_statuses where workflow_id=9;

insert into workflow_status_transitions (action,created_at,created_by,note_required,from_state_id,to_state_id,workflow_id,seq_order)
select action,created_at,created_by,note_required,cini_disbursement_status(from_state_id,9,22),cini_disbursement_status(to_state_id,9,22),22,seq_order from workflow_status_transitions where workflow_id=9;

insert into grant_type_workflow_mapping(grant_type_id,workflow_id,_default) values(4,20,false),(4,21,false),(4,22,false);
