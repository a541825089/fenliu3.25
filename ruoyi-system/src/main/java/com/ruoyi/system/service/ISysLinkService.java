package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysLink;

/**
 * 链接管理Service接口
 * 
 * @author ruoyi
 */
public interface ISysLinkService 
{
    /**
     * 查询链接管理
     * 
     * @param linkId 链接管理ID
     * @return 链接管理
     */
    public SysLink selectSysLinkById(Long linkId);

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
     * 批量删除链接管理
     * 
     * @param linkIds 需要删除的链接管理ID
     * @return 结果
     */
    public int deleteSysLinkByIds(Long[] linkIds);

    /**
     * 删除链接管理信息
     * 
     * @param linkId 链接管理ID
     * @return 结果
     */
    public int deleteSysLinkById(Long linkId);
}
