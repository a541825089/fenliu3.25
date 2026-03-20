package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysTicket;
import com.ruoyi.system.mapper.SysTicketMapper;
import com.ruoyi.system.service.ISysTicketService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysTicketServiceImpl implements ISysTicketService
{
    @Autowired
    private SysTicketMapper sysTicketMapper;

    @Override
    public SysTicket selectSysTicketById(Long ticketId)
    {
        return sysTicketMapper.selectSysTicketById(ticketId);
    }

    @Override
    public List<SysTicket> selectSysTicketList(SysTicket sysTicket)
    {
        return sysTicketMapper.selectSysTicketList(sysTicket);
    }

    @Override
    public int insertSysTicket(SysTicket sysTicket)
    {
        sysTicket.setCreateTime(DateUtils.getNowDate());
        return sysTicketMapper.insertSysTicket(sysTicket);
    }

    @Override
    public int updateSysTicket(SysTicket sysTicket)
    {
        sysTicket.setUpdateTime(DateUtils.getNowDate());
        return sysTicketMapper.updateSysTicket(sysTicket);
    }

    @Override
    public int deleteSysTicketByIds(Long[] ticketIds)
    {
        return sysTicketMapper.deleteSysTicketByIds(ticketIds);
    }

    @Override
    public int deleteSysTicketById(Long ticketId)
    {
        return sysTicketMapper.deleteSysTicketById(ticketId);
    }
}
