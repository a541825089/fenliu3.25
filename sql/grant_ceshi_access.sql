-- 目标：
-- 1) 给普通角色(common)赋予：链接管理/工单管理/号码管理 对应菜单与按钮权限
-- 2) 给用户 ceshi 绑定普通角色(common)

set @role_id := (
  select role_id from sys_role
  where role_key = 'common'
     or role_key = '4'
     or role_name in ('普通角色', '普通员工')
  order by (role_key = 'common') desc, (role_key = '4') desc, role_id asc
  limit 1
);
set @user_id := (select user_id from sys_user where user_name = 'ceshi' limit 1);

select @role_id as role_id, @user_id as user_id;

-- 绑定角色（如未绑定）
insert into sys_user_role(user_id, role_id)
select @user_id, @role_id
where @user_id is not null
  and @role_id is not null
  and not exists (
    select 1 from sys_user_role ur where ur.user_id = @user_id and ur.role_id = @role_id
  );

-- 赋权：先把三个业务菜单(C)本身授权给角色（按组件路径匹配，避免 perms/菜单结构差异导致漏授权）
insert into sys_role_menu(role_id, menu_id)
select @role_id, m.menu_id
from sys_menu m
where @role_id is not null
  and m.menu_type = 'C'
  and m.component in ('system/link/index', 'system/ticket/index', 'system/number/index')
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = @role_id and rm.menu_id = m.menu_id
  );

-- 授权：把上述三个菜单的按钮权限(F)全部授权（包含 list/query/add/edit/remove/export 等）
insert into sys_role_menu(role_id, menu_id)
select @role_id, m.menu_id
from sys_menu m
where @role_id is not null
  and m.menu_type = 'F'
  and (
    m.perms like 'system:link:%'
    or m.perms like 'system:ticket:%'
    or m.perms like 'system:number:%'
  )
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = @role_id and rm.menu_id = m.menu_id
  );

-- 授权兜底：如果菜单(C)的 component 不一致，仍可按 perms 前缀把菜单/按钮全部授权（C/F 都覆盖）
insert into sys_role_menu(role_id, menu_id)
select @role_id, m.menu_id
from sys_menu m
where @role_id is not null
  and m.menu_type in ('C', 'F')
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

-- 授权：补齐父级菜单（如果你的业务菜单挂在某个目录(M)下，否则 parent_id=0 这段不会插入任何数据）
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

-- 验证：查看 ceshi 当前角色与菜单授权情况
select u.user_id, u.user_name, r.role_id, r.role_name, r.role_key
from sys_user u
left join sys_user_role ur on ur.user_id = u.user_id
left join sys_role r on r.role_id = ur.role_id
where u.user_name = 'ceshi';

select m.menu_id, m.menu_name, m.menu_type, m.parent_id, m.component, m.perms
from sys_role_menu rm
join sys_menu m on m.menu_id = rm.menu_id
where rm.role_id = @role_id
  and (
    m.component in ('system/link/index', 'system/ticket/index', 'system/number/index')
    or m.perms like 'system:link:%'
    or m.perms like 'system:ticket:%'
    or m.perms like 'system:number:%'
  )
order by m.parent_id, m.menu_type, m.order_num, m.menu_id;
