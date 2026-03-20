-- ----------------------------
-- 1、表结构
-- ----------------------------
drop table if exists sys_ticket;
create table sys_ticket (
  ticket_id          bigint(20)      not null auto_increment    comment '工单ID',
  ticket_type        varchar(100)    default ''                 comment '工单类型（字典）',
  ticket_name        varchar(200)    not null                   comment '工单名称',
  ticket_link        varchar(1000)   default ''                 comment '工单链接',
  link_id            bigint(20)      default null               comment '分流链接ID（关联sys_link）',
  number_type        varchar(100)    default ''                 comment '号码类型（字典）',
  start_time         datetime                                   comment '开始时间',
  end_time           datetime                                   comment '到期时间',
  total_count        int(11)         default 0                  comment '工单总量',
  finish_count       int(11)         default 0                  comment '完成数量',
  down_ratio         int(11)         default 0                  comment '下号比率',
  ticket_account     varchar(200)    default ''                 comment '工单账号',
  ticket_password    varchar(200)    default ''                 comment '工单密码',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (ticket_id),
  key idx_ticket_link_id (link_id),
  key idx_ticket_status (status),
  key idx_ticket_type (ticket_type)
) engine=innodb auto_increment=100 comment = '工单管理表';

-- ----------------------------
-- 1.1、兼容升级（旧表缺字段时执行，不会重复添加）
-- ----------------------------
set @db_name := (select database());

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'ticket_type');
set @s := if(@c = 0, 'alter table sys_ticket add column ticket_type varchar(100) default '''' comment ''工单类型（字典）'' after ticket_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'ticket_name');
set @s := if(@c = 0, 'alter table sys_ticket add column ticket_name varchar(200) not null comment ''工单名称'' after ticket_type', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'ticket_link');
set @s := if(@c = 0, 'alter table sys_ticket add column ticket_link varchar(1000) default '''' comment ''工单链接'' after ticket_name', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'link_id');
set @s := if(@c = 0, 'alter table sys_ticket add column link_id bigint(20) default null comment ''分流链接ID（关联sys_link）'' after ticket_link', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'number_type');
set @s := if(@c = 0, 'alter table sys_ticket add column number_type varchar(100) default '''' comment ''号码类型（字典）'' after link_id', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'start_time');
set @s := if(@c = 0, 'alter table sys_ticket add column start_time datetime comment ''开始时间'' after number_type', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'end_time');
set @s := if(@c = 0, 'alter table sys_ticket add column end_time datetime comment ''到期时间'' after start_time', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'total_count');
set @s := if(@c = 0, 'alter table sys_ticket add column total_count int(11) default 0 comment ''工单总量'' after end_time', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'finish_count');
set @s := if(@c = 0, 'alter table sys_ticket add column finish_count int(11) default 0 comment ''完成数量'' after total_count', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'down_ratio');
set @s := if(@c = 0, 'alter table sys_ticket add column down_ratio int(11) default 0 comment ''下号比率'' after finish_count', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'ticket_account');
set @s := if(@c = 0, 'alter table sys_ticket add column ticket_account varchar(200) default '''' comment ''工单账号'' after down_ratio', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'ticket_password');
set @s := if(@c = 0, 'alter table sys_ticket add column ticket_password varchar(200) default '''' comment ''工单密码'' after ticket_account', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'status');
set @s := if(@c = 0, 'alter table sys_ticket add column status char(1) default ''0'' comment ''状态（0正常 1停用）'' after ticket_password', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'create_by');
set @s := if(@c = 0, 'alter table sys_ticket add column create_by varchar(64) default '''' comment ''创建者'' after status', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'create_time');
set @s := if(@c = 0, 'alter table sys_ticket add column create_time datetime comment ''创建时间'' after create_by', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'update_by');
set @s := if(@c = 0, 'alter table sys_ticket add column update_by varchar(64) default '''' comment ''更新者'' after create_time', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'update_time');
set @s := if(@c = 0, 'alter table sys_ticket add column update_time datetime comment ''更新时间'' after update_by', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

set @c := (select count(1) from information_schema.columns where table_schema = @db_name and table_name = 'sys_ticket' and column_name = 'remark');
set @s := if(@c = 0, 'alter table sys_ticket add column remark varchar(500) default null comment ''备注'' after update_time', 'select 1');
prepare stmt from @s; execute stmt; deallocate prepare stmt;

-- ----------------------------
-- 2、字典数据
-- ----------------------------
insert into sys_dict_type(dict_name, dict_type, status, create_by, create_time, remark)
select '工单类型', 'ticket_type', '0', 'admin', sysdate(), '工单类型'
where not exists (select 1 from sys_dict_type where dict_type = 'ticket_type');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 1, '云控', 'cloud', 'ticket_type', '', 'default', 'Y', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_type' and dict_value = 'cloud');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 2, '007', '007', 'ticket_type', '', 'default', 'N', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_type' and dict_value = '007');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 3, '海王', 'haiwang', 'ticket_type', '', 'default', 'N', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_type' and dict_value = 'haiwang');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 4, '太极云控', 'taiji', 'ticket_type', '', 'default', 'N', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_type' and dict_value = 'taiji');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 5, '火箭云控', 'rocket', 'ticket_type', '', 'default', 'N', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_type' and dict_value = 'rocket');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 6, 'SCRM Champion', 'scrm_champion', 'ticket_type', '', 'default', 'N', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_type' and dict_value = 'scrm_champion');

insert into sys_dict_type(dict_name, dict_type, status, create_by, create_time, remark)
select '号码类型', 'ticket_number_type', '0', 'admin', sysdate(), '号码类型'
where not exists (select 1 from sys_dict_type where dict_type = 'ticket_number_type');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 1, 'WhatsApp', 'whatsapp', 'ticket_number_type', '', 'default', 'Y', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_number_type' and dict_value = 'whatsapp');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 2, 'LINE', 'line', 'ticket_number_type', '', 'default', 'N', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_number_type' and dict_value = 'line');

insert into sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
select 3, 'Telegram', 'telegram', 'ticket_number_type', '', 'default', 'N', '0', 'admin', sysdate(), ''
where not exists (select 1 from sys_dict_data where dict_type = 'ticket_number_type' and dict_value = 'telegram');

-- ----------------------------
-- 3、菜单数据
-- ----------------------------
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单管理', '0', '2', 'ticket', 'system/ticket/index', 1, 0, 'C', '0', '0', 'system:ticket:list', 'form', 'admin', sysdate(), '', null, '工单管理菜单');

select @ticket_menu_id := last_insert_id();

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单查询', @ticket_menu_id, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:ticket:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单新增', @ticket_menu_id, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:ticket:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单修改', @ticket_menu_id, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:ticket:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单删除', @ticket_menu_id, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:ticket:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单导出', @ticket_menu_id, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:ticket:export',       '#', 'admin', sysdate(), '', null, '');
