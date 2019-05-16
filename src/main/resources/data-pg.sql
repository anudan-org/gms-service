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
VALUES ('2019-04-08 03:00:16.545000', 'System', 'vineet_prasani@email.com', 'Vineet', 'Prasani',
        'password', null, null, 1, 4);
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id, role_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'manasi.arora@gmail.com', 'Manasi', 'Grantee',
        'password', null, null, 1, 4);

INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id, role_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'ranjitvictor@gmail.com', 'IHF', 'Admin', 'password',
        null, null, 2, 2);
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id, role_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'vineet.prasani@gmail.com', 'Vineet', 'Prasani',
        'password',
        null, null, 2, 2);
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id, role_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'manasi.arora@enstratify.com', 'Manasi', 'PL',
        'password',
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
VALUES ('IHF - KPI Submissions Workflow', 2, now(), 'System', null, null, 'SUBMISSION');


-- Workflow Statuses
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name)
values (now(), 'System', 'DRAFT', false, null, null, 1, 'Draft');
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name)
values (now(), 'System', 'ONGOING', false, null, null, 1, 'On Going');
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name)
values (now(), 'System', 'CLOSED', true, null, null, 1, 'Closed');
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name)
values (now(), 'System', 'NOT_SUBMITTED', false, null, null, 2, 'Not Submitted');
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name)
values (now(), 'System', 'SUBMITTED', false, null, null, 2, 'Submitted');
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name)
values (now(), 'System', 'MODIFICATIONS REQUESTED', false, null, null, 2,
        'Modifications Requested');
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
                                     workflow_id, display_name)
values (now(), 'System', 'ACCEPTED', true, null, null, 2, 'Accepted');

-- Workflow Status Transitions
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id,note_required)
values ('Onboard', now(), 'System', null, null, 1, 2, 2, 1,false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id,note_required)
values ('Onboard', now(), 'System', null, null, 1, 2, 3, 1,false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id,note_required)
values ('Close', now(), 'System', null, null, 2, 3, 2, 1,false);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id,note_required)
values ('Submit', now(), 'System', null, null, 4, 5, 4, 2,false);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id,note_required)
values ('Save Draft', now(), 'System', null, null, 4, 4, 4, 2,false);

insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id,note_required)
values ('Request Modifications', now(), 'System', null, null, 5, 6, 2, 2,true);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id,note_required)
values ('Submit Modications', now(), 'System', null, null, 6, 5, 4, 2,true);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
                                               updated_by, from_state_id, to_state_id, role_id,
                                               workflow_id,note_required)
values ('Accept', now(), 'System', null, null, 5, 7, 2, 2,false);
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
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
                                        updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 4, 4);

-- Grants
insert into public.grants (organization_id, name, description, created_at, created_by, updated_at,
                           updated_by, grantor_org_id, status_name, substatus_id, start_date,
                           end_date,
                           grant_status_id)
VALUES (1,
        'Improving Outcomes for Rural TB Patients in Private Care via Pharmacist Referrals and Community Health Workers (Health-IIH-20170104; (HEA/000/TEDT/036) )',
        'A placeholder in programming code may also be used to indicate where specific code needs to be added, but the programmer has not yet written the code.',
        now(), 'System', null, null, 2, 'ONGOING', 4, '2019-02-01', '2020-01-31', 2);

-- Grant KPIs
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.01.01.05 - No. of TB patients notified',
        'QUARTERLY', 'QUANTITATIVE', 4, true,
        'TBs.01.01.05 - No. of TB patients notified', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.06 - No. of private providers (pharmacists/phys icians/diagnostic centers) approached',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.03.01.06 - No. of private providers (pharmacists/phys icians/diagnostic centers) approached',
        null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.07 - No. of workshops organized',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.03.01.07 - No. of workshops organized', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'TBs.03.01.07 - No. of workshops organized',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.45.56.23 - No. of workshops Bootstraped', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'HSS.02.01.15 - No. of CHW trained',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'HSS.02.01.15 - No. of CHW trained', null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'Trainees enrolled in workshop',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'Trainees enrolled in workshop', null, null,
        1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System',
        'What changes were visible on the ground and amongest stake holders as a consequence of the project activities in this reporting period?',
        'QUARTERLY', 'QUALITATIVE', 12, true,
        'What changes were visible on the ground and amongest stake holders as a consequence of the project activities in this reporting period?',
        null, null, 1);
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System', 'What were the learnings in this period?', 'QUARTERLY', 'QUALITATIVE', 12,
        true, 'What were the learnings in this period?', null, null, 1);

insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id)
VALUES (now(), 'System', 'Quarterly Progress and Outlook Report', 'QUARTERLY', 'DOCUMENT', 12,
        true, 'Quarterly Progress and Outlook Report', null, null, 1);

-- Submissions
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-04-30', null, 'Quarter 1', null, null, 1, 4);
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-07-31', null, 'Quarter 2', null, null, 1, 4);
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-10-31', null, 'Quarter 3', null, null, 1, 4);
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2020-01-31', null, 'Quarter 4', null, null, 1, 4);
-- Quantitative Data
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 100, 1, 1);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 200, 1, 2);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 300, 1, 3);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 500, 1, 4);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 100, 2, 1);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 200, 2, 2);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 300, 2, 3);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 500, 2, 4);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 100, 3, 1);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 200, 3, 2);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 300, 3, 3);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 500, 3, 4);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 100, 4, 1);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 200, 4, 2);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 300, 4, 3);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 500, 4, 4);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 100, 5, 1);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 200, 5, 2);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 300, 5, 3);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 500, 5, 4);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 100, 6, 1);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 200, 6, 2);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 300, 6, 3);
insert into grant_quantitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values (0, 500, 6, 4);

-- Qualitative Data
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 7, 1);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 7, 2);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 7, 3);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 7, 4);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 8, 1);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 8, 2);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 8, 3);
insert into grant_qualitative_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 8, 4);

-- Document Data
insert into grant_document_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 9, 1);
insert into grant_document_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 9, 2);
insert into grant_document_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 9, 3);
insert into grant_document_kpi_data (actuals, goal, grant_kpi_id, submission_id)
values ('', '', 9, 4);




