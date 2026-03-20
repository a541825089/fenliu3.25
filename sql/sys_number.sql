drop table if exists sys_number;
create table sys_number (
  number_id       bigint(20)    not null auto_increment,
  link_id         bigint(20)    default null,
  ticket_no       varchar(500)  default '',
  number_value    varchar(200)  not null,
  number_type     varchar(100)  default '',
  visit_count     int(11)       default 0,
  in_count        int(11)       default 0,
  status          char(1)       default '0',
  create_by       varchar(64)   default '',
  create_time     datetime,
  update_by       varchar(64)   default '',
  update_time     datetime,
  remark          varchar(500)  default null,
  primary key (number_id),
  key idx_number_link_id (link_id),
  key idx_number_type (number_type),
  key idx_number_status (status),
  key idx_number_ticket_no (ticket_no)
) engine=innodb auto_increment=100 comment='号码管理表';

set @db_name := (select database());

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'link_id');
set @s := if(@c = 0, 'alter table sys_number add column link_id bigint(20) default null after number_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'ticket_no');
set @s := if(@c = 0, 'alter table sys_number add column ticket_no varchar(500) default '''' after link_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'number_value');
set @s := if(@c = 0, 'alter table sys_number add column number_value varchar(200) not null after ticket_no', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'number_type');
set @s := if(@c = 0, 'alter table sys_number add column number_type varchar(100) default '''' after number_value', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'visit_count');
set @s := if(@c = 0, 'alter table sys_number add column visit_count int(11) default 0 after number_type', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'in_count');
set @s := if(@c = 0, 'alter table sys_number add column in_count int(11) default 0 after visit_count', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'status');
set @s := if(@c = 0, 'alter table sys_number add column status char(1) default ''0'' after in_count', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'create_by');
set @s := if(@c = 0, 'alter table sys_number add column create_by varchar(64) default '''' after status', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'create_time');
set @s := if(@c = 0, 'alter table sys_number add column create_time datetime after create_by', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'update_by');
set @s := if(@c = 0, 'alter table sys_number add column update_by varchar(64) default '''' after create_time', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'update_time');
set @s := if(@c = 0, 'alter table sys_number add column update_time datetime after update_by', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_number' and column_name = 'remark');
set @s := if(@c = 0, 'alter table sys_number add column remark varchar(500) default null after update_time', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('号码管理', '0', '3', 'number', 'system/number/index', 1, 0, 'C', '0', '0', 'system:number:list', 'phone', 'admin', sysdate(), '', null, '号码管理菜单');

select @number_menu_id := last_insert_id();

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('号码查询', @number_menu_id, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:number:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('号码新增', @number_menu_id, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:number:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('号码修改', @number_menu_id, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:number:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('号码删除', @number_menu_id, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:number:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('号码导出', @number_menu_id, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:number:export',       '#', 'admin', sysdate(), '', null, '');
