package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysTenant;
import java.util.List;

public interface SysTenantMapper
{
    SysTenant selectSysTenantById(Long tenantId);

    List<SysTenant> selectSysTenantList(SysTenant sysTenant);

    int insertSysTenant(SysTenant sysTenant);

    int updateSysTenant(SysTenant sysTenant);

    int deleteSysTenantById(Long tenantId);

    int deleteSysTenantByIds(Long[] tenantIds);
}

