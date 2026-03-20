package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysTicket;
import com.ruoyi.system.service.ISysTicketService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/ticket")
public class SysTicketController extends BaseController
{
    @Autowired
    private ISysTicketService sysTicketService;

    @PreAuthorize("@ss.hasPermi('system:ticket:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysTicket sysTicket)
    {
        startPage();
        List<SysTicket> list = sysTicketService.selectSysTicketList(sysTicket);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:ticket:export')")
    @Log(title = "工单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysTicket sysTicket)
    {
        List<SysTicket> list = sysTicketService.selectSysTicketList(sysTicket);
        ExcelUtil<SysTicket> util = new ExcelUtil<SysTicket>(SysTicket.class);
        util.exportExcel(response, list, "工单管理数据");
    }

    @PreAuthorize("@ss.hasPermi('system:ticket:query')")
    @GetMapping(value = "/{ticketId}")
    public AjaxResult getInfo(@PathVariable("ticketId") Long ticketId)
    {
        return AjaxResult.success(sysTicketService.selectSysTicketById(ticketId));
    }

    @PreAuthorize("@ss.hasPermi('system:ticket:add')")
    @Log(title = "工单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysTicket sysTicket)
    {
        sysTicket.setCreateBy(getUsername());
        return toAjax(sysTicketService.insertSysTicket(sysTicket));
    }

    @PreAuthorize("@ss.hasPermi('system:ticket:edit')")
    @Log(title = "工单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysTicket sysTicket)
    {
        sysTicket.setUpdateBy(getUsername());
        return toAjax(sysTicketService.updateSysTicket(sysTicket));
    }

    @PreAuthorize("@ss.hasPermi('system:ticket:remove')")
    @Log(title = "工单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ticketIds}")
    public AjaxResult remove(@PathVariable Long[] ticketIds)
    {
        return toAjax(sysTicketService.deleteSysTicketByIds(ticketIds));
    }
}
