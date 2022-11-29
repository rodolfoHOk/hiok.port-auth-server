delete from oauth2_registered_client;
delete from oauth2_authorization;
delete from oauth2_authorization_consent;
delete from user_group;
delete from group_role;
delete from users;
delete from groups;
delete from roles;

insert into roles (id, name, description) values (1, 'ADMIN', 'administrator user');
insert into roles (id, name, description) values (2, 'USER', 'common user');
insert into roles (id, name, description) values (3, 'CLIENT', 'common client');

insert into groups (id, name, description) values (1, 'ADMINS', 'administrators group');
insert into groups (id, name, description) values (2, 'USERS', 'users group');
insert into groups (id, name, description) values (3, 'CLIENTS', 'clients group');

insert into group_role (group_id, role_id) values (1, 1);
insert into group_role (group_id, role_id) values (1, 2);
insert into group_role (group_id, role_id) values (2, 2);
insert into group_role (group_id, role_id) values (3, 3);

insert into users (id, name, email, password, active, created_at) 
values ('5f953932-20ab-4bbd-89d2-cd1779964804', 'General Administrator', 'admin@portfolio.com.br', '$2y$10$gHMGh9FwYKJQTrensP6az.Xhgen.Jc4zao8xly4W3bH1TZXFaXpmO', true, current_timestamp); 
insert into users (id, name, email, password, active, created_at) 
values ('2b0c71cb-1d0b-4f1e-b11b-b46fd51192e0', 'Test User', 'user@portfolio.com.br', '$2y$10$Y.a3dbAuRTCYNPIB9K7rvexmBiDi5mkp3ABnXvT3JTCfUKdcJIKRe', true, current_timestamp);
insert into users (id, name, email, password, active, created_at) 
values ('6a9dcc34-06e0-4dc3-a7c7-ce0a82097e1e', 'Test Client', 'client@portfolio.com.br', '$2y$10$OGJQhXd5w84VmfVE5qgAIedSOsCgPdBdprX628SGbm3bMeyAlavOi', true, current_timestamp); 

insert into user_group (user_id, group_id) values ('5f953932-20ab-4bbd-89d2-cd1779964804', 1);
insert into user_group (user_id, group_id) values ('2b0c71cb-1d0b-4f1e-b11b-b46fd51192e0', 2);
insert into user_group (user_id, group_id) values ('6a9dcc34-06e0-4dc3-a7c7-ce0a82097e1e', 3);
