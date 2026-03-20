package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysTenantSubscription;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysTenantSubscriptionMapper
{
    SysTenantSubscription selectActiveByTenantId(@Param("tenantId") Long tenantId);

    List<String> selectFeatureCodesByPlanId(@Param("planId") Long planId);

    String selectTenantNameByTenantId(@Param("tenantId") Long tenantId);

    int insertSysTenantSubscription(SysTenantSubscription subscription);

    int updateSysTenantSubscription(SysTenantSubscription subscription);
}
