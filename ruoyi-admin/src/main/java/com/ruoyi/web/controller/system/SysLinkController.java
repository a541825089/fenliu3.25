package com.ruoyi.web.controller.system;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysLink;
import com.ruoyi.system.service.ISysLinkService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 链接管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping({ "/system/link", "/prod-api/system/link", "/dev-api/system/link" })
public class SysLinkController extends BaseController
{
    @Autowired
    private ISysLinkService sysLinkService;

    /**
     * 查询链接管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:link:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysLink sysLink)
    {
        startPage();
        List<SysLink> list = sysLinkService.selectSysLinkList(sysLink);
        return getDataTable(list);
    }

    /**
     * 导出链接管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:link:export')")
    @Log(title = "链接管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLink sysLink)
    {
        List<SysLink> list = sysLinkService.selectSysLinkList(sysLink);
        ExcelUtil<SysLink> util = new ExcelUtil<SysLink>(SysLink.class);
        util.exportExcel(response, list, "链接管理数据");
    }

    /**
     * 获取链接管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:link:query')")
    @GetMapping(value = "/{linkId}")
    public AjaxResult getInfo(@PathVariable("linkId") Long linkId)
    {
        return AjaxResult.success(sysLinkService.selectSysLinkById(linkId));
    }

    /**
     * 新增链接管理
     */
    @PreAuthorize("@ss.hasPermi('system:link:add')")
    @Log(title = "链接管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysLink sysLink)
    {
        sysLink.setCreateBy(getUsername());
        int rows = sysLinkService.insertSysLink(sysLink);
        if (rows <= 0)
        {
            return error("新增失败");
        }
        return AjaxResult.success(sysLink.getLinkId());
    }

    /**
     * 修改链接管理
     */
    @PreAuthorize("@ss.hasPermi('system:link:edit')")
    @Log(title = "链接管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysLink sysLink)
    {
        sysLink.setUpdateBy(getUsername());
        return toAjax(sysLinkService.updateSysLink(sysLink));
    }

    /**
     * 删除链接管理
     */
    @PreAuthorize("@ss.hasPermi('system:link:remove')")
    @Log(title = "链接管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{linkIds}")
    public AjaxResult remove(@PathVariable Long[] linkIds)
    {
        return toAjax(sysLinkService.deleteSysLinkByIds(linkIds));
    }
}
