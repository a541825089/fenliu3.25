package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.SysLink;
import com.ruoyi.system.domain.SysNumber;
import com.ruoyi.system.mapper.SysLinkMapper;
import com.ruoyi.system.mapper.SysNumberMapper;
import com.ruoyi.system.service.ISysNumberService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysNumberServiceImpl implements ISysNumberService
{
    @Autowired
    private SysNumberMapper sysNumberMapper;

    @Autowired
    private SysLinkMapper sysLinkMapper;

    @Override
    public SysNumber selectSysNumberById(Long numberId)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysNumberMapper.selectSysNumberById(numberId, tenantId);
    }

    @Override
    public List<SysNumber> selectSysNumberList(SysNumber sysNumber)
    {
        applyTenantScope(sysNumber);
        return sysNumberMapper.selectSysNumberList(sysNumber);
    }

    @Override
    public int insertSysNumber(SysNumber sysNumber)
    {
        applyTenantForWrite(sysNumber);
        checkLinkAccess(sysNumber.getLinkId(), sysNumber.getTenantId());
        String numbers = sysNumber.getNumberValue();
        if (numbers == null)
        {
            return 0;
        }
        String normalized = numbers.replace("\r\n", "\n");
        String[] lines = normalized.split("\n");
        List<String> values = new ArrayList<>();
        for (String line : lines)
        {
            String v = line == null ? "" : line.trim();
            if (!v.isEmpty())
            {
                values.add(v);
            }
        }
        if (values.isEmpty())
        {
            return 0;
        }
        Date now = DateUtils.getNowDate();
        if (values.size() == 1)
        {
            sysNumber.setNumberValue(values.get(0));
            sysNumber.setCreateTime(now);
            return sysNumberMapper.insertSysNumber(sysNumber);
        }
        List<SysNumber> list = new ArrayList<>(values.size());
        for (String v : values)
        {
            SysNumber item = new SysNumber();
            item.setTenantId(sysNumber.getTenantId());
            item.setLinkId(sysNumber.getLinkId());
            item.setTicketNo(sysNumber.getTicketNo());
            item.setNumberValue(v);
            item.setNumberType(sysNumber.getNumberType());
            item.setVisitCount(sysNumber.getVisitCount());
            item.setInCount(sysNumber.getInCount());
            item.setStatus(sysNumber.getStatus());
            item.setCreateBy(sysNumber.getCreateBy());
            item.setCreateTime(now);
            list.add(item);
        }
        return sysNumberMapper.insertSysNumberBatch(list);
    }

    @Override
    public int updateSysNumber(SysNumber sysNumber)
    {
        applyTenantForWrite(sysNumber);
        checkLinkAccess(sysNumber.getLinkId(), sysNumber.getTenantId());
        sysNumber.setUpdateTime(DateUtils.getNowDate());
        return sysNumberMapper.updateSysNumber(sysNumber);
    }

    @Override
    public int updateSysNumberStatus(SysNumber sysNumber)
    {
        applyTenantForWrite(sysNumber);
        sysNumber.setUpdateTime(DateUtils.getNowDate());
        return sysNumberMapper.updateSysNumberStatus(sysNumber);
    }

    @Override
    public int deleteSysNumberByIds(Long[] numberIds)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysNumberMapper.deleteSysNumberByIds(numberIds, tenantId);
    }

    @Override
    public int deleteSysNumberById(Long numberId)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysNumberMapper.deleteSysNumberById(numberId, tenantId);
    }

    private void checkLinkAccess(Long linkId, Long tenantId)
    {
        if (linkId == null)
        {
            return;
        }
        SysLink link = sysLinkMapper.selectSysLinkById(linkId, tenantId);
        if (link == null)
        {
            throw new ServiceException("分流链接不存在或无权限访问");
        }
    }

    private void applyTenantScope(SysNumber sysNumber)
    {
        if (sysNumber == null)
        {
            return;
        }
        if (!SecurityUtils.isAdmin())
        {
            sysNumber.setTenantId(currentUserTenantId());
        }
    }

    private void applyTenantForWrite(SysNumber sysNumber)
    {
        if (sysNumber == null)
        {
            return;
        }
        if (SecurityUtils.isAdmin())
        {
            if (sysNumber.getTenantId() == null)
            {
                Long tenantId = currentUserTenantId();
                sysNumber.setTenantId(tenantId != null ? tenantId : 1L);
            }
            return;
        }
        sysNumber.setTenantId(currentUserTenantId());
    }

    private Long resolveTenantIdForRead()
    {
        return SecurityUtils.isAdmin() ? null : currentUserTenantId();
    }

    private Long currentUserTenantId()
    {
        try
        {
            return SecurityUtils.getLoginUser().getUser().getTenantId();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
