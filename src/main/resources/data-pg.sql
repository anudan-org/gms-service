-- noinspection SqlDialectInspectionForFile

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
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Finance Mananger', 2, null, null); --7
insert into roles (created_at, created_by, name, organization_id, updated_at, updated_by)
values (now(), 'System', 'Finance Lead', 2, null, null); --8

-- Role Permissions
insert into roles_permission (permission, role_id) VALUES ('Create Grant',3);
insert into roles_permission (permission, role_id) VALUES ('Manage Workflows',3);


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
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'jayeeta@indiahealthfund.org', 'Jayeeta', 'PM',
        'password', null, null, 2); -- 9
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'jchowdhury@tatatrusts.org', 'Chowdhury', 'PL',
        'password', null, null, 2); -- 10
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'shreyas@indiahealthfund.org', 'Shreyas', 'FM',
        'password', null, null, 2); -- 11
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'loknath@indiahealthfund.org', 'Loknath', 'FL',
        'password', null, null, 2); -- 12
INSERT INTO public.users (created_at, created_by, email_id, first_name, last_name, password,
                          updated_at, updated_by, organization_id)
VALUES ('2019-04-08 03:00:16.545000', 'System', 'vapte@tatatrusts.org', 'Vapte', 'Partner',
        'password', null, null, 1); -- 13

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
insert into user_roles (role_id, user_id) VALUES (3,9);
insert into user_roles (role_id, user_id) VALUES (2,10);
insert into user_roles (role_id, user_id) VALUES (7,11);
insert into user_roles (role_id, user_id) VALUES (8,12);

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
values (now(), 'System','DRAFT (Grant)', FALSE, null, null, 1, 'DRAFT (Grant)',TRUE); --1
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','ONBOARDING APPROVAL REQUESTED', FALSE, null, null, 1, 'ONBOARDING APPROVAL REQUESTED',FALSE); --2
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','ONBOARDING APPROVED', FALSE, null, null, 1, 'ONBOARDING APPROVED',FALSE); --3
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','ONBOARDING REJECTED', FALSE, null, null, 1, 'ONBOARDING REJECTED',FALSE); --4
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','ONGOING', FALSE, null, null, 1, 'ONGOING',FALSE); --5
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','CLOSURE INITIATED', FALSE, null, null, 1, 'CLOSURE INITIATED',FALSE); --6
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','CLOSURE REPORT SUBMITTED', FALSE, null, null, 1, 'CLOSURE REPORT SUBMITTED',FALSE); --7
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','CLOSURE APPROVAL REQUESTED', FALSE, null, null, 1, 'CLOSURE APPROVAL REQUESTED',FALSE); --8
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','CLOSURE MODIFICATIONS REQUESTED', FALSE, null, null, 1, 'CLOSURE MODIFICATIONS REQUESTED',FALSE); --9
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','CLOSED', TRUE, null, null, 1, 'CLOSED',FALSE); --10
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','DRAFT', FALSE, null, null, 2, 'DRAFT',TRUE); --11
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','APROVAL PENDING', FALSE, null, null, 2, 'APROVAL PENDING',FALSE); --12
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','APPROVED', FALSE, null, null, 2, 'APPROVED',FALSE); --13
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','MODIFICATIONS REQUESTED', FALSE, null, null, 2, 'MODIFICATIONS REQUESTED',FALSE); --14
insert into public.workflow_statuses(created_at, created_by, name, terminal, updated_at, updated_by,
workflow_id, display_name,initial)
values (now(), 'System','MODIFICATIONS SUBMITTED', TRUE, null, null, 2, 'MODIFICATIONS SUBMITTED',FALSE); --15
-- Workflow Status Transitions
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Request Onboarding Approval', now(), 'System', null, null, 1, 2, 3, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Approve for Onboarding', now(), 'System', null, null, 2, 3, 2, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Request Modifications', now(), 'System', null, null, 2, 4, 2, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Submit Modifications', now(), 'System', null, null, 4, 2, 3, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Onboard Grant', now(), 'System', null, null, 3, 5, 3, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Initiate Grant Closure', now(), 'System', null, null, 5, 6, 3, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Submit Closure Report', now(), 'System', null, null, 6, 7, 4, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Request Closure Approval', now(), 'System', null, null, 7, 8, 3, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Close Grant', now(), 'System', null, null, 8, 10, 2, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Request Modifications', now(), 'System', null, null, 7, 9, 3, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Submit Modifications', now(), 'System', null, null, 9, 7, 4, 1, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Submit Report', now(), 'System', null, null, 11, 12, 4, 2, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Approve Submission', now(), 'System', null, null, 12, 13, 3, 2, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Request Modifications', now(), 'System', null, null, 12, 14, 3, 2, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Submit Modifications', now(), 'System', null, null, 14, 15, 4, 2, false);
insert into public.workflow_status_transitions(action, created_at, created_by, updated_at,
updated_by, from_state_id, to_state_id, role_id,
workflow_id, note_required)
values ('Approve Submission', now(), 'System', null, null, 15, 13, 3, 2, false);
-- Workflow State Permissions
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 3, 1);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 2, 2);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 2);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 2);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 2);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 3);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 3);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 3);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 3);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 3, 4);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 4);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 4);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 4);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 5);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 6);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 6);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 6);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 6);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 6);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 9);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 9);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 9);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 9);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 9);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 7);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 8);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 8);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 8);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 8);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 8);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 4, 10);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 3, 10);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 2, 10);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 8, 10);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'VIEW', null, null, 7, 10);
insert into workflow_state_permissions (created_at, created_by, permission, updated_at,
updated_by, role_id, workflow_status_id)
VALUES (now(), 'System', 'MANAGE', null, null, 4, 11);

-- Grants
insert into public.grants (organization_id, name, description, created_at, created_by, updated_at,
                           updated_by, grantor_org_id, status_name, substatus_id, start_date,
                           end_date,
                           grant_status_id, amount)
VALUES (1,
        'Improving Outcomes for Rural TB Patients in Private Care',
        'Improving Outcomes for Rural TB Patients in Private Care via Pharmacist Referrals and Community Health Workers (Health-IIH-20170104; (HEA/000/TEDT/036) )',
        now(), 'System', null, null, 2, 'ONGOING', 11, '2019-02-01', '2020-01-31', 1,3000000);
insert into public.grants (organization_id, name, description, created_at, created_by, updated_at,
                           updated_by, grantor_org_id, status_name, substatus_id, start_date,
                           end_date,
                           grant_status_id,amount)
VALUES (1,
        'When one gains density and volume, one is able to receive mind.',
        'Yes, there is wonderland, it dies with zen.',
        now(), 'System', null, null, 4, 'ONGOING', 12, '2019-02-01', '2020-01-31', 1,5000000);

-- Grant KPIs
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System',
        'TBs.01.01.05 - No. of TB patients notified',
        'QUARTERLY', 'QUANTITATIVE', 4, true,
        'TBs.01.01.05 - No. of TB patients notified', null, null, 1,'ACTIVITY'); -- 1
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System',
        'TBs.03.01.06 - No. of private providers (pharmacists/phys icians/diagnostic centers) approached',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.03.01.06 - No. of private providers (pharmacists/phys icians/diagnostic centers) approached',
        null, null, 1,'ACTIVITY'); -- 2
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System',
        'TBs.03.01.07 - No. of workshops organized',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.03.01.07 - No. of workshops organized', null, null, 1,'ACTIVITY'); -- 3
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System',
        'TBs.03.01.07 - No. of workshops organized',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'TBs.45.56.23 - No. of workshops Bootstraped', null, null, 1,'ACTIVITY'); -- 4
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System',
        'HSS.02.01.15 - No. of CHW trained',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'HSS.02.01.15 - No. of CHW trained', null, null, 1,'ACTIVITY'); -- 5
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System',
        'Trainees enrolled in workshop',
        'QUARTERLY', 'QUANTITATIVE', 12, true,
        'Trainees enrolled in workshop', null, null,
        1,'ACTIVITY'); -- 6
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System',
        'What changes were visible on the ground and amongest stake holders as a consequence of the project activities in this reporting period?',
        'QUARTERLY', 'QUALITATIVE', 12, true,
        'What changes were visible on the ground and amongest stake holders as a consequence of the project activities in this reporting period?',
        null, null, 1,null); -- 7
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System', 'What were the learnings in this period?', 'QUARTERLY', 'QUALITATIVE', 12,
        true, 'What were the learnings in this period?', null, null, 1,null); -- 8

insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System', 'Quarterly Progress and Outlook Report', 'QUARTERLY', 'DOCUMENT', 12,
        true, 'Quarterly Progress and Outlook Report', null, null, 1,null); -- 9
insert into public.grant_kpis (created_at, created_by, description, periodicity_unit,
                               kpi_type, periodicity, is_scheduled, title, updated_at,
                               updated_by, grant_id,kpi_reporting_type)
VALUES (now(), 'System',
        'Sample KPI Description',
        'HALF_YEARLY', 'QUANTITATIVE', 6, true,
        'Sample KPI',
        null, null, 2,'ACTIVITY'); -- 10

-- Submissions
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-04-30', null, 'Quarter 1', null, null, 1, 11); -- 1
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-08-31', null, 'Quarter 2', null, null, 1, 11); -- 2
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2019-10-31', null, 'Quarter 3', null, null, 1, 11); -- 3
insert into submissions (created_at, created_by, submit_by, submitted_on, title, updated_at,
                         updated_by, grant_id, submission_status_id)
VALUES (now(), 'System', '2020-01-31', null, 'Quarter 4', null, null, 1, 11); -- 4
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
VALUES (false, 'Purpose');
insert into grant_sections (deletable, section_name)
VALUES (false, 'Project Approach');
insert into grant_sections (deletable, section_name)
VALUES (true, 'Project Risks/challenges');
insert into grant_sections (deletable, section_name)
VALUES (true, 'Grant Terms & Conditions');

-- Grant Section Attributes ast Platform Level
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Description', 'multiline', true, null, 1); --1
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Description', 'multiline', true, null, 2); --2
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Description', 'multiline', true, null, 3); -- 3
insert into grant_section_attributes (deletable, field_name, field_type, required, type, section_id)
VALUES (false, 'Description', 'multiline', true, null, 4); -- 4


-- insert granter's grant sections configuration
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Purpose', 2); -- 1
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Project Approach', 2); -- 2
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Project Risks/challenges', 2); -- 3
insert into granter_grant_sections (deletable, section_name, granter_id)
VALUES (true, 'Grant Terms & Conditions', 2); -- 4



-- Insert Granter's grant section attributes
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Description', 'multiline', true, 2, 1); -- 1
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Description', 'multiline', true, 2, 2); -- 2
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Description', 'multiline', true, 2, 3); -- 3
insert into granter_grant_section_attributes (deletable, field_name, field_type, required,
                                              granter_id, section_id)
values (true, 'Description', 'multiline', true, 2, 4); -- 4


-- Insert Grant additional String based attributes
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('', 1, 1, 1);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('', 1, 2, 2);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('', 1, 3, 3);
insert into grant_string_attributes (value, grant_id, section_id, section_attribute_id)
VALUES ('Generic Terms and conditions', 1, 4, 4);


-- Granter Template
insert into templates (description, location, name, type, version, kpi_id, file_type)
VALUES ('Utilization reports are to be submitted when requested. It contains financial expenditure details for a given reporting period.',
        'templates/kpi/', 'utilization_report.xls', 'kpi', 1, 9, 'xls');
insert into templates (description, location, name, type, version, kpi_id, file_type)
VALUES ('Helper document to manage project budget', 'templates/kpi/', 'project_budget_sheet.xlsx',
        'kpi', 1, 9, 'xls');

-- Notifications
insert into notifications(message,read,user_id) values('New message',false,4);
