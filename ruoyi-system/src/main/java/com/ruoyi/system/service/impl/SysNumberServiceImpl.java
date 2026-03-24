package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.SysLink;
import com.ruoyi.system.domain.SysNumber;
import com.ruoyi.system.domain.SysTicket;
import com.ruoyi.system.mapper.SysLinkMapper;
import com.ruoyi.system.mapper.SysNumberMapper;
import com.ruoyi.system.mapper.SysTicketMapper;
import com.ruoyi.system.service.ISysNumberService;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlunit.BrowserVersion;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.WebWindow;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysNumberServiceImpl implements ISysNumberService
{
    @Autowired
    private SysNumberMapper sysNumberMapper;

    @Autowired
    private SysLinkMapper sysLinkMapper;

    @Autowired
    private SysTicketMapper sysTicketMapper;

    @Override
    public SysNumber selectSysNumberById(Long numberId)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysNumberMapper.selectSysNumberById(numberId, tenantId);
    }

    @Override
    public List<SysNumber> selectSysNumberList(SysNumber sysNumber)
    {
        applyTenantScope(sysNumber);
        return sysNumberMapper.selectSysNumberList(sysNumber);
    }

    @Override
    public int insertSysNumber(SysNumber sysNumber)
    {
        applyTenantForWrite(sysNumber);
        checkLinkAccess(sysNumber.getLinkId(), sysNumber.getTenantId());
        String numbers = sysNumber.getNumberValue();
        if (numbers == null)
        {
            return 0;
        }
        String normalized = numbers.replace("\r\n", "\n");
        String[] lines = normalized.split("\n");
        List<String> values = new ArrayList<>();
        for (String line : lines)
        {
            String v = line == null ? "" : line.trim();
            if (!v.isEmpty())
            {
                values.add(v);
            }
        }
        if (values.isEmpty())
        {
            return 0;
        }
        Date now = DateUtils.getNowDate();
        if (values.size() == 1)
        {
            sysNumber.setNumberValue(values.get(0));
            sysNumber.setCreateTime(now);
            return sysNumberMapper.insertSysNumber(sysNumber);
        }
        List<SysNumber> list = new ArrayList<>(values.size());
        for (String v : values)
        {
            SysNumber item = new SysNumber();
            item.setTenantId(sysNumber.getTenantId());
            item.setLinkId(sysNumber.getLinkId());
            item.setTicketNo(sysNumber.getTicketNo());
            item.setNumberValue(v);
            item.setNumberType(sysNumber.getNumberType());
            item.setVisitCount(sysNumber.getVisitCount());
            item.setInCount(sysNumber.getInCount());
            item.setStatus(sysNumber.getStatus());
            item.setCreateBy(sysNumber.getCreateBy());
            item.setCreateTime(now);
            list.add(item);
        }
        return sysNumberMapper.insertSysNumberBatch(list);
    }

    @Override
    public int updateSysNumber(SysNumber sysNumber)
    {
        applyTenantForWrite(sysNumber);
        checkLinkAccess(sysNumber.getLinkId(), sysNumber.getTenantId());
        sysNumber.setUpdateTime(DateUtils.getNowDate());
        return sysNumberMapper.updateSysNumber(sysNumber);
    }

    @Override
    public int updateSysNumberStatus(SysNumber sysNumber)
    {
        applyTenantForWrite(sysNumber);
        sysNumber.setUpdateTime(DateUtils.getNowDate());
        return sysNumberMapper.updateSysNumberStatus(sysNumber);
    }

    @Override
    public int deleteSysNumberByIds(Long[] numberIds)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysNumberMapper.deleteSysNumberByIds(numberIds, tenantId);
    }

    @Override
    public int deleteSysNumberById(Long numberId)
    {
        Long tenantId = resolveTenantIdForRead();
        return sysNumberMapper.deleteSysNumberById(numberId, tenantId);
    }

    @Override
    public String nextWsNumberByLink(Long linkId, String numberType)
    {
        Long tenantId = resolveTenantIdForRead();
        SysNumber n = sysNumberMapper.selectNextAvailableByLink(linkId, numberType, tenantId);
        if (n == null)
        {
            return null;
        }
        sysNumberMapper.touchNumber(n.getNumberId(), tenantId, SecurityUtils.getUsername(), DateUtils.getNowDate());
        return n.getNumberValue();
    }

    @Override
    public String resolveFinalUrl(String ticketLink, String ticketPassword)
    {
        UnlockResult r = unlock(ticketLink, ticketPassword);
        return r.finalUrl;
    }

    @Override
    public int importWsNumbersByTicketLink(String ticketLink, String ticketPassword, Long linkId, String ticketNo, String numberType)
    {
        UnlockResult r = unlock(ticketLink, ticketPassword);
        if (r.html == null || r.html.isEmpty())
        {
            return 0;
        }
        Long tenantId = currentUserTenantId();
        Map<String, String> statusMap = parseWsStatusMap(r.html);
        if (statusMap.isEmpty())
        {
            Set<String> values = extractWsNumbers(r.html);
            for (String v : values)
            {
                statusMap.put(v, "在线");
            }
        }
        if (statusMap.isEmpty())
        {
            return 0;
        }
        if (linkId != null)
        {
            checkLinkAccess(linkId, SecurityUtils.isAdmin() ? null : tenantId);
        }
        List<String> existing = sysNumberMapper.selectNumberValuesByLink(SecurityUtils.isAdmin() ? null : tenantId, linkId, numberType);
        Set<String> exists = new LinkedHashSet<>();
        if (existing != null)
        {
            exists.addAll(existing);
        }
        Date now = DateUtils.getNowDate();
        List<SysNumber> list = new ArrayList<>(statusMap.size());
        for (Map.Entry<String, String> e : statusMap.entrySet())
        {
            String v = e.getKey();
            String st = e.getValue() == null ? "" : e.getValue().trim();
            if (st.contains("离线") || st.contains("封禁"))
            {
                sysNumberMapper.deleteByValue(SecurityUtils.isAdmin() ? null : tenantId, linkId, v, numberType);
                continue;
            }
            if (exists.contains(v))
            {
                continue;
            }
            SysNumber item = new SysNumber();
            item.setTenantId(SecurityUtils.isAdmin() ? (tenantId != null ? tenantId : 1L) : tenantId);
            item.setLinkId(linkId);
            item.setTicketNo(ticketNo);
            item.setNumberValue(v);
            item.setNumberType(numberType);
            item.setVisitCount(0);
            item.setInCount(0);
            item.setStatus("0");
            item.setCreateBy(safeUsername());
            item.setCreateTime(now);
            list.add(item);
        }
        if (list.isEmpty())
        {
            return 0;
        }
        return sysNumberMapper.insertSysNumberBatch(list);
    }

    @Override
    public int syncWsNumbersAndCleanup()
    {
        List<SysTicket> tickets = sysTicketMapper.selectTicketsForWsSync();
        if (tickets == null || tickets.isEmpty())
        {
            return 0;
        }
        int changed = 0;
        for (SysTicket t : tickets)
        {
            try
            {
                if (t == null)
                {
                    continue;
                }
                String link = t.getTicketLink();
                if (link == null || link.trim().isEmpty())
                {
                    continue;
                }
                Long tenantId = t.getTenantId();
                Long linkId = t.getLinkId();
                if (linkId == null)
                {
                    continue;
                }
                String numberType = t.getNumberType();
                if (numberType == null || numberType.trim().isEmpty())
                {
                    numberType = "ws";
                }
                String ticketNo = String.valueOf(t.getTicketId());
                UnlockResult r = unlock(link, t.getTicketPassword());
                if (r.finalUrl != null && !r.finalUrl.isEmpty() && !r.finalUrl.equals(link))
                {
                    sysTicketMapper.updateTicketLink(t.getTicketId(), tenantId, r.finalUrl);
                    link = r.finalUrl;
                }
                if (r.html == null || r.html.isEmpty())
                {
                    continue;
                }
                Map<String, String> statusMap = parseWsStatusMap(r.html);
                if (statusMap.isEmpty())
                {
                    Set<String> values = extractWsNumbers(r.html);
                    for (String v : values)
                    {
                        statusMap.put(v, "在线");
                    }
                }
                if (statusMap.isEmpty())
                {
                    continue;
                }
                List<String> existing = sysNumberMapper.selectNumberValuesByLink(tenantId, linkId, numberType);
                Set<String> exists = new LinkedHashSet<>();
                if (existing != null)
                {
                    exists.addAll(existing);
                }
                Date now = DateUtils.getNowDate();
                List<SysNumber> inserts = new ArrayList<>();
                for (Map.Entry<String, String> e : statusMap.entrySet())
                {
                    String phone = e.getKey();
                    String st = e.getValue() == null ? "" : e.getValue().trim();
                    if (st.contains("离线") || st.contains("封禁"))
                    {
                        int del = sysNumberMapper.deleteByValue(tenantId, linkId, phone, numberType);
                        if (del > 0)
                        {
                            changed += del;
                        }
                        continue;
                    }
                    if (exists.contains(phone))
                    {
                        continue;
                    }
                    SysNumber item = new SysNumber();
                    item.setTenantId(tenantId);
                    item.setLinkId(linkId);
                    item.setTicketNo(ticketNo);
                    item.setNumberValue(phone);
                    item.setNumberType(numberType);
                    item.setVisitCount(0);
                    item.setInCount(0);
                    item.setStatus("0");
                    item.setCreateBy("system");
                    item.setCreateTime(now);
                    inserts.add(item);
                    exists.add(phone);
                }
                if (!inserts.isEmpty())
                {
                    changed += sysNumberMapper.insertSysNumberBatch(inserts);
                }
            }
            catch (Exception ignored) {}
        }
        return changed;
    }

    private static class UnlockResult
    {
        private final String finalUrl;
        private final String html;

        private UnlockResult(String finalUrl, String html)
        {
            this.finalUrl = finalUrl;
            this.html = html;
        }
    }

    private UnlockResult unlock(String ticketLink, String ticketPassword)
    {
        String url = ticketLink == null ? "" : ticketLink.trim();
        if (url.isEmpty())
        {
            throw new ServiceException("工单链接不能为空");
        }
        String password = ticketPassword == null ? "" : ticketPassword;
        try (WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED))
        {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.setPromptHandler((page, message, defaultValue) -> password);
            webClient.getPage(url);
            webClient.waitForBackgroundJavaScript(3000);
            boolean hasPopup = submitElementUiPassword(webClient, password, 15000);
            if (hasPopup && password.trim().isEmpty())
            {
                throw new ServiceException("工单密码不能为空");
            }
            webClient.waitForBackgroundJavaScript(15000);
            WebWindow window = webClient.getCurrentWindow();
            Page current = window == null ? null : window.getEnclosedPage();
            URL finalUrl = current == null ? null : current.getUrl();
            String html = current == null ? null : current.getWebResponse().getContentAsString();
            return new UnlockResult(finalUrl == null ? null : finalUrl.toString(), html);
        }
        catch (Exception e)
        {
            throw new ServiceException("解锁工单链接失败：" + e.getMessage());
        }
    }

    private boolean submitElementUiPassword(WebClient webClient, String password, long timeoutMs) throws Exception
    {
        if (webClient == null)
        {
            return false;
        }
        if (password == null)
        {
            password = "";
        }
        long deadline = System.currentTimeMillis() + Math.max(0, timeoutMs);
        boolean found = false;
        while (System.currentTimeMillis() < deadline)
        {
            WebWindow window = webClient.getCurrentWindow();
            Page current = window == null ? null : window.getEnclosedPage();
            if (!(current instanceof HtmlPage))
            {
                webClient.waitForBackgroundJavaScript(300);
                continue;
            }
            HtmlPage page = (HtmlPage) current;
            @SuppressWarnings("unchecked")
            List<HtmlInput> inputs = (List<HtmlInput>) (List<?>) page.getByXPath(
                "//div[contains(@class,'el-message-box')]//div[contains(@class,'el-message-box__input')]//input"
            );
            if (inputs != null && !inputs.isEmpty())
            {
                found = true;
                HtmlInput input = inputs.get(0);
                input.setValueAttribute(password);
                @SuppressWarnings("unchecked")
                List<HtmlElement> buttons = (List<HtmlElement>) (List<?>) page.getByXPath(
                    "//div[contains(@class,'el-message-box')]//div[contains(@class,'el-message-box__btns')]//button[contains(@class,'el-button--primary')]"
                );
                if (buttons != null && !buttons.isEmpty())
                {
                    buttons.get(0).click();
                    return true;
                }
            }
            webClient.waitForBackgroundJavaScript(300);
        }
        return found;
    }

    private Map<String, String> parseWsStatusMap(String html)
    {
        Map<String, String> out = new HashMap<>();
        if (html == null || html.isEmpty())
        {
            return out;
        }
        Pattern rowPattern = Pattern.compile("<tr\\s+class=\"el-table__row\"[\\s\\S]*?</tr>");
        Matcher rowMatcher = rowPattern.matcher(html);
        Pattern phonePattern = Pattern.compile("\\b60\\d{9,11}\\b");
        Pattern statusPattern = Pattern.compile("el-tag[^>]*>\\s*([^<\\s]+)\\s*<");
        while (rowMatcher.find())
        {
            String row = rowMatcher.group();
            Matcher pm = phonePattern.matcher(row);
            if (!pm.find())
            {
                continue;
            }
            String phone = pm.group();
            String status = "";
            Matcher sm = statusPattern.matcher(row);
            if (sm.find())
            {
                status = sm.group(1);
            }
            out.put(phone, status == null ? "" : status.trim());
        }
        return out;
    }

    private String safeUsername()
    {
        try
        {
            String u = SecurityUtils.getUsername();
            return u == null || u.isEmpty() ? "system" : u;
        }
        catch (Exception e)
        {
            return "system";
        }
    }

    private Set<String> extractWsNumbers(String content)
    {
        Set<String> out = new LinkedHashSet<>();
        if (content == null || content.isEmpty())
        {
            return out;
        }
        Pattern p = Pattern.compile("(?:\\+?60)?1\\d{8,9}");
        Matcher m = p.matcher(content);
        while (m.find())
        {
            String raw = m.group();
            if (raw == null || raw.isEmpty())
            {
                continue;
            }
            String digits = raw.replaceAll("\\D+", "");
            if (digits.startsWith("60"))
            {
                out.add(digits);
            }
            else if (digits.startsWith("1"))
            {
                out.add("60" + digits);
            }
        }
        return out;
    }

    private void checkLinkAccess(Long linkId, Long tenantId)
    {
        if (linkId == null)
        {
            return;
        }
        SysLink link = sysLinkMapper.selectSysLinkById(linkId, tenantId);
        if (link == null)
        {
            throw new ServiceException("分流链接不存在或无权限访问");
        }
    }

    private void applyTenantScope(SysNumber sysNumber)
    {
        if (sysNumber == null)
        {
            return;
        }
        if (!SecurityUtils.isAdmin())
        {
            sysNumber.setTenantId(currentUserTenantId());
        }
    }

    private void applyTenantForWrite(SysNumber sysNumber)
    {
        if (sysNumber == null)
        {
            return;
        }
        if (SecurityUtils.isAdmin())
        {
            if (sysNumber.getTenantId() == null)
            {
                Long tenantId = currentUserTenantId();
                sysNumber.setTenantId(tenantId != null ? tenantId : 1L);
            }
            return;
        }
        sysNumber.setTenantId(currentUserTenantId());
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
