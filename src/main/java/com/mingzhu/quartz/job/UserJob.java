package com.mingzhu.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class UserJob extends QuartzJobBean {

    //任务传入的参数
    private Integer uid;

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        System.out.println(String.format("Hello %s!", this.uid));
    }
}
