package com.xzy.cm.quartz.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzy.cm.mvc.controller.BaseController;
import com.xzy.cm.quartz.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class ScheduleController extends BaseController{
	
	@Autowired
	@Qualifier(value = "service/quartz/redis")
	private ScheduleService scheduleService;

	/**
	 * 创建一个任务
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/service/quartz/add_job")
	@ResponseBody
	public JSONObject addJob(@RequestBody JSONObject param) throws Exception {
		return scheduleService.addJob(param);
	}

	/**
	 * 暂停一个job
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/service/quartz/pause_job")
	@ResponseBody
	public JSONObject pauseJob(@RequestBody JSONObject param) throws Exception {
		return scheduleService.pauseJob(param);
	}

	/**
	 * 恢复一个job
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/service/quartz/resume_job")
	@ResponseBody
	public JSONObject resumeJob(@RequestBody JSONObject param) throws Exception {
		return scheduleService.resumeJob(param);
	}

	/**
	 * 删除一个job
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/service/quartz/delete_job")
	@ResponseBody
	public JSONObject deleteJob(@RequestBody JSONObject param) throws Exception {
		return scheduleService.deleteJob(param);
	}

	/**
	 * 立即执行这个job
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/service/quartz/run_now")
	@ResponseBody
	public JSONObject runAJobNow(@RequestBody JSONObject param) throws Exception {
		return scheduleService.runAJobNow(param);
	}

	/**
	 * 更新job执行时间
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/service/quartz/update_job_cro")
	@ResponseBody
	public JSONObject updateJobCro(@RequestBody JSONObject param) throws Exception {
		return scheduleService.updateJobCro(param);
	}

	/**
	 * 查询正在执行的job列表
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/service/quartz/get_running_job")
	@ResponseBody
	public JSONObject getRunningJob(@RequestBody JSONObject param) throws Exception {
		return scheduleService.getRunningJob(param);
	}

	/**
	 * 查询当前定时是否存在
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/service/quartz/check_job")
	@ResponseBody
	public JSONObject checkJob(@RequestBody JSONObject param) throws Exception {
		return scheduleService.checkJob(param);
	}
}
