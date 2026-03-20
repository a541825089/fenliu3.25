package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysLinkMapper;
import com.ruoyi.system.domain.SysLink;
import com.ruoyi.system.service.ISysLinkService;

/**
 * 链接管理Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysLinkServiceImpl implements ISysLinkService 
{
    @Autowired
    private SysLinkMapper sysLinkMapper;

    /**
     * 查询链接管理
     * 
     * @param linkId 链接管理ID
     * @return 链接管理
     */
    @Override
    public SysLink selectSysLinkById(Long linkId)
    {
        return sysLinkMapper.selectSysLinkById(linkId);
    }

    /**
     * 查询链接管理列表
     * 
     * @param sysLink 链接管理
     * @return 链接管理
     */
    @Override
    public List<SysLink> selectSysLinkList(SysLink sysLink)
    {
        return sysLinkMapper.selectSysLinkList(sysLink);
    }

    /**
     * 新增链接管理
     * 
     * @param sysLink 链接管理
     * @return 结果
     */
    @Override
    public int insertSysLink(SysLink sysLink)
    {
        sysLink.setCreateTime(DateUtils.getNowDate());
        return sysLinkMapper.insertSysLink(sysLink);
    }

    /**
     * 修改链接管理
     * 
     * @param sysLink 链接管理
     * @return 结果
     */
    @Override
    public int updateSysLink(SysLink sysLink)
    {
        sysLink.setUpdateTime(DateUtils.getNowDate());
        return sysLinkMapper.updateSysLink(sysLink);
    }

    /**
     * 批量删除链接管理
     * 
     * @param linkIds 需要删除的链接管理ID
     * @return 结果
     */
    @Override
    public int deleteSysLinkByIds(Long[] linkIds)
    {
        return sysLinkMapper.deleteSysLinkByIds(linkIds);
    }

    /**
     * 删除链接管理信息
     * 
     * @param linkId 链接管理ID
     * @return 结果
     */
    @Override
    public int deleteSysLinkById(Long linkId)
    {
        return sysLinkMapper.deleteSysLinkById(linkId);
    }
}
