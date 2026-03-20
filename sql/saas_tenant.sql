create table if not exists sys_plan (
  plan_id      bigint(20)   not null auto_increment,
  plan_name    varchar(100) not null,
  status       char(1)      default '0',
  create_by    varchar(64)  default '',
  create_time  datetime,
  update_by    varchar(64)  default '',
  update_time  datetime,
  remark       varchar(500) default null,
  primary key (plan_id)
) engine=innodb auto_increment=1 comment='租户套餐表';

create table if not exists sys_plan_feature (
  plan_id      bigint(20)   not null,
  feature_code varchar(50)  not null,
  primary key (plan_id, feature_code)
) engine=innodb comment='套餐功能表';

create table if not exists sys_tenant (
  tenant_id    bigint(20)   not null auto_increment,
  tenant_name  varchar(200) not null,
  contact_name varchar(100) default '',
  contact_phone varchar(50) default '',
  status       char(1)      default '0',
  create_by    varchar(64)  default '',
  create_time  datetime,
  update_by    varchar(64)  default '',
  update_time  datetime,
  remark       varchar(500) default null,
  primary key (tenant_id)
) engine=innodb auto_increment=1 comment='租户表';

create table if not exists sys_tenant_subscription (
  sub_id       bigint(20)   not null auto_increment,
  tenant_id    bigint(20)   not null,
  plan_id      bigint(20)   not null,
  start_time   datetime     not null,
  end_time     datetime     not null,
  status       char(1)      default '0',
  create_by    varchar(64)  default '',
  create_time  datetime,
  update_by    varchar(64)  default '',
  update_time  datetime,
  remark       varchar(500) default null,
  primary key (sub_id),
  key idx_sub_tenant (tenant_id)
) engine=innodb auto_increment=1 comment='租户订阅表';

set @db_name := (select database());

set @c := (select count(1) from information_schema.columns where table_schema=@db_name and table_name='sys_user' and column_name='tenant_id');
set @s := if(@c = 0, 'alter table sys_user add column tenant_id bigint(20) default 1 after dept_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema=@db_name and table_name='sys_link' and column_name='tenant_id');
set @s := if(@c = 0, 'alter table sys_link add column tenant_id bigint(20) default 1 after link_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema=@db_name and table_name='sys_ticket' and column_name='tenant_id');
set @s := if(@c = 0, 'alter table sys_ticket add column tenant_id bigint(20) default 1 after ticket_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema=@db_name and table_name='sys_number' and column_name='tenant_id');
set @s := if(@c = 0, 'alter table sys_number add column tenant_id bigint(20) default 1 after number_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @s := 'update sys_user set tenant_id = 1 where (tenant_id is null or tenant_id = 0) and user_id > 0';
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @s := 'update sys_link set tenant_id = 1 where (tenant_id is null or tenant_id = 0) and link_id > 0';
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @s := 'update sys_ticket set tenant_id = 1 where (tenant_id is null or tenant_id = 0) and ticket_id > 0';
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @s := 'update sys_number set tenant_id = 1 where (tenant_id is null or tenant_id = 0) and number_id > 0';
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.statistics where table_schema=@db_name and table_name='sys_link' and index_name='idx_sys_link_tenant');
set @s := if(@c = 0, 'create index idx_sys_link_tenant on sys_link(tenant_id, link_id)', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.statistics where table_schema=@db_name and table_name='sys_ticket' and index_name='idx_sys_ticket_tenant');
set @s := if(@c = 0, 'create index idx_sys_ticket_tenant on sys_ticket(tenant_id, ticket_id)', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.statistics where table_schema=@db_name and table_name='sys_number' and index_name='idx_sys_number_tenant');
set @s := if(@c = 0, 'create index idx_sys_number_tenant on sys_number(tenant_id, number_id)', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

insert into sys_plan(plan_id, plan_name, status, create_by, create_time, remark)
select 1, '默认套餐', '0', 'admin', sysdate(), '默认套餐'
where not exists (select 1 from sys_plan where plan_id = 1);

insert into sys_plan_feature(plan_id, feature_code)
select 1, 'LINK' where not exists (select 1 from sys_plan_feature where plan_id = 1 and feature_code = 'LINK');
insert into sys_plan_feature(plan_id, feature_code)
select 1, 'TICKET' where not exists (select 1 from sys_plan_feature where plan_id = 1 and feature_code = 'TICKET');
insert into sys_plan_feature(plan_id, feature_code)
select 1, 'NUMBER' where not exists (select 1 from sys_plan_feature where plan_id = 1 and feature_code = 'NUMBER');

insert into sys_tenant(tenant_id, tenant_name, status, create_by, create_time, remark)
select 1, '默认租户', '0', 'admin', sysdate(), '默认租户'
where not exists (select 1 from sys_tenant where tenant_id = 1);

insert into sys_tenant_subscription(sub_id, tenant_id, plan_id, start_time, end_time, status, create_by, create_time, remark)
select 1, 1, 1, sysdate(), date_add(sysdate(), interval 10 year), '0', 'admin', sysdate(), '默认订阅'
where not exists (select 1 from sys_tenant_subscription where tenant_id = 1 and status = '0');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '租户管理', '0', '9', 'tenant', 'system/tenant/index', 1, 0, 'C', '0', '0', 'system:tenant:list', 'tree', 'admin', sysdate(), '', null, '租户管理菜单'
where not exists (
  select 1 from sys_menu where menu_type = 'C' and component = 'system/tenant/index'
);
select @tenant_menu_id := (select menu_id from sys_menu where menu_type = 'C' and component = 'system/tenant/index' order by menu_id desc limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '租户查询', @tenant_menu_id, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:tenant:query',  '#', 'admin', sysdate(), '', null, ''
where @tenant_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @tenant_menu_id and menu_type='F' and perms='system:tenant:query');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '租户新增', @tenant_menu_id, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:tenant:add',    '#', 'admin', sysdate(), '', null, ''
where @tenant_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @tenant_menu_id and menu_type='F' and perms='system:tenant:add');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '租户修改', @tenant_menu_id, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:tenant:edit',   '#', 'admin', sysdate(), '', null, ''
where @tenant_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @tenant_menu_id and menu_type='F' and perms='system:tenant:edit');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '租户删除', @tenant_menu_id, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:tenant:remove', '#', 'admin', sysdate(), '', null, ''
where @tenant_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @tenant_menu_id and menu_type='F' and perms='system:tenant:remove');
