package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.SysLink;
import com.ruoyi.system.domain.SysTicket;
import com.ruoyi.system.mapper.SysLinkMapper;
import com.ruoyi.system.mapper.SysTicketMapper;
import com.ruoyi.system.service.ISysNumberService;
import com.ruoyi.system.service.ISysTicketService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysTicketServiceImpl implements ISysTicketService
{
    @Autowired
    private SysTicketMapper sysTicketMapper;

    @Autowired
    private SysLinkMapper sysLinkMapper;

    @Autowired
    private ISysNumberService sysNumberService;

    @Override
    public SysTicket selectSysTicketById(Long ticketId)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysTicketMapper.selectSysTicketById(ticketId, tenantId);
    }

    @Override
    public List<SysTicket> selectSysTicketList(SysTicket sysTicket)
    {
        applyTenantScope(sysTicket);
        return sysTicketMapper.selectSysTicketList(sysTicket);
    }

    @Override
    public int insertSysTicket(SysTicket sysTicket)
    {
        applyTenantForWrite(sysTicket);
        checkLinkAccess(sysTicket.getLinkId(), sysTicket.getTenantId());
        String originalLink = sysTicket.getTicketLink();
        if (originalLink != null && !originalLink.trim().isEmpty())
        {
            String finalUrl = sysNumberService.resolveFinalUrl(originalLink, sysTicket.getTicketPassword());
            if (finalUrl != null && !finalUrl.trim().isEmpty())
            {
                sysTicket.setTicketLink(finalUrl);
            }
        }
        sysTicket.setCreateTime(DateUtils.getNowDate());
        int rows = sysTicketMapper.insertSysTicket(sysTicket);
        if (rows > 0 && originalLink != null && !originalLink.trim().isEmpty())
        {
            sysNumberService.importWsNumbersByTicketLink(
                originalLink,
                sysTicket.getTicketPassword(),
                sysTicket.getLinkId(),
                String.valueOf(sysTicket.getTicketId()),
                sysTicket.getNumberType()
            );
        }
        return rows;
    }

    @Override
    public int updateSysTicket(SysTicket sysTicket)
    {
        applyTenantForWrite(sysTicket);
        checkLinkAccess(sysTicket.getLinkId(), sysTicket.getTenantId());
        String originalLink = sysTicket.getTicketLink();
        if (originalLink != null && !originalLink.trim().isEmpty())
        {
            String finalUrl = sysNumberService.resolveFinalUrl(originalLink, sysTicket.getTicketPassword());
            if (finalUrl != null && !finalUrl.trim().isEmpty())
            {
                sysTicket.setTicketLink(finalUrl);
            }
        }
        sysTicket.setUpdateTime(DateUtils.getNowDate());
        int rows = sysTicketMapper.updateSysTicket(sysTicket);
        if (rows > 0 && originalLink != null && !originalLink.trim().isEmpty())
        {
            sysNumberService.importWsNumbersByTicketLink(
                originalLink,
                sysTicket.getTicketPassword(),
                sysTicket.getLinkId(),
                String.valueOf(sysTicket.getTicketId()),
                sysTicket.getNumberType()
            );
        }
        return rows;
    }

    @Override
    public int deleteSysTicketByIds(Long[] ticketIds)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysTicketMapper.deleteSysTicketByIds(ticketIds, tenantId);
    }

    @Override
    public int deleteSysTicketById(Long ticketId)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysTicketMapper.deleteSysTicketById(ticketId, tenantId);
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

    private void applyTenantScope(SysTicket sysTicket)
    {
        if (sysTicket == null)
        {
            return;
        }
        if (!SecurityUtils.isAdmin())
        {
            sysTicket.setTenantId(currentUserTenantId());
        }
    }

    private void applyTenantForWrite(SysTicket sysTicket)
    {
        if (sysTicket == null)
        {
            return;
        }
        if (SecurityUtils.isAdmin())
        {
            if (sysTicket.getTenantId() == null)
            {
                Long tenantId = currentUserTenantId();
                sysTicket.setTenantId(tenantId != null ? tenantId : 1L);
            }
            return;
        }
        sysTicket.setTenantId(currentUserTenantId());
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
