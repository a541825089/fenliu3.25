package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysNumber;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysNumberMapper
{
    SysNumber selectSysNumberById(Long numberId);

    List<SysNumber> selectSysNumberList(SysNumber sysNumber);

    int insertSysNumber(SysNumber sysNumber);

    int insertSysNumberBatch(@Param("list") List<SysNumber> list);

    int updateSysNumber(SysNumber sysNumber);

    int updateSysNumberStatus(SysNumber sysNumber);

    int deleteSysNumberById(Long numberId);

    int deleteSysNumberByIds(Long[] numberIds);
}
