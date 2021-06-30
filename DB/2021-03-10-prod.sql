insert into workflow_statuses(display_name,initial,internal_status,name,terminal,workflow_id) values('Program Review',false,'REVIEW','Program Review',false,16);
update workflow_status_transitions set to_state_id=(select id from workflow_statuses where workflow_id=16 and name='Program Review') where id=125;
insert into workflow_status_transitions(action,note_required,from_state_id,to_state_id,workflow_id,seq_order) values('Submit',true,(select id from workflow_statuses where workflow_id=16 and name='Program Review'),76,16,2);
update workflow_status_transitions set seq_order=3 where id=126;
update workflow_status_transitions set seq_order=4 where id=127;
update workflow_status_transitions set to_state_id=(select id from workflow_statuses where workflow_id=16 and name='Program Review') where id=128;
insert into workflow_status_transitions(action,note_required,from_state_id,to_state_id,workflow_id,seq_order) values('Request Modifications',true,(select id from workflow_statuses where workflow_id=16 and name='Program Review'),75,16,53);
