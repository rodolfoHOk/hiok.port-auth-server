CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
	id uuid NOT NULL,
	created_at timestamp NOT NULL,
	email varchar(150) NOT NULL,
	"name" varchar(100) NOT NULL,
	"password" varchar(255) NOT NULL,
	active bool NOT NULL DEFAULT false,
	CONSTRAINT users_email_uc UNIQUE (email),
	CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE "groups" (
	id bigserial NOT NULL,
	description varchar(150) NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT groups_pk PRIMARY KEY (id)
);

CREATE TABLE roles (
	id bigserial NOT NULL,
	description varchar(150) NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT roles_pk PRIMARY KEY (id)
);

CREATE TABLE group_role (
	group_id int8 NOT NULL,
	role_id int8 NOT NULL,
	CONSTRAINT group_role_pk PRIMARY KEY (group_id, role_id),
	CONSTRAINT group_role_groups_fk FOREIGN KEY (group_id) REFERENCES public."groups"(id),
	CONSTRAINT group_role_roles_fk FOREIGN KEY (role_id) REFERENCES public.roles(id)
);

CREATE TABLE user_group (
	user_id uuid NOT NULL,
	group_id int8 NOT NULL,
	CONSTRAINT user_group_pk PRIMARY KEY (user_id, group_id),
	CONSTRAINT user_group_users_fk FOREIGN KEY (user_id) REFERENCES public.users(id),
	CONSTRAINT user_group_groups_fk FOREIGN KEY (group_id) REFERENCES public."groups"(id)
);
