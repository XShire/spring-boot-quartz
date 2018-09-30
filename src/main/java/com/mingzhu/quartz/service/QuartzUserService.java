package com.mingzhu.quartz.service;

import com.mingzhu.quartz.job.UserJob;
import com.mingzhu.quartz.job.User;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class QuartzUserService {

    private static final String JOB_GROUP = "event_job_group";
    private static final String TRIGGER_GROUP = "event_trigger_group";

    @Autowired
    private Scheduler scheduler;

    /**
     * 添加一个任务
     * @param user
     * @throws SchedulerException
     */
    public void addQuartz(User user) throws SchedulerException {

        Date triggerDate = user.getStartTime();

        JobDetail job = JobBuilder.newJob(UserJob.class)
                .withIdentity(user.getUid().toString(), JOB_GROUP)
                .usingJobData("uid", user.getUid())
                .build();

        //cron表达式 表示每隔i秒执行
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(String.format("0/%d * * * * ? ",10)).withMisfireHandlingInstructionDoNothing();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(user.getUid().toString(), TRIGGER_GROUP)
                .startAt(triggerDate)
                .withSchedule(scheduleBuilder)
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    /**
     * 删除定时任务
     * 1、获取到定时任务调度器
     * 2、停止触发器
     * 3、删除触发器
     * 4、删除任务
     * @param uid
     */
    public void deleteJob(Integer uid) throws SchedulerException {

        //停止触发器
        TriggerKey triggerKey  = TriggerKey.triggerKey(uid.toString(), TRIGGER_GROUP);
        scheduler.pauseTrigger(triggerKey);

        //删除触发器
        scheduler.unscheduleJob(triggerKey);

        //删除任务
        JobKey jobKey = JobKey.jobKey(uid.toString(),JOB_GROUP);
        scheduler.deleteJob(jobKey);

    }

}
