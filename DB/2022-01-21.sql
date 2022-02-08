delete from workflow_status_transitions
where workflow_id in (select id from workflows where object='DISBURSEMENT') and seq_order>49;
delete from workflow_status_transitions
where workflow_id in (select id from workflows where object='GRANT') and seq_order>49;
delete from workflow_status_transitions
where workflow_id in (select id from workflows where object='REPORT') and seq_order>49;
delete from workflow_status_transitions
where workflow_id in (select id from workflows where object='GRANTCLOSURE') and seq_order>49;