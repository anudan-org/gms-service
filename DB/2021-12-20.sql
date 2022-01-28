alter table messages add column subject text;

CREATE SEQUENCE hygiene_check_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

    CREATE TABLE hygiene_checks
    (
        id bigint NOT NULL DEFAULT nextval('hygiene_check_seq'::regclass),
        hygiene_query text COLLATE pg_catalog."default",
        active boolean,
        object character varying(20) COLLATE pg_catalog."default",
        message_id bigint,
        name character varying(500) COLLATE pg_catalog."default",
        description text COLLATE pg_catalog."default",
        scheduled_run character varying(50) COLLATE pg_catalog."default",
        CONSTRAINT hygiene_checks_pk PRIMARY KEY (id)
    );

insert into app_config(config_name,config_value) values('MISSING_ACTUAL_DISBURSEMENTS_POST_APPROVAL','2');

delete from app_config where config_name='KPI_REMINDER_NOTIFICATION_DAYS';
delete from app_config where config_name='SUBMISSION_ALTER_MAIL_SUBJECT';
delete from app_config where config_name='SUBMISSION_ALTER_MAIL_CONTENT';
delete from app_config where config_name='GRANT_ALERT_NOTIFICATION_MESSAGE';
delete from app_config where config_name='GRANT_STATE_CHANGED_NOTIFICATION_SUBJECT';
delete from app_config where config_name='REPORT_PERIOD_INTERVAL';
delete from app_config where config_name='GRANT_STATE_CHANGED_NOTIFICATION_MESSAGE';
delete from app_config where config_name='REPORT_STATE_CHANGED_NOTIFICATION_SUBJECT';
delete from app_config where config_name='REPORT_STATE_CHANGED_NOTIFICATION_MESSAGE';
delete from app_config where config_name='DISBURSEMENT_STATE_CHANGED_NOTIFICATION_MESSAGE';
delete from app_config where config_name='DISBURSEMENT_STATE_CHANGED_NOTIFICATION_SUBJECT';
delete from app_config where config_name='OWNERSHIP_CHANGED_NOTIFICATION_MESSAGE';
delete from app_config where config_name='OWNERSHIP_CHANGED_NOTIFICATION_SUBJECT';


CREATE OR REPLACE FUNCTION public.get_earliest_report_date(
	months integer,
	fordate date)
    RETURNS date
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare reportDate date;
begin
--forDate = forDate - interval '1 month'::interval;
	if (months=1) then
		reportDate = forDate + interval '1 month'::interval;
	elseif(months=3) then
		reportDate = forDate;-- + interval '3 month'::interval;
		if(extract('month' from reportDate)>=1 and extract('month' from reportDate)<=3) then
			reportDate = make_date(extract('year' from reportDate)::int,3,31);
		elseif(extract('month' from reportDate)>=4 and extract('month' from reportDate)<=6) then
			reportDate = make_date(extract('year' from reportDate)::int,6,30);
		elseif(extract('month' from reportDate)>=7 and extract('month' from reportDate)<=9) then
			reportDate = make_date(extract('year' from reportDate)::int,9,30);
		elseif(extract('month' from reportDate)>=10 and extract('month' from reportDate)<=12) then
			reportDate = make_date(extract('year' from reportDate)::int,12,31);
		end if;
	elseif(months=6) then
		reportDate = forDate;-- + interval '3 month'::interval;
		if(extract('month' from reportDate)>=1 and extract('month' from reportDate)<=6) then
			reportDate = make_date(extract('year' from reportDate)::int,6,30);
		elseif(extract('month' from reportDate)>=7 and extract('month' from reportDate)<=12) then
			reportDate = make_date(extract('year' from reportDate)::int,12,31);
		end if;
	elseif(months=12) then
		reportDate = forDate;-- + interval '3 month'::interval;
		if(extract('month' from reportDate)>=1 and extract('month' from reportDate)<=12) then
			reportDate = make_date(extract('year' from reportDate)::int,12,31);
		end if;
	end if;
 	return reportDate;
end;
$BODY$;


truncate table messages;

INSERT INTO public.messages VALUES (2, '%_for% has missing header information.', NULL);
INSERT INTO public.messages VALUES (1, 'Planned Disbursements does not match the Grant Amount of %grantamount%.', NULL);
INSERT INTO public.messages VALUES (3, 'Planned Disbursements is missing for this project.', NULL);
INSERT INTO public.messages VALUES (4, 'Requested amount cannot be greater than project''s undisbursed amount.', NULL);
INSERT INTO public.messages VALUES (5, 'The disbursed total cannot be greater than the approved amount of %requested_amount%.', NULL);
INSERT INTO public.messages VALUES (6, 'This grant is missing project KPIs.', NULL);
INSERT INTO public.messages VALUES (7, 'This grant is missing project documents.', NULL);
INSERT INTO public.messages VALUES (8, 'Disbursement record amount (%recorded_amount%) is lower than the disbursement approval amount (%requested_amount%). Unused approved amount (%unused_amount%) will not be available if you proceed.', NULL);
INSERT INTO public.messages VALUES (9, 'Grant has KPIs specified but this report is not checking for KPIs', NULL);
INSERT INTO public.messages VALUES (12, '<p>The following user(s) have registered but have performed no activity in Anudan Grant Management System. </p>
<table border="1" cellspace="0" cellpadding="0">
  <tr><td>Username</td>

<td>Email Address</td>
<td>Days Since Registration</td>
    %SUMMARY%
  </tr>
</table>', 'Hygiene Check | Registered users with no activity');
INSERT INTO public.messages VALUES (10, '<p>The following Grants have approved disbursement requests but are missing actual disbursements.</p>
<table border="1" cellspace="0" cellpadding="0">
  <tr><td>Grant Name </td>
    <td>Approved Disbursement Amount (INR)</td>
    <td>Actuals to be recorded by</td>
    %SUMMARY%
  </tr>
</table>
<hr>
<p>
</p>', 'Hygiene Check | Approved disbursement requests without actual disbursements ');
INSERT INTO public.messages VALUES (13, '<p>The following active grants have no KPIs.</p>
<table border="1" cellspace="0" cellpadding="0">
  <tr><td>Grants Name</td>
<td>Grant Owner</td>
    %SUMMARY%
  </tr>
</table>', 'Hygiene Check | Grants missing KPIs');
INSERT INTO public.messages VALUES (15, '<p>The following active grants have no disbursements.</p>
<table border="1" cellspace="0" cellpadding="0">
  <tr><td>Grants with missing Actual Disbursements</td>
<td>Grant Owner</td>
<td>No of days Grant has been Active</td>
    %SUMMARY%
  </tr>
</table>', 'Hygiene Check | Active Grants missing recorded disbursements');
INSERT INTO public.messages VALUES (11, '<p>The following user(s) have been invited to join the Anudan Grant Management System but have not yet registered.</p><table border="1" cellspace="0" cellpadding="0">
  <tr><td>Email Address</td>
<td>Date Invited</td>
<td>Days since invitation</td>
    %SUMMARY%
  </tr>
</table>', 'Hygiene Check | Unregistered Users');
INSERT INTO public.messages VALUES (14, '<p>Hygiene Check | Grants missing approved reports</p>
<table border="1" cellspace="0" cellpadding="0">
  <tr><td>Grant Name</td>
<td>Grant Owner</td>
<td>Number of Days Grant has been Active </td>
    %SUMMARY%
  </tr>
</table>', 'Hygiene Check | Grants missing approved reports');



INSERT INTO public.hygiene_checks VALUES (3, 'select string_agg(concat(''<tr><td>'',u.email_id,''</td><td>'',to_char(u.created_at,''DD-MM-YYYY''),''</td><td>'',DATE_PART(''day'',now()::timestamp-u.created_at::timestamp),'' days</td></tr>''),'''') summary,
(select string_agg(distinct a.email_id,'','') from users a
			inner join user_roles b on b.user_id=a.id
			inner join roles c on c.id=b.role_id
			where c.name=''Admin'' and a.active=true and a.deleted=false
			and a.organization_id=u.organization_id
			group by a.organization_id) emails_to,
u.organization_id grantor_org_id
from users u
inner join organizations b on u.organization_id=b.id
where b.organization_type=''GRANTER'' and
u.active=false and u.deleted=false
group by u.organization_id', true, 'USERS', 11, 'Invited tenant users who have never registered', 'Invited tenant users who have never registered', '0 0 0 1 * *');
INSERT INTO public.hygiene_checks VALUES (1, 'SELECT string_agg(concat(''<tr><td>'',X.grant_name,''</td><td>₹ '',to_char(X.approved_amount,''FM99,FM99,99,999D''),''</td><td>'',X.owner_name,''</td></tr>''),'''') summary,
	   string_agg(distinct X.admin_emails,'','') emails_to,
	   X.grantor_org_id
FROM   (SELECT a.id disbursement_id,
               a.grant_id,
			   g.grantor_org_id,
               g.name grant_name,
               a.requested_amount approved_amount,
			   ceil(extract (epoch from now()::timestamp-a.moved_on::timestamp)/(24*3600))  approved_since_in_days,
               a.moved_on,
			   c.owner recording_user,
			(select string_agg(distinct a.email_id,'','') from users a
			inner join user_roles b on b.user_id=a.id
			inner join roles c on c.id=b.role_id
			where c.name=''Admin'' and a.active=true and a.deleted=false
			and a.organization_id=g.grantor_org_id
			group by a.organization_id) admin_emails,
		(select case when active=false then concat(email_id,'' (unregistered)'') else concat(first_name,'' '',last_name,''('',email_id,'')'') end from users where id=c.owner) owner_name
        FROM   disbursements a
               inner join workflow_statuses b ON b.id = a.status_id
               inner join grants g ON g.id = a.grant_id
			   inner join disbursement_assignments c on c.disbursement_id=a.id and c.state_id=a.status_id
        WHERE  b.internal_status = ''ACTIVE'') X
WHERE  X.approved_since_in_days :: INT > (SELECT config :: INT
                                          FROM   (SELECT CASE
                                                           WHEN b.config_value
                                                                IS NOT
                                                                NULL THEN
                                                           b.config_value
                                                           ELSE a.config_value
                                                         END config
                                                  FROM   app_config a
                                                         left join org_config b
                                                                ON
a.config_name = b.config_name
WHERE
a.config_name = ''MISSING_ACTUAL_DISBURSEMENTS_POST_APPROVAL'') Y)
group by X.grantor_org_id', true, 'GRANT', 10, 'Approved disbursement requests without actual disbursements ', 'Approved disbursement requests without actual disbursements ', '0 0 0 1 * *');
INSERT INTO public.hygiene_checks VALUES (5, 'select string_agg(concat(''<tr><td>'',gr.name,''</td><td>'',(select concat(first_name,'' '',last_name,'' ('',email_id,'')'') from users where id=wa.assignments ),''</td></tr>''),'''') summary,
(select string_agg(distinct a.email_id,'','') from users a
			inner join user_roles b on b.user_id=a.id
			inner join roles c on c.id=b.role_id
			where c.name=''Admin'' and a.active=true and a.deleted=false
			and a.organization_id=gr.grantor_org_id
			group by a.organization_id) emails_to,
			 gr.grantor_org_id
from grants gr
inner join workflow_statuses w on w.id=gr.grant_status_id
inner join grant_assignments wa on wa.grant_id=gr.id and wa.state_id=gr.grant_status_id
where w.internal_status=''ACTIVE'' and gr.deleted=false and gr.id not in (
select distinct g.id from grants g
inner join grant_specific_sections s on s.grant_id=g.id
inner join grant_specific_section_attributes a on a.section_id=s.id
where a.field_type=''kpi'') group by gr.grantor_org_id', true, 'GRANT', 13, 'Active Grants missing KPIs', 'Active Grants missing KPIs', '0 0 0 1 * *');
INSERT INTO public.hygiene_checks VALUES (4, 'select string_agg(concat(''<tr><td>'',u.first_name,'' '',u.last_name,''</td><td>'',u.email_id,''</td><td>'',date_part(''day'',now()::timestamp-u.created_at::timestamp),'' days</td></tr>''),'''') summary,
(select string_agg(distinct a.email_id,'','') from users a
			inner join user_roles b on b.user_id=a.id
			inner join roles c on c.id=b.role_id
			where c.name=''Admin'' and a.active=true and a.deleted=false
			and a.organization_id=o.id
			group by a.organization_id) emails_to,
o.id grantor_org_id
from users u
inner join organizations o on o.id=u.organization_id
where o.organization_type=''GRANTER'' and u.active=true and u.deleted=false and u.id not in (
select distinct ga.assignments from grant_assignments ga
inner join grant_snapshot gs on gs.grant_id=ga.grant_id and ga.assignments is not null
union
select distinct ra.assignment from report_assignments ra
inner join report_snapshot rs on rs.report_id=ra.report_id and ra.assignment is not null
union
select distinct da.owner from disbursement_assignments da
inner join disbursement_snapshot ds on ds.disbursement_id=da.disbursement_id and da.owner is not null
)
group by o.id', true, 'USERS', 12, 'Active Users with no workflows', 'Active Users with no workflows', '0 0 0 1 * *');
INSERT INTO public.hygiene_checks VALUES (7, 'select string_agg(concat(''<tr><td>'',Y.name,''</td><td>'',Y.current_owner,''</td><td>'',date_part(''day'',now()::timestamp-Y.moved_on::timestamp),'' days</td></tr>''),'''') summary,
(select string_agg(distinct a.email_id,'','') from users a
			inner join user_roles b on b.user_id=a.id
			inner join roles c on c.id=b.role_id
			where c.name=''Admin'' and a.active=true and a.deleted=false
			and a.organization_id=Y.grantor_org_id
			group by a.organization_id) emails_to,
			Y.grantor_org_id
from (select g.grantor_org_id,g.name,case when wd.name is null then ''No Disbursement Requests found'' else wd.name end disbursement_status
	  , concat(u.first_name,'' '',u.last_name,''( '',u.email_id,'')'') current_owner, g.moved_on
	  from grants g
	  inner join grant_assignments ga on ga.grant_id=g.id and ga.state_id=g.grant_status_id
	  inner join users u on u.id=ga.assignments
inner join workflow_statuses w on w.id=g.grant_status_id
left join disbursements d on d.grant_id=g.id
left join workflow_statuses wd on wd.id=d.status_id
left join actual_disbursements ad on ad.disbursement_id=d.id
where w.internal_status=''ACTIVE'' and (
	d.id is null or
	(d.id is not null and wd.internal_status=''CLOSED'' and ad.id is null
	) or
	(d.id is not null and wd.internal_status!=''CLOSED'' and ad.id is not null
	)
	) and now()>g.start_date + interval ''1 month''::interval order by g.id
			   ) Y group by Y.grantor_org_id', true, 'GRANT', 15, 'Active Grants missing actual disbursement records', 'Active Grants missing actual disbursement records', '0 0 0 1 * *');
INSERT INTO public.hygiene_checks VALUES (6, 'select string_agg(concat(''<tr><td>'',Y.grant_name,''</td><td>'',Y.grant_owner,''</td><td>'',date_part(''day'',now()::timestamp-Y.moved_on::timestamp),'' days</td></tr>''),'''') summary,
(select string_agg(distinct a.email_id,'','') from users a
			inner join user_roles b on b.user_id=a.id
			inner join roles c on c.id=b.role_id
			where c.name=''Admin'' and a.active=true and a.deleted=false
			and a.organization_id=Y.org
			group by a.organization_id) emails_to,
			y.org grantor_org_id
from (select X.org,X.id,X.grant_name,X.start_date grant_start_date,X.grant_owner,X.moved_on,
get_earliest_report_date(min(X.lowest_frequency),X.start_date) expected_reporting_date ,
(select exists (select * from reports r
inner join workflow_statuses w on w.id=r.status_id
where w.internal_status=''CLOSED''
and r.grant_id=X.id and r.moved_on<=get_earliest_report_date(min(X.lowest_frequency),X.start_date)))
from (select g.id,w.name status,g.grantor_org_id org,g.name grant_name,
case when sa.frequency=''monthly'' then 1 else
case when sa.frequency=''quarterly'' then 3 else
case when sa.frequency=''half-yearly'' then 6 else 12 end
end end lowest_frequency,
g.start_date::date,
(select concat(first_name, '' '',last_name,'' ('',email_id,'')'') from users wu where wu.id=ga.assignments) grant_owner,
g.moved_on
from grants g
inner join workflow_statuses w on w.id=g.grant_status_id
inner join grant_assignments ga on ga.grant_id=g.id and ga.state_id=g.grant_status_id
inner join grant_string_attributes sa on sa.grant_id=g.id
inner join grant_specific_section_attributes s on s.id=sa.section_attribute_id
where w.internal_status=''ACTIVE'' and g.deleted=false and s.field_type=''kpi'' order by g.id) X
group by X.id,X.grant_owner,X.moved_on,X.grant_name,X.status,X.start_date,X.org) y
group by y.org', true, 'GRANT', 14, 'Active Grants with KPI''s but no approved reports', 'Active Grants with KPI''s but no approved reports', '0 0 0 1 * *');