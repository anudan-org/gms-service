-- App Configuration
insert into app_config (config_name, config_value)
VALUES ('KPI_REMINDER_NOTIFICATION_DAYS', '30'),
       ('KPI_SUBMISSION_WINDOW_DAYS', '20');

-- Organizations
INSERT INTO public.organizations (organization_type, code, created_at, created_by, name, updated_at,
                                  updated_by)
VALUES ('GRANTEE', 'IIH', '2019-04-08 03:02:02.431000', 'System', 'INNOVATORS IN HEALTH', null, null);
INSERT INTO public.organizations (organization_type, code, created_at, created_by, name, updated_at,
                                  updated_by)
VALUES ('GRANTER', 'IHF', '2019-04-08 03:02:02.431000', 'System', 'India Health Fund', null, null);

INSERT INTO public.organizations (organization_type, code, created_at, created_by, name, updated_at,
                                  updated_by)
VALUES ('PLATFORM', 'ANUDAN', '2019-04-08 03:02:02.431000', 'System', 'Anudan', null, null);

-- Granters
INSERT into public.granters (id, host_url, image_name, navbar_color, navbar_text_color)
values (2, 'ihf', 'indian_health_fund.png', '#459890', '#fff');


-- Grantees
INSERT into public.grantees (id
)
values (1);

-- Platform
INSERT into public.platform (id, host_url, image_name, navbar_color)
VALUES (3, 'anudan', 'anudan.png', '#a41029');

-- Organization Configs
insert into org_config (granter_id, config_name, config_value)
VALUES (2, 'KPI_REMINDER_NOTIFICATION_DAYS', '30'),
       (2, 'KPI_SUBMISSION_WINDOW_DAYS', '20');

-- Roles
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Admin', 2, null, null);
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Program Lead', 2, null, null);
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Program Manager', 2, null, null);
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Admin', 1, null, null);
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Admin', 3, null, null);


-- Users
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id, role_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'grantee@grantee.com', 'Grantee', 'Customer',
        'password', null, null, 1, 4);
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id, role_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'ihf-admin@ihf.com', 'IHF', 'Admin', 'password',
        null, null, 2, 2);

INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id, role_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'anudan-admin@ihf.com', 'Anudan', 'Admin',
        'password', null, null, 3, 4);

-- Workflows
insert into public.workflows (name, granter_id, created_at, created_by, updated_at,
                              updated_by, object)
VALUES ('IHF - Grants Workflow', 2, now(), 'System', null, null, 'GRANT');
insert into public.workflows (name, granter_id, created_at, created_by, updated_at,
                              updated_by, object)
VALUES ('IHF - KPIs Workflow', 2, now(), 'System', null, null, 'KPI');


-- Workflow Statuses
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id)
values (now(), 'System', 'DRAFT', false, null, null, 1);
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id)
values (now(), 'System', 'ONGOING', false, null, null, 1);
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id)
values (now(), 'System', 'CLOSED', true, null, null, 1);
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id)
values (now(), 'System', 'NOT SUBMITTED', false, null, null, 2);
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id)
values (now(), 'System', 'SUBMITTED', false, null, null, 2);
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id)
values (now(), 'System', 'MODIFICATIONS REQUESTED', false, null, null, 2);
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id)
values (now(), 'System', 'ACCEPTED', true, null, null, 2);

-- Workflow Status Transitions
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id)
values ('Onboard', now(), 'System', null, null, 1, 2, 2, 1);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id)
values ('Onboard', now(), 'System', null, null, 1, 2, 3, 1);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id)
values ('Close', now(), 'System', null, null, 2, 3, 2, 1);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id)
values ('Submit', now(), 'System', null, null, 4, 5, 4, 2);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id)
values ('Request Modifications', now(), 'System', null, null, 5, 6, 2, 2);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id)
values ('Submit Modications', now(), 'System', null, null, 6, 5, 4, 2);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id)
values ('Accept', now(), 'System', null, null, 5, 7, 2, 2);
-- Workflow State Permissions
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 2, 1);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 2);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 2);

-- Grants
insert into public.grants (organization_id, name, description, created_at, created_by, updated_at,
                           updated_by, grantor_org_id, status_name, substatus, start_date, end_date,
                           status_id)
VALUES (1, 'Improving Outcomes for Rural TB Patients in Private Care via Pharmacist Referrals and Community Health Workers (Health-IIH-20170104; (HEA/000/TEDT/036) )',
        'A placeholder in programming code may also be used to indicate where specific code needs to be added, but the programmer has not yet written the code.',
        now(), 'System', null, null, 2, 'ONGOING', null, '2019-02-01', '2020-01-31', 2);

-- Grant KPIs
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, status, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.01.01.05 - No. of TB patients notified',
        'QUARTERLY', 'QUANTITATIVE', 4, true, 'NOT_SUBMITTED',
        'TBs.01.01.05 - No. of TB patients notified', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, status, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.06 - No. of private providers (pharmacists/phys icians/diagnostic centers) approached',
        'QUARTERLY', 'QUANTITATIVE', 12, true, 'NOT_SUBMITTED',
        'TBs.03.01.06 - No. of private providers (pharmacists/phys icians/diagnostic centers) approached', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, status, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.07 - No. of workshops organized',
        'QUARTERLY', 'QUANTITATIVE', 12, true, 'NOT_SUBMITTED',
        'TBs.03.01.07 - No. of workshops organized', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, status, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.07 - No. of workshops organized',
        'QUARTERLY', 'QUANTITATIVE', 12, true, 'NOT_SUBMITTED',
        'TBs.03.01.07 - No. of workshops organized', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, status, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'HSS.02.01.15 - No. of CHW trained',
        'QUARTERLY', 'QUANTITATIVE', 12, true, 'NOT_SUBMITTED',
        'HSS.02.01.15 - No. of CHW trained', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, status, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'Any deviations from the plan, if yes, mention them and reasons for the same?',
        'QUARTERLY', 'QUALITATIVE', 12, true, 'NOT_SUBMITTED',
        'Any deviations from the plan, if yes, mention them and reasons for the same?', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, status, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'What changes were visible on the ground and amongest stake holders as a consequence of the project activities in this reporting period?',
        'QUARTERLY', 'QUALITATIVE', 12, true, 'NOT_SUBMITTED',
        'What changes were visible on the ground and amongest stake holders as a consequence of the project activities in this reporting period?', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, status, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        '
What were the learnings in this period?',
        'QUARTERLY', 'QUALITATIVE', 12, true, 'NOT_SUBMITTED',
        '
What were the learnings in this period?', null, null, 1);

-- Quantitative KPIs
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-04-30', NULL, 1,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-07-31', NULL, 1,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-10-31', NULL, 1,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2020-01-31', NULL, 1,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-04-30', NULL, 2,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-07-31', NULL, 2,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-10-31', NULL, 2,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2020-01-31', NULL, 2,4);
--
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-04-30', NULL, 3,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-07-31', NULL, 3,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-10-31', NULL, 3,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2020-01-31', NULL, 3,4);
--
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-04-30', NULL, 4,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-07-31', NULL, 4,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-10-31', NULL, 4,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2020-01-31', NULL, 4,4);
--
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-04-30', NULL, 5,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-07-31', NULL, 5,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2019-10-31', NULL, 5,4);
insert into public.grant_quantitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                                submitted_on_date, grant_kpi_id,status_id)
values (0, 100, 'NOT SUBMITTED', '2020-01-31', NULL, 5,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-04-30',null,6,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-07-31',null,6,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-10-31',null,6,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2020-01-31',null,6,4);

insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-04-30',null,7,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-07-31',null,7,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-10-31',null,7,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2020-01-31',null,7,4);

insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-04-30',null,8,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-07-31',null,8,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2019-10-31',null,8,4);
insert into grant_qualitative_kpi_data (actuals, goal, status_name, submit_by_date,
                                        submitted_on_date, grant_kpi_id,status_id)
VALUES ('','Eleatess sunt plasmators de magnum parma.','NOT SUBMITTED','2020-01-31',null,8,4);
