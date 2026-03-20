package com.ruoyi.system.service;

import java.util.Map;

public interface ISysTenantSubscriptionService
{
    String checkFeatureAccess(Long tenantId, String featureCode);

    Map<String, Object> getTenantAccessInfo(Long tenantId);
}

