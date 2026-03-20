package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysTicket;
import java.util.List;

public interface SysTicketMapper
{
    SysTicket selectSysTicketById(Long ticketId);

    List<SysTicket> selectSysTicketList(SysTicket sysTicket);

    int insertSysTicket(SysTicket sysTicket);

    int updateSysTicket(SysTicket sysTicket);

    int deleteSysTicketById(Long ticketId);

    int deleteSysTicketByIds(Long[] ticketIds);
}
