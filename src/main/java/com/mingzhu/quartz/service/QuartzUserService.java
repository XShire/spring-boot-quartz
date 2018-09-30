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
     * 1、需要获取到任务调度器Scheduler
     * 2、定义jobDetail
     * 3、定义trigger
     * 4、使用Scheduler添加任务
     * @param user
     * @throws SchedulerException
     */
    public void addQuartz(User user) throws SchedulerException {

        //任务开始时间
        Date triggerDate = user.getStartTime();

        //定义jobDetail
        JobDetail job = JobBuilder.newJob(UserJob.class)
                .withIdentity(user.getUid().toString(), JOB_GROUP)
                .usingJobData("uid", user.getUid())
                .build();

        //cron表达式 表示每隔i秒执行
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(String.format("0/%d * * * * ? ",10)).withMisfireHandlingInstructionDoNothing();

        //定义trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(user.getUid().toString(), TRIGGER_GROUP)
                .startAt(triggerDate)
                .withSchedule(scheduleBuilder)
                .build();

        //使用Scheduler添加任务
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

    /**
     * 修改一个任务
     * @param uid
     * @throws SchedulerException
     */
    public void updateJob(Integer uid) throws SchedulerException {

        //查询任务
        TriggerKey triggerKey = TriggerKey.triggerKey(uid.toString(), TRIGGER_GROUP);
        CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
        if(trigger == null) {
            return;
        }

        //cron表达式 表示每隔i秒执行
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(String.format("0/%d * * * * ? ",8)).withMisfireHandlingInstructionDoNothing();

        //重新定义trigger
        trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(scheduleBuilder)
                .build();

        //修改任务
        scheduler.rescheduleJob(triggerKey, trigger);

    }

    /**
     * 暂停所有的定时任务
     * @throws SchedulerException
     */
    public void standby() throws SchedulerException {
        scheduler.standby();
    }

    /**
     * 启动所有的定时任务
     * @throws SchedulerException
     */
    public void startJobs() throws SchedulerException {
        if(!scheduler.isShutdown()) {
            scheduler.start();
        }
    }

    /**
     * 关闭所有的定时任务
     * @throws SchedulerException
     */
    public void shutdownJobs() throws SchedulerException {
        if(!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

}
