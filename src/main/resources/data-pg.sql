-- App Configuration
insert into app_config (config_name, config_value)
VALUES ('KPI_REMINDER_NOTIFICATION_DAYS', '30'),
       ('KPI_SUBMISSION_WINDOW_DAYS', '20'),
       ('SUBMISSION_ALTER_MAIL_SUBJECT', 'Submission Alert'),
       ('SUBMISSION_ALTER_MAIL_CONTENT',
        'Submission for %SUBMISSION_TITLE% has been recently updated to %SUBMISSION_STATUS%. Your action is required.');

-- Organizations
INSERT INTO public.organizations (organization_type, code, created_at, created_by, name, updated_at,
                                  updated_by)
VALUES ('GRANTEE', 'IIH', '2019-04-08 03:02:02.431000', 'System', 'INNOVATORS IN HEALTH', null,
        null);
INSERT INTO public.organizations (organization_type, code, created_at, created_by, name, updated_at,
                                  updated_by)
VALUES ('GRANTER', 'IHF', '2019-04-08 03:02:02.431000', 'System', 'India Health Fund', null, null);

INSERT INTO public.organizations (organization_type, code, created_at, created_by, name, updated_at,
                                  updated_by)
VALUES ('PLATFORM', 'ANUDAN', '2019-04-08 03:02:02.431000', 'System', 'Anudan', null, null);
INSERT INTO public.organizations (organization_type, code, created_at, created_by, name, updated_at,
                                  updated_by)
VALUES ('GRANTER', 'TATR', '2019-04-08 03:02:02.431000', 'System', 'Tata Trust', null, null);

-- Granters
INSERT into public.granters (id, host_url, image_name, navbar_color, navbar_text_color)
values (2, 'ihf', 'indian_health_fund.png', '#232323', '#fff');
INSERT into public.granters (id, host_url, image_name, navbar_color, navbar_text_color)
values (4, 'tatr', 'tata_trust.png', '#ED1B24', '#fff');


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
values (now(), 'System', 'Admin', 2, null, null); -- 1
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Program Lead', 2, null, null); -- 2
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Program Manager', 2, null, null); -- 3
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Admin', 1, null, null); -- 4
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Admin', 3, null, null); -- 5
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Admin', 4, null, null); --6

-- Role Permissions
insert into roles_permission (permission, role_id) VALUES ('Create Grant',3);


-- Users
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'grantee@grantee.com', 'Grantee', 'Customer',
        'password', null, null, 1); -- 1
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'vineet_prasani@email.com', 'Vineet', 'Prasani',
        'password', null, null, 2); -- 2
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'manasi.arora@gmail.com', 'Manasi', 'Grantee',
        'password', null, null, 1); -- 3

INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'ranjitvictor@gmail.com', 'Ranjit', 'Victor',
        'password',
        null, null, 2); -- 4
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'vineet.prasani@gmail.com', 'Vineet', 'Prasani',
        'password',
        null, null, 2); -- 5
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'manasi.arora@enstratify.com', 'Manasi', 'PL',
        'password',
        null, null, 2); -- 6

INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'anudan-admin@ihf.com', 'Anudan', 'Admin',
        'password', null, null, 3); -- 7
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'admin@tatr.com', 'TATR', 'Admin',
        'password', null, null, 4); -- 8

insert into user_roles (role_id, user_id) VALUES (4,1);
insert into user_roles (role_id, user_id) VALUES (1,2);
insert into user_roles (role_id, user_id) VALUES (2,2);
insert into user_roles (role_id, user_id) VALUES (1,3);
insert into user_roles (role_id, user_id) VALUES (2,3);
insert into user_roles (role_id, user_id) VALUES (1,4);
insert into user_roles (role_id, user_id) VALUES (3,4);
insert into user_roles (role_id, user_id) VALUES (3,5);
insert into user_roles (role_id, user_id) VALUES (3,6);
insert into user_roles (role_id, user_id) VALUES (5,7);
insert into user_roles (role_id, user_id) VALUES (6,8);

-- Workflows
insert into public.workflows (name, granter_id, created_at, created_by, updated_at,
                              updated_by, object)
VALUES ('IHF - Grants Workflow', 2, now(), 'System', null, null, 'GRANT');
insert into public.workflows (name, granter_id, created_at, created_by, updated_at,
                              updated_by, object)
VALUES ('IHF - KPI Submissions Workflow', 2, now(), 'System', null, null, 'SUBMISSION');
insert into public.workflows (name, granter_id, created_at, created_by, updated_at,
                              updated_by, object)
VALUES ('TATR - Grants Workflow', 4, now(), 'System', null, null, 'GRANT');
insert into public.workflows (name, granter_id, created_at, created_by, updated_at,
                              updated_by, object)
VALUES ('TATR - KPI Submissions Workflow', 4, now(), 'System', null, null, 'SUBMISSION');


-- Workflow Statuses
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'DRAFT', false, null, null, 1, 'Draft',true); -- 1
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'ONBOARDING APPROVED', false, null, null, 1, 'Approved for onboarding',false); -- 2
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'ONGOING', false, null, null, 1, 'On Going',false); -- 3
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'CLOSED', true, null, null, 1, 'Closed',false); -- 4
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name, initial)
values (now(), 'System', 'NOT_SUBMITTED', false, null, null, 2, 'Not Submitted', true); -- 5
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'SUBMITTED', false, null, null, 2, 'Submitted',false); -- 6
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'MODIFICATIONS REQUESTED', false, null, null, 2,
        'Modifications Requested',false); -- 7
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'ACCEPTED', true, null, null, 2, 'Accepted',false); -- 8
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'DRAFT', false, null, null, 3, 'Draft',true); -- 9
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'ONGOING', false, null, null, 3, 'In Progress',false); -- 10
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'CLOSED', true, null, null, 3, 'Completed',false); -- 11
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'NOT_SUBMITTED', false, null, null, 4, 'Draft',true); -- 12
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'SUBMITTED', false, null, null, 4, 'Submitted',false); -- 13
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'REJECTED', false, null, null, 4,
        'Rejected',false); -- 14
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name,initial)
values (now(), 'System', 'ACCEPTED', true, null, null, 4, 'Accepted',false); -- 15

-- Workflow Status Transitions
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Request Approval', now(), 'System', null, null, 1, 2, 3, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Onboard', now(), 'System', null, null, 2, 3, 2, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Close', now(), 'System', null, null, 3, 4, 3, 1, false);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Save Draft', now(), 'System', null, null, 5, 5, 4, 2, false);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Submit', now(), 'System', null, null, 5, 6, 4, 2, true);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Request Modications', now(), 'System', null, null, 6, 7, 3, 2, true);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Submit Modifications', now(), 'System', null, null, 7, 6, 4, 2, true);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Accept', now(), 'System', null, null, 6, 8, 3, 2, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Onboard', now(), 'System', null, null, 8, 9, 6, 3, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Close', now(), 'System', null, null, 9, 10, 6, 3, false);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Submit', now(), 'System', null, null, 11, 12, 4, 4, false);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Save Draft', now(), 'System', null, null, 11, 11, 4, 4, false);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Reject', now(), 'System', null, null, 12, 13, 6, 4, true);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id, note_required)
values ('Accept', now(), 'System', null, null, 12, 14, 6, 4, false);

-- Workflow State Permissions
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 2);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 3, 1);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 1);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 2, 2);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 3);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 3);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 3);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 4, 4);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 6, 8);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 9);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 6, 9);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 6, 12);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 4, 11);

-- Grants
insert into public.grants (organization_id, name, description, created_at, created_by, updated_at,
                           updated_by, grantor_org_id, status_name, substatus_id, start_date,
                           end_date,
                           grant_status_id)
VALUES (1,
        'Improving Outcomes for Rural TB Patients in Private Care',
        'Improving Outcomes for Rural TB Patients in Private Care via Pharmacist Referrals and Community Health Workers (Health-IIH-20170104; (HEA/000/TEDT/036) )',
        now(), 'System', null, null, 2, 'ONGOING', 5, '2019-02-01', '2020-01-31', 1);
insert into public.grants (organization_id, name, description, created_at, created_by, updated_at,
                           updated_by, grantor_org_id, status_name, substatus_id, start_date,
                           end_date,
                           grant_status_id)
VALUES (1,
        'When one gains density and volume, one is able to receive mind.',
        'Yes, there is wonderland, it dies with zen.',
        now(), 'System', null, null, 4, 'ONGOING', 12, '2019-02-01', '2020-01-31', 1);

-- Grant KPIs
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.01.01.05 - No. of TB patients notified',
        'QUARTERLY', 'QUANTITATIVE', 4, true,
        'TBs.01.01.05 - No. of TB patients notified', null, null, 1); -- 1
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.06 - No. of private providers (pharmacists/phys icians/diagnostic centers) approached',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.03.01.06 - No. of private providers (pharmacists/phys icians/diagnostic centers) approached',
        null, null, 1); -- 2
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.07 - No. of workshops organized',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.03.01.07 - No. of workshops organized', null, null, 1); -- 3
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.07 - No. of workshops organized',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.45.56.23 - No. of workshops Bootstraped', null, null, 1); -- 4
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'HSS.02.01.15 - No. of CHW trained',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'HSS.02.01.15 - No. of CHW trained', null, null, 1); -- 5
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'Trainees enrolled in workshop',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'Trainees enrolled in workshop', null, null,
        1); -- 6
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'What changes were visible on the ground and amongest stake holders as a consequence of the project activities in this reporting period?',
        'QUARTERLY', 'QUALITATIVE', 12, true,
        'What changes were visible on the ground and amongest stake holders as a consequence of the project activities in this reporting period?',
        null, null, 1); -- 7
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System', 'What were the learnings in this period?', 'QUARTERLY', 'QUALITATIVE', 12,
        true, 'What were the learnings in this period?', null, null, 1); -- 8

insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System', 'Quarterly Progress and Outlook Report', 'QUARTERLY', 'DOCUMENT', 12,
        true, 'Quarterly Progress and Outlook Report', null, null, 1); -- 9
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'Sample KPI Description',
        'HALF_YEARLY', 'QUANTITATIVE', 6, true,
        'Sample KPI',
        null, null, 2); -- 10

-- Submissions
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-04-30', null, 'Quarter 1', null, null, 1, 5); -- 1
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-07-31', null, 'Quarter 2', null, null, 1, 5); -- 2
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-10-31', null, 'Quarter 3', null, null, 1, 5); -- 3
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2020-01-31', null, 'Quarter 4', null, null, 1, 5); -- 4
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-04-30', null, 'Half Yearly 1', null, null, 2, 12); -- 5
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2020-01-31', null, 'Half Yearly 2', null, null, 2, 12); -- 6

-- Quantitative Data
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 100, 1, 1,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 200, 1, 2,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 300, 1, 3,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 500, 1, 4,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 100, 2, 1,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 200, 2, 2,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 300, 2, 3,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 500, 2, 4,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 100, 3, 1,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 200, 3, 2,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 300, 3, 3,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 500, 3, 4,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 100, 4, 1,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 200, 4, 2,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 300, 4, 3,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 500, 4, 4,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 100, 5, 1,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 200, 5, 2,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 300, 5, 3,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 500, 5, 4,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 100, 6, 1,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 200, 6, 2,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 300, 6, 3,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 500, 6, 4,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 1000, 10, 5,true);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values (0, 500, 10, 6,true);

-- Qualitative Data
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 7, 1,true);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 7, 2,true);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 7, 3,true);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 7, 4,true);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 8, 1,true);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 8, 2,true);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 8, 3,true);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 8, 4,true);

-- Document Data
insert into grant_document_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 9, 1,true);
insert into grant_document_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 9, 2,true);
insert into grant_document_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 9, 3,true);
insert into grant_document_kpi_data (actuals, goal, grant_kpi_id, submission_id,to_report)
values ('', '', 9, 4,true);

-- Grant Sections at Platform Level
insert into grant_sections (deletable, section_name)
VALUES (false, 'Basic Details');
insert into grant_sections (deletable, section_name)
VALUES (false, 'Project Details');
insert into grant_sections (deletable, section_name)
VALUES (true, 'Financial Details');
insert into grant_sections (deletable, section_name)
VALUES (true, 'Report Schedule');

-- Grant Section Attributes ast Platform Level
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Grant Making Authority', 'string', true, null, 1); --1
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Grant Receiving Organization', 'string', true, null, 1); --2
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Grant Receiving Individual', 'string', true, null, 1); --3
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Project Name', 'string', true, null, 2); --4
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Project Description', 'string', true, null, 2); -- 5
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Project Purpose', 'string', true, null, 2); -- 6
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Bank Name', 'string', true, null, 3); -- 7
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Bank A/C No', 'string', true, null, 3); -- 8
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Bank IFSC Code', 'string', true, null, 3); -- 9

-- insert granter's grant sections configuration
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Basic Details', 2); -- 1
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Grant Towards Project', 2); -- 2
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Financial Details', 2); -- 3
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Involved Parties', 4); -- 4
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Project Summary', 4); -- 5
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Superfluous Information ', 4); -- 6
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Legal/Supporting Documents', 2); -- 7


-- Insert Granter's grant section attributes
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Grant Making Authority', 'string', true, 2, 1); -- 1
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Grant Receiving Organization', 'string', true, 2, 1); -- 2
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Grant Receiving Individual', 'string', true, 2, 1); -- 3
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Project Name', 'string', true, 2, 2); -- 4
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Project Description', 'string', true, 2, 2); -- 5
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Project Purpose', 'string', true, 2, 2); -- 6
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Bank Name', 'string', true, 2, 3); -- 7
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Bank A/C No.', 'string', true, 2, 3); -- 8
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'IFSC Code', 'string', true, 2, 3); -- 9
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Grant Making Authority', 'string', true, 4, 1); -- 10
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Grant Receiving Organization', 'string', true, 4, 1); -- 11
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Project Name', 'string', true, 4, 2); -- 12
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Project Description', 'string', true, 4, 2); -- 13
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'State of the nation', 'string', true, 4, 6); -- 14
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Grant Letter', 'document', true, 2, 7); -- 15
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'AWP', 'document', true, 2, 7); -- 16


-- Insert Grant additional String based attributes
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('India Health Fund', 1, 1, 1);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('Innovators in Health', 1, 1, 2);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('Pradeep Sadan', 1, 1, 3);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('Improving Outcomes for Rural TB Patients in Private Care', 1, 2, 4);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('Improving Outcomes for Rural TB Patients in Private Care via Pharmacist Referrals and Community Health Workers (Health-IIH-20170104; (HEA/000/TEDT/036) )',
        1, 2, 5);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('Instead of jumbling bloody joghurt with steak, use one package gravy and one jar black cardamon fine-mesh strainer.',
        1, 2, 6);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('HDFC Bank', 1, 3, 7);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('007710000475869', 1, 3, 8);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('HDFC0000045', 1, 3, 9);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('Tata Trust', 2, 4, 10);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('Innovators in Health', 2, 4, 11);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('JN Tata Endowment for Higher Education', 2, 5, 12);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('The JN Tata Endowment, formalised and established in 1892, selects candidates of caliber and credentials and enables them to pursue quality higher education at some of the best institutes in the world.',
        2, 5, 13);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('A definitive, analytical and meticulous account of the present state of the nation – from a constitutional perspective',
        2, 6, 14);
-- Insert additional document based attributes
insert into grant_document_attributes (file_type, location, name, version, grant_id, section_id,
                                       section_attribute_id)
values ('pdf','IHF/grants/1/','grant_letter.pdf',1,1,7,15);
insert into grant_document_attributes (file_type, location, name, version, grant_id, section_id,
                                       section_attribute_id)
values ('xls','IHF/grants/1/','annual_work_plan.xls',1,1,7,16);

-- Granter Template
insert into templates (description, location, name, type, version, kpi_id, file_type)
VALUES ('Utilization reports are to be submitted when requested. It contains financial expenditure details for a given reporting period.',
        'templates/kpi/', 'utilization_report.xls', 'kpi', 1, 9, 'xls');
insert into templates (description, location, name, type, version, kpi_id, file_type)
VALUES ('Helper document to manage project budget', 'templates/kpi/', 'project_budget_sheet.xlsx',
        'kpi', 1, 9, 'xls');


