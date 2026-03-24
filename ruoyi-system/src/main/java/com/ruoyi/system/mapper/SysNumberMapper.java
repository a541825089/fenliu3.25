package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysNumber;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysNumberMapper
{
    SysNumber selectSysNumberById(@Param("numberId") Long numberId, @Param("tenantId") Long tenantId);

    List<SysNumber> selectSysNumberList(SysNumber sysNumber);

    int insertSysNumber(SysNumber sysNumber);

    int insertSysNumberBatch(@Param("list") List<SysNumber> list);

    int updateSysNumber(SysNumber sysNumber);

    int updateSysNumberStatus(SysNumber sysNumber);

    int deleteSysNumberById(@Param("numberId") Long numberId, @Param("tenantId") Long tenantId);

    int deleteSysNumberByIds(@Param("numberIds") Long[] numberIds, @Param("tenantId") Long tenantId);

    SysNumber selectNextAvailableByLink(@Param("linkId") Long linkId, @Param("numberType") String numberType, @Param("tenantId") Long tenantId);

    int touchNumber(@Param("numberId") Long numberId, @Param("tenantId") Long tenantId, @Param("updateBy") String updateBy, @Param("updateTime") java.util.Date updateTime);

    List<String> selectNumberValuesByLink(@Param("tenantId") Long tenantId, @Param("linkId") Long linkId, @Param("numberType") String numberType);

    int deleteByValue(@Param("tenantId") Long tenantId, @Param("linkId") Long linkId, @Param("numberValue") String numberValue, @Param("numberType") String numberType);
}
