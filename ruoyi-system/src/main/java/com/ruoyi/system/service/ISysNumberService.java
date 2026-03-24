package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysNumber;
import java.util.List;

public interface ISysNumberService
{
    SysNumber selectSysNumberById(Long numberId);

    List<SysNumber> selectSysNumberList(SysNumber sysNumber);

    int insertSysNumber(SysNumber sysNumber);

    int updateSysNumber(SysNumber sysNumber);

    int updateSysNumberStatus(SysNumber sysNumber);

    int deleteSysNumberByIds(Long[] numberIds);

    int deleteSysNumberById(Long numberId);

    String nextWsNumberByLink(Long linkId, String numberType);

    String resolveFinalUrl(String ticketLink, String ticketPassword);

    int importWsNumbersByTicketLink(String ticketLink, String ticketPassword, Long linkId, String ticketNo, String numberType);

    int syncWsNumbersAndCleanup();
}
