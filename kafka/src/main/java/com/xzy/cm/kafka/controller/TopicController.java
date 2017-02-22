package com.xzy.cm.kafka.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzy.cm.common.exception.BussinessException;
import com.xzy.cm.common.exception.ErrorCodeEnum;
import com.xzy.cm.common.helper.JOHelper;
import com.xzy.cm.mvc.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xiongzhanyuan on 2017/2/15.
 */
@Controller
public class TopicController extends BaseController{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 生产消息
     * @return
     */
    @RequestMapping(value = "/service/producer/add")
    @ResponseBody
    public JSONObject addProducer(@RequestBody JSONObject param) throws BussinessException{
        if (ObjectUtils.isEmpty(param)) {
            throw new BussinessException(ErrorCodeEnum.SYSTEM_ERROR.PARAMETERFORMATERROR);
        }
        String topic = param.getString("topic");
        String msg = param.getString("msg");
        kafkaTemplate.send(topic, msg);
        return JOHelper.gen("data", "生产消息成功", "status", 1);
    }

}
