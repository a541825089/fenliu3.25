package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysNumber;
import com.ruoyi.system.service.ISysNumberService;
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
@RequestMapping("/system/number")
public class SysNumberController extends BaseController
{
    @Autowired
    private ISysNumberService sysNumberService;

    @PreAuthorize("@ss.hasPermi('system:number:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysNumber sysNumber)
    {
        startPage();
        List<SysNumber> list = sysNumberService.selectSysNumberList(sysNumber);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:number:export')")
    @Log(title = "号码管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysNumber sysNumber)
    {
        List<SysNumber> list = sysNumberService.selectSysNumberList(sysNumber);
        ExcelUtil<SysNumber> util = new ExcelUtil<SysNumber>(SysNumber.class);
        util.exportExcel(response, list, "号码管理数据");
    }

    @PreAuthorize("@ss.hasPermi('system:number:query')")
    @GetMapping(value = "/{numberId}")
    public AjaxResult getInfo(@PathVariable("numberId") Long numberId)
    {
        return AjaxResult.success(sysNumberService.selectSysNumberById(numberId));
    }

    @PreAuthorize("@ss.hasPermi('system:number:add')")
    @Log(title = "号码管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysNumber sysNumber)
    {
        sysNumber.setCreateBy(getUsername());
        return toAjax(sysNumberService.insertSysNumber(sysNumber));
    }

    @PreAuthorize("@ss.hasPermi('system:number:edit')")
    @Log(title = "号码管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysNumber sysNumber)
    {
        sysNumber.setUpdateBy(getUsername());
        return toAjax(sysNumberService.updateSysNumber(sysNumber));
    }

    @PreAuthorize("@ss.hasPermi('system:number:edit')")
    @Log(title = "号码管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysNumber sysNumber)
    {
        sysNumber.setUpdateBy(getUsername());
        return toAjax(sysNumberService.updateSysNumberStatus(sysNumber));
    }

    @PreAuthorize("@ss.hasPermi('system:number:remove')")
    @Log(title = "号码管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{numberIds}")
    public AjaxResult remove(@PathVariable Long[] numberIds)
    {
        return toAjax(sysNumberService.deleteSysNumberByIds(numberIds));
    }
}
