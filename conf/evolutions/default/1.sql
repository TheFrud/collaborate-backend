# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table asset (
  id                        bigint not null,
  asset_container_id        bigint not null,
  title                     varchar(255),
  description               varchar(255),
  category                  varchar(255),
  creation_date             timestamp not null,
  user_id                   bigint,
  constraint pk_asset primary key (id))
;

create table asset_container (
  id                        bigint not null,
  project_id                bigint not null,
  title                     varchar(255),
  description               varchar(255),
  category                  varchar(255),
  creation_date             timestamp not null,
  status                    varchar(255),
  constraint pk_asset_container primary key (id))
;

create table comment (
  id                        bigint not null,
  asset_id                  bigint not null,
  text                      varchar(255),
  creation_date             timestamp not null,
  constraint pk_comment primary key (id))
;

create table project (
  id                        bigint not null,
  title                     varchar(256) not null,
  creation_date             timestamp not null,
  description               varchar(255),
  security_policy           varchar(255),
  constraint pk_project primary key (id))
;

create table tag (
  id                        bigint not null,
  project_id                bigint not null,
  text                      varchar(255),
  constraint pk_tag primary key (id))
;

create table user_ (
  id                        bigint not null,
  auth_token                varchar(255),
  email_address             varchar(256) not null,
  sha_password              bytea not null,
  full_name                 varchar(256) not null,
  creation_date             timestamp not null,
  constraint uq_user__email_address unique (email_address),
  constraint pk_user_ primary key (id))
;

create sequence asset_seq;

create sequence asset_container_seq;

create sequence comment_seq;

create sequence project_seq;

create sequence tag_seq;

create sequence user__seq;

alter table asset add constraint fk_asset_asset_container_1 foreign key (asset_container_id) references asset_container (id);
create index ix_asset_asset_container_1 on asset (asset_container_id);
alter table asset add constraint fk_asset_user_2 foreign key (user_id) references user_ (id);
create index ix_asset_user_2 on asset (user_id);
alter table asset_container add constraint fk_asset_container_project_3 foreign key (project_id) references project (id);
create index ix_asset_container_project_3 on asset_container (project_id);
alter table comment add constraint fk_comment_asset_4 foreign key (asset_id) references asset (id);
create index ix_comment_asset_4 on comment (asset_id);
alter table tag add constraint fk_tag_project_5 foreign key (project_id) references project (id);
create index ix_tag_project_5 on tag (project_id);



# --- !Downs

drop table if exists asset cascade;

drop table if exists asset_container cascade;

drop table if exists comment cascade;

drop table if exists project cascade;

drop table if exists tag cascade;

drop table if exists user_ cascade;

drop sequence if exists asset_seq;

drop sequence if exists asset_container_seq;

drop sequence if exists comment_seq;

drop sequence if exists project_seq;

drop sequence if exists tag_seq;

drop sequence if exists user__seq;

