package com.mingzhu.quartz.controller;

import com.mingzhu.quartz.job.User;
import com.mingzhu.quartz.service.QuartzUserService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class QuartzUserController {

    @Autowired
    QuartzUserService quartzUserService;


    /**
     * 添加一个任务
     * @param user
     * @throws SchedulerException
     */
    @GetMapping("/add")
    public void addQuartz(User user) throws SchedulerException {

        System.out.println("start........................................");
        System.out.println(user);
        if(user!=null){
            System.out.println(user.toString());
            quartzUserService.addQuartz(user);
        }

    }

    /**
     * 删除定时任务
     * @param uid
     * @throws SchedulerException
     */
    @GetMapping("/delete")
    public void deleteJob(@RequestParam Integer uid) throws SchedulerException {

        quartzUserService.deleteJob(uid);

    }
}
