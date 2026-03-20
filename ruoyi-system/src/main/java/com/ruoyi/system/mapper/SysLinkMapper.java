package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysLink;
import org.apache.ibatis.annotations.Param;

/**
 * 链接管理Mapper接口
 * 
 * @author ruoyi
 */
public interface SysLinkMapper 
{
    /**
     * 查询链接管理
     * 
     * @param linkId 链接管理ID
     * @return 链接管理
     */
    public SysLink selectSysLinkById(@Param("linkId") Long linkId, @Param("tenantId") Long tenantId);

    /**
     * 查询链接管理列表
     * 
     * @param sysLink 链接管理
     * @return 链接管理集合
     */
    public List<SysLink> selectSysLinkList(SysLink sysLink);

    /**
     * 新增链接管理
     * 
     * @param sysLink 链接管理
     * @return 结果
     */
    public int insertSysLink(SysLink sysLink);

    /**
     * 修改链接管理
     * 
     * @param sysLink 链接管理
     * @return 结果
     */
    public int updateSysLink(SysLink sysLink);

    /**
     * 删除链接管理
     * 
     * @param linkId 链接管理ID
     * @return 结果
     */
    public int deleteSysLinkById(@Param("linkId") Long linkId, @Param("tenantId") Long tenantId);

    /**
     * 批量删除链接管理
     * 
     * @param linkIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysLinkByIds(@Param("linkIds") Long[] linkIds, @Param("tenantId") Long tenantId);
}
