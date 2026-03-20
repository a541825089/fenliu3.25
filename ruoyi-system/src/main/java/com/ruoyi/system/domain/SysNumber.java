package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SysNumber extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long tenantId;

    private Long numberId;

    @Excel(name = "分流链接ID")
    private Long linkId;

    @Excel(name = "链接URL")
    private String linkUrl;

    @Excel(name = "工单号")
    private String ticketNo;

    @Excel(name = "号码")
    private String numberValue;

    @Excel(name = "号码类型")
    private String numberType;

    @Excel(name = "访问次数")
    private Integer visitCount;

    @Excel(name = "进线人数")
    private Integer inCount;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getNumberId()
    {
        return numberId;
    }

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
    }

    public void setNumberId(Long numberId)
    {
        this.numberId = numberId;
    }

    public Long getLinkId()
    {
        return linkId;
    }

    public void setLinkId(Long linkId)
    {
        this.linkId = linkId;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public String getTicketNo()
    {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo)
    {
        this.ticketNo = ticketNo;
    }

    public String getNumberValue()
    {
        return numberValue;
    }

    public void setNumberValue(String numberValue)
    {
        this.numberValue = numberValue;
    }

    public String getNumberType()
    {
        return numberType;
    }

    public void setNumberType(String numberType)
    {
        this.numberType = numberType;
    }

    public Integer getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount)
    {
        this.visitCount = visitCount;
    }

    public Integer getInCount()
    {
        return inCount;
    }

    public void setInCount(Integer inCount)
    {
        this.inCount = inCount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("tenantId", getTenantId())
            .append("numberId", getNumberId())
            .append("linkId", getLinkId())
            .append("linkUrl", getLinkUrl())
            .append("ticketNo", getTicketNo())
            .append("numberValue", getNumberValue())
            .append("numberType", getNumberType())
            .append("visitCount", getVisitCount())
            .append("inCount", getInCount())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
