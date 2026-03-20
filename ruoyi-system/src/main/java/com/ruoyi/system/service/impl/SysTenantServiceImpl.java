package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.domain.SysTenantSubscription;
import com.ruoyi.system.mapper.SysTenantMapper;
import com.ruoyi.system.mapper.SysTenantSubscriptionMapper;
import com.ruoyi.system.service.ISysTenantService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysTenantServiceImpl implements ISysTenantService
{
    @Autowired
    private SysTenantMapper tenantMapper;

    @Autowired
    private SysTenantSubscriptionMapper subscriptionMapper;

    @Override
    public SysTenant selectSysTenantById(Long tenantId)
    {
        return tenantMapper.selectSysTenantById(tenantId);
    }

    @Override
    public List<SysTenant> selectSysTenantList(SysTenant sysTenant)
    {
        return tenantMapper.selectSysTenantList(sysTenant);
    }

    @Override
    @Transactional
    public int insertSysTenant(SysTenant sysTenant)
    {
        sysTenant.setCreateTime(DateUtils.getNowDate());
        int rows = tenantMapper.insertSysTenant(sysTenant);
        if (rows > 0)
        {
            Date now = DateUtils.getNowDate();
            SysTenantSubscription sub = new SysTenantSubscription();
            sub.setTenantId(sysTenant.getTenantId());
            sub.setPlanId(1L);
            sub.setStartTime(now);
            sub.setEndTime(addYears(now, 1));
            sub.setStatus("0");
            sub.setCreateBy(sysTenant.getCreateBy());
            sub.setCreateTime(now);
            subscriptionMapper.insertSysTenantSubscription(sub);
        }
        return rows;
    }

    @Override
    public int updateSysTenant(SysTenant sysTenant)
    {
        sysTenant.setUpdateTime(DateUtils.getNowDate());
        return tenantMapper.updateSysTenant(sysTenant);
    }

    @Override
    public int deleteSysTenantByIds(Long[] tenantIds)
    {
        return tenantMapper.deleteSysTenantByIds(tenantIds);
    }

    @Override
    public int deleteSysTenantById(Long tenantId)
    {
        return tenantMapper.deleteSysTenantById(tenantId);
    }

    @Override
    @Transactional
    public int renewTenant(Long tenantId, Long planId, Date endTime)
    {
        if (tenantId == null)
        {
            throw new ServiceException("租户ID不能为空");
        }
        if (planId == null)
        {
            planId = 1L;
        }
        if (endTime == null)
        {
            throw new ServiceException("到期时间不能为空");
        }
        SysTenant tenant = tenantMapper.selectSysTenantById(tenantId);
        if (tenant == null)
        {
            throw new ServiceException("租户不存在");
        }
        SysTenantSubscription active = subscriptionMapper.selectActiveByTenantId(tenantId);
        Date now = DateUtils.getNowDate();
        if (active == null)
        {
            SysTenantSubscription sub = new SysTenantSubscription();
            sub.setTenantId(tenantId);
            sub.setPlanId(planId);
            sub.setStartTime(now);
            sub.setEndTime(endTime);
            sub.setStatus("0");
            sub.setCreateTime(now);
            return subscriptionMapper.insertSysTenantSubscription(sub);
        }
        active.setPlanId(planId);
        active.setEndTime(endTime);
        active.setUpdateTime(now);
        return subscriptionMapper.updateSysTenantSubscription(active);
    }

    private Date addYears(Date date, int years)
    {
        if (date == null)
        {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }
}
