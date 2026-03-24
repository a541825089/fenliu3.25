package com.ruoyi.quartz.task;

import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.service.ISysJobService;
import com.ruoyi.system.service.ISysNumberService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component("wsNumberStatusTask")
public class WsNumberStatusTask implements ApplicationRunner
{
    private static final Logger log = LoggerFactory.getLogger(WsNumberStatusTask.class);

    @Autowired
    private ISysNumberService sysNumberService;

    @Autowired
    private ISysJobService jobService;

    @Override
    public void run(ApplicationArguments args)
    {
        try
        {
            ensureJob();
        }
        catch (Exception e)
        {
            log.warn("ensure WS job failed: {}", e.getMessage());
        }
    }

    public void ensureJob()
    {
        String invokeTarget = "wsNumberStatusTask.checkWsStatus()";
        String cron = "0 */1 * * * ?";
        SysJob query = new SysJob();
        query.setJobName("WS号码状态检测");
        List<SysJob> jobs = jobService.selectJobList(query);
        if (jobs != null && !jobs.isEmpty())
        {
            SysJob job = jobs.get(0);
            if (!cron.equals(job.getCronExpression()))
            {
                job.setCronExpression(cron);
                try { jobService.updateJob(job); } catch (Exception e) { log.warn("update WS job cron failed: {}", e.getMessage()); }
            }
            if (!ScheduleConstants.Status.NORMAL.getValue().equals(job.getStatus()))
            {
                SysJob db = jobService.selectJobById(job.getJobId());
                db.setStatus(ScheduleConstants.Status.NORMAL.getValue());
                try { jobService.changeStatus(db); } catch (Exception e) { log.warn("enable WS job failed: {}", e.getMessage()); }
            }
            return;
        }
        SysJob job = new SysJob();
        job.setJobName("WS号码状态检测");
        job.setJobGroup("DEFAULT");
        job.setInvokeTarget(invokeTarget);
        job.setCronExpression(cron);
        job.setMisfirePolicy(ScheduleConstants.MISFIRE_DO_NOTHING);
        job.setConcurrent("1");
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        job.setCreateBy("system");
        try
        {
            jobService.insertJob(job);
            SysJob db = jobService.selectJobById(job.getJobId());
            db.setStatus(ScheduleConstants.Status.NORMAL.getValue());
            jobService.changeStatus(db);
        }
        catch (Exception e)
        {
            log.warn("create WS job failed: {}", e.getMessage());
        }
    }

    public void checkWsStatus()
    {
        sysNumberService.syncWsNumbersAndCleanup();
    }
}
