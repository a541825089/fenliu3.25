package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SysTicket extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long ticketId;

    @Excel(name = "工单类型")
    private String ticketType;

    @Excel(name = "工单名称")
    private String ticketName;

    @Excel(name = "工单链接")
    private String ticketLink;

    @Excel(name = "分流链接ID")
    private Long linkId;

    @Excel(name = "分流链接")
    private String linkUrl;

    @Excel(name = "号码类型")
    private String numberType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "到期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Excel(name = "工单总量")
    private Integer totalCount;

    @Excel(name = "完成数量")
    private Integer finishCount;

    @Excel(name = "下号比率")
    private Integer downRatio;

    @Excel(name = "工单账号")
    private String ticketAccount;

    @Excel(name = "工单密码")
    private String ticketPassword;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getTicketId()
    {
        return ticketId;
    }

    public void setTicketId(Long ticketId)
    {
        this.ticketId = ticketId;
    }

    public String getTicketType()
    {
        return ticketType;
    }

    public void setTicketType(String ticketType)
    {
        this.ticketType = ticketType;
    }

    public String getTicketName()
    {
        return ticketName;
    }

    public void setTicketName(String ticketName)
    {
        this.ticketName = ticketName;
    }

    public String getTicketLink()
    {
        return ticketLink;
    }

    public void setTicketLink(String ticketLink)
    {
        this.ticketLink = ticketLink;
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

    public String getNumberType()
    {
        return numberType;
    }

    public void setNumberType(String numberType)
    {
        this.numberType = numberType;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getFinishCount()
    {
        return finishCount;
    }

    public void setFinishCount(Integer finishCount)
    {
        this.finishCount = finishCount;
    }

    public Integer getDownRatio()
    {
        return downRatio;
    }

    public void setDownRatio(Integer downRatio)
    {
        this.downRatio = downRatio;
    }

    public String getTicketAccount()
    {
        return ticketAccount;
    }

    public void setTicketAccount(String ticketAccount)
    {
        this.ticketAccount = ticketAccount;
    }

    public String getTicketPassword()
    {
        return ticketPassword;
    }

    public void setTicketPassword(String ticketPassword)
    {
        this.ticketPassword = ticketPassword;
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
            .append("ticketId", getTicketId())
            .append("ticketType", getTicketType())
            .append("ticketName", getTicketName())
            .append("ticketLink", getTicketLink())
            .append("linkId", getLinkId())
            .append("linkUrl", getLinkUrl())
            .append("numberType", getNumberType())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("totalCount", getTotalCount())
            .append("finishCount", getFinishCount())
            .append("downRatio", getDownRatio())
            .append("ticketAccount", getTicketAccount())
            .append("ticketPassword", getTicketPassword())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
