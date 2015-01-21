# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table asset (
  id                        bigint not null,
  asset_container_id        bigint not null,
  title                     varchar(255),
  description               TEXT,
  category                  varchar(255),
  link                      varchar(255),
  creation_date             timestamp not null,
  user_id                   bigint,
  constraint pk_asset primary key (id))
;

create table asset_activity (
  id                        bigint not null,
  creation_date             timestamp not null,
  message                   varchar(255),
  constraint pk_asset_activity primary key (id))
;

create table asset_container (
  id                        bigint not null,
  title                     varchar(255),
  description               TEXT,
  category                  varchar(255),
  creation_date             timestamp not null,
  project_id                bigint,
  status                    varchar(255),
  constraint pk_asset_container primary key (id))
;

create table asset_container_activity (
  id                        bigint not null,
  creation_date             timestamp not null,
  message                   varchar(255),
  constraint pk_asset_container_activity primary key (id))
;

create table project (
  id                        bigint not null,
  title                     varchar(256) not null,
  creation_date             timestamp not null,
  description               TEXT,
  security_policy           varchar(255),
  constraint pk_project primary key (id))
;

create table project_activity (
  id                        bigint not null,
  project_id                bigint not null,
  creation_date             timestamp not null,
  message                   varchar(255),
  constraint pk_project_activity primary key (id))
;

create table tag (
  id                        bigint not null,
  project_id                bigint not null,
  text                      varchar(255),
  constraint pk_tag primary key (id))
;

create table userr (
  id                        bigint not null,
  auth_token                varchar(255),
  username                  varchar(255),
  bio                       TEXT,
  rating                    bigint,
  email_address             varchar(256) not null,
  sha_password              bytea not null,
  full_name                 varchar(256) not null,
  creation_date             timestamp not null,
  constraint uq_userr_email_address unique (email_address),
  constraint pk_userr primary key (id))
;


create table project_userr (
  project_id                     bigint not null,
  userr_id                       bigint not null,
  constraint pk_project_userr primary key (project_id, userr_id))
;
create sequence asset_seq;

create sequence asset_activity_seq;

create sequence asset_container_seq;

create sequence asset_container_activity_seq;

create sequence project_seq;

create sequence project_activity_seq;

create sequence tag_seq;

create sequence userr_seq;

alter table asset add constraint fk_asset_asset_container_1 foreign key (asset_container_id) references asset_container (id);
create index ix_asset_asset_container_1 on asset (asset_container_id);
alter table asset add constraint fk_asset_user_2 foreign key (user_id) references userr (id);
create index ix_asset_user_2 on asset (user_id);
alter table asset_container add constraint fk_asset_container_project_3 foreign key (project_id) references project (id);
create index ix_asset_container_project_3 on asset_container (project_id);
alter table project_activity add constraint fk_project_activity_project_4 foreign key (project_id) references project (id);
create index ix_project_activity_project_4 on project_activity (project_id);
alter table tag add constraint fk_tag_project_5 foreign key (project_id) references project (id);
create index ix_tag_project_5 on tag (project_id);



alter table project_userr add constraint fk_project_userr_project_01 foreign key (project_id) references project (id);

alter table project_userr add constraint fk_project_userr_userr_02 foreign key (userr_id) references userr (id);

# --- !Downs

drop table if exists asset cascade;

drop table if exists asset_activity cascade;

drop table if exists asset_container cascade;

drop table if exists asset_container_activity cascade;

drop table if exists project cascade;

drop table if exists project_userr cascade;

drop table if exists project_activity cascade;

drop table if exists tag cascade;

drop table if exists userr cascade;

drop sequence if exists asset_seq;

drop sequence if exists asset_activity_seq;

drop sequence if exists asset_container_seq;

drop sequence if exists asset_container_activity_seq;

drop sequence if exists project_seq;

drop sequence if exists project_activity_seq;

drop sequence if exists tag_seq;

drop sequence if exists userr_seq;

