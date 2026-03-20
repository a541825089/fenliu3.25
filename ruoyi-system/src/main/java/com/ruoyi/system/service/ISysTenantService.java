package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysTenant;
import java.util.Date;
import java.util.List;

public interface ISysTenantService
{
    SysTenant selectSysTenantById(Long tenantId);

    List<SysTenant> selectSysTenantList(SysTenant sysTenant);

    int insertSysTenant(SysTenant sysTenant);

    int updateSysTenant(SysTenant sysTenant);

    int deleteSysTenantByIds(Long[] tenantIds);

    int deleteSysTenantById(Long tenantId);

    int renewTenant(Long tenantId, Long planId, Date endTime);
}

