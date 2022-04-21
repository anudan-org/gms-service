alter table grants add column refund_amount double precision;
alter table grants add column refund_reason text;

CREATE SEQUENCE actual_refunds_id_seq;
CREATE TABLE actual_refunds
(
    id bigint NOT NULL DEFAULT nextval('actual_refunds_id_seq'::regclass),
    amount double precision,
    note text,
    refund_date timestamp,
    created_date timestamp,
    created_by bigint,
    refund_attachment text,
    grant_id bigint,
    CONSTRAINT actual_refunds_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE closure_documents_id_seq;
CREATE TABLE closure_documents
(
    id bigint NOT NULL DEFAULT nextval('closure_documents_id_seq'::regclass),
    location text,
    uploaded_on timestamp,
    uploaded_by bigint,
    name text,
    extension varchar(10),
    closure_id bigint,
    CONSTRAINT closure_documents_pkey PRIMARY KEY (id)
);

alter table actual_refunds rename column grant_id to associated_grant_id;

alter table closure_specific_sections add column is_refund boolean default false;

alter table closure_specific_sections add column is_system_generated boolean default false;

alter table grant_specific_sections add column is_system_generated boolean default false;

alter table report_specific_sections add column is_system_generated boolean default false;

alter table closure_snapshot
add column grant_refund_amount double precision,
add column grant_refund_reason text,
add column actual_refunds text;

update report_specific_sections  set is_system_generated=true where section_name='Project Indicators' or section_name='Project Funds';
update closure_specific_sections  set is_system_generated=true where section_name='Project Indicators' or section_name='Project Funds';
update report_specific_sections  set is_system_generated=true where section_name='Project Indicators' or section_name='Project Funds';


alter table grants add column actual_spent double precision;

insert into messages(id,message) values(16,'The request refund amount cannot be greater than the available amount.');
insert into workflow_validations (id,object,validation_query,type,active,message_id) values(11,'CLOSURE','select case when b>(a) then true else false end _failed from ( select id,sum(available) a,sum(requested_refund) b,sum(actual_refunds) c from ( select a.id,sum(ad.actual_amount)-g.actual_spent available,g.refund_amount requested_refund,0 actual_refunds from grant_closure a inner join grants g on g.id=a.grant_id inner join disbursements d on d.grant_id=g.id inner join actual_disbursements ad on ad.disbursement_id=d.id inner join actual_refunds ar on ar.associated_grant_id=g.id where a.id=%closureId% group by a.id,g.id,g.actual_spent,g.refund_amount union select %closureId%,0,0, sum(amount) from grant_closure a inner join actual_refunds b on b.associated_grant_id=a.grant_id where a.id=%closureId% group by a.grant_id)X group by X.id) Y','WARN',true,16);

alter table closure_snapshot add column actual_spent double precision;

alter table closure_snapshot add column closure_docs text;

update notifications set message=replace(message,'/home/','/landing/');


update app_config set config_value='<p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <b class="go-to-disbursement-class">Grant Closure Request Note for %GRANT_NAME%</b> to review.</p>
<p>
  %GRANTEE% user:<a class="go-to-closure-class" href="%GRANTEE_CLOSURE_LINK%">Click here</a>
</p>
<p style="color: #000;">%GRANTER% user: <a class="go-to-closure-class" href="%GRANTER_CLOSURE_LINK%">Click here</a></p>
<p>Grant Closure Request workflow status changed for <strong>%GRANTEE%</strong></p> <p style="color: #000;"><strong>Change Summary: </strong></p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Closure Request for:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;"><span style="font-size: 14px; color: #000; font-weight: normal;">%GRANT_NAME%</span> </span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <p>This is an automatically generated email. Please do not reply to this message.</p>'
where config_name='CLOSURE_STATE_CHANGED_MAIL_MESSAGE';

update workflow_validations set validation_query='select case when refund_amount>(disbursed-actual_spent-actuals) then true else false end _failed from ( select a.grant_id,b.refund_amount,b.actual_spent,sum(actual_amount) disbursed,X.actuals from grant_closure a inner join grants b on b.id=a.grant_id inner join disbursements c on c.grant_id=b.id inner join actual_disbursements d on d.disbursement_id=c.id inner join (select a.grant_id,sum(c.amount) actuals from  grant_closure a inner join grants b on b.id=a.grant_id inner join actual_refunds c on c.associated_grant_id=a.grant_id where a.id=%closureId% group by a.grant_id) X on X.grant_id=a.grant_id where a.id=%closureId% group by a.grant_id,b.refund_amount,b.actual_spent,X.actuals) Y' where object='CLOSURE';

insert into workflow_validations(id,object,validation_query,type,active,message_id) values(12,'CLOSURE','select exists (select c.internal_status,b.* from grant_closure a inner join disbursements b on b.grant_id=a.grant_id inner join workflow_statuses c on c.id=b.status_id where a.id=%closureId% and ((c.internal_status=''DRAFT'' and (select count(*) from grant_closure_history where id=%closureId%) > 0) or c.internal_status=''REVIEW'' or c.internal_status=''ACTIVE'' )) _failed','WARN',true,17);
insert into messages (id,message) values(17,'There is a disbursement in progress');
update workflow_validations set validation_query='select distinct case when requested_amount>(b.amount-disbursed_amount_for_grant(b.id)) then true else false end _failed,(b.amount-disbursed_amount_for_grant(b.id)) _undisbursed  from disbursements a left join actual_disbursements c on c.disbursement_id = a.id inner join grants b on b.id=a.grant_id where a.id=%disbursementId%' where id=5;