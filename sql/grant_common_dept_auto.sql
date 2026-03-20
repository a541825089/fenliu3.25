-- 目的：
-- 1) 确保普通角色（common/普通角色/普通员工）拥有 链接管理/工单管理/号码管理 的菜单与按钮权限
-- 2) 将“普通员工/普通用户”部门下的所有用户，自动绑定到普通角色（用于补齐历史用户）

set @role_id := (
  select role_id from sys_role
  where (role_key = 'common' or role_key = '4' or role_name in ('普通角色', '普通员工'))
    and status = '0'
    and del_flag = '0'
  order by (role_key = 'common') desc, (role_key = '4') desc, role_id asc
  limit 1
);

-- 1) 授权菜单(C)
insert into sys_role_menu(role_id, menu_id)
select @role_id, m.menu_id
from sys_menu m
where @role_id is not null
  and m.menu_type = 'C'
  and m.component in ('system/link/index', 'system/ticket/index', 'system/number/index')
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = @role_id and rm.menu_id = m.menu_id
  );

-- 2) 授权按钮(F)（以及兜底：按 perms 前缀授权 C/F）
insert into sys_role_menu(role_id, menu_id)
select @role_id, m.menu_id
from sys_menu m
where @role_id is not null
  and m.menu_type in ('C','F')
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

-- 3) 补齐父级目录菜单(M)
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

-- 4) 将“普通员工/普通用户”部门下的所有用户绑定 common 角色（补齐历史用户）
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

-- 5) 自检：看看这次识别到的 role_id 和绑定人数
select @role_id as role_id;
select count(1) as common_user_count from sys_user_role where role_id = @role_id;

