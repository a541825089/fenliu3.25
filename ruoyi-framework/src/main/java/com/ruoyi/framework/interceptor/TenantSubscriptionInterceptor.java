package com.ruoyi.framework.interceptor;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.system.service.ISysTenantSubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantSubscriptionInterceptor implements HandlerInterceptor
{
    @Autowired
    private ISysTenantSubscriptionService tenantSubscriptionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        LoginUser loginUser = null;
        try
        {
            loginUser = SecurityUtils.getLoginUser();
        }
        catch (Exception e)
        {
            loginUser = null;
        }
        if (loginUser == null || loginUser.getUser() == null)
        {
            return true;
        }
        if (loginUser.getUser().isAdmin())
        {
            return true;
        }
        String uri = request.getRequestURI();
        String featureCode = resolveFeature(uri);
        if (featureCode == null)
        {
            return true;
        }
        Long tenantId = loginUser.getUser().getTenantId();
        String msg = tenantSubscriptionService.checkFeatureAccess(tenantId, featureCode);
        if (msg == null)
        {
            return true;
        }
        AjaxResult result = AjaxResult.error(402, msg);
        Map<String, Object> accessInfo = tenantSubscriptionService.getTenantAccessInfo(tenantId);
        if (accessInfo != null && !accessInfo.isEmpty())
        {
            result.put("tenantAccess", accessInfo);
        }
        ServletUtils.renderString(response, JSON.toJSONString(result));
        return false;
    }

    private String resolveFeature(String uri)
    {
        if (uri == null)
        {
            return null;
        }
        String path = uri;
        if (path.startsWith("/prod-api"))
        {
            path = path.substring("/prod-api".length());
        }
        if (path.startsWith("/dev-api"))
        {
            path = path.substring("/dev-api".length());
        }
        if (path.startsWith("/system/link"))
        {
            return "LINK";
        }
        if (path.startsWith("/system/ticket"))
        {
            return "TICKET";
        }
        if (path.startsWith("/system/number"))
        {
            return "NUMBER";
        }
        return null;
    }
}
