package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysTicket;
import java.util.List;

public interface ISysTicketService
{
    SysTicket selectSysTicketById(Long ticketId);

    List<SysTicket> selectSysTicketList(SysTicket sysTicket);

    int insertSysTicket(SysTicket sysTicket);

    int updateSysTicket(SysTicket sysTicket);

    int deleteSysTicketByIds(Long[] ticketIds);

    int deleteSysTicketById(Long ticketId);
}
