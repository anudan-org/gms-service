--changes on 23-Sep added by Raghavendra
INSERT INTO workflow_validations
(id, "object", validation_query, "type", active, message_id)
VALUES(13, 'CLOSURE', 'select case when reason is null then true else false end _failed  from grant_closure where id=%closureId%', 'WARN', true, 18);

INSERT INTO public.messages
(id, message, subject)
VALUES(18, 'Closure Reason is empty',null);

--changes on 10-Oct
UPDATE public.messages
SET message='The requested refund amount cannot be more than the unspent amount.', subject=NULL
WHERE id=16;

UPDATE public.workflow_validations
SET "object"='CLOSURE', validation_query='select case when refund_amount>(disbursed-actual_spent) then true else false end _failed from ( select a.grant_id,b.refund_amount,b.actual_spent,sum(actual_amount) disbursed from grant_closure a inner join grants b on b.id=a.grant_id inner join disbursements c on c.grant_id=b.id inner join actual_disbursements d on d.disbursement_id=c.id where a.id=%closureId% group by a.grant_id,b.refund_amount,b.actual_spent) Y', "type"='WARN', active=true, message_id=16
WHERE id=11;

INSERT INTO public.workflow_validations
(id, "object", validation_query, "type", active, message_id)
VALUES(14, 'CLOSURE', 'select case when actuals > refund_amount then true else false end _failed from ( select a.grant_id,b.refund_amount,X.actuals from grant_closure a inner join grants b on b.id=a.grant_id inner join (select a.grant_id,sum(c.amount) actuals from  grant_closure a 	inner join grants b on b.id=a.grant_id 	inner join actual_refunds c on c.associated_grant_id=a.grant_id where a.id=%closureId% group by a.grant_id) X on X.grant_id=a.grant_id where a.id=%closureId% group by a.grant_id,b.refund_amount,X.actuals) Y', 'WARN', true, 19);

INSERT INTO public.messages
(id, message, subject)
VALUES(19, 'The actual refund amount received cannot be more than the requested refund amount.', NULL);

10/11/2022

ALTER TABLE grant_closure ADD refund_amount double precision;
ALTER TABLE grant_closure ADD refund_reason text ;
ALTER TABLE grant_closure ADD actual_spent double precision;
ALTER TABLE grant_closure ADD interest_earned double precision;
ALTER TABLE public.closure_snapshot ADD interest_earned bigint;

closure_snapshot

update grant_closure c set refund_amount=g.refund_amount, refund_reason=g.refund_reason , actual_spent=g.actual_spent ,
interest_earned =g.interest_earned 
from grants g where g.id  = c.grant_id ;


ALTER TABLE grants drop column refund_amount ;
ALTER TABLE grants drop column refund_reason ;
ALTER TABLE grants drop column actual_spent;
ALTER TABLE grants drop column interest_earned ;

UPDATE public.workflow_validations
SET "object"='CLOSURE', validation_query='select case when refund_amount>(disbursed-actual_spent) then true else false end _failed from ( select a.grant_id,a.refund_amount,a.actual_spent,sum(actual_amount) disbursed from grant_closure a inner join grants b on b.id=a.grant_id inner join disbursements c on c.grant_id=b.id inner join actual_disbursements d on d.disbursement_id=c.id where a.id=%closureId% group by a.grant_id,a.refund_amount,a.actual_spent) Y', "type"='WARN', active=true, message_id=16
WHERE id=11;

UPDATE public.workflow_validations
SET "object"='CLOSURE', validation_query='select case when actuals > refund_amount then true else false end _failed from ( select a.grant_id,a.refund_amount,X.actuals from grant_closure a inner join grants b on b.id=a.grant_id inner join (select a.grant_id,sum(c.amount) actuals from  grant_closure a 	inner join grants b on b.id=a.grant_id 	inner join actual_refunds c on c.associated_grant_id=a.grant_id where a.id=%closureId% group by a.grant_id) X on X.grant_id=a.grant_id where a.id=%closureId% group by a.grant_id,a.refund_amount,X.actuals) Y', "type"='WARN', active=true, message_id=19
WHERE id=14;



UPDATE public.workflow_validations
SET "object"='CLOSURE', validation_query='select case when refund_amount>(disbursed+interest-actual_spent) then true else false end _failed from ( select a.grant_id,a.refund_amount,a.actual_spent, disbursed_amount_for_grant(a.grant_id) disbursed , coalesce(a.interest_earned,0) interest from grant_closure a inner join grants b on b.id=a.grant_id where a.id=%closureId% ) Y', "type"='WARN', active=true, message_id=16
WHERE id=11;


UPDATE public.data_export_config
SET "name"='Disbursements', description=NULL, query='select a.grant_id as "Anudan Grant Identifier", b.reference_no as "Grant Reference No", a.id as "Anudan Disbursement Identifier", requested_amount::text as "Requested Amount", a.reason as "Request Note", f.actual_amount::text as "Disbursement Amount",f.other_sources as "Funds from other Sources", to_char(f.disbursement_date::DATE,''dd-mm-yyyy'') as "Date" from disbursements a inner join grants b on b.id=a.grant_id  inner join workflow_statuses c on c.id=b.grant_status_id inner join workflow_statuses d on d.id=a.status_id left join actual_disbursements f on f.disbursement_id=a.id where ((select count(*) from disbursement_history where id=a.id)>0) and b.grantor_org_id=%tenantId% and b.deleted=false', tenant=NULL, category='ACTIVE_GRANTS_DETAILS', params=NULL
WHERE id=2;

--5/12/2022
alter table notifications add column closure_id bigint


INSERT INTO public.data_export_config
(id, "name", description, query, tenant, category, params)
VALUES(10, 'Closure', NULL, 'select a.grant_id as "Anudan Grant Identifier", b.reference_no as "Grant Reference No", a.id as "Anudan Closure Identifier", 
r.reason as "Closure Reason", a.description as "Description", disbursed_amount_for_grant(a.grant_id)::text as "Amount Received", a.interest_earned::text as "Interest Earned",  a.actual_spent::text as "Actual Spent",
a.refund_amount::text as "Refund Requested",
ar.amount::text as "Actual Refund Received",
to_char(ar.refund_date::date, ''dd/mon/yyyy'') as "Refund Received Date",
d.name as "Closure Status"
from grant_closure a 
inner join grants b on b.id=a.grant_id  
inner join workflow_statuses c on c.id=b.grant_status_id 
inner join workflow_statuses d on d.id=a.status_id
left join closure_reasons r on r.id  = a.reason 
left join actual_refunds ar on ar.associated_grant_id = a.grant_id
where b.grantor_org_id=%tenantId% and a.deleted=false', NULL, 'ACTIVE_GRANTS_DETAILS', NULL);

--19/12/2022 - dev updated. UAT to be updated.

INSERT INTO workflows
(id, created_at, created_by, description, "name", "object", updated_at, updated_by, granter_id)
VALUES(nextval('workflows_id_seq'), now(), 'System', 'Anudan new default closure workflow for External grant types', 'Anudan new default closure workflow for External grant types', 'GRANTCLOSURE', NULL, NULL, 11);


INSERT INTO workflow_statuses
(id, created_at, created_by, display_name, initial, internal_status, "name", terminal, updated_at, updated_by, verb, workflow_id)
VALUES(nextval('workflow_statuses_id_seq'), now(), 'System', 'Draft', true, 'DRAFT', 'Draft', false, NULL, NULL, NULL, 24);

INSERT INTO workflow_statuses
(id, created_at, created_by, display_name, initial, internal_status, "name", terminal, updated_at, updated_by, verb, workflow_id)
VALUES(nextval('workflow_statuses_id_seq'), now(), 'System', 'Program Review', false, 'REVIEW', 'Program Review', false, NULL, NULL, NULL, 24);

INSERT INTO workflow_statuses
(id, created_at, created_by, display_name, initial, internal_status, "name", terminal, updated_at, updated_by, verb, workflow_id)
VALUES(nextval('workflow_statuses_id_seq'), now(), 'System', 'Finance Review', false, 'REVIEW', 'Finance Review', false, NULL, NULL, NULL, 24);

INSERT INTO workflow_statuses
(id, created_at, created_by, display_name, initial, internal_status, "name", terminal, updated_at, updated_by, verb, workflow_id)
VALUES(nextval('workflow_statuses_id_seq'), now(), 'System', 'Partner Review', false, 'ACTIVE', 'Partner Review', false, NULL, NULL, NULL, 24);

INSERT INTO workflow_statuses
(id, created_at, created_by, display_name, initial, internal_status, "name", terminal, updated_at, updated_by, verb, workflow_id)
VALUES(nextval('workflow_statuses_id_seq'), now(), 'System', 'MLE Review', false, 'REVIEW', 'MLE Review', false, NULL, NULL, NULL, 24);

INSERT INTO workflow_statuses
(id, created_at, created_by, display_name, initial, internal_status, "name", terminal, updated_at, updated_by, verb, workflow_id)
VALUES(nextval('workflow_statuses_id_seq'), now(), 'System', 'Finance Review', false, 'REVIEW', 'Finance Review', false, NULL, NULL, NULL, 24);

INSERT INTO workflow_statuses
(id, created_at, created_by, display_name, initial, internal_status, "name", terminal, updated_at, updated_by, verb, workflow_id)
VALUES(nextval('workflow_statuses_id_seq'), now(), 'System', 'CEO/ED Approval', false, 'REVIEW', 'CEO/ED Approval', false, NULL, NULL, NULL, 24);

INSERT INTO workflow_statuses
(id, created_at, created_by, display_name, initial, internal_status, "name", terminal, updated_at, updated_by, verb, workflow_id)
VALUES(nextval('workflow_statuses_id_seq'), now(), 'System', 'Closed', false, 'CLOSED', 'Closed', true, NULL, NULL, NULL, 24);
----
select * from workflow_statuses where workflow_id=24

INSERT INTO workflow_status_transitions
(id, "action", created_at, created_by, note_required, updated_at, updated_by, from_state_id, role_id, to_state_id, workflow_id, seq_order, is_forward_direction, allow_transition_on_validation_warning)
VALUES(nextval('workflow_status_transitions_id_seq'), 'Hub review', now(), 'System', true, NULL, NULL, 111, NULL, 112, 24, 1, true, true);

INSERT INTO workflow_status_transitions
(id, "action", created_at, created_by, note_required, updated_at, updated_by, from_state_id, role_id, to_state_id, workflow_id, seq_order, is_forward_direction, allow_transition_on_validation_warning)
VALUES(nextval('workflow_status_transitions_id_seq'), 'Finance review', now(), 'System', true, NULL, NULL, 112, NULL, 113, 24, 2, true, true);

INSERT INTO workflow_status_transitions
(id, "action", created_at, created_by, note_required, updated_at, updated_by, from_state_id, role_id, to_state_id, workflow_id, seq_order, is_forward_direction, allow_transition_on_validation_warning)
VALUES(nextval('workflow_status_transitions_id_seq'), 'Partner submission', now(), 'System', true, NULL, NULL, 113, NULL, 114, 24, 3, true, true);

INSERT INTO workflow_status_transitions
(id, "action", created_at, created_by, note_required, updated_at, updated_by, from_state_id, role_id, to_state_id, workflow_id, seq_order, is_forward_direction, allow_transition_on_validation_warning)
VALUES(nextval('workflow_status_transitions_id_seq'), 'MLE review', now(), 'System', true, NULL, NULL, 114, NULL, 115, 24, 4, true, true);

INSERT INTO workflow_status_transitions
(id, "action", created_at, created_by, note_required, updated_at, updated_by, from_state_id, role_id, to_state_id, workflow_id, seq_order, is_forward_direction, allow_transition_on_validation_warning)
VALUES(nextval('workflow_status_transitions_id_seq'), 'Finance review 2', now(), 'System', true, NULL, NULL, 115, NULL, 116, 24, 5, true, true);

INSERT INTO workflow_status_transitions
(id, "action", created_at, created_by, note_required, updated_at, updated_by, from_state_id, role_id, to_state_id, workflow_id, seq_order, is_forward_direction, allow_transition_on_validation_warning)
VALUES(nextval('workflow_status_transitions_id_seq'), 'Final approval', now(), 'System', true, NULL, NULL, 116, NULL, 117, 24, 6, true, true);

INSERT INTO workflow_status_transitions
(id, "action", created_at, created_by, note_required, updated_at, updated_by, from_state_id, role_id, to_state_id, workflow_id, seq_order, is_forward_direction, allow_transition_on_validation_warning)
VALUES(nextval('workflow_status_transitions_id_seq'), 'Approved', now(), 'System', true, NULL, NULL, 117, NULL, 118, 24, 7, true, false);

--24/12/2022.. to be updated in dev and uat.
UPDATE public.app_config
SET config_name='CLOSURE_STATE_CHANGED_MAIL_MESSAGE', config_value='<p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <a class="go-to-closure-class" href="%GRANTCLOSURE_LINK%">Grant Closure Request Note for %GRANT_NAME%</a> to review.</p> <p>Grant Closure Request workflow status changed for <strong>%GRANTEE%</strong></p> <p style="color: #000;"><strong>Change Summary: </strong></p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Closure Request for:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;"><span style="font-size: 14px; color: #000; font-weight: normal;">"%GRANT_NAME%"</span> </span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <p>This is an automatically generated email. Please do not reply to this message.</p>', description=NULL, configurable=false, "key"=NULL, "type"=NULL
WHERE id=41;


INSERT INTO public.data_export_config
(id, "name", description, query, tenant, category, params)
VALUES(nextval('data_export_seq'), 'Closure - Workflow', NULL, 'select g.id as "Anudan Grant Identifier",c.id as "Anudan Closure Identifier", cs.id as "Anudan Workflow Identifier",g.reference_no as "Grant Reference No",
g.name as "Grant Name",
(select concat(first_name,'' '',last_name) from users where id=moved_by) as "Moved By",
(select name from workflow_statuses where id=cs.from_state_id) as "From State",
(select name from workflow_statuses where id=cs.to_state_id) as "To State",
(select concat(first_name,'' '',last_name) from users where id=assigned_to_id) as "Moved To",
to_char(cs.moved_on::TIMESTAMP,''dd-mm-yyyy hh12:mi AM'')::text as "Moved On",
cs.from_note as "Note"
from closure_snapshot cs
inner join grant_closure c on c.id = cs.closure_id 
inner join grants g on g.id=c.grant_id 
where g.grantor_org_id=%tenantId% and c.deleted=false
order by g.id,cs.moved_on,cs.id', NULL, 'ACTIVE_GRANTS_DETAILS', NULL);

