package com.xzy.cm.mvc.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzy.cm.common.exception.BussinessException;
import com.xzy.cm.common.exception.ErrorCodeEnum;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xiongzhanyuan on 2017/2/16.
 */
public class BaseController {

    protected static final Logger log = Logger.getLogger("CONTROLLER");

    /**
     * 处理自定义异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BussinessException.class)
    @ResponseBody
    public JSONObject handleMyException(BussinessException ex) {
        JSONObject jo = new JSONObject();
        if (ex.getMessage().contains("#")) {
            jo.put("message", "系统异常!请尽快联系管理员!");
        } else {
            jo.put("message", ex.getMessage());
        }
        jo.put("error_code", ex.getErrorCodeEnum().getResultCode());

        log.error(ex.getMessage(), ex);
        return jo;
    }

    /**
     * 处理运行时异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public JSONObject handleThrowable(Throwable ex) {
        BussinessException b_ex = null;
        if (!(ex instanceof BussinessException)) {
            b_ex = new BussinessException(ErrorCodeEnum.SYSTEM_ERROR, ex);
        } else {
            b_ex = (BussinessException) ex;
        }
        return handleMyException(b_ex);
    }
}
