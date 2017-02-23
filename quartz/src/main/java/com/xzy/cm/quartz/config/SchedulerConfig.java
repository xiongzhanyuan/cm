package com.xzy.cm.quartz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * Created by xiongzhanyuan on 17/2/23.
 */
@Configuration
public class SchedulerConfig {

    @Autowired
    private DataSource dataSource;

    @Bean(name = "spring.scheduler.quartz", initMethod = "start", destroyMethod = "destroy")
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(false);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("appctx");
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));
        schedulerFactoryBean.setStartupDelay(30);
        schedulerFactoryBean.setOverwriteExistingJobs(true);

        return schedulerFactoryBean;
    }
}
