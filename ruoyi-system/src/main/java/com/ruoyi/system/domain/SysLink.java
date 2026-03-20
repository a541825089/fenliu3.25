package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 链接管理对象 sys_link
 * 
 * @author ruoyi
 */
public class SysLink extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 链接ID */
    private Long linkId;

    /** 投放国家 */
    @Excel(name = "投放国家")
    private String targetCountry;

    /** 链接URL */
    @Excel(name = "链接URL")
    private String linkUrl;

    /** 链接描述 */
    @Excel(name = "链接描述")
    private String linkDescription;

    /** 回复语 */
    @Excel(name = "回复语")
    private String replyMsg;

    /** IP防护（0开启 1关闭） */
    @Excel(name = "IP防护", readConverterExp = "0=开启,1=关闭")
    private String ipProtection;

    /** 随机打乱（0开启 1关闭） */
    @Excel(name = "随机打乱", readConverterExp = "0=开启,1=关闭")
    private String isScramble;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setLinkId(Long linkId) 
    {
        this.linkId = linkId;
    }

    public Long getLinkId() 
    {
        return linkId;
    }
    public void setTargetCountry(String targetCountry) 
    {
        this.targetCountry = targetCountry;
    }

    public String getTargetCountry() 
    {
        return targetCountry;
    }
    public void setLinkUrl(String linkUrl) 
    {
        this.linkUrl = linkUrl;
    }

    public String getLinkUrl() 
    {
        return linkUrl;
    }
    public void setLinkDescription(String linkDescription) 
    {
        this.linkDescription = linkDescription;
    }

    public String getLinkDescription() 
    {
        return linkDescription;
    }
    public void setReplyMsg(String replyMsg) 
    {
        this.replyMsg = replyMsg;
    }

    public String getReplyMsg() 
    {
        return replyMsg;
    }
    public void setIpProtection(String ipProtection) 
    {
        this.ipProtection = ipProtection;
    }

    public String getIpProtection() 
    {
        return ipProtection;
    }
    public void setIsScramble(String isScramble) 
    {
        this.isScramble = isScramble;
    }

    public String getIsScramble() 
    {
        return isScramble;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("linkId", getLinkId())
            .append("targetCountry", getTargetCountry())
            .append("linkUrl", getLinkUrl())
            .append("linkDescription", getLinkDescription())
            .append("replyMsg", getReplyMsg())
            .append("ipProtection", getIpProtection())
            .append("isScramble", getIsScramble())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
