package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysTenantSubscription;
import com.ruoyi.system.mapper.SysTenantSubscriptionMapper;
import com.ruoyi.system.service.ISysTenantSubscriptionService;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysTenantSubscriptionServiceImpl implements ISysTenantSubscriptionService
{
    private static final long CACHE_MS = 30_000L;

    @Autowired
    private SysTenantSubscriptionMapper subscriptionMapper;

    private final Map<Long, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public String checkFeatureAccess(Long tenantId, String featureCode)
    {
        if (tenantId == null)
        {
            return "未绑定租户";
        }
        AccessSnapshot snapshot = getSnapshot(tenantId);
        if (!snapshot.active)
        {
            return "订阅已到期，请联系客服tg: `https://t.me/XMB88886` 续费";
        }
        if (featureCode != null && !featureCode.isEmpty() && !snapshot.features.contains(featureCode))
        {
            return "未购买该功能";
        }
        return null;
    }

    @Override
    public Map<String, Object> getTenantAccessInfo(Long tenantId)
    {
        if (tenantId == null)
        {
            return Collections.emptyMap();
        }
        AccessSnapshot snapshot = getSnapshot(tenantId);
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", tenantId);
        map.put("tenantName", snapshot.tenantName);
        map.put("active", snapshot.active);
        map.put("endTime", snapshot.endTime);
        map.put("features", snapshot.features);
        return map;
    }

    private AccessSnapshot getSnapshot(Long tenantId)
    {
        long now = System.currentTimeMillis();
        CacheEntry entry = cache.get(tenantId);
        if (entry != null && now - entry.cachedAtMs < CACHE_MS)
        {
            return entry.snapshot;
        }
        AccessSnapshot snapshot = loadSnapshot(tenantId);
        cache.put(tenantId, new CacheEntry(now, snapshot));
        return snapshot;
    }

    private AccessSnapshot loadSnapshot(Long tenantId)
    {
        SysTenantSubscription sub = subscriptionMapper.selectActiveByTenantId(tenantId);
        String tenantName = subscriptionMapper.selectTenantNameByTenantId(tenantId);
        if (sub == null)
        {
            return new AccessSnapshot(false, null, tenantName, Collections.emptySet());
        }
        Date endTime = sub.getEndTime();
        boolean active = endTime != null && endTime.getTime() >= DateUtils.getNowDate().getTime();
        Set<String> features = new HashSet<>();
        if (sub.getPlanId() != null)
        {
            List<String> codes = subscriptionMapper.selectFeatureCodesByPlanId(sub.getPlanId());
            if (codes != null)
            {
                features.addAll(codes);
            }
        }
        return new AccessSnapshot(active, endTime, tenantName, Collections.unmodifiableSet(features));
    }

    private static class CacheEntry
    {
        private final long cachedAtMs;
        private final AccessSnapshot snapshot;

        private CacheEntry(long cachedAtMs, AccessSnapshot snapshot)
        {
            this.cachedAtMs = cachedAtMs;
            this.snapshot = snapshot;
        }
    }

    private static class AccessSnapshot
    {
        private final boolean active;
        private final Date endTime;
        private final String tenantName;
        private final Set<String> features;

        private AccessSnapshot(boolean active, Date endTime, String tenantName, Set<String> features)
        {
            this.active = active;
            this.endTime = endTime;
            this.tenantName = tenantName;
            this.features = features;
        }
    }
}
