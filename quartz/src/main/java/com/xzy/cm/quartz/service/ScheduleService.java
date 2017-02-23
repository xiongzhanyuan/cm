package com.xzy.cm.quartz.service;

import com.alibaba.fastjson.JSONObject;
import org.quartz.SchedulerException;

public interface ScheduleService {

	/**
	 * 创建一个任务
	 *
	 * @param param
	 * @throws Exception
	 */
	JSONObject addJob(JSONObject param) throws Exception;

	/**
	 * 暂停一个job
	 *
	 * @param param
	 * @return
	 */
	JSONObject pauseJob(JSONObject param) throws Exception;

	/**
	 * 恢复一个job
	 *
	 * @param param
	 * @return
	 */
	JSONObject resumeJob(JSONObject param) throws Exception;

	/**
	 * 删除一个job
	 *
	 * @param param
	 * @return
	 */
	JSONObject deleteJob(JSONObject param) throws Exception;

	/**
	 * 立即执行这个job
	 *
	 * @param param
	 * @return
	 */
	JSONObject runAJobNow(JSONObject param) throws Exception;

	/**
	 * 更新job执行时间
	 *
	 * @param param
	 * @return
	 */
	JSONObject updateJobCro(JSONObject param) throws Exception;

	/**
	 * 查询正在执行的job列表
	 *
	 * @param param
	 * @return
	 */
	JSONObject getRunningJob(JSONObject param) throws Exception;

	/**
	 * 查询当前定时是否存在
	 *
	 * @param param
	 * @return
	 */
	JSONObject checkJob(JSONObject param) throws Exception;
}
