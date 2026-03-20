-- 目的：修复“能看到菜单但提示当前操作没有权限”的问题
-- 核心原因通常是：菜单(C)没有配置 perms，导致角色即使拿到了菜单，也拿不到接口需要的权限字符串。
-- 本脚本会：
-- 1) 定位普通角色（role_key=common 或 role_key=4 或 role_name=普通角色/普通员工）
-- 2) 确保链接/工单/号码 3 个菜单(C)的 perms 分别为 system:link:list / system:ticket:list / system:number:list
-- 3) 给该角色补齐对应菜单(C) + 按钮(F)授权
-- 4) 给“普通员工/普通用户”部门下所有用户绑定该角色（补齐历史用户）

set @role_id := (
  select role_id from sys_role
  where (role_key = 'common' or role_key = '4' or role_name in ('普通角色', '普通员工'))
    and status = '0'
    and del_flag = '0'
  order by (role_key = 'common') desc, (role_key = '4') desc, role_id asc
  limit 1
);

set @link_menu_id := (
  select menu_id from sys_menu
  where menu_type = 'C' and component = 'system/link/index'
  order by menu_id asc
  limit 1
);
set @ticket_menu_id := (
  select menu_id from sys_menu
  where menu_type = 'C' and component = 'system/ticket/index'
  order by menu_id asc
  limit 1
);
set @number_menu_id := (
  select menu_id from sys_menu
  where menu_type = 'C' and component = 'system/number/index'
  order by menu_id asc
  limit 1
);

-- 确保 3 个业务菜单(C) perms 正确（仅在为空时补齐，避免覆盖你已有配置）
update sys_menu m
join (
  select menu_id from sys_menu
  where menu_type = 'C'
    and component = 'system/link/index'
    and (perms is null or perms = '')
) t on t.menu_id = m.menu_id
set m.perms = 'system:link:list';

update sys_menu m
join (
  select menu_id from sys_menu
  where menu_type = 'C'
    and component = 'system/ticket/index'
    and (perms is null or perms = '')
) t on t.menu_id = m.menu_id
set m.perms = 'system:ticket:list';

update sys_menu m
join (
  select menu_id from sys_menu
  where menu_type = 'C'
    and component = 'system/number/index'
    and (perms is null or perms = '')
) t on t.menu_id = m.menu_id
set m.perms = 'system:number:list';

-- 如果按钮(F)菜单不存在，则补齐按钮权限菜单（这决定了非管理员是否能看到按钮 & 是否能调用接口）
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '链接查询', @link_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'system:link:query', '#', 'admin', sysdate(), '', null, ''
where @link_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @link_menu_id and menu_type='F' and perms='system:link:query');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '链接新增', @link_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'system:link:add', '#', 'admin', sysdate(), '', null, ''
where @link_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @link_menu_id and menu_type='F' and perms='system:link:add');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '链接修改', @link_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'system:link:edit', '#', 'admin', sysdate(), '', null, ''
where @link_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @link_menu_id and menu_type='F' and perms='system:link:edit');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '链接删除', @link_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'system:link:remove', '#', 'admin', sysdate(), '', null, ''
where @link_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @link_menu_id and menu_type='F' and perms='system:link:remove');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '链接导出', @link_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'system:link:export', '#', 'admin', sysdate(), '', null, ''
where @link_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @link_menu_id and menu_type='F' and perms='system:link:export');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '工单查询', @ticket_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'system:ticket:query', '#', 'admin', sysdate(), '', null, ''
where @ticket_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @ticket_menu_id and menu_type='F' and perms='system:ticket:query');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '工单新增', @ticket_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'system:ticket:add', '#', 'admin', sysdate(), '', null, ''
where @ticket_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @ticket_menu_id and menu_type='F' and perms='system:ticket:add');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '工单修改', @ticket_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'system:ticket:edit', '#', 'admin', sysdate(), '', null, ''
where @ticket_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @ticket_menu_id and menu_type='F' and perms='system:ticket:edit');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '工单删除', @ticket_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'system:ticket:remove', '#', 'admin', sysdate(), '', null, ''
where @ticket_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @ticket_menu_id and menu_type='F' and perms='system:ticket:remove');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '工单导出', @ticket_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'system:ticket:export', '#', 'admin', sysdate(), '', null, ''
where @ticket_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @ticket_menu_id and menu_type='F' and perms='system:ticket:export');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '号码查询', @number_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'system:number:query', '#', 'admin', sysdate(), '', null, ''
where @number_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @number_menu_id and menu_type='F' and perms='system:number:query');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '号码新增', @number_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'system:number:add', '#', 'admin', sysdate(), '', null, ''
where @number_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @number_menu_id and menu_type='F' and perms='system:number:add');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '号码修改', @number_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'system:number:edit', '#', 'admin', sysdate(), '', null, ''
where @number_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @number_menu_id and menu_type='F' and perms='system:number:edit');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '号码删除', @number_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'system:number:remove', '#', 'admin', sysdate(), '', null, ''
where @number_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @number_menu_id and menu_type='F' and perms='system:number:remove');
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '号码导出', @number_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'system:number:export', '#', 'admin', sysdate(), '', null, ''
where @number_menu_id is not null
  and not exists (select 1 from sys_menu where parent_id = @number_menu_id and menu_type='F' and perms='system:number:export');

-- 授权：业务菜单(C)
insert into sys_role_menu(role_id, menu_id)
select @role_id, m.menu_id
from sys_menu m
where @role_id is not null
  and m.menu_type = 'C'
  and m.component in ('system/link/index', 'system/ticket/index', 'system/number/index')
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = @role_id and rm.menu_id = m.menu_id
  );

-- 授权：按钮(F)（按 perms 前缀）
insert into sys_role_menu(role_id, menu_id)
select @role_id, m.menu_id
from sys_menu m
where @role_id is not null
  and m.menu_type = 'F'
  and m.perms is not null
  and m.perms <> ''
  and (
    m.perms like 'system:link:%'
    or m.perms like 'system:ticket:%'
    or m.perms like 'system:number:%'
  )
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = @role_id and rm.menu_id = m.menu_id
  );

-- 授权：补齐父级目录菜单(M)
insert into sys_role_menu(role_id, menu_id)
select @role_id, p.menu_id
from sys_menu c
join sys_menu p on p.menu_id = c.parent_id
where @role_id is not null
  and c.menu_type = 'C'
  and c.component in ('system/link/index', 'system/ticket/index', 'system/number/index')
  and c.parent_id is not null
  and c.parent_id <> 0
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = @role_id and rm.menu_id = p.menu_id
  );

-- 绑定：部门为普通员工/普通用户的用户都绑定到该角色（补齐历史用户）
insert into sys_user_role(user_id, role_id)
select u.user_id, @role_id
from sys_user u
join sys_dept d on d.dept_id = u.dept_id
where @role_id is not null
  and u.del_flag = '0'
  and d.del_flag = '0'
  and d.status = '0'
  and d.dept_name in ('普通员工', '普通用户')
  and not exists (
    select 1 from sys_user_role ur where ur.user_id = u.user_id and ur.role_id = @role_id
  );

-- 自检：查看普通角色是否拿到了 list 权限
select @role_id as role_id;
select menu_id, menu_name, menu_type, component, perms
from sys_menu
where component in ('system/link/index', 'system/ticket/index', 'system/number/index');
