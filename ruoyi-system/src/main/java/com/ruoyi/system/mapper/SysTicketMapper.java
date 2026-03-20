package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysTicket;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysTicketMapper
{
    SysTicket selectSysTicketById(@Param("ticketId") Long ticketId, @Param("tenantId") Long tenantId);

    List<SysTicket> selectSysTicketList(SysTicket sysTicket);

    int insertSysTicket(SysTicket sysTicket);

    int updateSysTicket(SysTicket sysTicket);

    int deleteSysTicketById(@Param("ticketId") Long ticketId, @Param("tenantId") Long tenantId);

    int deleteSysTicketByIds(@Param("ticketIds") Long[] ticketIds, @Param("tenantId") Long tenantId);
}
