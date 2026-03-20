package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
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
        Long tenantId = resolveTenantIdForRead();
        return sysLinkMapper.selectSysLinkById(linkId, tenantId);
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
        applyTenantScope(sysLink);
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
        applyTenantForWrite(sysLink);
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
        applyTenantForWrite(sysLink);
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
        Long tenantId = resolveTenantIdForRead();
        return sysLinkMapper.deleteSysLinkByIds(linkIds, tenantId);
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
        Long tenantId = resolveTenantIdForRead();
        return sysLinkMapper.deleteSysLinkById(linkId, tenantId);
    }

    private void applyTenantScope(SysLink sysLink)
    {
        if (sysLink == null)
        {
            return;
        }
        if (!SecurityUtils.isAdmin())
        {
            sysLink.setTenantId(currentUserTenantId());
        }
    }

    private void applyTenantForWrite(SysLink sysLink)
    {
        if (sysLink == null)
        {
            return;
        }
        if (SecurityUtils.isAdmin())
        {
            if (sysLink.getTenantId() == null)
            {
                Long tenantId = currentUserTenantId();
                sysLink.setTenantId(tenantId != null ? tenantId : 1L);
            }
            return;
        }
        sysLink.setTenantId(currentUserTenantId());
    }

    private Long resolveTenantIdForRead()
    {
        return SecurityUtils.isAdmin() ? null : currentUserTenantId();
    }

    private Long currentUserTenantId()
    {
        try
        {
            return SecurityUtils.getLoginUser().getUser().getTenantId();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
