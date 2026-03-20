package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysNumber;
import com.ruoyi.system.mapper.SysNumberMapper;
import com.ruoyi.system.service.ISysNumberService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysNumberServiceImpl implements ISysNumberService
{
    @Autowired
    private SysNumberMapper sysNumberMapper;

    @Override
    public SysNumber selectSysNumberById(Long numberId)
    {
        return sysNumberMapper.selectSysNumberById(numberId);
    }

    @Override
    public List<SysNumber> selectSysNumberList(SysNumber sysNumber)
    {
        return sysNumberMapper.selectSysNumberList(sysNumber);
    }

    @Override
    public int insertSysNumber(SysNumber sysNumber)
    {
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
        sysNumber.setUpdateTime(DateUtils.getNowDate());
        return sysNumberMapper.updateSysNumber(sysNumber);
    }

    @Override
    public int updateSysNumberStatus(SysNumber sysNumber)
    {
        sysNumber.setUpdateTime(DateUtils.getNowDate());
        return sysNumberMapper.updateSysNumberStatus(sysNumber);
    }

    @Override
    public int deleteSysNumberByIds(Long[] numberIds)
    {
        return sysNumberMapper.deleteSysNumberByIds(numberIds);
    }

    @Override
    public int deleteSysNumberById(Long numberId)
    {
        return sysNumberMapper.deleteSysNumberById(numberId);
    }
}
