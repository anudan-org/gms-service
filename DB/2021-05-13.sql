truncate table data_export_config;

INSERT INTO public.data_export_config (id, name, description, query, tenant, category, params) VALUES (4, 'Reports', NULL, 'select g.id as "Anudan Grant Identifiedr",
g.reference_no as "Grant Reference Code",
secattr.field_name as "KPI Name",
str.grant_level_target as "Grant Level Target",
str.frequency as "Reporting Frequency",
r.id as "Anudan Report Identifier",
r.name as "Report Name",
to_char(r.start_date::DATE,''dd-mm-yyyy'') as "Report Start Date",to_char(r.end_date::DATE,''dd-mm-yyyy'') as "Report End Date",
to_char(r.due_date::DATE,''dd-mm-yyyy'') as "Report Due Date",
str.target as "Report Level Target",
ws.name as "Report Status",
case when ws.internal_status=''CLOSED'' then (select to_char(moved_on::DATE,''dd-mm-yyyy'') from report_snapshot where report_id=r.id and to_state_id=r.status_id order by id desc limit 1) else null end as "Report Approved Date",
str.actual_target as "Report Level Achieved"
from reports r inner join grants g on g.id=r.grant_id
inner join grant_types gt on gt.id=g.grant_type_id
inner join workflow_statuses ws on ws.id=r.status_id
inner join report_specific_sections sec on sec.report_id=r.id
inner join report_specific_section_attributes secattr on secattr.section_id=sec.id
inner join report_string_attributes str on str.section_id=sec.id and str.section_attribute_id=secattr.id and str.report_id=r.id
where ((select count(*) from report_history where id=r.id)>0) and secattr.field_type=''kpi'' and g.deleted=false and g.grantor_org_id=%tenantId%', NULL, 'ACTIVE_GRANTS_DETAILS', NULL);
INSERT INTO public.data_export_config (id, name, description, query, tenant, category, params) VALUES (2, 'Disbursements', NULL, 'select a.grant_id as "Anudan Grant Identifier",
b.reference_no as "Grant Reference No",
a.id as "Anudan Disbursement Identifier",
requested_amount::text as "Requested Amount",
a.reason as "Request Note",
f.actual_amount::text as "Disbursement Amount",
to_char(f.disbursement_date::DATE,''dd-mm-yyyy'') as "Disbursement Date"
from disbursements a inner join grants b on b.id=a.grant_id
inner join workflow_statuses c on c.id=b.grant_status_id
inner join workflow_statuses d on d.id=a.status_id
left join actual_disbursements f on f.disbursement_id=a.id
where ((select count(*) from disbursement_history where id=a.id)>0) and b.grantor_org_id=%tenantId%', NULL, 'ACTIVE_GRANTS_DETAILS', NULL);
INSERT INTO public.data_export_config (id, name, description, query, tenant, category, params) VALUES (7, 'Grants - Workflow', NULL, 'select g.id as "Anudan Grant Identifier",gs.id as "Anudan Workflow Identifier",g.reference_no as "Grant Reference No",
gs.name as "Grant Name",
(select concat(first_name,'' '',last_name) from users where id=assigned_by) as "Moved By",
(select name from workflow_statuses where id=gs.from_state_id) as "From State",
(select name from workflow_statuses where id=gs.to_state_id) as "To State",
(select concat(first_name,'' '',last_name) from users where id=assigned_to_id) as "Moved To",
to_char(gs.moved_on::TIMESTAMP,''dd-mm-yyyy hh12:mi AM'')::text as "Moved On",
gs.from_note as "Note"
from grant_snapshot gs
inner join grants g on g.id=gs.grant_id
where g.grantor_org_id=%tenantId%
order by g.id,gs.moved_on,gs.id', NULL, 'ACTIVE_GRANTS_DETAILS', NULL);
INSERT INTO public.data_export_config (id, name, description, query, tenant, category, params) VALUES (8, 'Reports - Workflow', NULL, 'select g.id as "Anudan Grant Identifier",g.reference_no as "Grant Reference No",
g.name as "Grant Name",r.id as "Anudan Report Identifier",rs.id as "Anudan Workflow Identifier",r.name as "Report Name",
(select concat(first_name,'' '',last_name) from users where id=moved_by) as "Moved By",
(select name from workflow_statuses where id=rs.from_state_id) as "From State",
(select name from workflow_statuses where id=rs.to_state_id) as "To State",
(select concat(first_name,'' '',last_name) from users where id=assigned_to_id) as "Moved To",
to_char(rs.moved_on::TIMESTAMP,''dd-mm-yyyy hh12:mi AM'')::text as "Moved On",
rs.from_note as "Note"
from report_snapshot rs
inner join reports r on r.id=rs.report_id
inner join grants g on g.id=r.grant_id
where g.grantor_org_id=%tenantId%
order by g.id,r.id,rs.moved_on,rs.id', NULL, 'ACTIVE_GRANTS_DETAILS', NULL);
INSERT INTO public.data_export_config (id, name, description, query, tenant, category, params) VALUES (9, 'Disbursements - Workflow', NULL, 'select g.id as "Anudan Grant Identifier",g.reference_no as "Grant Reference No",
g.name as "Grant Name",d.id as "Anudan Dsibursement Identifier",ds.id as "Anudan Workflow Identifier",
(select concat(first_name,'' '',last_name) from users where id=moved_by) as "Moved By",
(select name from workflow_statuses where id=ds.from_state_id) as "From State",
(select name from workflow_statuses where id=ds.to_state_id) as "To State",
(select concat(first_name,'' '',last_name) from users where id=assigned_to_id) as "Moved To",
to_char(ds.moved_on::TIMESTAMP,''dd-mm-yyyy hh12:mi AM'')::text as "Moved On",
ds.from_note as "Note"
from disbursement_snapshot ds
inner join disbursements d on d.id=ds.disbursement_id
inner join grants g on g.id=d.grant_id
where g.grantor_org_id=%tenantId%
order by g.id,d.id,ds.moved_on,ds.id', NULL, 'ACTIVE_GRANTS_DETAILS', NULL);
INSERT INTO public.data_export_config (id, name, description, query, tenant, category, params) VALUES (1, 'Grants', NULL, 'select o.name as "Grant Making Organization",g.id as "Anudan Grant Identifier",
g.reference_no as "Grant Reference Code",
g.name as "Grant Name",
(select name from workflow_statuses where id=g.grant_status_id) as "Grant Status",
gt.name as "Grant Type",
to_char(g.start_date::DATE,''dd-mm-yyyy'') as "Start Date",
to_char(g.end_date::DATE,''dd-mm-yyyy'') as "End Date",
g.amount::text as "Grant Amount",
case when gt.internal=true then o.name else io.name end as "Implementing Organization",
g.representative as "Implementing Organization Representative",
case when g.amended then ''Yes'' else ''No'' end as "Is Amended",
%grantTags%
from grants g
inner join organizations o on o.id=g.grantor_org_id
inner join grant_types gt on gt.id=g.grant_type_id
left join organizations io on io.id=g.organization_id
left join (select gid,%grantTagSelectDefs% from
crosstab(''select g_id,name tag_name,gt_id from (select g.id g_id,ot.name,case when gt.id is null then '''''''' else ot.name end gt_id from grants g
inner join org_tags ot on g.grantor_org_id=ot.tenant
left join grant_tags gt on gt.org_tag_id=ot.id and gt.grant_id=g.id) y'',''select name from org_tags where tenant=%tenantId% order by name'')
as final_result (gid bigint,%grantTagDefs%) group by gid) y on y.gid=g.id
where ((select count(*) from grant_history where id=g.id)>0) and
o.id=%tenantId%;', NULL, 'ACTIVE_GRANTS_DETAILS', '{''grantTags'':''select string_agg(concat(''"'',name,''" as "Tag - '',name,''"''),'','') from org_tags where tenant=%tenantId% group by tenant'',''grantTagDefs'':''select string_agg(concat(''"'',name,''" text''),'','') from org_tags where tenant=%tenantId% group by tenant''}');


CREATE extension tablefunc;