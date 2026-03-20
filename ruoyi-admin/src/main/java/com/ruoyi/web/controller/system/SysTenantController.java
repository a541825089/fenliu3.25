package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.service.ISysTenantService;
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
@RequestMapping({ "/system/tenant", "/prod-api/system/tenant", "/dev-api/system/tenant" })
public class SysTenantController extends BaseController
{
    @Autowired
    private ISysTenantService tenantService;

    @PreAuthorize("@ss.hasPermi('system:tenant:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysTenant sysTenant)
    {
        startPage();
        List<SysTenant> list = tenantService.selectSysTenantList(sysTenant);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:tenant:query')")
    @GetMapping(value = "/{tenantId}")
    public AjaxResult getInfo(@PathVariable("tenantId") Long tenantId)
    {
        return AjaxResult.success(tenantService.selectSysTenantById(tenantId));
    }

    @PreAuthorize("@ss.hasPermi('system:tenant:add')")
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysTenant sysTenant)
    {
        sysTenant.setCreateBy(getUsername());
        return toAjax(tenantService.insertSysTenant(sysTenant));
    }

    @PreAuthorize("@ss.hasPermi('system:tenant:edit')")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysTenant sysTenant)
    {
        sysTenant.setUpdateBy(getUsername());
        return toAjax(tenantService.updateSysTenant(sysTenant));
    }

    @PreAuthorize("@ss.hasPermi('system:tenant:edit')")
    @Log(title = "租户续费", businessType = BusinessType.UPDATE)
    @PutMapping("/renew")
    public AjaxResult renew(@RequestBody SysTenant sysTenant)
    {
        return toAjax(tenantService.renewTenant(sysTenant.getTenantId(), sysTenant.getPlanId(), sysTenant.getEndTime()));
    }

    @PreAuthorize("@ss.hasPermi('system:tenant:remove')")
    @Log(title = "租户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tenantIds}")
    public AjaxResult remove(@PathVariable Long[] tenantIds)
    {
        return toAjax(tenantService.deleteSysTenantByIds(tenantIds));
    }
}

