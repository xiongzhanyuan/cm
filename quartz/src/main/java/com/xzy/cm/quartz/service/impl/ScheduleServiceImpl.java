package com.xzy.cm.quartz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xzy.cm.common.exception.BussinessException;
import com.xzy.cm.common.exception.ErrorCodeEnum;
import com.xzy.cm.common.helper.JOHelper;
import com.xzy.cm.quartz.service.ScheduleService;

import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service(value = "service/quartz/redis")
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private Scheduler scheduler;

	@Override
	public JSONObject addJob(JSONObject param) throws Exception {
		String jobName = param.getString("jobName");
		String jobGroup = param.getString("jobGroup");
		String triggerName = param.getString("triggerName");
		String cronExpression = param.getString("cronExpression"); //时间cron表达式
		String desc = param.getString("desc");
		String clazz = param.getString("clazz");	//要执行的任务的类名
		JSONObject data = param.getJSONObject("data");

		if (StringUtils.isBlank(jobName) || StringUtils.isBlank(jobGroup) ||
				StringUtils.isBlank(triggerName)) {
			throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
		}

		CronTriggerImpl trigger = new CronTriggerImpl();
		trigger.setCronExpression(cronExpression);
		trigger.setName(triggerName);
		trigger.setGroup(jobGroup);
		trigger.setMisfireInstruction(trigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		trigger.setDescription(desc);
		JobDetail job = JobBuilder.newJob((Class<? extends Job>) Class.forName(clazz)).withIdentity(jobName, jobGroup).withDescription(desc).build();
		job.getJobDataMap().put("data", data);

		scheduler.scheduleJob(job, trigger);

		JSONObject result = new JSONObject();
		result.put("data", "创建成功");
		result.put("status", 1);
		return result;
	}

	@Override
	public JSONObject pauseJob(JSONObject param) throws Exception {
		String jobName = param.getString("jobName");
		String jobGroup = param.getString("jobGroup");
		if (StringUtils.isBlank(jobName) || StringUtils.isBlank(jobGroup)) {
			throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
		}
		scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));

		JSONObject result = new JSONObject();
		result.put("data", "暂停成功");
		result.put("status", 1);
		return result;
	}

	@Override
	public JSONObject resumeJob(JSONObject param) throws Exception {
		String jobName = param.getString("jobName");
		String jobGroup = param.getString("jobGroup");
		if (StringUtils.isBlank(jobName) || StringUtils.isBlank(jobGroup)) {
			throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
		}

		scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));

		JSONObject result = new JSONObject();
		result.put("data", "恢复成功");
		result.put("status", 1);
		return result;
	}

	@Override
	public JSONObject deleteJob(JSONObject param) throws Exception {
		String jobName = param.getString("jobName");
		String jobGroup = param.getString("jobGroup");
		if (StringUtils.isBlank(jobName) || StringUtils.isBlank(jobGroup)) {
			throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
		}
		boolean status = scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));

		JSONObject result = new JSONObject();
		if (status) {
			result.put("data", "删除成功");
		} else {
			result.put("data", "删除失败");
		}
		result.put("status", 1);
		return result;
	}

	@Override
	public JSONObject runAJobNow(JSONObject param) throws Exception {
		String jobName = param.getString("jobName");
		String jobGroup = param.getString("jobGroup");
		if (StringUtils.isBlank(jobName) || StringUtils.isBlank(jobGroup)) {
			throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
		}

		scheduler.triggerJob(JobKey.jobKey(jobName, jobGroup));

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", "执行成功");
		jsonObject.put("status", 1);
		return jsonObject;
	}

	@Override
	public JSONObject updateJobCro(JSONObject param) throws Exception {
		String jobName = param.getString("jobName");
		String jobGroup = param.getString("jobGroup");
		String cronExpression = param.getString("cronExpression");

		if (StringUtils.isBlank(jobName) || StringUtils.isBlank(jobGroup) ||
				StringUtils.isBlank(cronExpression)) {
			throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
		}

		//获取triggerKey
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		Trigger oldTrigger = scheduler.getTrigger(triggerKey);

		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

		//判断是否有old的trigger
		if (oldTrigger != null) {

			TriggerBuilder tb = oldTrigger.getTriggerBuilder();
			Trigger newTrigger = tb.withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

			scheduler.rescheduleJob(triggerKey, newTrigger);
		} else {
			//如果没有trigger, 默认trigger: name group
			String triggerName = "default_trigger_" + new Date().getTime();
			String triggerGroup = triggerName;

			TriggerKey tk = new TriggerKey(triggerName, triggerGroup);

			TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(tk).withSchedule(cronScheduleBuilder).forJob(jobName, jobGroup);
			Trigger trigger = triggerBuilder.build();
			scheduler.scheduleJob(trigger);

		}

		JSONObject result = new JSONObject();
		result.put("data", "更新成功");
		result.put("status", 1);
		return result;
	}

	@Override
	public JSONObject getRunningJob(JSONObject param) throws Exception {
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<JSONObject> jobList = new ArrayList<JSONObject>(executingJobs.size());
		for (JobExecutionContext executingJob : executingJobs) {
			JSONObject jobJson = new JSONObject();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();

			jobJson.put("jobName", jobKey.getName());
			jobJson.put("jobGroup", jobKey.getGroup());
			jobJson.put("triggerName", trigger.getKey().getName());
			jobJson.put("triggerGroup", trigger.getKey().getGroup());

			//获取执行状态
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			jobJson.put("jobStatus", triggerState.name());
			if (trigger instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				String cronExpression = cronTrigger.getCronExpression();
				jobJson.put("cronExpression", cronExpression);
			}
			jobList.add(jobJson);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("list", jobList);
		return jsonObject;
	}

	@Override
	public JSONObject checkJob(JSONObject param) throws Exception {
		JSONObject paramJo = param.getJSONObject("param");
		String jobName = paramJo.getString("jobName");
		String jobGroup = paramJo.getString("jobGroup");
		JobKey key = JobKey.jobKey(jobName, jobGroup);
		Boolean flag = scheduler.checkExists(key);
		return JOHelper.gen("flag", flag);
	}
}
