insert into granter_closure_templates(name,description,published,private_to_closure,granter_id,default_template) select 'Default Anudan Grant Closure Template','Default Anudan Grant Closure Template',true,false,id,true from granters;

delete from workflow_status_transitions
where workflow_id in (select id from workflows where object='GRANT-CLOSURE') and seq_order>49;

insert into workflows(created_at,created_by,description,name,object,granter_id) values(now(),'System','Anudan Support default closure workflow for External grant types','Anudan Support default closure workflow for External grant types','GRANTCLOSURE',11);

insert into workflow_statuses(created_at,created_by,display_name,initial,internal_status,name,terminal,workflow_id)
values (now(),'System','Initiated',true,'DRAFT','Initiated',false,23),
(now(),'System','Partner Review',false,'REVIEW','Partner Review',false,23),
(now(),'System','Program Review',false,'REVIEW','Program Review',false,23),
(now(),'System','Finance Review',false,'REVIEW','Finance Review',false,23),
(now(),'System','Program Review 2',false,'REVIEW','Program Review 2',false,23),
(now(),'System','Partner Review 2',false,'REVIEW','Partner Review 2',false,23),
(now(),'System','Finance Review 2',false,'REVIEW','Finance Review 2',false,23),
(now(),'System','GMT Review',false,'REVIEW','GMT Review',false,23),
(now(),'System','CEO Review',false,'REVIEW','CEO Review',false,23),
(now(),'System','Closed',false,'CLOSED','Closed',false,23);


insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Partner Review',true,101,102,1,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Program Review',true,102,103,2,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Finance Review',true,103,104,3,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('GMT Review',true,104,108,8,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('CEO Approval',true,108,109,9,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Approve',true,109,110,10,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Program Review 2',true,104,105,4,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Partner Review 2',true,105,106,5,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('Finance Review 2',true,106,107,6,true,true,23);
insert into workflow_status_transitions (action,note_required,from_state_id,to_state_id,seq_order,is_forward_direction,allow_transition_on_validation_warning,workflow_id) values('GMT Review',true,107,108,7,true,true,23);


