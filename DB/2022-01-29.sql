insert into granter_closure_templates(name,description,published,private_to_closure,granter_id,default_template) select 'Default Anudan Grant Closure Template','Default Anudan Grant Closure Template',true,false,id,true from granters;

delete from workflow_status_transitions
where workflow_id in (select id from workflows where object='GRANT-CLOSURE') and seq_order>49;

insert into workflows(created_at,created_by,description,name,object,granter_id) values(now(),'System','Anudan Support default closure workflow for External grant types','Anudan Support default closure workflow for External grant types','GRANTCLOSURE',11);

insert into workflow_statuses(created_at,created_by,display_name,initial,internal_status,name,terminal,workflow_id)
values (now(),'System','Initiated',true,'DRAFT','Initiated',false,23),
(now(),'System','Partner Review',false,'ACTIVE','Partner Review',false,23),
(now(),'System','Program Review',false,'REVIEW','Program Review',false,23),
(now(),'System','Finance Review',false,'REVIEW','Finance Review',false,23),
(now(),'System','Program Review [Refund]',false,'REVIEW','Program Review [Refund]',false,23),
(now(),'System','Partner Review [Refund]',false,'ACTIVE','Partner Review [Refund]',false,23),
(now(),'System','Finance Review [Refund]',false,'REVIEW','Finance Review [Refund]',false,23),
(now(),'System','GMT Review',false,'REVIEW','GMT Review',false,23),
(now(),'System','CEO Review',false,'REVIEW','CEO Review',false,23),
(now(),'System','Closed',false,'CLOSED','Closed',true,23);


insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Closure Initiated',true,100,101,1,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Submit Closure Documents',true,101,102,2,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Finance Review',true,102,103,3,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('GMT Review',true,103,107,8,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('CEO Approval',true,107,108,9,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Approve',true,108,109,10,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Request Refund',true,103,104,4,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Process Refund',true,104,105,5,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Review Refund',true,105,106,6,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('GMT Review',true,106,107,7,true,true,23);

insert into grant_type_workflow_mapping (grant_type_id,workflow_id,_default)
values(1,23,true);