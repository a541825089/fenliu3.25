-- ----------------------------
-- 1、表结构
-- ----------------------------
drop table if exists sys_link;
create table sys_link (
  link_id           bigint(20)      not null auto_increment    comment '链接ID',
  target_country    varchar(50)     default ''                 comment '投放国家',
  link_url          varchar(500)    not null                   comment '链接URL',
  link_description  varchar(500)    default ''                 comment '链接描述',
  reply_msg         varchar(1000)   default ''                 comment '回复语',
  ip_protection     char(1)         default '1'                comment 'IP防护（0开启 1关闭）',
  is_scramble       char(1)         default '1'                comment '随机打乱（0开启 1关闭）',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (link_id)
) engine=innodb auto_increment=100 comment = '链接管理表';

-- ----------------------------
-- 1.1、兼容升级（旧表缺字段时执行，不会重复添加）
-- ----------------------------
set @db_name := (select database());

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'target_country');
set @s := if(@c = 0, 'alter table sys_link add column target_country varchar(50) default '''' comment ''投放国家'' after link_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'link_description');
set @s := if(@c = 0, 'alter table sys_link add column link_description varchar(500) default '''' comment ''链接描述'' after link_url', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'reply_msg');
set @s := if(@c = 0, 'alter table sys_link add column reply_msg varchar(1000) default '''' comment ''回复语'' after link_description', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'ip_protection');
set @s := if(@c = 0, 'alter table sys_link add column ip_protection char(1) default ''1'' comment ''IP防护（0开启 1关闭）'' after reply_msg', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'is_scramble');
set @s := if(@c = 0, 'alter table sys_link add column is_scramble char(1) default ''1'' comment ''随机打乱（0开启 1关闭）'' after ip_protection', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'status');
set @s := if(@c = 0, 'alter table sys_link add column status char(1) default ''0'' comment ''状态（0正常 1停用）'' after is_scramble', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'update_by');
set @s := if(@c = 0, 'alter table sys_link add column update_by varchar(64) default '''' comment ''更新者'' after create_time', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'update_time');
set @s := if(@c = 0, 'alter table sys_link add column update_time datetime comment ''更新时间'' after update_by', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_link' and column_name = 'remark');
set @s := if(@c = 0, 'alter table sys_link add column remark varchar(500) default null comment ''备注'' after update_time', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

-- ----------------------------
-- 2、菜单数据
-- ----------------------------
-- 一级菜单: 链接管理
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('链接管理', '0', '1', 'link', 'system/link/index', 1, 0, 'C', '0', '0', 'system:link:list', 'link', 'admin', sysdate(), '', null, '链接管理菜单');

-- 按钮权限
select @menu_id := last_insert_id();

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('链接查询', @menu_id, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:link:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('链接新增', @menu_id, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:link:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('链接修改', @menu_id, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:link:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('链接删除', @menu_id, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:link:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('链接导出', @menu_id, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:link:export',       '#', 'admin', sysdate(), '', null, '');
