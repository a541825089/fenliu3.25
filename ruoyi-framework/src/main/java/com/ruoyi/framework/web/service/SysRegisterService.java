package com.ruoyi.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.domain.SysTenantSubscription;
import com.ruoyi.system.mapper.SysTenantMapper;
import com.ruoyi.system.mapper.SysTenantSubscriptionMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import java.util.Date;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * 注册校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysTenantMapper tenantMapper;

    @Autowired
    private SysTenantSubscriptionMapper tenantSubscriptionMapper;

    /**
     * 注册
     */
    @Transactional
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            Date now = DateUtils.getNowDate();
            Long tenantId = createExpiredTenantForUser(username, now);
            sysUser.setTenantId(tenantId);
            sysUser.setNickName(username);
            sysUser.setPwdUpdateDate(now);
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag)
            {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                msg = "注册失败,请联系系统管理人员";
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    private Long createExpiredTenantForUser(String username, Date now)
    {
        SysTenant tenant = new SysTenant();
        tenant.setTenantName(username);
        tenant.setStatus("0");
        tenant.setCreateBy(username);
        tenant.setCreateTime(now);
        tenantMapper.insertSysTenant(tenant);
        Long tenantId = tenant.getTenantId();

        Date endTime = new Date(now.getTime() - 1000L);
        Date startTime = new Date(endTime.getTime() - 1000L);
        SysTenantSubscription sub = new SysTenantSubscription();
        sub.setTenantId(tenantId);
        sub.setPlanId(1L);
        sub.setStartTime(startTime);
        sub.setEndTime(endTime);
        sub.setStatus("0");
        sub.setCreateBy(username);
        sub.setCreateTime(now);
        tenantSubscriptionMapper.insertSysTenantSubscription(sub);
        return tenantId;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
